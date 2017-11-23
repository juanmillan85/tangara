package com.mentor.labs.quizzes.service

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import com.mentor.labs.quizzes.domain.{Feedback, Question, QuizInfo}

trait QuizService {
  def getPendingQuizzes(userId: String): Seq[QuizInfo]

  def getDescription(id: Long): String

  def askQuestion(uid: Long, id: String): Question

  def submitAnswer(uid: Long, id: String*): Feedback
}

class QuizBot extends Actor {
  override def receive: Receive = ???
}


object QuizBot {

  case class StartQuiz(tempalteQuizid: Long)

  case class ResumeQuiz(quizId: Long)

  object FinishQuiz

  object RequestNextQuestion

  object RequestScore

  case class SubmitAnswer(answers: List[String])

  case class RequestPendingQuizzes(topic: Option[String])

  case class RequestDoneQuizzes(topic: Option[String])

  case class RequestPublishedQuizzes(topic: Option[String])


  object QuizStarted

  object QuizResumed

  object QuizFinished

  def props(): Props = Props(new QuizBot())
}
