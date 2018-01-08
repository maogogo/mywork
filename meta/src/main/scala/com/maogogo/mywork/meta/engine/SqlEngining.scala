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
    val tempSql = s"(${createListingSql}) A"

    if (hasGroupingColumns) {
      println("11111111111")
      val aggregateUniqueSql = s"(${SqlTemplate(tempSql, getAggregateListingColumn(), getAggregateAdaper, getAggregateGroup(), getHaving)}) B"
      hasUniqueColumns match {
        case true ⇒
          println("2222222222222")
          s"(${SqlTemplate(aggregateUniqueSql, getAggregateListingColumn(false), None, getAggregateGroup(false), None)}) C"
        case _ ⇒
          println("3333333")
          aggregateUniqueSql
      }
    } else {
      println("44444444444")
      tempSql
    }
  }

  //  def getGroupingColumn: String = {
  //    groupings.map(SqlTemplate.toAggregateColumn(None))
  //  }

  //  def getAggregateColumn: String = {
  //    (groupings ++ selectings).map(SqlTemplate.toAggregateColumn(None))
  //  }

  def getAggregateGroup(isUnique: Boolean = true): Option[String] = {
    Option((groupings.map(SqlTemplate.toAggregateListingColumn) ++ getOtherColumns(isUnique)))
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
    val commonProps = groupings.filter(SqlTemplate.filterCommonGrouping)
    if (commonProps.size == 0) throw new ServiceException(s"can not found common grouping")
    commonProps.map { prop ⇒
      s"${SqlTemplate.asName(index - 1)}.${prop.cellLabel}=${SqlTemplate.asName(index)}.${prop.cellLabel}"
    } mkString (" AND ")
  }

  /**
   * 聚合清单字段
   */
  def getAggregateListingColumn(isUnique: Boolean = true): String = {
    (groupings ++ selectings).map(SqlTemplate.toAggregateListingColumn) ++ getOtherColumns(isUnique)
  }

  /**
   * 最底层的清单字段
   */
  def getListingColumn: String = {
    (groupings ++ selectings ++ filterings).map(SqlTemplate.toListingColumn) ++ getOtherColumns() ++
      Seq(group.flatMap(_.propertyHaving.map(_.hfilterColumn)).getOrElse(""))
  }

  def getOtherColumns(isUnique: Boolean = true): Seq[String] = {
    group.map { g ⇒
      g.groupingColumns.getOrElse(Seq.empty) ++
        (if (isUnique) g.uniqueColumns.getOrElse(Seq.empty) else Seq.empty)
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