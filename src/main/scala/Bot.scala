import cats.effect.IO
import cats.effect.std.Random
import com.influxdb.client.scala.{InfluxDBClientScalaFactory, InfluxDBClientScala}
import InfluxExt.*
import scala.concurrent.duration.FiniteDuration



case class Transaction(from: Token, to: Token, fraction: Float)

type MarketState = Unit


def operate(pancake: Pancake, influx: InfluxDBClientScala, cadence: FiniteDuration): IO[Unit] =
    operateOnce(pancake, influx).start >>
    IO.sleep(cadence) >>
    operate(pancake, influx, cadence)


def operateOnce(pancake: Pancake, influx: InfluxDBClientScala, rank_threshold: Float=0.9): IO[Unit] =
    val marketState = ()
    for {
        _ <- IO.println("Bot doing financee stof :^|")
        transactions <- obtainTransactions(marketState)
        // _ <- logPredictions(influx, transactions).start
        // _ <- applyTransactions(transactions, pancake, influx)
    } yield ()


def obtainTransactions(marketState: MarketState) : IO[List[Transaction]] =
    for {
        // Por cada par de tokens un movimiento aleatorio
        rand <- Random.scalaUtilRandom[IO]
        fraction <- rand.nextFloat
        transaction <- IO(List(Transaction(Token.BUSD, Token.BNB, fraction*2-1)))
    } yield (transaction)