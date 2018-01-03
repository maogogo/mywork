package com.maogogo.mywork.common.inject

import com.twitter.inject.server.TwitterServer
import com.typesafe.config._
import com.maogogo.mywork.common.modules.ConfigModule
import java.net.InetSocketAddress
import com.twitter.logging.Level
import com.twitter.logging.Logging.LevelFlaggable
import com.twitter.inject.TwitterModule
import com.twitter.util.Await

trait MainServer extends TwitterServer {

  implicit val config: Config = ConfigModule.provideConfig
  override val adminPort = flag("admin.port", new InetSocketAddress(config.getInt("admin.port")), "")

  val level: Level = Level.ERROR
  override val levelFlag = flag("log.level", level, "")

  val servicesModule: TwitterModule
  override def modules = Seq(servicesModule)

  val logo: String

  def injectServer: Unit

  override def postWarmup() {
    //    val services = servicesModule.services(injector)
    //    Await.all(services: _*)
    injectServer

    info(s"${logo}\t${adminPort}\t${config.origin}")
    Await.ready(adminHttpServer)
  }

}