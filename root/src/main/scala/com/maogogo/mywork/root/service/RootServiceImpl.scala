package com.maogogo.mywork.root.service

import com.maogogo.mywork.thrift._
import com.twitter.util.Future
import javax.inject.Inject
import com.maogogo.mywork.common.dispatch.MergeringDispatchImpl

class RootServiceImpl @Inject() (meta: MetaService.MethodPerEndpoint, engine: EngineService.MethodPerEndpoint,
  dispatch: MergeringDispatchImpl) extends RootService.MethodPerEndpoint {

  /**
   *
   * 结果数据处理顺序
   * 1、合并
   * 2、排序（去掉非法数据）
   * 3、生成合计项
   *
   *
   */
  def queryReport(req: RootQueryReq): Future[RootQueryResp] = {

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

  def build(rows: Seq[Row])(fallback: Seq[Row] => Seq[Row]): Seq[Row] = fallback(rows)

}