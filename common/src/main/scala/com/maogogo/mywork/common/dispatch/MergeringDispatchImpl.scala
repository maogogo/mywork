package com.maogogo.mywork.common.dispatch

import com.maogogo.mywork.thrift._
import com.twitter.util.Future

class MergeringDispatchImpl extends MergerService.MethodPerEndpoint {

  def queryReport(req: MergerQueryReq): Future[MergerQueryResp] = {
    ???
  }
}