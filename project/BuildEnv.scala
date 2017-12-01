import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

/** sets the build environment */
object BuildEnvPlugin extends AutoPlugin {

  // make sure it triggers automatically
  override def trigger = AllRequirements
  override def requires = JvmPlugin

  object autoImport {
    object BuildEnv extends Enumeration {
      val Production, Stage, Test, Aliyun, HJ, TaiBao, TT, UAT, PET, PET1, PET2, PROD, PROD1, PROD2, Developement = Value
    }

    val buildEnv = settingKey[BuildEnv.Value]("the current build environment")
  }
  import autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    buildEnv := {
      sys.props.get("env")
        .orElse(sys.env.get("BUILD_ENV"))
        .flatMap {
          case "prod" => Some(BuildEnv.Production)
          case "stage" => Some(BuildEnv.Stage)
          case "test" => Some(BuildEnv.Test)
          case "dev" => Some(BuildEnv.Developement)
          case "aliyun" => Some(BuildEnv.Aliyun)
          case "hj" => Some(BuildEnv.HJ)
          case "taibao" => Some(BuildEnv.TaiBao)
          case "tt" => Some(BuildEnv.TT)
          case "uat" => Some(BuildEnv.UAT)
          case "pet" => Some(BuildEnv.PET)
          case "pet1" => Some(BuildEnv.PET1)
          case "pet2" => Some(BuildEnv.PET2)

          //case "prod" => Some(BuildEnv.PROD)
          case "prod1" => Some(BuildEnv.PROD1)
          case "prod2" => Some(BuildEnv.PROD2)

          case unkown => None
        }
        .getOrElse(BuildEnv.Developement)
    },
    // give feed back
    onLoadMessage := {
      // depend on the old message as well
      val defaultMessage = onLoadMessage.value
      val env = buildEnv.value
      s"""|$defaultMessage
          |Running in build environment: $env""".stripMargin
    })

}
