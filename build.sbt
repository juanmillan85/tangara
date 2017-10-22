enablePlugins(AutomateHeaderPlugin)
headerLicense := Some(HeaderLicense.ALv2("2017", "Juan David Millan-Cifuentes"))
organization := "com.mentor.labs"
name := "tangara"
version := "0.1"
scalaVersion := "2.12.3"

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

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

val Http4sVersion  = "0.17.5"
val LogbackVersion = "1.2.3"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"     %% "http4s-circe"        % Http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
  "ch.qos.logback" % "logback-classic"      % LogbackVersion,
  "io.circe"       %% "circe-generic"       % "0.8.0",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.8.0"
)

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

enablePlugins(JavaAppPackaging)
