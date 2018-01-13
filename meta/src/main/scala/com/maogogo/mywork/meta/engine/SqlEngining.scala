package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

trait SqlEngining {

  val builder: SqlEngineBuilder
  def packing: Seq[QueryReq]

}

class SqlTable(table: Table, selectings: Seq[Property], groupings: Seq[Property],
  filterings: Seq[Property], group: Option[PropertyGroup]) {

  lazy val tableName = table.tableName + group.flatMap(_.tableEx).getOrElse("")

  lazy val allFilters = groupings ++ filterings

  implicit def seq2String(strs: Seq[String]): String = strs.distinct.filterNot(_.isEmpty).mkString(", ")

  def createListingSql: String = {
    SqlTemplate(tableName, getListingColumn, getListingAdapter)
  }

  /**
   * 带有聚合函数的listing
   */
  def createAggregateListingSql: String = {
    val tempSql = s"(${createListingSql}) A"
    //需要分组处理
    hasGroupingColumns match {
      case true ⇒
        val aggregateUniqueSql = s"(${SqlTemplate(tempSql, getAggregateListingColumn(), getAggregateAdaper, getAggregateGroup(), getHaving)}) B"
        hasUniqueColumns match {
          case true ⇒
            //需要去重处理
            s"(${SqlTemplate(aggregateUniqueSql, getAggregateListingColumn(false), None, getAggregateGroup(false), None)}) C"
          case _ ⇒ aggregateUniqueSql
        }
      case _ ⇒ tempSql
    }

  }

  private[this] def getAggregateGroup(isUnique: Boolean = true): Option[String] = {
    Option((groupings.map(SqlTemplate.toAggregateListingColumn) ++ getOtherColumns(isUnique)))
  }

  private[this] def getHaving: Option[String] = {
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
  private[this] def getAggregateListingColumn(isUnique: Boolean = true): String = {
    (groupings ++ selectings).map(SqlTemplate.toAggregateListingColumn) ++ getOtherColumns(isUnique)
  }

  /**
   * 最底层的清单字段(这里必须去重)
   */
  private[this] def getListingColumn: String = {
    //维度 + 指标 + 过滤条件
    (groupings ++ selectings ++ filterings).map(SqlTemplate.toListingColumn) ++ getOtherColumns() ++
      Seq(group.flatMap(_.propertyHaving.map(_.hfilterColumn)).getOrElse("")).distinct
  }

  /**
   * 加入额外的字段
   * 第一层:grouping_columns + unique_columns
   * 第二层:grouping_columns
   */
  private[this] def getOtherColumns(isUnique: Boolean = true): Seq[String] = {
    group.map { g ⇒
      g.groupingColumns.getOrElse(Seq.empty) ++
        (if (isUnique) g.uniqueColumns.getOrElse(Seq.empty) else Seq.empty)
    } getOrElse (Seq.empty)
  }

  def getAllParams: Option[Seq[String]] = Option(getListingParams ++ getAggregateParams)

  def getListingParams: Seq[String] =
    allFilters.filter(SqlTemplate.filterListingAdaper).map(_.values).flatten.flatten

  def getAggregateParams: Seq[String] =
    allFilters.filter(SqlTemplate.filterAggregateAdaper).map(_.values).flatten.flatten

  def getListingAdapter: Option[String] = {
    Option(toAdapter(allFilters.filter(SqlTemplate.filterListingAdaper).map(_.cellFiltering.getOrElse(""))))
  }

  def getAggregateAdaper: Option[String] = {
    Option(toAdapter(allFilters.filter(SqlTemplate.filterAggregateAdaper).map(_.cellFiltering.getOrElse(""))))
  }

  def toAdapter: PartialFunction[Seq[String], String] = {
    case adapters ⇒ adapters.distinct.filterNot(_.isEmpty).mkString(" AND ")
  }

  def hasGroupingColumns: Boolean =
    group.map(g ⇒ SqlTemplate.optionSeqIsEmpty(g.groupingColumns)).getOrElse(false)

  def hasUniqueColumns: Boolean =
    group.map(g ⇒ SqlTemplate.optionSeqIsEmpty(g.uniqueColumns)).getOrElse(false)

}

object SqlTable {

  def apply(table: Table, selectings: Seq[Property], groupings: Seq[Property], filterings: Seq[Property],
    group: Option[PropertyGroup]) = new SqlTable(table, selectings, groupings, filterings, group)

  def apply(table: Table, groupings: Seq[Property], filterings: Seq[Property], group: Option[PropertyGroup]) =
    new SqlTable(table, Seq.empty, groupings, filterings, group)
}