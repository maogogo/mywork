package com.maogogo.mywork.common.modules

import com.typesafe.config.Config
import com.twitter.finagle.ThriftMux
import com.twitter.scrooge.ThriftService
import com.google.inject.{ Provides, Singleton }
import com.typesafe.config._
import java.io.File
import org.slf4j.LoggerFactory
import com.twitter.finagle.Thrift

/**
 *
 */
trait BaseConfigModule { self =>
  lazy val Log = LoggerFactory.getLogger(getClass)

  def zookServer(implicit config: Config) = (s: String) => {
    val label = s"${RPC.rpcServerPrefix}${s}"
    s"zk!${config.getString(label)}"
  }

  def zookClient[T: Manifest](s: String)(implicit config: Config) = {
    provideClient(config.getString(s"${RPC.rpcClientPrefix}${s}"))
  }

  def provideClient[T: Manifest](path: String) = ThriftMux.client.newIface[T](s"zk2!${path}")

  def provideServices(injector: com.twitter.inject.Injector): Map[String, ThriftService] = ???

  def services(injector: com.twitter.inject.Injector) = {
    val rand = new scala.util.Random
    val config = injector.instance[Config]
    //val filter = new HandlerFilter[com.twitter.finagle.mux.Request, com.twitter.finagle.mux.Response]
    provideServices(injector).map { kv =>
      val (name, service) = kv
      val anounce = s"${zookServer(config)(name)}!" + Math.abs(rand.nextInt)
      ThriftMux.server.serveIface(s":*", service).announce(anounce)
    }.toSeq
  }

  /**
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
  val rpcEndpoints = "rpc.endpoints"
  val rpcNamespace = "rpc.namespace"
  val rpcServerPrefix = "rpc.server."
  val rpcClientPrefix = "rpc.client."
}

