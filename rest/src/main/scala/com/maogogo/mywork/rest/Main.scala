package com.maogogo.mywork.rest

import com.twitter.util.Await
import com.twitter.finagle.Http
import com.twitter.inject.server.TwitterServer
import java.net.InetSocketAddress
import com.twitter.logging.Level
import com.twitter.logging.Logging.LevelFlaggable
import io.finch._
import com.maogogo.mywork.rest.modules.ServicesModule

object Main extends TwitterServer {

  val config = ServicesModule.provideConfig
  override val adminPort = flag("admin.port", new InetSocketAddress(config.getInt("admin.port")), "")

  val level: Level = Level.ERROR
  override val levelFlag = flag("log.level", level, "")

  override def modules = Seq(ServicesModule)

  override def postWarmup() {

    val endpoints = ServicesModule.endpoints(injector).toService
    val filters = ServicesModule.filters(injector)
    val server = Http.server.serve(s":${config.getInt("http.port")}", filters andThen endpoints)

    onExit {
      server.close()
    }

    info(s"${logo}\t${adminPort}")
    Await.ready(adminHttpServer)
  }

  lazy val logo = """
      ____  _________________
     / __ \/ ____/ ___/_  __/
    / /_/ / __/  \__ \ / /   
   / _, _/ /___ ___/ // /    
  /_/ |_/_____//____//_/     """

}