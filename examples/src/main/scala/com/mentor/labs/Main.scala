package com.mentor.labs;
import fs2.{Stream, Task}
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.util.StreamApp

object Main extends StreamApp {

  private[this] val logger = org.log4s.getLogger

  val jsonService = HttpService {
    case req@POST -> Root / "hello" =>
      for {
      // Decode a User request
        user <- req.as(jsonOf[User])
        // Encode a hello response
        resp <- Ok(Hello(user.name).asJson)
      } yield (resp)
    case GET -> Root =>
      Ok("Your webapp tangara is ready!")
  }

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    val port = args.headOption.map(_.toInt).getOrElse(8080)

    logger.info(s"Starting service on port $port")

    BlazeBuilder
      .bindHttp(port, "0.0.0.0")
      .mountService(jsonService, "/")
      .serve
  }
}
