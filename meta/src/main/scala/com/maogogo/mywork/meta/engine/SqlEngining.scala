package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._
import org.slf4j.LoggerFactory

trait SqlEngining {

  lazy val QueryPrefix = "C_"

  lazy val Log = LoggerFactory.getLogger(getClass)

  def packing(tableProperty: TableProperty, req: RootQueryReq): Seq[QuerySql]

  def sqlTemplate(tableName: String, selecting: String, filtering: Option[String] = None,
    grouping: Option[String] = None, having: Option[String] = None) = {
    s"""
  SELECT ${selecting} 
  FROM ${tableName}
  ${filtering.getOrElse("")}
  ${grouping.getOrElse("")}
  ${having.getOrElse("")}
"""
  }

  def toProperty: PartialFunction[(Option[Seq[PropertyBinding]], Seq[Property]), Option[Seq[Property]]] = {
    case (bindings, properties) =>
      bindings.map {
        _.map { bind =>
          val propertyOption = properties.find(_.id == bind.propertyId)
          if (propertyOption.isEmpty) {
            Log.error(s"can not found property for table by${bind.propertyId}")
            throw new ServiceException(s"can not found property for table by ${bind.propertyId}", Some(ErrorCode.MetaError))
          }
          propertyOption.get.copy(support = Some(PropertySupport(values = bind.propertyValues)))
        }
      }
  }

  def toCellLabel: PartialFunction[Seq[Property], Seq[String]] = {
    case cells =>
      cells.sortBy(_.cellIndex).map { cell =>
        cell.cellExpression match {
          case Some(x) if x.isEmpty => s"${x} AS '${cell.cellLabel}'"
          case _ => s"${cell.cellColumn}"
        }
      }
  }

  def toCellHeader: PartialFunction[Seq[Property], Seq[CellHeader]] = {
    case properties if properties.size > 0 =>
      properties.map { p =>
        CellHeader(label = p.label, parentLabel = None, cellIndex = p.cellIndex, rowSpan = None, colSpan = None, parentRowSpan = None, parentColSpan = None)
      }
    case _ => Seq.empty
  }

  def toCellFilter: PartialFunction[Property, (String, Option[Seq[String]])] = {
    case x =>
      val values = x.support match {
        case Some(s) if s.values.isDefined => s.values
        case _ => None
      }

      x.cellFilters match {
        case Some(s) if s.endsWith("in") || s.endsWith("IN") =>
          val d = values match {
            case Some(seq) if seq.size > 0 => seq.map { x => "?" }.mkString("(", ", ", ")")
            case _ => "''"
          }

          (s"${s} ${d}", values)
        case Some(s) if !s.isEmpty => (s, values) //通过外接传值
        case _ => ("", None)
      }

  }

  def toParams: PartialFunction[Seq[PropertyBinding], Option[Seq[String]]] = {
    case bindings =>
      Some(bindings.filter(_.propertyValues.isDefined).map(_.propertyValues.get.mkString("\'", ", ", "\'")))
  }

}

case class ListSelectingAndFilteringResp(select1: String, select2: String, filter1: String, filter2: String, headers: Seq[CellHeader], params: Option[Seq[String]])