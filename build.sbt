import sbt.Keys._

enablePlugins(AutomateHeaderPlugin)
headerLicense := Some(HeaderLicense.ALv2("2017", "Juan David Millan-Cifuentes"))
organization in ThisBuild := "com.mentor.labs"
name in ThisBuild := "tangara"
version in ThisBuild := "0.1"
scalaVersion in ThisBuild := "2.12.2"


resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

val nlu = project
  .in(file("nlu"))
  .settings(name := "nlu")
  .settings(libraryDependencies ++= Dependencies.compile)
  .settings(libraryDependencies ++= Dependencies.integrationTest)

val `quiz-service` = project
  .in(file("quiz-service"))
  .settings(name := "quiz-service")
  .settings(libraryDependencies ++= Dependencies.compileAkka)
  .settings(libraryDependencies ++= Dependencies.testCompile)

val examples = project
  .in(file("examples"))
  .settings(name := "examples")
  .settings(libraryDependencies ++= Dependencies.compile)
  .settings(libraryDependencies ++= Dependencies.testCompile)

val root = project
  .in(file("."))
  .settings(name := "tangara")
  .aggregate(nlu, examples, `quiz-service`)

enablePlugins(JavaAppPackaging)
