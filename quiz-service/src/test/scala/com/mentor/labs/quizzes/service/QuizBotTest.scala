package com.mentor.labs.quizzes.service

import akka.testkit.TestProbe
import org.scalamock.scalatest.MockFactory

class QuizBotTest extends BaseAkkaWordTest("quizBotTest") with MockFactory with InMemoryCleanup {
  "A QuizBot" should {

    "ask a question after loaded" in {
      Given("a quiz bot manager")
      val quizBotManager = createTestBot("quizBot1")

      When("a quiz bot manager recives the command create Quiz")

      system.stop(quizBotManager)
    }
  }

  private def createTestBot(nameBot: String) =
    system.actorOf(QuizBot.props(nameBot, parent.ref), nameBot)

  private val parent = TestProbe()

}
