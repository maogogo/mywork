import sbt._
import sbt.Keys._
import Process._
import Settings._
import Publish.{ settings => publishSettings }
import Dependencies._

//for interface
lazy val thrift = module("thrift", false)
  .settings(thriftSettings: _*)
  .settings(publishSettings: _*)
  .settings(libraryDependencies ++= mcompile(thriftDependency: _*))

// for java client
lazy val client = module("client", false)
  .dependsOn(thrift % "test->test;compile->compile")
  .settings(publishSettings: _*)
  .settings(
    libraryDependencies ++= mcompile(commonDependency ++ serverDependency: _*)
  )

//commons
lazy val common = module("common", false)
  .dependsOn(thrift % "test->test;compile->compile")
  .settings(
    libraryDependencies ++=
      mcompile(commonDependency ++ injectDependency ++ serverDependency ++ 
        mysqlDependency ++ redisDependency ++ mongoDependency ++ jdbcDependency: _*)
  )

lazy val rest = module("rest")
  .dependsOn(common % "test->test;compile->compile")
  .settings(
    libraryDependencies ++=
      mcompile(finchDependency: _*) ++
      mtest(testDependency: _*)
  )

lazy val merger = module("merger")
  .dependsOn(common % "test->test;compile->compile")
  .settings(libraryDependencies ++= mtest(testDependency: _*))

lazy val leaf = module("leaf")
  .dependsOn(common % "test->test;compile->compile")
  .settings(libraryDependencies ++= mtest(testDependency: _*) 
  )

lazy val meta = module("meta")
  .dependsOn(common % "test->test;compile->compile")
  .settings(libraryDependencies ++= mtest(testDependency: _*))

lazy val root = module("root")
  .dependsOn(common % "test->test;compile->compile")
  .settings(libraryDependencies ++= mtest(testDependency: _*))


lazy val backend = module("backend")
  .dependsOn(common % "test->test;compile->compile")
  .settings(
    libraryDependencies ++=
      mcompile(finchDependency: _*) ++
      mtest(testDependency: _*)
  )

lazy val all = Project(id = Globals.name, base = file(".")) //(project in file("."))
  .aggregate(thrift, backend, client, common, rest, root, meta, merger, leaf)
  //.settings(defaultScalariformSettings: _*)
