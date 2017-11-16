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

package com.mentor.labs.nlu.engine


import fs2.Task

import scala.language.higherKinds
import cats.data.Reader
import com.mentor.labs.nlu.engine.Clients.WebClient
import com.mentor.labs.nlu.engine.NLUEngine.{Answer, Query, UserID}
import com.typesafe.config.ConfigFactory
import io.circe.{Decoder, Encoder}


trait NLUEngine[A, B] {

  import io.circe._

  def encoder: Encoder[A]

  def decoder: Decoder[B]

  def parse(query: A): Reader[WebClient, Task[B]] = Reader {
    (webClient: WebClient) => webClient.get(query)(encoder, decoder)
  }
}

object NLUEngine {
  type UserID = String

  sealed trait Query

  sealed trait Answer

}

object NLUEngineSyntax {

  implicit class NLUEngineOps[A <: Query, B <: Answer](query: A) {
    def parse(implicit nluEngine: NLUEngine[A, B]): Reader[WebClient, Task[B]] =
      nluEngine.parse(query)
  }

}

object Clients {


  trait WebClient {
    def get[A, B](person: A)(implicit encoder: Encoder[A], decoder: Decoder[B]): Task[B]
  }

  object Http4SClient extends WebClient {


    import io.circe._
    import io.circe.syntax._

    import org.http4s.Uri
    import org.http4s.circe._
    import org.http4s.dsl._
    import org.http4s.client._
    import org.http4s.client.blaze._

    private val setting = ConfigFactory.load()
    private val Port = setting.getInt("nlu.port")
    private val Host = setting.getString("nlu.host")
    val URL: String = s"http://$Host:$Port"
    val httpClient = PooledHttp1Client()

    def get[A, B](person: A)(implicit encoder: Encoder[A], decoder: Decoder[B]): Task[B] = {
      val req = POST(Uri.fromString(URL).right.get, person.asJson)
      httpClient.expect(req)(jsonOf[B])
    }

    def stop(): Task[Unit] = {
      httpClient.shutdown
    }
  }

}

object RASANLU {

  case class Query(q: String) extends NLUEngine.Query

  case class Answer(text: String, intent: String) extends NLUEngine.Answer

  import io.circe._, io.circe.generic.semiauto._

  implicit val rasaNLUEngine = new NLUEngine[Query, Answer] {
    def encoder: Encoder[Query] = deriveEncoder[Query]

    def decoder: Decoder[Answer] = deriveDecoder[Answer]
  }


}

object RASACORENLU {

  case class Query(id: UserID = "default", query: String)

  case class Answer(id: UserID = "default", text: String)

  import io.circe._, io.circe.generic.semiauto._

  implicit val rasCoreEngine = new NLUEngine[Query, Answer] {
    def encoder: Encoder[Query] = deriveEncoder[Query]

    def decoder: Decoder[Answer] = deriveDecoder[Answer]
  }
}
