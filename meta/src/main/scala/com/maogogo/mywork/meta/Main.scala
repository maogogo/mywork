package com.maogogo.mywork.meta

import com.twitter.inject.server.TwitterServer
import com.typesafe.config.Config
import java.net.InetSocketAddress
import com.twitter.logging.Level
import com.twitter.logging.Logging.LevelFlaggable
import com.twitter.util.Await
import com.maogogo.mywork.meta.modules.ServicesModule
import com.maogogo.mywork.common.modules.ConfigModule

object Main extends TwitterServer {

  implicit val config: Config = ConfigModule.provideConfig
  override val adminPort = flag("admin.port", new InetSocketAddress(config.getInt("admin.port")), "")

  val level: Level = Level.ERROR
  override val levelFlag = flag("log.level", level, "")

  lazy val servicesModule = new ServicesModule
  override def modules = Seq(servicesModule)

  override def postWarmup() {

    val services = servicesModule.services(injector)

    Await.all(services: _*)
    info(s"${logo}\t${adminPort}\t${config.origin}")
    Await.ready(adminHttpServer)
  }

  private val logo = """
      __  __________________ 
     /  |/  / ____/_  __/   |
    / /|_/ / __/   / / / /| |
   / /  / / /___  / / / ___ |
  /_/  /_/_____/ /_/ /_/  |_|"""
}