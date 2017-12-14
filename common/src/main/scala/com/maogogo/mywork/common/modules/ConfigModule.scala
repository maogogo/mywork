package com.maogogo.mywork.common.modules

import com.typesafe.config.Config
import com.twitter.finagle.ThriftMux
import com.twitter.scrooge.ThriftService
import com.google.inject.{ Provides, Singleton }
import com.typesafe.config._
import java.io.File
import org.slf4j.LoggerFactory
import com.twitter.finagle.Thrift
import java.net.InetSocketAddress
import javax.inject.Inject
import com.github.racc.tscg.TypesafeConfig

/**
 *
 */
sealed trait ConfigModule { self =>

  def provideConfig: Config = {

    val env = {
      val envProperty = System.getProperty("env")
      val envSystem = if (envProperty == null) System.getenv("env") else envProperty
      if (envSystem == null) "dev" else envSystem
    }

    val path = ConfigFactory.load.getString(env)
    //Log.info(s"\n ===>> loading path: ${path} by env: ${env}")

    ConfigFactory parseFile (new File(path)) resolve
  }

}

object ConfigModule extends ConfigModule

object RPC {
  val endpoints = "rpc.endpoints"
  val namespace = "rpc.namespace"
  val serverPrefix = "rpc.server."
  val clientPrefix = "rpc.client."
}

