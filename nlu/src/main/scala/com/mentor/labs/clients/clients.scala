package com.mentor.labs

import fs2.Task
import io.circe.{Decoder, Encoder}

package object clients {

  trait WebClient {
    def post[A, B](person: A)(resource: String)(implicit encoder: Encoder[A], decoder: Decoder[B]): Task[B]
  }

  class Http4SClient extends WebClient {


    import io.circe._
    import io.circe.syntax._
    import org.http4s.Uri
    import org.http4s.circe._
    import org.http4s.client._
    import org.http4s.client.blaze._
    import org.http4s.dsl._

    private val httpClient = PooledHttp1Client()

    def post[A, B](person: A)(url: String)(implicit encoder: Encoder[A], decoder: Decoder[B]): Task[B] = {
      val req = POST(Uri.fromString(url).right.get, person.asJson)
      httpClient.expect(req)(jsonOf[B])
    }

    def stop(): Unit =
      httpClient.shutdown.unsafeRunSync()
  }

  object http4SClient extends Http4SClient

}
