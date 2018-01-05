package com.mentor.labs.quizzes.service

import akka.testkit.TestProbe
import com.mentor.labs.quizzes.service.QuizBotManager._
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class QuizBotManagerTest extends BaseAkkaWordTest("quizBotTest") with MockFactory with InMemoryCleanup {

  import QuizBot._

  "A QuizBotManager" should {

    "generate a quiz based on a template" in {
      Given("a quiz bot manager")
      val quizBotManager = createTestBot("quizBotManager1")

      When("a quiz bot manager recives the command create Quiz")
      quizBotManager ! CreateQuiz("uid", 1L)

      Then("a quizBot is created")
      TestProbe().expectActor("/user/quizBotManager1/quizBot-uid*")

      And("the client should get QuizStarted")
      expectMsg(QuizStarted)
      system.stop(quizBotManager)
    }

    "generate a random quiz" in {
      val quizBotManager = createTestBot("quizBotManager1")
      quizBotManager ! CreateRandomQuiz("uid")
      TestProbe().expectActor("/user/quizBotManager1/quizBot-uid*")
      expectMsg(QuizStarted)
      system.stop(quizBotManager)
    }

    "resume a quiz " in {
      Given("a quiz bot manager")
      val quizBotManager = createTestBot("quizBotManager1")

      And("a random quizBot is started and stoped")
      quizBotManager ! CreateRandomQuiz("uid")
      expectMsg(QuizStarted)
      changedQuizBotStateAndStoped()

      When("a quiz bot manager recives the command create ResumeQuiz")
      quizBotManager ! ResumeQuiz("uid0L")

      Then("a quizBot is resumed")
      expectMsg(QuizResumed)
      system.stop(quizBotManager)
    }

    "create a query bot and request pending quizzes" in {
      Given("a quiz bot manager")
      val quizBotManager = createTestBot("quizBotManager1")

      When("a bot manager gets an user request of pending quizzes")
      quizBotManager ! RequestPendingQuizzes("uid", None)

      Then("a queryBot is created")
      TestProbe().expectActor("/user/quizBotManager1/queryBot-uid")
      system.stop(quizBotManager)
    }
  }

  private def changedQuizBotStateAndStoped() = {
    system.actorSelection("/user/quizBotManager1/quizBot-uid*")
      .resolveOne()
      .value match {
      case Some(Success(actorRef)) =>
        actorRef ! Cmd("Hello")
        expectMsg(Evt("Hello-0"))
        parent watch actorRef
        actorRef ! StopQuiz
        parent.expectTerminated(actorRef)
      case x => fail()
    }
  }

  private def createTestBot(nameBot: String) =
    system.actorOf(QuizBotManager.props(), nameBot)

  private val parent = TestProbe()
}
