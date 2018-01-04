package com.maogogo.mywork.meta.service

import com.maogogo.mywork.thrift._
import javax.inject.Inject
import com.twitter.util.Future
import com.maogogo.mywork.meta.engine._

class EngineServiceImpl @Inject() (dao: MetaServiceDao) extends EngineService.MethodPerEndpoint {

  def toQuerySql(req: ReportReq): Future[Seq[QuerySql]] = {

    for {
      tabPropertySeq ← dao.findTableProperties
      tabPropOption = tabPropertySeq.find(_.table.id == req.tableId)
      _ = if (tabPropOption.isEmpty) throw new ServiceException(s"can not found table and properties by tableId[${req.tableId}]")
    } yield {
      implicit val builder = new SqlEngineBuilder(req, tabPropOption.get)
      //清单查询
      (req.isListing || req.selecting.isEmpty) match {
        case true ⇒ new ListingSqlEngining
        case _ ⇒ new ComplexSqlEngining
      }
    }

    ???
  }

}