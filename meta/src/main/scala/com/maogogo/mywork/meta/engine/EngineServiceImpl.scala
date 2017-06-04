package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import com.twitter.util.Future

class EngineServiceImpl extends EngineService.FutureIface {

  def engining(req: RootQueryReq): Future[Seq[SqlEngine]] = {
    ???
  }
}