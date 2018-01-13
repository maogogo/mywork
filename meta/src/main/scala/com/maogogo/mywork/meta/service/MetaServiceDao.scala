package com.maogogo.mywork.meta.service

import javax.inject.{ Inject, Named }
import com.maogogo.mywork.common.modules.DataSourcePool
import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import com.twitter.util.Future
import com.maogogo.mywork.thrift.{ Row ⇒ TRow, _ }
import com.twitter.finagle.mysql.Row
import com.google.inject.{ Provides, Singleton }
import com.maogogo.mywork.common.jdbc._

@Provides @Singleton
class MetaServiceDao @Inject() (implicit builder: ConnectionBuilder) {

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

  def optionStringToSeq(str: Option[String]): Option[Seq[String]] = str.map(_.split(",").map(_.trim).filter(_.nonEmpty))

  def findProperties: Future[Seq[Property]] = {

    //    for {
    //      properties ← SQL {
    //        _.prepare(s"select * from ${DB.TProperty}")().map(resultSet { rowToProperty })
    //      }
    //      expressions ← SQL {
    //        _.prepare(s"select * from ${DB.TPropertyExpression}")().map(resultSet { rowToExpression })
    //      }
    //      havingFilters ← SQL {
    //        _.prepare(s"select * from ${DB.TPropertyHaving}")().map(resultSet { rowToHavingFilter })
    //      }
    //    } yield {
    //      val expressionMap = expressions.groupBy(_.propertyId)
    //
    //      properties.map { prop ⇒
    //        val propertyHaving = havingFilters.find(_.id == prop.propertyGroup.propertyHaving.map(_.id).getOrElse(""))
    //        val propGroup = prop.propertyGroup.copy(propertyHaving = propertyHaving)
    //        val propertyExpressions = expressionMap.get(prop.id)
    //        prop.copy(propertyExpressions = propertyExpressions, propertyGroup = propGroup)
    //      }
    //    }

    ???

  }

  def findTables: Future[Seq[Table]] = {
    val sql = s"select * from ${DB.TTable} where record_state='1'"
    
    val d = SQL.list(sql) { r =>
      val ThriftAnyVal.StringVal(s) = r.cells(0).cellValue
      Option(s)
    }
    //    SQL {
    //      _.prepare(sql)().map(resultSet { rowToTable })
    //    }

    ???
  }

  def findTableProperties: Future[Seq[TableProperties]] = {
    //    for {
    //      tables ← findTables
    //      properties ← findProperties
    //      tabProps ← SQL {
    //        _.prepare(s"select * from ${DB.TTableProperty}")().map(resultSet { rowToTablePropertyRelate })
    //      }
    //    } yield {
    //      tabProps.groupBy(_.tableId).toSeq.map {
    //        case (tableId, tablePropertyRelates) ⇒
    //          val tableOption = tables.find(_.id == tableId)
    //
    //          if (tableOption.isEmpty)
    //            throw new ServiceException(s"can not Table by id : ${tableId}")
    //
    //          val props = tablePropertyRelates.map { relate ⇒
    //            val propertyOption = properties.find(_.id == relate.propertyId)
    //            if (propertyOption.isEmpty)
    //              throw new ServiceException(s"can not Property by id : ${relate.propertyId} for Table[${tableId}]")
    //
    //            val cellColumn = relate.cellColumn match {
    //              case Some(c) if c.nonEmpty ⇒ c
    //              case _ ⇒ propertyOption.get.cellColumn
    //            }
    //
    //            val cellFiltering = relate.cellFiltering match {
    //              case Some(f) if f.nonEmpty ⇒ Option(f)
    //              case _ ⇒ propertyOption.get.cellFiltering
    //            }
    //            //这里植入特有的配置(这块可以没有)
    //            propertyOption.get.copy(cellColumn = cellColumn, cellFiltering = cellFiltering)
    //          }
    //          TableProperties(tableOption.get, props)
    //      }
    //    }
    ???
  }

  //  def rowToTable(row: Row): Option[Table] =
  //    Option(Table(
  //      id = row("id").asString,
  //      label = row("label").asString,
  //      tableName = row("table_name").asString,
  //      dbSchema = row("db_schema").asOptionString,
  //      isListing = row("is_listing").asOptionBool.getOrElse(false)))
  //
  //  def rowToProperty(row: Row): Option[Property] = {
  //    val cellType = row("cell_type").asInt match {
  //      case 1 ⇒ PropertyType.Grouping
  //      case 2 ⇒ PropertyType.Selecting
  //      case _ ⇒ PropertyType.Unknown
  //    }
  //
  //    val propGroup = PropertyGroup(
  //      tableEx = row("table_ex").asOptionString,
  //      selectingFiltering = row("selecting_filtering").asOptionString,
  //      groupingColumns = optionStringToSeq(row("grouping_columns").asOptionString),
  //      uniqueColumns = optionStringToSeq(row("unique_columns").asOptionString),
  //      propertyHaving = Option(PropertyHaving(id = row("hfilter_id").asString, label = "",
  //        hfilterColumn = "", hfilterMethod = "", hfilterSymbol = "", hfilterValue = "")))
  //
  //    Option(Property(
  //      id = row("id").asString,
  //      label = row("label").asString,
  //      propertyType = cellType,
  //      isSpecial = row("is_special").asBool,
  //      cellIndex = row("cell_index").asOptionInt.getOrElse(9999),
  //      cellColumn = row("cell_column").asString,
  //      cellLabel = row("cell_label").asString,
  //      cellFiltering = row("cell_filtering").asOptionString,
  //      cellValue = row("cell_value").asOptionString,
  //      aggregationMethod = row("aggregation_method").asOptionString,
  //      valueDisplayFormat = row("value_display_format").asOptionString,
  //      formulaScript = row("formula_script").asOptionString,
  //      relateIds = optionStringToSeq(row("relate_ids").asOptionString),
  //      propertyGroup = propGroup))
  //    //tableEx = row("table_ex").asOptionString))
  //  }
  //
  //  def rowToExpression(row: Row): Option[PropertyExpression] =
  //    Option(PropertyExpression(
  //      id = row("id").asString,
  //      propertyId = row("property_id").asString,
  //      label = row("label").asString,
  //      cellExpression = row("cell_expression").asString))
  //
  //  def rowToHavingFilter(row: Row): Option[PropertyHaving] =
  //    Option(PropertyHaving(id = row("id").asString, label = row("label").asString,
  //      hfilterColumn = row("hfilter_column").asString,
  //      hfilterMethod = row("hfilter_method").asString,
  //      hfilterSymbol = row("hfilter_symbol").asString,
  //      hfilterValue = row("hfilter_value").asString))
  //
  //  def rowToTablePropertyRelate(row: Row): Option[TablePropertyRelate] =
  //    Option(TablePropertyRelate(
  //      tableId = row("table_id").asString,
  //      propertyId = row("property_id").asString,
  //      cellColumn = row("cell_column").asOptionString,
  //      cellFiltering = row("cell_filtering").asOptionString))
}

case class TablePropertyRelate(tableId: String, propertyId: String, cellColumn: Option[String], cellFiltering: Option[String])
