package com.maogogo.mywork.merger.service

import com.maogogo.mywork.thrift._
import com.twitter.util.Future
import javax.inject.Inject
import com.maogogo.mywork.common.dispatch.LeafingDispatchImpl

class MergerServiceImpl @Inject() (dispatch: LeafingDispatchImpl) extends MergerService.MethodPerEndpoint {

  def queryReport(req: MergerQueryReq): Future[MergerQueryResp] = {
    ???
  }
}