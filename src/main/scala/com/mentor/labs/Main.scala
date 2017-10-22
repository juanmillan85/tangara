/*
 * Copyright 2017 Juan David Millan-Cifuentes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mentor.labs

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
