package com.maogogo.mywork.root.service

import com.maogogo.mywork.thrift._
import com.twitter.util.Future
import javax.inject.Inject
import com.maogogo.mywork.common.dispatch.MergeringDispatchImpl

class RootServiceImpl @Inject() (meta: MetaService.MethodPerEndpoint) extends RootService.MethodPerEndpoint { //@Inject() (meta: MetaService.MethodPerEndpoint, engine: EngineService.MethodPerEndpoint,
  //dispatch: MergeringDispatchImpl) extends RootService.MethodPerEndpoint {

  def hi(name: String): Future[String] = {
    meta.getRandomCache()
    //Future.value(s"hello:$name")
  }

  /**
   *
   * 结果数据处理顺序
   * 1、合并
   * 2、排序（去掉非法数据）
   * 3、生成合计项
   *
   *
   */
  def queryReport(req: ReportReq): Future[ReportResp] = {

    //    for {
    //      x <- engine.engining(req) handle {
    //        case e: Throwable =>
    //          println("e => " + e.getMessage)
    //          e.printStackTrace()
    //          Seq.empty
    //      }
    //    } yield {
    //      x.map { s =>
    //        println("=" * 50)
    //        println(s.sql)
    //        println("=" * 50)
    //      }
    //    }
    //
    //    Future.value(RootQueryResp("", Seq.empty, Seq.empty, None))

    ???
  }

  def build(rows: Seq[Row])(fallback: Seq[Row] ⇒ Seq[Row]): Seq[Row] = fallback(rows)

  def executeReport(req: QueryReq): Future[ReportResp] = {
    ???
  }

  def executeToStaging(req: QueryReq): Future[ExecuteResp] = {
    ???
  }

  def queryToStaging(req: ReportReq): Future[ExecuteResp] = {
    ???
  }

}