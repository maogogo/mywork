import sbt._
import Keys._
import com.twitter.scrooge.ScroogeSBT
import com.twitter.scrooge.ScroogeSBT.autoImport._

object BuildSettings {
  
  lazy val basicSettings = Seq(
    scalaVersion := "2.11.8",
    version := "1.0.1-SNAPSHOT",
    organization := "com.maogogo",
    shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " },
    resolvers ++= Dependencies.repositories
  )

  lazy val thriftSettings = Seq(
    //(scroogeThriftSourceFolder in Compile) <<= baseDirectory { _ / "../rbac-thrift" }
    (scroogeThriftSourceFolder in Compile) := baseDirectory.value
  )

}