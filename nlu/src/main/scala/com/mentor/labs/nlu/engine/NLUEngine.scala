package com.mentor.labs.nlu.engine


import fs2.Task

import scala.language.higherKinds
import cats.data.Reader
import com.mentor.labs.clients._
import com.mentor.labs.nlu.engine.NLUEngine.Query
import io.circe._

trait NLUEngine[A <: Query] {
  type B

  def encoder: Encoder[A]

  def decoder: Decoder[B]

  def resourceName(id: String): String

  def parse(query: A): Reader[WebClient, Task[B]] = Reader {
    (webClient: WebClient) => webClient.post(query)(resourceName(query.id))(encoder, decoder)
  }
}

object NLUEngine {

  trait Query {
    def id: String = ""
  }

}

object NLUEngineSyntax {

  implicit class NLUEngineOps[A <: Query](query: A) {
    def parse(implicit nluEngine: NLUEngine[A]): Reader[WebClient, Task[nluEngine.B]] =
      nluEngine.parse(query)
  }

}