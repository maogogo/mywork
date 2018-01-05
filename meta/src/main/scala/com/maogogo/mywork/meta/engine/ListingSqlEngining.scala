package com.maogogo.mywork.meta.engine

import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.thrift._

class ListingSqlEngining(implicit val builder: SqlEngineBuilder) extends SqlEngining {

  def packing: Seq[QuerySql] = {
    // grouping 分组
    val sqlTables = createSqlTable
    // 创建子查询
    val tempTable = createTempTable(sqlTables)
    // 前缀
    val prefix = if (sqlTables.size == 1) None else Option("A")
    // 创建SQL
    val sql = SqlTemplate(tempTable, builder.createListingLabel(prefix), getAdapter(sqlTables))
    //TODO(Toan) 这里缺少 sort 和 limit
    Seq(QuerySql(sql, None, getParams(sqlTables), builder.groupingProps.size, builder.headers))
  }

  def createTempTable(sqlTables: Seq[SqlTable]): String = {
    sqlTables.zipWithIndex.foldLeft("") { (fragment, sqlTableWithIndex) ⇒
      val (sqlTable, index) = sqlTableWithIndex
      val r = fragment match {
        case x if x.nonEmpty ⇒ s"${x} INNER JOIN"
        case _ ⇒ ""
      }
      s"${r} (${sqlTable.createListingSql}) ${SqlTemplate.asName(index)} ON ${sqlTable.createJoinOn(index)}"
    }
  }

  def getParams(sqlTables: Seq[SqlTable]): Option[Seq[String]] =
    Option(sqlTables.flatMap(_.getListingParams) ++ sqlTables.flatMap(_.getAggregateParams))

  def getAdapter(sqlTables: Seq[SqlTable]): Option[String] =
    Option(sqlTables.flatMap(_.getAggregateAdaper).distinct.filterNot(_.isEmpty).mkString(" AND "))

  def createSqlTable: Seq[SqlTable] = {
    if (builder.groupingProps.size == 0) {
      //全部grouping
      val groupingProps = builder.allProps.filterNot(x ⇒ x.propertyType == PropertyType.Selecting || x.propertyType == PropertyType.Combining)
      Seq(SqlTable(builder.table, groupingProps, builder.filteringProps, None))

    } else {
      //部分 grouping
      val commonGrouping = builder.groupingProps.filter(SqlTemplate.filterCommonGrouping)
      builder.groupingProps.filterNot(SqlTemplate.filterCommonGrouping).groupBy(_.propertyGroup).map {
        case (group, props) ⇒
          SqlTable(builder.table, commonGrouping ++ props, builder.filteringProps, Option(group))
      } toSeq
    }
  }

}
