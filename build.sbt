import sbt.Keys._

enablePlugins(AutomateHeaderPlugin)
headerLicense := Some(HeaderLicense.ALv2("2017", "Juan David Millan-Cifuentes"))
organization in ThisBuild := "com.mentor.labs"
name in ThisBuild := "tangara"
version in ThisBuild := "0.1"
scalaVersion in ThisBuild := "2.12.2"

//lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
//
//lazy val buildSettings = Seq(
//  organization := "com.city-labs.juan",
//  name := "tangara",
//  version := "0.1",
//  scalaVersion := "2.12.3",
//  scalafmtVersion := "1.3.0",
//  scalafmtOnCompile in ThisBuild := true,
//  compileScalastyle := scalastyle.in(Compile).toTask("").value,
//  (compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value,
//  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
//)
//lazy val baseSettings = Seq(
//  scalacOptions ++= compilerOptions ++ Seq(
//    "-Ywarn-unused-import"
//  ),
//  testOptions in Test += Tests.Argument("-oF"),
//  scalacOptions in (Compile, console) := compilerOptions,
//  scalacOptions in (Compile, test) := compilerOptions,
//  resolvers ++= Seq(
//    Resolver.sonatypeRepo("releases"),
//    Resolver.sonatypeRepo("snapshots")
//  ),
//  mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE"
//)
//
////scalacOptions ++= scalafixScalacOptions.value
//
//buildSettings ++ baseSettings

//lazy val compilerOptions = Seq(
//  "-deprecation",
//  "-encoding",
//  "UTF-8",
//  "-feature",
//  "-language:existentials",
//  "-language:higherKinds",
//  "-unchecked",
//  "-Xfatal-warnings",
//  "-Yno-adapted-args",
//  "-Ywarn-dead-code",
//  "-Ywarn-numeric-widen",
//  "-Xfuture"
//)
// Only necessary for SNAPSHOT releases

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


//  .settings(libraryDependencies += Dependencies.ScalaTest)
//  .settings(sharedPublishSettings: _*)
//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

enablePlugins(JavaAppPackaging)
