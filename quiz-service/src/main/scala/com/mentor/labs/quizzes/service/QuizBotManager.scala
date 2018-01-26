package com.mentor.labs.quizzes.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import com.mentor.labs.quizzes.service.QuizBotManager._
import com.mentor.labs.quizzes.service.QueryBot._

case class Cmd(data: String)

case class Evt(data: String)

case class ExampleState(events: List[String] = List.empty) {
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)

  def size: Int = events.length

  override def toString: String = events.reverse.toString
}

class QuizBotManager extends Actor {
  override def receive: Receive = {
    case CreateQuiz(uid: String, tempalteQuizId: Long) =>
      createQuizBot(s"$uid$tempalteQuizId", sender())

    case CreateRandomQuiz(uid) =>
      createQuizBot(s"${uid}0L", sender())

    case ResumeQuiz(quizId) =>
      createQuizBot(quizId, sender())

    case RequestPendingQuizzes(uid, topics) =>
      createQueryBot(uid, topics, sender()) ! GetPendingQuizzes

    case RequestDoneQuizzes(uid, topics) =>
      createQueryBot(uid, topics, sender()) ! GetDoneQuizzes

    case RequestPublishedQuizzes(uid, topics) =>
      createQueryBot(uid, topics, sender()) ! GetPublishQuizzes
  }

  private val queryBotPrefix = "queryBot"
  private val quizBotPrefix = "quizBot"

  private def createQuizBot(quizBotId: String, replyTo: ActorRef) =
    context.actorOf(QuizBot.props(name = s"$quizBotId", client = replyTo), s"$quizBotPrefix-$quizBotId")


  private def createQueryBot(botId: String, topic: Option[String], replyTo: ActorRef) =
    context.actorOf(QueryBot.props(name = s"$botId", topic = topic, client = replyTo), s"$queryBotPrefix-$botId")
}

case object Load

object QuizBotManager {

  case class CreateQuiz(uid: String, tempalteQuizid: Long)

  case class CreateRandomQuiz(uid: String)

  case class ResumeQuiz(quizId: String)

  case class CancelQuiz(quizId: String)

  object RequestNextQuestion

  case class RequestTotalScore(uid: String)


  case class SubmitAnswer(answers: List[String])

  case class RequestPendingQuizzes(uid: String, topic: Option[String])

  case class RequestDoneQuizzes(uid: String, topic: Option[String])

  case class RequestPublishedQuizzes(uid: String, topic: Option[String])


  object QuizFinished

  def props(): Props = Props(new QuizBotManager())
}
