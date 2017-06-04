package com.maogogo.mywork.meta.dao

import com.maogogo.mywork.common._
import javax.inject.{ Inject, Named }
import com.maogogo.mywork.common.modules.DataSourcePool
import com.maogogo.mywork.common.dao._
import com.twitter.util.Future
import com.maogogo.mywork.thrift.{ Row => TRow, _ }
import com.twitter.finagle.mysql.Row
import com.maogogo.mywork.common.cache._
import com.twitter.scrooge.BinaryThriftStructSerializer

class MetaServiceDao @Inject() (@Named("connections") val connections: Seq[DataSourcePool]) extends BaseDao with ConnectionBuilder {

  val shardId: Int = 0

  def findTableProperties(): Future[Seq[(String, String)]] = {
    val sql = s"select * from t_table_property"
    build {
      _.prepare(sql)().map { resultSet { rowToTableProperty } }
    }
  }

  def findProperties: Future[Seq[Property]] = {
    val sql = s"select * from t_property"
    build {
      _.prepare(sql)().map { resultSet { rowToProperty } }
    }
  }

  /**
   * 查询table
   */
  def findTables: Future[Seq[Table]] = {
    val sql = s"select * from t_tables"
    build {
      _.prepare(sql)().map { resultSet { rowToTable } }
    }
  }

  private[this] def rowToTableProperty(row: Row): Option[(String, String)] =
    Some((row("table_id").asString, row("property_id").asString))

  private[this] def rowToProperty(row: Row): Option[Property] = {
    val id = row("id").asString
    val cell = PropertyCell(
      id = id,
      label = row("label").asString,
      cellColumn = row("cell_column").asString,
      cellLabel = row("cell_label").asString,
      propertyFilters = row("property_filters").asOptionString
    )
    row("cell_type").asInt match {
      case PropertyType.Selecting.value =>
        Some(Property.Selecting(PropertySelecting(
          propertyCell = cell,
          cellExpression = row("cell_exception").asOptionString,
          aggregationMethod = row("aggregation_method").asOptionString,
          valueDisplayFormat = row("value_display_format").asOptionString,
          tableEx = row("table_ex").asOptionString,
          uniqueColumns = row("unique_columns").asOptionString,
          isFixedShow = row("is_fixed_show").asBool,
          formulaScript = row("").asOptionString
        )))
      case PropertyType.Grouping.value =>
        Some(Property.Grouping(PropertyGrouping(cell, None)))
      case PropertyType.Filtering.value =>
        Some(Property.Filtering(PropertyFiltering(cell)))
      case PropertyType.Combining.value =>
        Some(Property.Combining(PropertyCombining(cell)))
      case _ =>
        throw new ServiceException(s"can not found [${id}] property type", Some(ErrorCode.MetaError))
    }

  }

  private[this] def rowToTable(row: Row): Option[Table] =
    Some(Table(
      id = row("id").asString,
      dbTableName = row("db_table_name").asString,
      isListing = row("is_listing").asOptionBool,
      dbSchema = row("db_schema").asOptionString, forceIndex = None
    ))

}