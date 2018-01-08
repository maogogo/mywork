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
  FROM ${tabName} 
  ${_filtering}
  ${_grouping} ${_having}
""".stripMargin.trim
  }

  def optionSeqIsEmpty: PartialFunction[Option[Seq[String]], Boolean] = {
    case x ⇒ x.nonEmpty && x.map(_.filter(_.isEmpty)).getOrElse(Seq.empty).size > 0
  }

  def toLabel: PartialFunction[Property, String] = {
    case prop ⇒
      prop.propertyType match {
        case PropertyType.Selecting ⇒ toSelectingLabel(prop)
        case PropertyType.Grouping ⇒ toGroupingLabel(prop)
        case _ ⇒ ""
      }
  }

  private[this] def toGroupingLabel: PartialFunction[Property, String] = {
    case prop ⇒ if (prop.cellLabel.nonEmpty) prop.cellLabel else prop.cellColumn
  }

  private[this] def toSelectingLabel: PartialFunction[Property, String] = {
    case prop ⇒
      val label = toGroupingLabel(prop)
      prop.parentId match {
        case Some(pid) if pid.nonEmpty ⇒ s"${prop.cellLabel}_${pid}"
        case _ ⇒ label
      }
  }

  def filterCommonGrouping: PartialFunction[Property, Boolean] = {
    case prop ⇒ prop.propertyType == PropertyType.Grouping && prop.isSpecial
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

  def toAggregateColumn(prefix: Option[String] = None): PartialFunction[Property, String] = {
    case prop if prop.propertyType == PropertyType.Selecting ⇒
      val label = SqlTemplate.toLabel(prop)
      s"SUM(${label}) AS '${label}'"
    //TODO(Toan) 这里的grouping有问题
    case prop if prop.propertyType == PropertyType.Grouping ⇒ SqlTemplate.toLabel(prop)
    //case prop if prop.propertyType == PropertyType.CommonGrouping ⇒ s"${prefix.getOrElse("")}${SqlTemplate.toLabel(prop)}"
    //case prop if prop.propertyType == PropertyType.Combining ⇒ s"0 as '${SqlTemplate.toLabel(prop)}'"
    case _ ⇒ ""
  }

  def toAggregateListingColumn: PartialFunction[Property, String] = {
    case prop ⇒
      prop.propertyType match {
        case PropertyType.Selecting ⇒
          val method = prop.aggregationMethod match {
            case Some(m) if !m.isEmpty ⇒ m.toUpperCase
            case _ ⇒ "SUM"
          }
          val label = SqlTemplate.toLabel(prop)
          s"${method}(${label}) AS '${label}'"
        case PropertyType.Grouping ⇒ prop.cellLabel
        case _ ⇒ ""
      }
  }

  /**
   * 主要是清单字段(维度+指标+过滤)
   */
  def toListingColumn: PartialFunction[Property, String] = {
    case prop ⇒
      prop.propertyType match {
        case PropertyType.Selecting ⇒ toSelectingListingColumn(prop)
        case PropertyType.Grouping ⇒ toGroupingListingColumn(prop)
        case _ ⇒ ""
      }
  }

  private[this] def toSelectingListingColumn: PartialFunction[Property, String] = {
    case prop ⇒
      val label = toLabel(prop)
      prop.cellFiltering match {
        case Some(filtering) if filtering.nonEmpty ⇒
          val cellValue = prop.cellValue match {
            case Some(v) if v.nonEmpty ⇒ v
            case _ ⇒ "1"
          }
          s"(CASE WHEN ${filtering} THEN ${cellValue} ELSE 0 END) AS '${label}'"
        case _ ⇒
          if (prop.cellColumn == label) label else s"${prop.cellColumn} AS '${label}'"
      }
  }

  private[this] def toGroupingListingColumn: PartialFunction[Property, String] = {
    case prop ⇒
      val label = toLabel(prop)
      prop.propertyExpressions match {
        case Some(expressions) if expressions.nonEmpty ⇒
          val whenEx = expressions.foldLeft("") { (s, e) ⇒
            s"${s} WHEN ${e.cellExpression} THEN '${e.label}'"
          }
          s"(CASE ${whenEx} ELSE '' END) AS '${label}'"
        case _ ⇒ if (prop.cellColumn == label) label else s"${prop.cellColumn} AS '${label}'"
      }
  }

}

object SqlTemplate extends SqlTemplate