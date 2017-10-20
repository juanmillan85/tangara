import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

case class User(name: String)
case class Hello(greeting: String)


import fs2.{Stream, Task}
// import fs2.{Stream, Task}

import org.http4s.server.blaze._
// import org.http4s.server.blaze._

import org.http4s.util.StreamApp
// import org.http4s.util.StreamApp

object Main extends StreamApp {
  val jsonService = HttpService {
    case req @ POST -> Root / "hello" =>
      for {
      // Decode a User request
        user <- req.as(jsonOf[User])
        // Encode a hello response
        resp <- Ok(Hello(user.name).asJson)
      } yield (resp)
  }

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(9000, "localhost")
      .mountService(jsonService, "/")
      .serve
  }
}