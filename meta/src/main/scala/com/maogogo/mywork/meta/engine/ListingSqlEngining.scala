package com.maogogo.mywork.meta.engine

import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.thrift._

//@Provides @Singleton
class ListingSqlEngining(implicit val builder: SqlEngineBuilder) extends SqlEngining {

  def packing: Seq[QuerySql] = {

    val sqlTables = createSqlTable

    val tempTable = sqlTables.zipWithIndex.foldLeft("") { (fragment, sqlTableWithIndex) ⇒
      val (sqlTable, index) = sqlTableWithIndex
      val r = fragment match {
        case x if x.nonEmpty ⇒ s"${x} INNER JOIN"
        case _ ⇒ ""
      }
      s"${r} (${sqlTable.createListingSql}) ${SqlTemplate.asName(index)} ON ${sqlTable.createJoinOn(index)}"
    }

    val prefix = sqlTables.size match {
      case 1 ⇒ None
      case _ ⇒ Option("A")
    }

    val adapter = sqlTables.flatMap(_.getAggregateAdaper).distinct.filterNot(_.isEmpty).mkString(" AND ")

    val sql = SqlTemplate(tempTable, builder.createListingLabel(prefix), Option(adapter))

    val params = sqlTables.flatMap(_.getListingParams) ++ sqlTables.flatMap(_.getAggregateParams)

    Seq(QuerySql(sql, None, Option(params), builder.groupingProps.size, builder.headers))
  }

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
//
//  def packing(tableProperty: TableProperty, req: RootQueryReq): Seq[QuerySql] = {
//    val resp = getListSelectingAndFilteringResp(tableProperty, req)
//    Seq(toListingSql(tableProperty, req.paging, resp))
//  }
//
//  def getListSelectingAndFilteringResp(tableProperty: TableProperty, req: RootQueryReq): ListSelectingAndFilteringResp = {
//    val selecting1 = toListingLabel(tableProperty.properties).mkString(", ")
//    val selecting2 = toListingLabel(tableProperty.properties.filter(_.propertyType == PropertyType.Selecting)).mkString(", ")
//
//    val filtering = toSeqProperty((req.filtering, tableProperty.properties)).map(_.map(toCellFilter))
//
//    val filter1 = filtering.map { _.filterNot { _._1.startsWith(QueryPrefix) } }
//    val filter2 = filtering.map { _.filter { _._1.startsWith(QueryPrefix) } }
//
//    val filtering1 = filter1.map(_.map(_._1)) match {
//      case Some(fs) if fs.size > 0 => fs.mkString("WHERE ", " AND ", "")
//      case _ => ""
//    }
//    val filtering2 = filter2.map(_.map(_._1)) match {
//      case Some(fs) if fs.size > 0 => fs.mkString("WHERE ", " AND ", "")
//      case _ => ""
//    }
//    val params1 = filter1.map(_.flatMap(_._2).flatten)
//    val params2 = filter2.map(_.flatMap(_._2).flatten)
//
//    val params = Seq(params1, params2).flatten.flatten
//    val headers = toCellHeader(tableProperty.properties.filter(_.propertyType == PropertyType.Selecting))
//
//    ListSelectingAndFilteringResp(selecting1, selecting2, filtering1, filtering2, headers, Some(params))
//  }
//
//  def toListingSql(tableProperty: TableProperty, paging: Option[Paging], resp: ListSelectingAndFilteringResp): QuerySql = {
//    val params = resp.params
//    val level1SQL =
//      sqlTemplate(tableProperty.table.dbTableName, resp.select1, Some(resp.filter1), None)
//
//    //第二层过滤项
//    val level2SQL = sqlTemplate(s"(${level1SQL}) A", resp.select2, Some(resp.filter2), None)
//
//    val groupingColumns = tableProperty.properties.size
//
//    val limit = paging match {
//      case Some(page) =>
//        val _limit = if (page.limit == 0) 20 else page.limit
//        s"limit ${page.offset / _limit}, 2"
//      case _ => "limit 1"
//    }
//
//    QuerySql(sql = level2SQL, countSql = Some(s"select count(1) from (${level2SQL}) K"), resp.params, groupingColumns, resp.headers)
//  }
//
//}