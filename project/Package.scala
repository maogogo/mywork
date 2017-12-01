import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._

object Package {

  lazy val linuxSettings: Seq[Setting[_]] = Seq(
    packageName in Universal := packageName.value,
    maintainer in Linux := Globals.maintainer,
    packageSummary in Linux := Globals.summary,
    packageDescription in Linux := Globals.description
  //daemonUser in Linux         := Globals.serviceDaemonUser,
  //daemonGroup in Linux        := Globals.serviceDaemonGroup
  )

  lazy val rpmSettings: Seq[Setting[_]] = linuxSettings ++ Seq(
    packageArchitecture in Rpm := "x86_64",
    rpmLicense := Some("BSD"),
    rpmVendor := Globals.organizationName,
    maintainerScripts in Rpm := Map())

  lazy val mappingSettings: Seq[Setting[_]] = rpmSettings ++ Seq(
    mappings in Universal += {
      ((resourceDirectory in Compile).value / "application.conf") -> "conf/application.conf"
    },
    mappings in Universal += {
      ((resourceDirectory in Compile).value / "logback.xml") -> "conf/logback.xml"
    },
    bashScriptExtraDefines ++= Seq(
      """addJava "-Xmx14G"""",
      s"""addJava "-Denv=${sys.props.getOrElse("env", "dev")}"""",
      """addJava "-Dconfig.file=${app_home}/../conf/application.conf"""",
      """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml""""))

}