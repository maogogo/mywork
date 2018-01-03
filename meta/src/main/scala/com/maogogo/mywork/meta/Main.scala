package com.maogogo.mywork.meta

import com.maogogo.mywork.common.inject.MainServer
import com.maogogo.mywork.meta.modules.ServicesModule
import com.twitter.util.Await

object Main extends MainServer {

  lazy val servicesModule = new ServicesModule

  def injectServer: Unit = {
    val services = servicesModule.services(injector)
    Await.all(services: _*)
  }

  val logo = """
      __  __________________ 
     /  |/  / ____/_  __/   |
    / /|_/ / __/   / / / /| |
   / /  / / /___  / / / ___ |
  /_/  /_/_____/ /_/ /_/  |_|"""
}