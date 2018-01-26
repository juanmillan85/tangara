package com.mentor.labs.quizzes.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.persistence._

class QuizBot(quizBotId: String, client: ActorRef) extends Actor with PersistentActor with ActorLogging {

  import QuizBot._

  self ! Load
  var state = ExampleState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  def numEvents =
    state.size

  override def receiveRecover: Receive = {
    case evt: Evt =>
      updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) => {
      log.info(s"State:$state")
      log.info(s"State Dif:$snapshot")
      state = snapshot
    }

    case RecoveryCompleted => {
      log.info(s"${self.path} Notification recovery complete")
    }
  }

  val snapShotInterval = 1000

  override def receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}")) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
        //   saveSnapshot(state)
        log.info(s"$state")
        client ! event
      }
    case Load =>
      if (state.events.isEmpty) client ! QuizStarted
      else client ! QuizResumed

    case SaveSnapshotSuccess(metadata) => {
      log.info(s"${self.path} Notification save snapshot success")
    }
    case SaveSnapshotFailure(metadata, reason) => {
      log.info(s"${self.path} Notification save snapshot failure")
    }
    case StopQuiz =>
      context.stop(self)
      client ! QuizStoped
  }

  override def persistenceId: String = quizBotId
}

object QuizBot {

  case object QuizStarted

  case object QuizResumed

  case object QuizStoped

  case object StopQuiz

  case object RequestNextQuestion

  def props(name: String, client: ActorRef): Props = Props(new QuizBot(name, client))
}
