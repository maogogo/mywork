import sbt._
import sbt.Keys._
import com.typesafe.sbt.packager.archetypes._
import com.twitter.scrooge.ScroogeSBT.autoImport._
import com.twitter.scrooge.ScroogeSBT._
import Package._

object Settings {

  lazy val basicSettings: Seq[Setting[_]] = Seq(
    organization := Globals.organization,
    version := Globals.version,
    scalaVersion := Globals.scalaVersion,
    autoScalaLibrary := false,
    //logLevel := Level.Warn,
    //crossScalaVersions  := Globals.crossScalaVersions,
    resolvers ++= Resolvers.repositories,
    javacOptions := Seq( //"-source", Globals.jvmVersion,
    //"-target", Globals.jvmVersion
    ),
    scalacOptions := Seq(
      "-encoding", "utf8",
      "-g:vars",
      "-unchecked",
      "-deprecation",
      "-Yresolve-term-conflict:package"),
    fork in Test := false,
    parallelExecution in Test := false,
    publishArtifact in (Compile, packageSrc) := false,
    publishArtifact in (Compile, packageDoc) := false,
    shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }) ++ resourceSettings

  lazy val thriftSettings = Seq(
    (scroogeThriftSourceFolder in Compile) := baseDirectory.value)

  lazy val noPublishing: Seq[Setting[_]] = Seq(publish := {}, publishLocal := {}, publishArtifact := false)

  lazy val resourceSettings: Seq[Setting[_]] = {
    val excludedResources = Seq("application.conf", "logback.xml")
    Seq(
      mappings in (Compile, packageBin) ~= {
        _.filterNot { case (file, _) => excludedResources.contains(file.getName) }
      },
      mappings in (Compile, packageBin) ~= {
        _.filterNot { case (file, _) => file.getParentFile.name == "conf" }
      })
  }

  def module(name: String, hasPackage: Boolean = true): Project = {
    val id = s"${Globals.name}-$name"

    hasPackage match {
      case true =>
        Project(id = id, base = file(name),
          settings = basicSettings ++ Seq(Keys.name := id) ++ mappingSettings)
          .enablePlugins(JavaServerAppPackaging)
      case _ =>
        Project(id = id, base = file(name),
          settings = basicSettings ++ Seq(Keys.name := id))
    }

  }

}
