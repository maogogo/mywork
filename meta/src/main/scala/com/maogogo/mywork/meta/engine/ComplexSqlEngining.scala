package com.maogogo.mywork.meta.engine

import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.thrift._

class ComplexSqlEngining(implicit val builder: SqlEngineBuilder) extends SqlEngining {

  def packing: Seq[QuerySql] = {
    val resp = builder.selectingProps.groupBy(_.propertyGroup).map {
      case (group, props) ⇒
        val sqlTable = SqlTable(builder.table, props, builder.getAllGroupings, builder.filteringProps, Option(group))
        // 子查询
        val listingTable = sqlTable.createAggregateListingSql
        //这里的最后一层要从全部的id 里面获取信息(builder)
        val sql = SqlTemplate(listingTable, builder.createAggregateLabel(group), None, Option(builder.getGroupingColumn), None)
        QuerySql(sql, None, None, 0, Seq.empty)
    } toSeq

    resp.zipWithIndex.foreach { x ⇒
      println(s"sql[${x._2}] => \n ${x._1.sql}")
    }

    resp
  }

}