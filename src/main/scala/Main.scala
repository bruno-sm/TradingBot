import concurrent.duration.DurationInt
import cats.effect.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.crypto.Credentials
import com.influxdb.client.scala.{InfluxDBClientScalaFactory, InfluxDBClientScala}
import com.influxdb.client.write.Point
import InfluxExt.*
import scala.concurrent.duration.FiniteDuration
import scala.io.Source
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level


object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] =
    val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger]
    rootLogger.setLevel(Level.ERROR)
    
    val influxUrl : String = sys.env.get("INFLUXDB_URL").getOrElse(throw new RuntimeException("Please set the influxdb url in the INFLUXDB_URL environment variable"))
    val influxToken = sys.env.get("INFLUXDB_TOKEN").getOrElse(throw new RuntimeException("Please set the influxdb token in the INFLUXDB_TOKEN environment variable"))
    val influxOrg = sys.env.get("INFLUXDB_ORG").getOrElse(throw new RuntimeException("Please set the influxdb organization in the INFLUXDB_ORG environment variable"))
    val influxBucket = sys.env.get("INFLUXDB_BUCKET").getOrElse(throw new RuntimeException("Please set the influxdb bucket in the INFLUXDB_BUCKET environment variable"))
    val influx = InfluxDBClientScalaFactory.create(influxUrl, influxToken.toCharArray, influxOrg, influxBucket)

    val providerUrl = Source.fromFile("/run/secrets/web3_provider_url").getLines().mkString
    val web3 = Web3j.build(new HttpService(providerUrl))

    val publicKey = Source.fromFile("/run/secrets/public_key").getLines().mkString
    val privateKey = Source.fromFile("/run/secrets/private_key").getLines().mkString
    val credentials = Credentials.create(privateKey, publicKey)
    val pancake = Pancake(web3, credentials)

    for {
      f_monitor <- monitor_price(pancake, influx, 1.minute).start
      f_operate <- operate(pancake, influx, 5.minutes).start
      _ <- f_monitor.join
    } yield ExitCode.Success


  def monitor_price(pancake: Pancake, influx: InfluxDBClientScala, cadence: FiniteDuration): IO[Unit] =
    pancake.pairPrice(Token.BNB, Token.BUSD).flatMap(
    (pancakePrice, pancakeLastChangeTS) =>
      influx.sotrePoint(Point.measurement("price").addTag("dex", "pancake").addTag("token", Token.BNB.ticket).addTag("base", Token.BUSD.ticket).addField("amount", pancakePrice.eth)) >>
      IO.println(s"BNB price: $pancakePrice")
    )
    .start >>
    IO.sleep(cadence) >>
    monitor_price(pancake, influx, cadence)