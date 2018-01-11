package com.maogogo.mywork.meta.engine

import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.thrift._
import com.twitter.util.Stopwatch

class ComplexSqlEngining(implicit val builder: SqlEngineBuilder) extends SqlEngining {

  def packing: Seq[QuerySql] = {
    //对指标分组处理(如果指标分组size==0是有问题的)
    val watch = Stopwatch.start()
    val resp = (builder.allSelectings).groupBy(_.propertyGroup).map {
      case (group, selectings) ⇒
        val sqlTable = SqlTable(builder.table, selectings, builder.getAllGroupings, builder.filteringProps, Option(group))
        // 子查询
        val listingTable = sqlTable.createAggregateListingSql
        //这里的最后一层要从全部的id 里面获取信息(builder)
        val sql = SqlTemplate(listingTable, builder.createAggregateLabel(group), None, Option(builder.getGroupingColumn), None)
        QuerySql(sql, None, sqlTable.getAllParams, builder.getAllGroupings.size, builder.headers)
    } toSeq

    println(s"create query sql total timeout: ${watch().inMillis}")

    resp.zipWithIndex.foreach { x ⇒
      println(s"sql[${x._2}] => \n ${x._1.sql}")
    }

    resp
  }

}