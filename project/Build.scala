import sbt.Keys._
import sbt.plugins.JvmPlugin
import sbt.{AutoPlugin, Resolver, _}

object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectSettings = Seq(
    scalaVersion := "2.11.8",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // yes, this is 2 args
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Xfatal-warnings"
    ),
    resolvers ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
       Resolver.sonatypeRepo("releases"),
       Resolver.sonatypeRepo("snapshots")),
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "joda-time" % "joda-time" % "2.9.6",
      "org.joda" % "joda-convert" % "1.8.1",
      "com.google.inject" % "guice" % "4.1.0"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
}
