package com.mentor.labs.quizzes.service

import akka.testkit.TestProbe
import org.scalamock.scalatest.MockFactory

import scala.language.postfixOps

class QuizBotManagerTest extends BaseAkkaWordTest("quizBotTest") with MockFactory {


  "A QuizBotManager" should {

    "generate a random quiz bot" in {
      true
      //      val graphActor = createGraphActor("graphActor1")
      //      graphActor ! createGraphServiceCmd
      //      commandServiceProbe.expectMsg(createGraph)
      //      commandServiceProbe.reply(graphQueryResult)
      //
      //      expectMsg(graphQueryResult)
      //      system.stop(graphActor)
    }

    "retrieve expandNode data calling graph service when is not previously expanded" in {
      true
      //      val graphActor = createGraphActor("graphActor2")
      //      graphActor ! createGraphServiceCmd
      //      commandServiceProbe.expectMsg(createGraph)
      //      commandServiceProbe.reply(graphQueryResultEmpty)
      //      expectMsg(graphQueryResultEmpty)
      //      graphActor ! expandNodeServiceCmd
      //      commandServiceProbe.expectMsg(expandNode)
      //      commandServiceProbe.reply(graphQueryResultEmpty)
      //      expectMsg(graphQueryResultEmpty)
      //
      //      graphActor ! expandNode2ServiceCmd
      //
      //      commandServiceProbe.expectMsg(expandNode2)
      //      commandServiceProbe.reply(graphQueryResultEmpty)
      //      expectMsg(graphQueryResultEmpty)
      //      system.stop(graphActor)
    }
  }
  private val parent = TestProbe()
}
