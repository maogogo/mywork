package com.maogogo.mywork.root.service

import com.maogogo.mywork.thrift._
import com.twitter.util.Future
import javax.inject.Inject

class RootServiceImpl @Inject() (meta: MetaService.FutureIface, engine: EngineService.FutureIface) extends RootService.FutureIface {

  def queryReport(req: RootQueryReq): Future[RootQueryResp] = {

    for {
      x <- engine.engining(req)
    } yield {
      x.map { s =>
        println("=" * 50)
        println(s.sql)
        println("=" * 50)
      }
    }

    Future.value(RootQueryResp("", Seq.empty, Seq.empty, None))
  }
}