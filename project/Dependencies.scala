import sbt._

object Dependencies {

  object Versions {
    val AkkaHttp = "10.0.10"
    val Akka = "2.5.6"
    val Circe = "0.8.0"
    val Cats = "0.9.0"
    val PureConfig = "0.7.2"
    val ScalaLogging = "3.5.0"
    val AkkaHttpCirce = "1.12.0"
    val LogBack = "1.2.3"
    val ScalaTest = "3.0.4"
    val Http4S = "0.17.5"
    val WiremockVersion = "2.8.0"
    val TypesafeConfig = "1.3.1"
    val ScalaMockVersion = "3.5.0"
  }

  val AkkaHttp: ModuleID = "com.typesafe.akka" %% "akka-http" % Versions.AkkaHttp
  lazy val AkkaStream: ModuleID = "com.typesafe.akka" %% "akka-stream" % Versions.Akka
  lazy val AkkaTestkit: ModuleID = "com.typesafe.akka" %% "akka-testkit" % Versions.Akka
  lazy val AkkaActor: ModuleID = "com.typesafe.akka" %% "akka-actor" % Versions.Akka

  val Cats: ModuleID = "org.typelevel" %% "cats" % Versions.Cats

  val Circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-generic-extras",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-shapes",
    "io.circe" %% "circe-literal"
  ).map(_ % Versions.Circe)

  val PureConfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % Versions.PureConfig
  val AkkaHttpCirce: ModuleID = "de.heikoseeberger" %% "akka-http-circe" % Versions.AkkaHttpCirce
  private val ScalaTestBase = "org.scalatest" %% "scalatest" % Versions.ScalaTest
  val ScalaTest: ModuleID = ScalaTestBase % "test"
  val ScalaTestIt: ModuleID = ScalaTestBase % "it"
  val ScalaLogging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Versions.ScalaLogging
  val Logback: ModuleID = "ch.qos.logback" % "logback-classic" % Versions.LogBack

  val HTTP4S: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-client",
    "org.http4s" %% "http4s-circe").map(_ % Versions.Http4S)

  val TypesafeConfig: ModuleID = "com.typesafe" % "config" % Versions.TypesafeConfig
  val ScalaMock: ModuleID = "org.scalamock" %% "scalamock-scalatest-support" % Versions.ScalaMockVersion % Test
  val WirewMock: ModuleID = "com.github.tomakehurst" % "wiremock" % Versions.WiremockVersion % Test

  val compile: Seq[ModuleID] = Seq(TypesafeConfig, Logback) ++ HTTP4S ++ Circe

  val compileAkka: Seq[ModuleID] = Seq(AkkaHttp, AkkaStream, AkkaTestkit, AkkaActor, AkkaHttpCirce, TypesafeConfig, Logback) ++ HTTP4S ++ Circe

  val testCompile: Seq[ModuleID] = Seq(ScalaTest, ScalaMock)
  val integrationTest: Seq[ModuleID] = testCompile ++ Seq(WirewMock)
}