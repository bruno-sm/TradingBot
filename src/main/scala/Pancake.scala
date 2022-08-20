import cats.effect.*
import org.web3j.protocol.Web3j
import org.web3j.crypto.Credentials
import java.math.BigInteger
import contractwrappers.{PancakeFactory, PancakePair, PancakeRouter}
import org.web3j.utils.Convert
import Token.*


class Pancake(web3: Web3j, credentials: Credentials):
    val routerContract = PancakeRouter.load("0x10ED43C718714eb63d5aA57B78B54704E256024E", web3, credentials, BigInteger("0"), BigInteger("0"))
    val factoryContract = PancakeFactory.load("0xcA143Ce32Fe78f1f7019d7d551a6402fC5350c73", web3, credentials, BigInteger("0"), BigInteger("0"))

    def pairContract(token0: Token, token1: Token): IO[PancakePair] =
        for {
            pairAddress <- IO(factoryContract.getPair(token0.address, token1.address).send)
            pair <- IO(PancakePair.load(pairAddress, web3, credentials, BigInteger("0"), BigInteger("0")))
        } yield pair


    def getAmountOut(amount: TokenAmount, toToken: Token): IO[(TokenAmount, BigInt)] =
        def getReservesFromAndToToken(token_from: Token, pair: PancakePair, reserves: org.web3j.tuples.generated.Tuple3[BigInteger, BigInteger, BigInteger]) =
            IO(pair.token0.send).map(token0_address =>
                if (token_from.address == token0_address) {
                    (reserves.getValue1, reserves.getValue2)
                } else {
                    (reserves.getValue2, reserves.getValue1)
                }
            )
            
        for {
            pair <- pairContract(amount.token, toToken)
            reserves <- IO(pair.getReserves.send)
            (reservesFromToken, reservesToToken) <- getReservesFromAndToToken(amount.token, pair, reserves)
            lastInteractionTimestamp <- IO(reserves.getValue3)
            amountOut <- IO(routerContract.getAmountOut(amount.wei.bigInteger, reservesFromToken, reservesToToken).send)
        } yield (TokenAmount.fromWei(toToken, amountOut), lastInteractionTimestamp)


    def pairPrice(token0: Token, token1: Token): IO[(TokenAmount, BigInt)] =
      val amountIn = TokenAmount.fromEth(token0, 1)
      getAmountOut(amountIn, token1)