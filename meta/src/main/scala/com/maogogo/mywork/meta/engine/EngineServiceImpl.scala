package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import com.twitter.util.Future
import javax.inject.Inject

class EngineServiceImpl extends EngineService.MethodPerEndpoint {

  def engining(req: ReportReq): Future[Seq[QuerySql]] = {

    //    for {
    //      tableProperty <- data.findTableProperty(req.tableId)
    //    } yield {
    //      println("tableProperty.table.isListing ==>>" + tableProperty.table.isListing)
    //      val d = tableProperty.table.isListing match {
    //        case Some(false) => new SimpleSqlEngining().packing(tableProperty, req)
    //        case _ => new ListingSqlEngining().packing(tableProperty, req)
    //      }
    //
    //      d.map { x =>
    //        println("1=" * 50)
    //        println(x.sql)
    //        println("2=" * 50)
    //      }
    //
    //      Seq.empty
    //    }

    ???
  }

  def toQuerySql(req: ReportReq): Future[Seq[QuerySql]] = {
    ???
  }
}