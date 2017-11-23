package com.mentor.labs.quizzes.service

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.testkit._
import akka.util.Timeout
import org.scalatest._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.language.postfixOps

trait BaseWordTest
  extends WordSpecLike
    with Matchers
    with GivenWhenThen
    with BeforeAndAfter
    with BeforeAndAfterAll


abstract class BaseAkkaWordTest(systemName: String) extends TestKit(ActorSystem(systemName))
  with ImplicitSender with BaseWordTest {

  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  implicit class TestProbeOps(probe: TestProbe) {

    def expectActor(path: String, max: FiniteDuration = 3.seconds): ActorRef = {
      probe.within(max) {
        var actor = null: ActorRef
        probe.awaitAssert {
          (probe.system actorSelection path).tell(Identify(path), probe.ref)
          probe.expectMsgPF(100 milliseconds) {
            case ActorIdentity(`path`, Some(ref)) => actor = ref
          }
        }
        actor
      }
    }
  }
}