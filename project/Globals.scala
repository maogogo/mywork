import sbt._

object Globals {
  val name = "mywork"
  val version = "0.0.1"
  val scalaVersion = "2.11.11"
  val crossScalaVersions = Seq("2.10.6", "2.11.11")
  val jvmVersion = "1.7"

  val homepage = Some(url("http://www.maogogo.com"))
  val startYear = Some(2016)
  val summary = "An concurrent mailer service, based on Akka."
  val description = "An concurrent mailer service, based on Akka."
  val maintainer = "Taon <toan@maogogo.com>"
  val licenses = url("http://www.opensource.org/licenses/bsd-license.php")

  val organizationName = "maogogo Inc."
  val organization = "com.maogogo"
  val organizationHomepage = Some(url("http://www.maogogo.com"))

  val sourceUrl = "https://github.com/maogogo/mywork.git"
  val scmUrl = "git@github.com/maogogo/mywork.git"
  val scmConnection = "scm:git:git@github.com/maogogo/mywork.git"

  val serviceDaemonUser = "mywork"
  val serviceDaemonGroup = "mywork"

  val baseCredentials: Seq[Credentials] = Seq(
    Credentials(Path.userHome / ".sbt" / ".credentials"))

  val snapshotRepo = "http://123.56.183.194:8081/nexus/content/repositories/"

  val pomDevelopers =
    <id>Toan</id><name>Toan</name><url>http://maogogo.com</url>;

  val pomLicense =
    <licenses>
      <license>
        <name>The BSD 3-Clause License</name>
        <url>http://opensource.org/licenses/BSD-3-Clause</url>
        <distribution>repo</distribution>
      </license>
    </licenses>;
}