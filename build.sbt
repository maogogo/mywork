import sbt._
import Keys._
import Process._

import BuildSettings._
import Dependencies._

lazy val thrift = Project("my-thrift", file("thrift"))
	.settings(basicSettings: _*)
  .settings(thriftSettings: _*)
  .settings(libraryDependencies ++= thriftDependency)

lazy val common = Project("my-common", file("common"))
	.dependsOn(thrift)
	.settings(basicSettings: _*)
  .settings(libraryDependencies ++= commonDependency)
  .settings(libraryDependencies ++= mysqlDependency)
  .settings(libraryDependencies ++= serverDependency)
  .settings(libraryDependencies ++= redisDependency)
  .settings(libraryDependencies ++= mongoDependency)
  //.settings(libraryDependencies ++= kafkaDependency)

lazy val rest = Project("my-rest", file("rest"))
	.dependsOn(common)
  .settings(basicSettings: _*)
  .settings(libraryDependencies ++= finchDependency)
  .enablePlugins(JavaServerAppPackaging)

lazy val leaf = Project("my-leaf", file("leaf"))
	.dependsOn(common)
  .settings(basicSettings: _*)
  .enablePlugins(JavaServerAppPackaging)

lazy val merger = Project("my-merger", file("merger"))
	.dependsOn(common)
  .settings(basicSettings: _*)
  .enablePlugins(JavaServerAppPackaging)

lazy val meta = Project("my-meta", file("meta"))
	.dependsOn(common)
  .settings(basicSettings: _*)
  .enablePlugins(JavaServerAppPackaging)

lazy val root = Project("my-root", file("root"))
	.dependsOn(common)
  .settings(basicSettings: _*)
  .enablePlugins(JavaServerAppPackaging)

lazy val all = Project("mywork", file(".")) //(project in file("."))
  .aggregate(thrift, rest, common, root, meta, merger, leaf)
  //.settings(defaultScalariformSettings: _*)