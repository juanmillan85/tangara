package com.mentor.labs.quizzes.service

import akka.actor.{Actor, ActorRef, Props}

class QueryBot(name: String, topic: Option[String], client: ActorRef) extends Actor {
  override def receive: Receive = {
    case a => println(a)
  }
}

object QueryBot {
  case object GetPendingQuizzes
  case object GetDoneQuizzes
  case object GetPublishQuizzes
  def props(name: String, topic: Option[String], client: ActorRef) =
    Props(new QueryBot(name, topic, client))
}
