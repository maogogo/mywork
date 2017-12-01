import sbt._

object Dependencies {

  object Versions {

    val scalatest = "3.0.4"
    val junit = "4.12"

    val slf4j = "1.7.25"
    val logback = "1.2.3"
    val config = "1.3.2"
    val guava = "23.0"
    val lang3 = "3.7"
    val json4s = "3.5.3"
    val scalaIO = "0.4.3-1"

    val twitter = "17.11.0"
    val finagle = "17.11.0"
    val guice = "4.1.0"
    val jsr = "3.0.2"
    val inject = "17.11.0"

    val finch = "0.15.1"

    val thrift = "0.9.3"
    val scrooge = "17.11.0"

    val solr = "0.0.16"
    val redis = "2.9.0"
    val mongo = "3.1.1"
    val csv = "1.3.5"
    val jsch = "0.1.54"
    val kafka = "0.10.1.2"

    val bonecp = "0.8.0.RELEASE"

  }

  val testDependency = Seq(
    "org.scalactic" %% "scalactic" % Versions.scalatest,
    "org.scalatest" %% "scalatest" % Versions.scalatest,
    "junit" % "junit" % Versions.junit)

  val commonDependency = Seq(
    "org.slf4j" % "slf4j-api" % Versions.slf4j,
    "org.slf4j" % "log4j-over-slf4j" % Versions.slf4j,
    "org.slf4j" % "jcl-over-slf4j" % Versions.slf4j,
    "org.slf4j" % "jul-to-slf4j" % Versions.slf4j,
    "ch.qos.logback" % "logback-core" % Versions.logback,
    "ch.qos.logback" % "logback-classic" % Versions.logback,
    "ch.qos.logback" % "logback-access" % Versions.logback,
    "com.typesafe" % "config" % Versions.config,
    "org.apache.commons" % "commons-lang3" % Versions.lang3,
    "com.google.guava" % "guava" % Versions.guava,
    "org.json4s" %% "json4s-native" % Versions.json4s,
    "org.json4s" %% "json4s-jackson" % Versions.json4s,
    "com.github.scala-incubator.io" %% "scala-io-core" % Versions.scalaIO,
    "com.github.scala-incubator.io" %% "scala-io-file" % Versions.scalaIO)

  val injectDependency = Seq(
    "com.twitter" %% "twitter-server" % Versions.twitter,
    "com.twitter" %% "finagle-core" % Versions.finagle,
    "com.google.inject" % "guice" % Versions.guice,
    "com.google.code.findbugs" % "jsr305" % Versions.jsr,
    "com.twitter" %% "inject-core" % Versions.inject,
    "com.twitter" %% "inject-server" % Versions.inject) map {
      _ excludeAll (
        ExclusionRule(organization = "org.slf4j", name = "slf4j-api"),
        ExclusionRule(organization = "org.slf4j", name = "slf4j-jdk14"),
        ExclusionRule(organization = "org.slf4j", name = "slf4j-log4j12"))
    }

  val serverDependency = Seq(
    "com.twitter" %% "util-zk" % Versions.finagle,
    "com.twitter" %% "finagle-stats" % Versions.finagle,
    "com.twitter" %% "finagle-thriftmux" % Versions.finagle,
    "com.twitter" %% "finagle-serversets" % Versions.finagle) map {
      _ excludeAll (
        ExclusionRule(organization = "org.slf4j", name = "slf4j-api"),
        ExclusionRule(organization = "org.slf4j", name = "slf4j-jdk14"),
        ExclusionRule(organization = "org.slf4j", name = "slf4j-log4j12"))
    }

  val finchDependency = Seq(
    "com.twitter" %% "finagle-http" % Versions.finagle,
    "com.github.finagle" %% "finch-core" % Versions.finch,
    "com.github.finagle" %% "finch-json4s" % Versions.finch,
    "com.github.finagle" %% "finch-oauth2" % Versions.finch)

  val thriftDependency = Seq(
    "org.apache.thrift" % "libthrift" % Versions.thrift,
    "com.twitter" %% "scrooge-generator" % Versions.scrooge,
    "com.twitter" %% "scrooge-core" % Versions.scrooge,
    //"com.twitter" %% "scrooge-runtime" % "4.5.0",
    "com.twitter" %% "scrooge-serializer" % Versions.scrooge,
    "com.twitter" %% "finagle-thrift" % Versions.finagle) map {
      _ excludeAll (
        ExclusionRule(organization = "org.slf4j", name = "slf4j-api"),
        ExclusionRule(organization = "org.slf4j", name = "slf4j-jdk14"),
        ExclusionRule(organization = "org.slf4j", name = "slf4j-log4j12"))
    }

  val mysqlDependency = Seq(
    "com.twitter" %% "finagle-mysql" % Versions.finagle)

  val jdbcDependency = Seq(
    "com.facebook.presto" % "presto-jdbc" % "0.188",
    "com.jolbox" % "bonecp" % Versions.bonecp)

  val solrDependency = Seq(
    "com.github.takezoe" %% "solr-scala-client" % Versions.solr)

  val redisDependency = Seq(
    "com.twitter" %% "finagle-redis" % Versions.finagle,
    "redis.clients" % "jedis" % Versions.redis)

  val kafkaDependency = Seq(
    "net.cakesolutions" %% "scala-kafka-client" % Versions.kafka,
    "net.cakesolutions" %% "scala-kafka-client-akka" % Versions.kafka)

  val mongoDependency = Seq(
    "org.mongodb" %% "casbah" % Versions.mongo)

  val csvDependency = Seq(
    "com.github.tototoshi" %% "scala-csv" % Versions.csv)

  val jschDependency = Seq(
    "com.jcraft" % "jsch" % Versions.jsch)

  def mcompile(modules: ModuleID*): Seq[ModuleID] = modules map (_ % "compile")
  def mprovided(modules: ModuleID*): Seq[ModuleID] = modules map (_ % "provided")
  def mtest(modules: ModuleID*): Seq[ModuleID] = modules map (_ % "test")
  def mruntime(modules: ModuleID*): Seq[ModuleID] = modules map (_ % "runtime")
  def mcontainer(modules: ModuleID*): Seq[ModuleID] = modules map (_ % "container")

}