package com.maogogo.mywork.merger

import com.twitter.inject.server.TwitterServer
import com.twitter.util._
import java.net.InetSocketAddress
import com.twitter.inject.server.TwitterServer
import com.typesafe.config.Config
import com.twitter.logging.Level
import com.twitter.logging.Logging.LevelFlaggable
import com.maogogo.mywork.merger.modules.ServicesModule
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
      __  ___                         
     /  |/  /__  _________ ____  _____
    / /|_/ / _ \/ ___/ __ `/ _ \/ ___/
   / /  / /  __/ /  / /_/ /  __/ /    
  /_/  /_/\___/_/   \__, /\___/_/     
                   /____/             """

}