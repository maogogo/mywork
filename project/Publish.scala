import sbt._
import sbt.Keys._

object Publish {

  lazy val settings: Seq[Setting[_]] = Seq(
    organizationName := Globals.organizationName,
    organizationHomepage := Globals.organizationHomepage,
    homepage := Globals.homepage,
    startYear := Globals.startYear,
    organizationHomepage := Globals.organizationHomepage,
    crossPaths := true,
    pomExtra := projectPomExtra,
    credentials ++= Globals.baseCredentials,
    pomIncludeRepository := { _ => false },
    publishMavenStyle := true,
    licenses += "BSD-Style" -> Globals.licenses,
    publishTo := ({
      if (isSnapshot.value)
        Some("snapshots" at Globals.snapshotRepo + "snapshots/")
      else
        Some("releases" at Globals.snapshotRepo + "releases/")
    }))

  def projectPomExtra = {
    <scm>
      <url>{ Globals.scmUrl }</url>
      <connection>{ Globals.scmConnection }</connection>
    </scm>
    <developers>
      <developer>
        { Globals.pomDevelopers }
      </developer>
    </developers>
  }

}