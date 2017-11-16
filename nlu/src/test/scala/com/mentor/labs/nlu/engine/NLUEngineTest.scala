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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.duration._
import scala.language.postfixOps

trait BaseWordSpec
  extends WordSpecLike
    with Matchers
    with GivenWhenThen
    with BeforeAndAfter
    with BeforeAndAfterAll

class NLUEngineTest extends BaseWordSpec with ScalaFutures {
  private implicit val timeout = Timeout(10 seconds)
  private implicit val defaultPatience = PatienceConfig(timeout = Span(20, Seconds), interval = Span(200, Millis))

  private val setting = ConfigFactory.load()
  private val Port = setting.getInt("nlu.port")
  private val Host = setting.getString("nlu.host")
  private val wireMockServer = new WireMockServer(wireMockConfig().port(Port))
  before {
    wireMockServer.resetAll()
  }

  override protected def beforeAll(): Unit = {
    wireMockServer.start()
    WireMock.configureFor(Host, Port)
  }

  override def afterAll(): Unit = {
    wireMockServer.stop()
  }

  val OK_STATUS = 200
  "The nlu engine" should {

    "return a valid answer when queried" in {
      Seq(
        (RASANLU_QUERY_RESPONSE, RASANLU.Query("Hello"),
          RASANLU.Answer("show me chinese restaurants", "restaurant_search"))
      ).foreach {
        case (json, query, expectedAnswer) =>

          Given("An query")
          val webClient = Clients.Http4SClient

          And("a nlu engine mock web service client")

          stubFor(
            post(urlMatching("/"))
              //              .withHeader("Content-type", containing("application/x-www-form-urlencoded"))
              //              .withHeader("Authorization", containing("""OAuth oauth_consumer_key="AAXXX", oauth_signature_method="HMAC-SHA1","""))
              .withRequestBody(containing(""""q":"Hello""""))
              .willReturn(aResponse()
                .withStatus(OK_STATUS)
                .withBody(json)))
          import RASANLU._
          //          import RASACORENLU._
          import NLUEngineSyntax._
          When("parse query")
          val answer = query.parse.run(webClient)
          answer.unsafeRun() shouldBe expectedAnswer
          webClient.stop().unsafeRunSync()
      }
    }

  }

  val RASANLU_QUERY_RESPONSE: String =
    """
      |{
      |    "text": "show me chinese restaurants",
      |    "intent": "restaurant_search",
      |    "entities": [
      |    {
      |      "start": 8,
      |      "end": 15,
      |      "value": "chinese",
      |      "entity": "cuisine"
      |    }
      |    ]
      |  }
    """.stripMargin

  val RASACORE_ANSWER: String =
    """
      |{
      |    "tracker": {
      |        "latest_message": {
      |            "intent": {
      |                "name": "inform",
      |                "confidence": 0.9942229966089088
      |            },
      |            "entities": [
      |                {
      |                    "start": 14,
      |                    "entity": "cuisine",
      |                    "end": 21,
      |                    "extractor": "ner_crf",
      |                    "value": "Mexican"
      |                }
      |            ],
      |            "intent_ranking": [
      |                {
      |                    "name": "inform",
      |                    "confidence": 0.9942229966089088
      |                },
      |                {
      |                    "name": "affirm",
      |                    "confidence": 0.0029706890932477193
      |                },
      |                {
      |                    "name": "thankyou",
      |                    "confidence": 0.00108016836014455
      |                },
      |                {
      |                    "name": "deny",
      |                    "confidence": 0.0008439833590144821
      |                },
      |                {
      |                    "name": "greet",
      |                    "confidence": 0.0006312090289424468
      |                },
      |                {
      |                    "name": "request_info",
      |                    "confidence": 0.00025095354974214985
      |                }
      |            ],
      |            "text": "looking for a Mexican restaurant"
      |        },
      |        "sender_id": "default",
      |        "slots": {
      |            "info": null,
      |            "cuisine": "Mexican",
      |            "matches": null,
      |            "location": null,
      |            "people": null,
      |            "price": null
      |        }
      |    },
      |    "next_action": "utter_ask_location"
      |}
    """.stripMargin
}