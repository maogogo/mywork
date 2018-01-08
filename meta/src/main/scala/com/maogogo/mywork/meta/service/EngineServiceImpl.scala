package com.maogogo.mywork.meta.service

import com.maogogo.mywork.thrift._
import javax.inject.Inject
import com.twitter.util.Future
import com.maogogo.mywork.meta.engine._
import org.slf4j.LoggerFactory

class EngineServiceImpl @Inject() (dao: MetaServiceDao) extends EngineService.MethodPerEndpoint {

  lazy val Log = LoggerFactory.getLogger(getClass)

  def toQuerySql(req: ReportReq): Future[Seq[QuerySql]] = {
    Log.info(s"SqlEngining Report Request : ${req}")

    val resp = for {
      tabPropertySeq ← dao.findTableProperties
      tabPropOption = tabPropertySeq.find(_.table.id == req.tableId)
      _ = if (tabPropOption.isEmpty) {
        Log.error(s"can not found table and properties by tableId[${req.tableId}]")
        throw new ServiceException(s"can not found table and properties by tableId[${req.tableId}]")
      }
    } yield {
      implicit val builder = new SqlEngineBuilder(req, tabPropOption.get)
      //清单查询
      val sqlEngine: SqlEngining = builder.isListing match {
        case true ⇒ new ListingSqlEngining //清单查询
        case _ ⇒ new ComplexSqlEngining //报表查询
      }

      sqlEngine.packing
    }

    resp handle {
      case e: Throwable ⇒
        Log.error(s"sql engine error ⇒", e)
    }

    resp
  }

}