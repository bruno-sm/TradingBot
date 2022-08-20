package InfluxExt

import cats.effect.*
import akka.stream.scaladsl.{Keep, Source}
import akka.actor.ActorSystem
import com.influxdb.client.scala.InfluxDBClientScala
import com.influxdb.client.write.Point
import java.time.Instant
import com.influxdb.client.domain.WritePrecision


implicit val system: ActorSystem = ActorSystem("ArbitrageBot")

extension (client: InfluxDBClientScala)
    def sotrePoint(p: Point): IO[Unit] = IO(Source.single(p.time(Instant.now, WritePrecision.NS)).toMat(client.getWriteScalaApi.writePoint())(Keep.right).run)