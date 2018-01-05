package com.mentor.labs.nlu.engine

import com.mentor.labs.nlu.engine.NLUEngine.Query
import com.typesafe.config.ConfigFactory
import io.circe.{ Decoder, Encoder }
import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._

package object rasa {
  implicit val config: Configuration = Configuration.default.withSnakeCaseKeys.withDefaults
  private val setting                = ConfigFactory.load()

  case class RasaNluQuery(q: String) extends Query

  case class RasaNluAnswer(text: String, intent: String, entities: Seq[Entity])

  case class Entity(start: Int, end: Int, value: String, entity: String)

  implicit val entity: Decoder[Entity] = deriveDecoder[Entity]

  implicit val rasaNLUEngine = new NLUEngine[RasaNluQuery] {
    type B = RasaNluAnswer

    private val Port = setting.getInt("nlu.port")
    private val Host = setting.getString("nlu.host")
    val URL: String  = s"http://$Host:$Port"

    def resourceName(id: String) = s"$URL/parse"

    def encoder: Encoder[RasaNluQuery] = deriveEncoder[RasaNluQuery]

    def decoder: Decoder[RasaNluAnswer] = deriveDecoder[RasaNluAnswer]
  }

  case class RasaCoreQuery(override val id: String = "default", query: String) extends Query

  case class RasaCoreAnswer(tracker: Tracker, nextAction: String)

  case class Tracker(latestMessage: Message, slots: Slot, senderId: String)

  case class Slot(concerts: String, venues: String)

  case class Message(entities: List[Entity], intentRanking: List[Intent], text: String, intent: Intent)

  case class Intent(confidence: Double, name: String)

  implicit val slot: Decoder[Slot]       = deriveDecoder[Slot]
  implicit val intent: Decoder[Intent]   = deriveDecoder[Intent]
  implicit val message: Decoder[Message] = deriveDecoder[Message]
  implicit val tracker: Decoder[Tracker] = deriveDecoder[Tracker]

  implicit val rasaCoreParse = new NLUEngine[RasaCoreQuery] {
    type B = RasaCoreAnswer

    private val Port = setting.getInt("rasa_core.port")
    private val Host = setting.getString("rasa_core.host")
    val URL: String  = s"http://$Host:$Port"

    def resourceName(id: String) = s"$URL/conversations/$id/parse"

    def encoder: Encoder[RasaCoreQuery] = deriveEncoder[RasaCoreQuery]

    def decoder: Decoder[RasaCoreAnswer] = deriveDecoder[RasaCoreAnswer]
  }

  abstract class RasaCoreEvent

  case class EventString(event: String, name: String, value: String) extends RasaCoreEvent

  case class EventInt(event: String, name: String, value: Int) extends RasaCoreEvent

  implicit val eventStringValue: Encoder[EventString] = deriveEncoder[EventString]

  case class RasaCoreContinue(override val id: String = "default", executedAction: String, events: Seq[EventString])
      extends Query

  implicit val rasaCoreEvents = new NLUEngine[RasaCoreContinue] {
    type B = RasaCoreAnswer
    private val Port = setting.getInt("rasa_core.port")
    private val Host = setting.getString("rasa_core.host")
    val URL: String  = s"http://$Host:$Port"

    def resourceName(id: String) = s"$URL/conversations/$id/continue"

    def encoder: Encoder[RasaCoreContinue] = deriveEncoder[RasaCoreContinue]

    def decoder: Decoder[RasaCoreAnswer] = deriveDecoder[RasaCoreAnswer]
  }

}
