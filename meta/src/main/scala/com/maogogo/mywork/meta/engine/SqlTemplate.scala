package com.maogogo.mywork.meta.engine

import com.maogogo.mywork.thrift._

trait SqlTemplate {

  def asName(level: Int) = (65 + level).toChar

  def apply(tabName: String, listing: String, filtering: Option[String]): String = {
    this(tabName, listing, filtering, None, None)
  }

  def apply(tabName: String, listing: String, filtering: Option[String] = None,
    grouping: Option[String] = None, having: Option[String] = None): String = {

    val _filtering = filtering match {
      case Some(f) if !f.isEmpty ⇒ s"WHERE ${f}"
      case _ ⇒ ""
    }
    val _grouping = grouping match {
      case Some(g) if !g.isEmpty ⇒ s"GROUP BY ${g}"
      case _ ⇒ ""
    }
    val _having = having match {
      case Some(h) if !h.isEmpty ⇒ s"HAVING ${h}"
      case _ ⇒ ""
    }

    s"""
  SELECT ${listing} 
  FROM ${tabName} ${_filtering}
  ${_grouping} ${_having}
""".stripMargin.trim
  }

  def toLabel: PartialFunction[Property, String] = {
    case prop ⇒ toLabel(None)(prop)
  }

  def toLabel(prefix: Option[String] = None): PartialFunction[Property, String] = {
    case prop ⇒
      val label = prop.cellLabel
      prop.parentId match {
        case Some(pid) if pid.nonEmpty ⇒ s"${prop.cellLabel}_${pid}"
        case _ ⇒
          prefix match {
            case Some(p) if p.nonEmpty ⇒ s"${p}.${label}"
            case _ ⇒ label
          }
      }
  }

  def filterCommonGrouping: PartialFunction[Property, Boolean] = {
    case prop ⇒ prop.propertyType == PropertyType.CommonGrouping
  }

  def filterListingAdaper: PartialFunction[Property, Boolean] = {
    case prop ⇒
      (prop.propertyExpressions.isEmpty || prop.propertyExpressions.getOrElse(Seq.empty).size == 0) &&
        prop.cellFiltering.nonEmpty &&
        (prop.values.nonEmpty && prop.values.getOrElse(Seq.empty).size > 0)
  }

  def filterAggregateAdaper: PartialFunction[Property, Boolean] = {
    case prop ⇒
      (prop.propertyExpressions.nonEmpty && prop.propertyExpressions.getOrElse(Seq.empty).size > 0) &&
        prop.cellFiltering.nonEmpty &&
        (prop.values.nonEmpty && prop.values.getOrElse(Seq.empty).size > 0)
  }

  def toAggregateColumn: PartialFunction[Property, String] = {
    case prop if prop.propertyType == PropertyType.Selecting ⇒
      val label = SqlTemplate.toLabel(prop)
      s"SUM(${label}) AS '${label}'"
    case prop if prop.propertyType == PropertyType.Grouping ||
      prop.propertyType == PropertyType.CommonGrouping || prop.propertyType == PropertyType.Filtering ⇒
      prop.cellLabel
    case _ ⇒ "0"
  }

  /**
   *
   */
  //  def toAggregateUniqueColumn: PartialFunction[Property, String] = {
  //    case prop if prop.propertyType == PropertyType.Selecting ⇒
  //      val label = SqlTemplate.toLabel(prop)
  //      s"SUM(${label}) AS '${label}'"
  //    case prop if prop.propertyType == PropertyType.Grouping ||
  //      prop.propertyType == PropertyType.CommonGrouping || prop.propertyType == PropertyType.Filtering ⇒
  //      prop.cellLabel
  //    case _ ⇒ ""
  //  }

  def toAggregateListingColumn: PartialFunction[Property, String] = {
    case prop if prop.propertyType == PropertyType.Selecting ⇒
      val method = prop.aggregationMethod match {
        case Some(m) if !m.isEmpty ⇒ m.toUpperCase
        case _ ⇒ "SUM"
      }
      val label = SqlTemplate.toLabel(prop)
      s"${method}(${label}) AS '${label}'"
    case prop if prop.propertyType == PropertyType.Grouping ||
      prop.propertyType == PropertyType.CommonGrouping || prop.propertyType == PropertyType.Filtering ⇒
      prop.cellLabel
    case _ ⇒ ""
  }

  /**
   * 主要是清单字段
   */
  def toListingColumn: PartialFunction[Property, String] = {
    case prop if prop.propertyType == PropertyType.Selecting ⇒
      val label = toLabel(prop)
      prop.cellFiltering match {
        case Some(filtering) if filtering.nonEmpty ⇒
          val cellValue = prop.cellValue match {
            case Some(v) if v.nonEmpty ⇒ v
            case _ ⇒ "1"
          }
          s"(CASE WHEN ${filtering} THEN ${cellValue} ELSE 0 END)"
        case _ ⇒ if (prop.cellColumn == label) label else s"${prop.cellColumn} AS '${label}'"
      }
    case prop if prop.propertyType == PropertyType.Grouping ||
      prop.propertyType == PropertyType.CommonGrouping || prop.propertyType == PropertyType.Filtering ⇒
      val label = toLabel(prop)
      prop.propertyExpressions match {
        case Some(expressions) if expressions.nonEmpty ⇒
          val whenEx = expressions.foldLeft("") { (s, e) ⇒
            s"${s} WHEN ${e.cellExpression} THEN '${e.label}'"
          }
          s"(CASE ${whenEx} ELSE '' END) AS '${prop.cellLabel}'"
        case _ ⇒ if (prop.cellColumn == label) label else s"${prop.cellColumn} AS '${label}'"
      }
    case _ ⇒ ""
  }

}

object SqlTemplate extends SqlTemplate