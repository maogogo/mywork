package com.maogogo.mywork.leaf.service

import com.maogogo.mywork.thrift._
import com.twitter.util.Future

class LeafServiceImpl extends LeafService.MethodPerEndpoint {

  def queryReport(req: MergerQueryReq): Future[MergerQueryResp] = {
    ???
  }
}