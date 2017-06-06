package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import com.maogogo.mywork.thrift.ErrorCode.MetaError

class ListingSqlEngining extends SqlEngining {

  def packing(tableProperty: TableProperty, req: RootQueryReq): Seq[QuerySql] = {
    val resp = getListSelectingAndFilteringResp(tableProperty, req)
    Seq(toListingSql(tableProperty, req.paging, resp))
  }

  def getListSelectingAndFilteringResp(tableProperty: TableProperty, req: RootQueryReq): ListSelectingAndFilteringResp = {
    val selecting1 = toCellLabel(tableProperty.properties).mkString(", ")
    val selecting2 = toCellLabel(tableProperty.properties.filter(_.propertyType == PropertyType.Selecting)).mkString(", ")

    val filtering = toProperty((req.filtering, tableProperty.properties)).map(_.map(toCellFilter))

    val filter1 = filtering.map { _.filterNot { _._1.startsWith(QueryPrefix) } }
    val filter2 = filtering.map { _.filter { _._1.startsWith(QueryPrefix) } }

    val filtering1 = filter1.map(_.map(_._1)) match {
      case Some(fs) if fs.size > 0 => fs.mkString("WHERE ", " AND ", "")
      case _ => ""
    }
    val filtering2 = filter2.map(_.map(_._1)) match {
      case Some(fs) if fs.size > 0 => fs.mkString("WHERE ", " AND ", "")
      case _ => ""
    }
    val params1 = filter1.map(_.flatMap(_._2).flatten)
    val params2 = filter2.map(_.flatMap(_._2).flatten)

    val params = Seq(params1, params2).flatten.flatten
    val headers = toCellHeader(tableProperty.properties.filter(_.propertyType == PropertyType.Selecting))

    ListSelectingAndFilteringResp(selecting1, selecting2, filtering1, filtering2, headers, Some(params))
  }

  def toListingSql(tableProperty: TableProperty, paging: Option[Paging], resp: ListSelectingAndFilteringResp): QuerySql = {
    val params = resp.params
    val level1SQL =
      sqlTemplate(tableProperty.table.dbTableName, resp.select1, Some(resp.filter1), None)

    //第二层过滤项
    val level2SQL = sqlTemplate(s"(${level1SQL}) A", resp.select2, Some(resp.filter2), None)

    val groupingColumns = tableProperty.properties.size

    val limit = paging match {
      case Some(page) =>
        val _limit = if (page.limit == 0) 20 else page.limit
        s"limit ${page.offset / _limit}, 2"
      case _ => "limit 1"
    }

    QuerySql(sql = level2SQL, countSql = Some(s"select count(1) from (${level2SQL}) K"), resp.params, groupingColumns, resp.headers)
  }

}