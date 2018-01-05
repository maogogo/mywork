package com.maogogo.mywork.meta.engine

import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.thrift._

class ComplexSqlEngining(implicit val builder: SqlEngineBuilder) extends SqlEngining {

  def packing: Seq[QuerySql] = {
    builder.selectingProps.groupBy(_.propertyGroup).map {
      case (group, props) ⇒
        val sqlTable = SqlTable(builder.table, props, builder.groupingProps, builder.filteringProps, Option(group))
        // 子查询
        val listingTable = sqlTable.createAggregateListingSql
        val sql = SqlTemplate(listingTable, sqlTable.getAggregateColumn, None, Option(sqlTable.getGroupingColumn), None)

        QuerySql(sql, None, None, 0, Seq.empty)
    }
  } toSeq

}