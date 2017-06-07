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

class MetaServiceDao @Inject() (@Named("connections") val connections: Seq[DataSourcePool]) extends ConnectionBuilder {

  val shardId: Int = 0

  def findTableProperties(): Future[Seq[(String, String)]] = {
    val sql = s"select * from t_table_property"
    println("=>>" + sql)
    build {
      _.prepare(sql)().map { resultSet { rowToTableProperty } }
    }
  }

  def findProperties: Future[Seq[Property]] = {
    val sql = s"select * from t_property"
    println("=>>" + sql)
    build {
      _.prepare(sql)().map { resultSet { rowToProperty } }
    }
  }

  /**
   * 查询table
   */
  def findTables: Future[Seq[Table]] = {
    val sql = s"select * from t_tables"
    println("=>>" + sql)
    build {
      _.prepare(sql)().map { resultSet { rowToTable } }
    }
  }

  private[this] def rowToTableProperty(row: Row): Option[(String, String)] =
    Some((row("table_id").asString, row("property_id").asString))

  private[this] def rowToProperty(row: Row): Option[Property] = {
    val cellType = PropertyType(row("cell_type").asInt)

    Some(Property(
      id = row("id").asString,
      label = row("label").asString,
      propertyType = cellType,
      cellColumn = row("cell_column").asString,
      cellLabel = row("cell_label").asString,
      cellIndex = row("cell_index").asOptionInt.getOrElse(1000),
      cellExpression = row("cell_expression").asOptionString,
      aggregationMethod = row("aggregation_method").asOptionString,
      cellFilters = row("cell_filters").asOptionString,
      havingFilters = row("having_filters").asOptionString,
      valueDisplayFormat = row("value_display_format").asOptionString,
      valueDisplayKey = row("value_display_key").asOptionString,
      tableEx = row("table_ex").asOptionString,
      uniqueColumns = row("unique_columns").asOptionString,
      levelColumns = row("level_columns").asOptionString,
      isFixedShow = row("is_fixed_show").asOptionBool.getOrElse(false),
      formulaScript = row("formula_script").asOptionString,
      relateIds = row("relate_ids").asOptionString
    ))

  }

  private[this] def rowToTable(row: Row): Option[Table] =
    Some(Table(
      id = row("id").asString,
      dbTableName = row("db_table_name").asString,
      isListing = row("is_listing").asOptionBool,
      dbSchema = row("db_schema").asOptionString, forceIndex = None
    ))

}