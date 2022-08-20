import org.web3j.utils.Convert

enum Token(val ticket: String, val address: String):
    case BNB extends Token("BNB", "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c".toLowerCase)
    case BUSD extends Token("BUSD", "0xe9e7CEA3DedcA5984780Bafc599bD69ADd087D56".toLowerCase)
    case ETH extends Token("ETH", "0x2170ed0880ac9a755fd29b2688956bd959f933f8")
    case ADA extends Token("ADA", "0x3ee2200efb3400fabb9aacf31297cbdd1d435d47")
    case DOGE extends Token("DOGE", "0xba2ae424d960c26247dd6c32edc70b295c744c43")
    case DOT extends Token("DOT", "0x7083609fce4d1d8dc0c979aab8c869ea2c873402")
    case SHIB extends Token("SHIB", "0x2859e4544c4bb03966803b044a93563bd2d0dd4d")



case class TokenAmount protected (token: Token, wei: BigInt):
    lazy val eth: Double = Convert.fromWei(wei.toString, Convert.Unit.ETHER).doubleValue

    override def toString() =
      eth.toString + " " + token.ticket

    def + (that: TokenAmount) =
        assert(this.token == that.token)
        new TokenAmount(this.token, this.wei + that.wei)

    def - (that: TokenAmount) =
        assert(this.token == that.token)
        new TokenAmount(this.token, this.wei - that.wei)

    def * (x: Int) =
        new TokenAmount(this.token, this.wei * x)


object TokenAmount:
    def fromWei(token: Token, amount: BigInt) =
        new TokenAmount(token, amount)

    def fromEth(token: Token, amount: Double) =
        new TokenAmount(token, Convert.toWei(amount.toString, Convert.Unit.ETHER).toBigInteger)