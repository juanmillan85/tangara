import sbt.Keys._

enablePlugins(AutomateHeaderPlugin)
headerLicense := Some(HeaderLicense.ALv2("2017", "Juan David Millan-Cifuentes"))
organization in ThisBuild := "com.mentor.labs"
name in ThisBuild := "tangara"
version in ThisBuild := "0.1"
scalaVersion in ThisBuild := "2.12.2"


resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

val compileSettings = Seq(libraryDependencies ++= Dependencies.compile)

val nlu = project
  .in(file("nlu"))
  .settings(name := "tangara")
  .settings(compileSettings)
  .settings(libraryDependencies ++= Dependencies.testCompile)

val examples = project
  .in(file("examples"))
  .settings(name := "tangara")
  .settings(compileSettings)
  .settings(libraryDependencies ++= Seq(Dependencies.ScalaTest))

val root = project
  .in(file("."))
  .settings(name := "tangara")
  .aggregate(nlu, examples)

enablePlugins(JavaAppPackaging)
