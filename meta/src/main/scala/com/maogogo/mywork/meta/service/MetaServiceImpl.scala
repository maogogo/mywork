package com.maogogo.mywork.meta.service

import com.maogogo.mywork.thrift._
import com.twitter.util.Future

class MetaServiceImpl extends MetaService.FutureIface {

  def getRandomCache(): Future[String] = {
    ???
  }

  def getRandomPartition(): Future[Int] = {
    ???
  }

}