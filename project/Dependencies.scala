import sbt._

object Dependencies {

  val repositories = Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    //Resolver.bintrayRepo("cakesolutions", "maven"),
    //"OSS Sonatype" at "https://repo1.maven.org/maven2/",
    "Spring IO" at "https://repo.spring.io/release",
    "Artima Maven Repository" at "http://repo.artima.com/releases",
    "twttr" at "https://maven.twttr.com/",
    //"amateras-repo" at "http://amateras.sourceforge.jp/mvn/",
    //"spray repo" at "http://repo.spray.io",
    "finch-server" at "http://storage.googleapis.com/benwhitehead_me/maven/public",
    "sonatype-oss-snapshot" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

	val slf4jVersion = "1.7.25"
  val logbackVersion = "1.2.2"
  val configVersion = "1.3.1"
  val guavaVersion = "21.0"
  val lang3Version = "3.5"
  val json4sJackson = "3.5.2"

  val commonDependency = Seq(

    "org.scalactic" %% "scalactic" % "3.0.1" % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "junit" % "junit" % "4.12" % "test",
    //"org.scala-lang" % "scala-compiler" % "2.11.8",
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "log4j-over-slf4j" % slf4jVersion,
    "org.slf4j" % "jcl-over-slf4j" % slf4jVersion,
    "org.slf4j" % "jul-to-slf4j" % slf4jVersion,
    "ch.qos.logback" % "logback-core" % logbackVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "ch.qos.logback" % "logback-access" % logbackVersion,
    "com.typesafe" % "config" % configVersion,
    "org.apache.commons" % "commons-lang3" % lang3Version,
    "com.google.guava" % "guava" % guavaVersion,
    "org.json4s" %% "json4s-native" % json4sJackson,
    "org.json4s" %% "json4s-jackson" % json4sJackson
  )

  val serverVersion = "1.29.0"
  val finagleVersion = "6.44.0"
  val guiceVersion = "4.1.0"
  val jsrVersion = "3.0.2"
  val injectVersion = "2.1.6"

  val serverDependency = Seq(
    "com.twitter" %% "twitter-server" % serverVersion,
    "com.twitter" %% "finagle-core" % finagleVersion,
    "com.twitter" %% "finagle-http" % finagleVersion,
    //"org.scalaj" %% "scalaj-http" % "2.3.0",
    "com.twitter" %% "finagle-stats" % finagleVersion,
    "com.twitter" %% "finagle-thriftmux" % finagleVersion,
    "com.twitter" %% "finagle-serversets" % finagleVersion excludeAll (
      ExclusionRule(organization = "org.slf4j", name = "slf4j-jdk14"),
      ExclusionRule(organization = "org.slf4j", name = "slf4j-log4j12")),
    "com.google.inject" % "guice" % guiceVersion,
    "com.google.code.findbugs" % "jsr305" % jsrVersion,
    "com.twitter.inject" %% "inject-core" % injectVersion,
    "com.twitter.inject" %% "inject-server" % injectVersion
  )

  val finchVersion = "0.14.1"
  
  val finchDependency = Seq(
    "com.github.finagle" %% "finch-core" % finchVersion,
    "com.github.finagle" %% "finch-json4s" % finchVersion,
    "com.github.finagle" %% "finch-oauth2" % finchVersion
  )

  val thriftVersion = "0.9.0"
  val scroogeVersion = "4.16.0"

  val thriftDependency = Seq(
    "org.apache.thrift" % "libthrift" % thriftVersion,
    "com.twitter" %% "scrooge-core" % scroogeVersion,
    "com.twitter" %% "scrooge-runtime" % "4.5.0",
    "com.twitter" %% "scrooge-serializer" % scroogeVersion,
    "com.twitter" %% "finagle-thrift" % finagleVersion
  )

  val mysqlVersion = "6.0.5"
  val solrVersion = "0.0.15"
  val redisVersion = "3.4"
  val mongoVersion = "3.1.1"
  val csvVersion = "1.3.4"
  val jschVersion = "0.1.54"

  val mysqlDependency = Seq(
    "com.twitter" %% "finagle-mysql" % finagleVersion
  )

  val solrDependency = Seq(
    "com.github.takezoe" %% "solr-scala-client" % solrVersion
  )

  val redisDependency = Seq(
    "net.debasishg" %% "redisclient" % redisVersion
  )

  val kafkaDependency = Seq(
    "net.cakesolutions" %% "scala-kafka-client" % "0.10.1.2",
    "net.cakesolutions" %% "scala-kafka-client-akka" % "0.10.1.2"
  )

  val mongoDependency = Seq(
    "org.mongodb" %% "casbah" % mongoVersion
  )

  val csvDependency = Seq(
    "com.github.tototoshi" %% "scala-csv" % csvVersion
  )

  val jschDependency = Seq(
    "com.jcraft" % "jsch" % jschVersion
  )
  
}