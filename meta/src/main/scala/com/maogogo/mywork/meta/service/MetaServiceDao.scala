package com.maogogo.mywork.meta.service

import javax.inject.{ Inject, Named }
import com.maogogo.mywork.common.modules.DataSourcePool
import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import com.twitter.util.Future
import com.maogogo.mywork.thrift.{ Row => TRow, _ }
import com.twitter.finagle.mysql.Row

class MetaServiceDao @Inject() (@Named("connections") val connections: Seq[DataSourcePool]) extends ConnectionBuilder {

  val shardId: Int = 0

  object DB {
    //维度指标
    val TProperty = "t_property"
    //表
    val TTable = "t_table"
    //表、维度和指标
    val TTableProperty = "t_table_property"
    //维度关系
    val TPropertyExpression = "t_property_expression"

    val TPropertyHaving = "t_property_having"
  }

  def optionStringToSeq(str: Option[String]): Option[Seq[String]] = str.map(_.split(",", -1).filter(_.nonEmpty))

  def findProperties: Future[Seq[Property]] = {

    for {
      properties <- build {
        _.prepare(s"select * from ${DB.TProperty} where record_state='1'")().map(resultSet { rowToProperty })
      }
      expressions <- build {
        _.prepare(s"select * from ${DB.TPropertyExpression}")().map(resultSet { rowToExpression })
      }
      havingFilters <- build {
        _.prepare(s"select * from ${DB.TPropertyHaving}")().map(resultSet { rowToHavingFilter })
      }
    } yield {
      val expressionMap = expressions.groupBy(_.propertyId)
      properties.map { prop =>
        val propertyHaving = havingFilters.find(_.id == prop.hfilterId.getOrElse(""))
        val propertyExpressions = expressionMap.get(prop.id)
        prop.copy(propertyHaving = propertyHaving, propertyExpressions = propertyExpressions)
      }
    }
  }

  def findTables: Future[Seq[Table]] = {
    val sql = s"select * from ${DB.TTable} where record_state='1'"
    build {
      _.prepare(sql)().map(resultSet { rowToTable })
    }
  }

  def findTableProperties: Future[Seq[TableProperties]] = {
    for {
      tables <- findTables
      properties <- findProperties
      tabProps <- build {
        _.prepare(s"select * from ${DB.TTableProperty}")().map(resultSet { rowToTablePropertyRelate })
      }
    } yield {
      tabProps.groupBy(_.tableId).toSeq.map {
        case (tableId, tablePropertyRelates) =>
          val tableOption = tables.find(_.id == tableId)

          if (tableOption.isEmpty)
            throw new ServiceException(s"can not Table by id : ${tableId}")

          val props = tablePropertyRelates.map { relate =>
            val propertyOption = properties.find(_.id == relate.propertyId)
            if (propertyOption.isEmpty)
              throw new ServiceException(s"can not Property by id : ${relate.propertyId} for Table[${tableId}]")
            propertyOption.get.copy(tableCellColumn = Option(relate.cellColumn), tableCellFiltering = Option(relate.cellFiltering))
          }
          TableProperties(tableOption.get, props)
      }
    }
  }

  def rowToTable(row: Row): Option[Table] =
    Option(Table(
      id = row("id").asString,
      label = row("label").asString,
      tableName = row("table_name").asString,
      dbSchema = row("db_schema").asOptionString,
      isListing = row("is_listing").asOptionBool.getOrElse(false)))

  def rowToProperty(row: Row): Option[Property] = {
    val cellType = row("cell_type").asOptionInt.getOrElse(9)
    Option(Property(
      id = row("id").asString,
      label = row("label").asString,
      propertyType = PropertyType(cellType),
      cellIndex = row("cell_index").asOptionInt.getOrElse(9999),
      cellColumn = row("cell_column").asString,
      cellLabel = row("cell_label").asString,
      cellFiltering = row("cell_filtering").asOptionString,
      selectingFiltering = row("selecting_filter").asOptionString,
      cellValue = row("cell_value").asOptionString,
      aggregationMethod = row("aggregation_method").asOptionString,
      groupingColumns = optionStringToSeq(row("grouping_columns").asOptionString),
      havingColumns = optionStringToSeq(row("having_columns").asOptionString),
      hfilterId = row("hfilter_id").asOptionString,
      propertyHaving = None,
      valueDisplayFormat = row("value_display_format").asOptionString,
      formulaScript = row("formula_script").asOptionString,
      relateIds = optionStringToSeq(row("relate_ids").asOptionString),
      tableEx = row("table_ex").asOptionString))
  }

  def rowToExpression(row: Row): Option[PropertyExpression] =
    Option(PropertyExpression(
      id = row("id").asString,
      propertyId = row("property_id").asString,
      label = row("label").asString,
      cellExpression = row("cell_expression").asString))

  def rowToHavingFilter(row: Row): Option[PropertyHaving] =
    Option(PropertyHaving(id = row("id").asString, label = row("label").asString,
      hfilterColumn = row("hfilter_column").asString,
      hfilterMethod = row("hfilter_method").asString,
      hfilterSymbol = row("hfilter_symbol").asString,
      hfilterValue = row("hfilter_value").asString))

  def rowToTablePropertyRelate(row: Row): Option[TablePropertyRelate] =
    Option(TablePropertyRelate(
      tableId = row("table_id").asString,
      propertyId = row("property_id").asString,
      cellColumn = row("cell_column").asString,
      cellFiltering = row("cell_filtering").asString))
}

case class TablePropertyRelate(tableId: String, propertyId: String, cellColumn: String, cellFiltering: String)
