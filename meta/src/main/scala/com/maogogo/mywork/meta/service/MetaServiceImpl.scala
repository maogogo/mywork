package com.maogogo.mywork.meta.service

import com.maogogo.mywork.thrift._
import com.twitter.util.Future
import javax.inject.Inject

class MetaServiceImpl @Inject() (dao: MetaServiceDao) extends MetaService.MethodPerEndpoint {

  def getRandomCache(): Future[String] = {
    dao.findTest.map(_.flatten.mkString(","))
  }

  def getRandomPartition(): Future[Int] = {
    ???
  }

}