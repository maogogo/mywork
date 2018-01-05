package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

trait SqlEngining {

  val builder: SqlEngineBuilder
  def packing: Seq[QuerySql]

}

class SqlTable(table: Table, selectings: Seq[Property], groupings: Seq[Property],
  filterings: Seq[Property], group: Option[PropertyGroup]) {

  lazy val tableName = table.tableName + group.flatMap(_.tableEx).getOrElse("")

  lazy val allFilters = groupings ++ filterings

  implicit def seq2String(strs: Seq[String]): String = strs.distinct.filterNot(_.isEmpty).mkString(", ")

  def createListingSql: String = {
    SqlTemplate(tableName, getListingColumn, getListingAdapter)
  }

  def createAggregateListingSql: String = {
    val tempSql = createListingSql
    if (hasGroupingColumns) {
      val aggregateSql = SqlTemplate(tempSql, getAggregateListingColumn(), getAggregateAdaper, getAggregateGroup(), getHaving)
      hasUniqueColumns match {
        case true ⇒ SqlTemplate(aggregateSql, getAggregateListingColumn(false), getAggregateAdaper, getAggregateGroup(false), getHaving)
        case _ ⇒ aggregateSql
      }
    } else {
      tempSql
    }
  }

  def getGroupingColumn: String = {
    groupings.map(SqlTemplate.toAggregateColumn)
  }

  def getAggregateColumn: String = {
    (groupings ++ selectings).map(SqlTemplate.toAggregateColumn)
  }

  def getAggregateGroup(isHaving: Boolean = true): Option[String] = {
    Option((groupings.map(SqlTemplate.toAggregateListingColumn) ++ getOtherColumns(isHaving)))
  }

  def getHaving: Option[String] = {
    group.flatMap(_.propertyHaving.map { h ⇒
      s"${h.hfilterMethod.toUpperCase}(${h.hfilterColumn}) ${h.hfilterSymbol} ${h.hfilterValue}"
    })
  }

  /**
   * 清单的时候需要考虑 join
   */
  def createJoinOn(index: Int): String = {
    groupings.filter(SqlTemplate.filterCommonGrouping).map {
      case prop ⇒
        s"${SqlTemplate.asName(index)}.${prop.cellLabel}=${SqlTemplate.asName(index + 1)}.${prop.cellLabel}"
    } mkString (" AND ")
  }

  /**
   * 聚合清单字段
   */
  def getAggregateListingColumn(isHaving: Boolean = true): String = {
    (groupings ++ selectings).map(SqlTemplate.toAggregateListingColumn) ++ getOtherColumns(isHaving)
  }

  /**
   * 最底层的清单字段
   */
  def getListingColumn: String =
    (groupings ++ selectings ++ filterings).map(SqlTemplate.toListingColumn) ++ getOtherColumns()

  def getOtherColumns(isHaving: Boolean = true): Seq[String] = {
    group.map { g ⇒
      g.groupingColumns.getOrElse(Seq.empty) ++
        g.uniqueColumns.getOrElse(Seq.empty) ++
        (if (isHaving) Seq(g.propertyHaving.map(_.hfilterColumn).getOrElse("")) else Seq.empty)
    } getOrElse (Seq.empty)
  }

  def getListingParams: Seq[String] =
    allFilters.filter(SqlTemplate.filterListingAdaper).map(_.values).flatten.flatten

  def getAggregateParams: Seq[String] =
    allFilters.filter(SqlTemplate.filterAggregateAdaper).map(_.values).flatten.flatten

  def getListingAdapter: Option[String] = {
    Option(allFilters.filter(SqlTemplate.filterListingAdaper).map(_.cellFiltering.getOrElse("")).distinct.filterNot(_.isEmpty).mkString(" AND "))
  }

  def getAggregateAdaper: Option[String] = {
    Option(allFilters.filter(SqlTemplate.filterAggregateAdaper).map(_.cellFiltering.getOrElse("")).distinct.filterNot(_.isEmpty).mkString(" AND "))
  }

  def hasGroupingColumns: Boolean =
    group.map(g ⇒ g.groupingColumns.nonEmpty && g.groupingColumns.getOrElse(Seq.empty).size > 0).getOrElse(false)

  def hasUniqueColumns: Boolean =
    group.map(g ⇒ g.uniqueColumns.nonEmpty && g.uniqueColumns.getOrElse(Seq.empty).size > 0).getOrElse(false)

}

object SqlTable {

  def apply(table: Table, selectings: Seq[Property], groupings: Seq[Property], filterings: Seq[Property],
    group: Option[PropertyGroup]) = new SqlTable(table, selectings, groupings, filterings, group)

  def apply(table: Table, groupings: Seq[Property], filterings: Seq[Property], group: Option[PropertyGroup]) =
    new SqlTable(table, Seq.empty, groupings, filterings, group)
}