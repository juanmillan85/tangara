package com.mentor.labs.nlu.engine

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.mentor.labs.clients._
import com.mentor.labs.nlu.engine.rasa.{Entity, Intent, Message, RasaCoreAnswer, Slot, Tracker}
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import shapeless.HNil

import scala.concurrent.duration._
import scala.language.postfixOps

class NLUEngineTest extends BaseWordTest with ScalaFutures {
  private val OK_STATUS = 200
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

  "The nlu engine" should {
    import com.mentor.labs.nlu.engine.rasa._
    import NLUEngineSyntax._

    "return a valid answer when queried using rasa nlu" in {
      Given("rasa nlu query")
      val (json, requestBody, query, expectedAnswer) = (RASANLU_ANSWER,
        """"q":"Hello""", RasaNluQuery("Hello"),
        RasaNluAnswer(
          text = "show me chinese restaurants",
          intent = "restaurant_search",
          Vector(Entity(
            start = 8,
            end = 15,
            value = "chinese",
            entity = "cuisine"))
        ))

      And("mock nlu engine")
      stubFor(post(urlMatching("/parse"))
        .withRequestBody(containing(requestBody))
        .willReturn(aResponse().withStatus(OK_STATUS).withBody(json)))

      And("web client")
      val webClient = new Http4SClient()

      When("parse rasa nlu query")
      val answer = query.parse.run(webClient)

      Then("the answer should be correctly serialized")
      answer.unsafeRun() shouldBe expectedAnswer
      webClient.stop()
    }

    "return a valid answer when queried using rasa core" in {
      Given("rasa rasa core query")
      val (json, requestBody, expectedQuery, expectedAnswer) = (RASACORE_ANSWER,
        """"query":"where can I find a concert""",
        RasaCoreQuery(query = "where can I find a concert"),
        EXPECTED_RASA_CORE_ANSWER)

      And("mock rasa core nlu engine")
      stubFor(
        post(urlMatching("/conversations/default/parse"))
          .withRequestBody(containing(requestBody))
          .willReturn(aResponse().withStatus(OK_STATUS).withBody(json)))

      And("web client")
      val webClient = new Http4SClient()

      When("parse query")
      val answer = expectedQuery.parse.run(webClient)

      Then("the answer should be correctly serialized")
      answer.unsafeRun() shouldBe expectedAnswer
      webClient.stop()
    }

    "return a valid answer when updating the state of rasa core" in {
      Given("rasa rasa core query")
      val (json, requestBody, expectedQuery, expectedAnswer) =
        (RASACORE_ANSWER, RASA_CONTINUE,
          RasaCoreContinue(
            executedAction = "search_restaurants",
            events = Vector(
              EventString(event = "slot", name = "cuisine", value = "mexican"),
              EventString(event = "slot", name = "people", value = "5"))
          ), EXPECTED_RASA_CORE_ANSWER)

      And("mock rasa core nlu engine")
      stubFor(
        post(urlMatching("/conversations/default/continue"))
          .withRequestBody(containing(requestBody))
          .willReturn(aResponse().withStatus(OK_STATUS).withBody(json)))

      And("web client")
      val webClient = new Http4SClient()

      When("parse query")
      val answer = expectedQuery.parse.run(webClient)

      Then("the answer should be correctly serialized")
      answer.unsafeRun() shouldBe expectedAnswer
      webClient.stop()
    }

  }

  private val RASANLU_ANSWER: String =
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

  private val RASACORE_ANSWER: String =
    """
      |{
      |	"next_action": "action_search_concerts",
      |	"tracker": {
      |		"latest_message": {
      |			"entities": [],
      |			"intent_ranking": [{
      |				"confidence": 0.343798481818302,
      |				"name": "search_concerts"
      |			}, {
      |				"confidence": 0.1890310272039713,
      |				"name": "goodbye"
      |			}, {
      |				"confidence": 0.1646903865980037,
      |				"name": "search_venues"
      |			}, {
      |				"confidence": 0.13945144969564696,
      |				"name": "greet"
      |			}, {
      |				"confidence": 0.1086858547565994,
      |				"name": "compare_reviews"
      |			}, {
      |				"confidence": 0.05434279992747628,
      |				"name": "thankyou"
      |			}],
      |			"text": "where can I find a concert",
      |			"intent": {
      |				"confidence": 0.343798481818302,
      |				"name": "search_concerts"
      |			}
      |		},
      |		"slots": {
      |			"concerts": "",
      |			"venues": ""
      |		},
      |		"sender_id": "default"
      |	}
      |}
    """.stripMargin

  private val RASA_CONTINUE: String =
    """{"id":"default","executed_action":"search_restaurants",
      |"events":[{"event":"slot","name":"cuisine","value":"mexican"},
      |{"event":"slot","name":"people","value":"5"}]}""".stripMargin
      .replaceAll("\n", "")

  private val EXPECTED_RASA_CORE_ANSWER = RasaCoreAnswer(Tracker(
    latestMessage = Message(
      entities = List.empty[Entity],
      intentRanking = List(
        Intent(confidence = 0.343798481818302, name = "search_concerts"),
        Intent(confidence = 0.1890310272039713, name = "goodbye"),
        Intent(confidence = 0.1646903865980037, name = "search_venues"),
        Intent(confidence = 0.13945144969564696, name = "greet"),
        Intent(confidence = 0.1086858547565994, name = "compare_reviews"),
        Intent(confidence = 0.05434279992747628, name = "thankyou")
      ),
      intent = Intent(confidence = 0.343798481818302, name = "search_concerts"),
      text = "where can I find a concert"
    ),
    slots = Slot("", ""),
    senderId = "default"),
    nextAction = "action_search_concerts")
}