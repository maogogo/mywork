package com.maogogo.mywork.common.modules

import com.typesafe.config.Config
import com.twitter.finagle.ThriftMux
import com.twitter.scrooge.ThriftService
import com.google.inject.{ Provides, Singleton }
import com.typesafe.config.ConfigFactory
import java.io.File
import org.slf4j.LoggerFactory

trait BaseConfigModule { self =>

  lazy val Log = LoggerFactory.getLogger(getClass)

  def zookClient[T: Manifest](s: String)(implicit config: Config) = {
    provideClient(config.getString(s"${RPC.clientPrefix}${s}"))
  }

  /**
   * 创建 client
   */
  def provideClient[T: Manifest](path: String) = ThriftMux.client.newIface[T](s"zk2!${path}")

  /**
   * 创建server
   */
  def provideServices(injector: com.twitter.inject.Injector): Map[String, ThriftService] = ???

  /**
   *
   */
  def services(injector: com.twitter.inject.Injector) = {
    val rand = new scala.util.Random
    val config = injector.instance[Config]

    provideServices(injector).map {
      case (name, service) =>

        val label = s"${RPC.serverPrefix}${name}"
        val zkPath = s"zk!${config.getString(label)}"

        val anounce = s"${zkPath}!" + Math.abs(rand.nextInt)
        ThriftMux.server.serveIface(s":*", service).announce(anounce)
    }.toSeq
  }

  /**
   *
   */
  @Provides @Singleton
  implicit def provideConfig: Config = {

    val env = {
      val envProperty = System.getProperty("env")
      val envSystem = if (envProperty == null) System.getenv("env") else envProperty
      if (envSystem == null) "dev" else envSystem
    }

    val path = ConfigFactory.load.getString(env)
    Log.info(s"loading path: ${path} by env: ${env}")
    ConfigFactory parseFile (new File(path)) resolve
  }

}

object RPC {
  val serverPrefix = "rpc.server."
  val clientPrefix = "rpc.client."
}