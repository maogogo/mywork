package com.maogogo.mywork.root

import com.maogogo.mywork.common.inject.MainServer
import com.maogogo.mywork.root.modules.ServicesModule
import com.twitter.util.Await

object Main extends MainServer {

  lazy val servicesModule = new ServicesModule

  def injectServer: Unit = {
    val services = servicesModule.services(injector)
    Await.all(services: _*)
  }

  val logo = """
      ____  ____  ____  ______
     / __ \/ __ \/ __ \/_  __/
    / /_/ / / / / / / / / /   
   / _, _/ /_/ / /_/ / / /    
  /_/ |_|\____/\____/ /_/     
                            """

}