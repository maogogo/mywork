//package com.maogogo.mywork.meta.engine
//
//import com.maogogo.mywork.thrift._
//
//class SimpleSqlEngining extends SqlEngining {
//
//  def packing(tableProperty: TableProperty, req: RootQueryReq): Seq[QuerySql] = {
//
//    val grouping = toSeqProperty(req.grouping, tableProperty.properties)
//    val selecting = toSeqProperty(req.selecting, tableProperty.properties)
//    val filtering = toSeqProperty(req.filtering, tableProperty.properties)
//
//    val relateSelecting = selecting.map(_.filter(_.propertyType == PropertyType.Combining).flatMap { p =>
//      p.relateIds.map {
//        _.split(",").map { id =>
//          toProperty(Some(p.id))(PropertyBinding(id), tableProperty.properties)
//        }
//      }
//    } flatten)
//
//    if (selecting.isEmpty)
//      throw new ServiceException("query has no selectings", Some(ErrorCode.MetaError))
//
//    toSimpleSql(tableProperty.table.dbTableName, grouping, (selecting.get ++ relateSelecting.getOrElse(Seq.empty)), filtering)
//  }
//
//  def toSimpleSql(tableName: String, grouping: Option[Seq[Property]], selecting: Seq[Property], filtering: Option[Seq[Property]]): Seq[QuerySql] = {
//
//    //指标分组
//    val selectingGroup = getSelectingGroup(selecting)
//    //表头
//    val headers = getCellHeader(grouping, selecting)
//
//    selectingGroup.map { gs =>
//      //GroupBy 字段
//      val groupColumn = toGroupingColumn(grouping).getOrElse(Seq.empty)
//      //两层SQL语句
//      val isThirdLevel = gs.uniqueColumns match {
//        case Some(h) if h.size > 0 => true
//        case _ => true
//      }
//      //指标字段标签
//      val selectingLabel1 = isThirdLevel match {
//        case false => toSelectingLabel(gs.selecting) //max
//        case _ => toSelectingColumn(!isThirdLevel)(gs.selecting)
//      }
//
//      //第一层维度字段及指标聚合函数
//      val columns1 = toMakeColumn("", groupColumn,
//        gs.uniqueColumns.getOrElse(Seq.empty),
//        gs.levelColumns.getOrElse(Seq.empty),
//        selectingLabel1).getOrElse("")
//
//      val selectingLabel2 = toSelectingColumn(!isThirdLevel)(gs.selecting)
//
//      //第二层维度字段及指标聚合函数
//      val columns2 = toMakeColumn("", groupColumn,
//        gs.levelColumns.getOrElse(Seq.empty),
//        selectingLabel2).getOrElse("")
//      //第三层维度字段及指标聚合函数
//      val columns3 = toMakeColumn("", groupColumn, toSelectingColumn(isThirdLevel)(gs.selecting)).getOrElse("")
//
//      //第一层分组字段
//      val grouping1 = isThirdLevel match {
//        case false => toMakeColumn("GROUP BY ", groupColumn, gs.uniqueColumns.getOrElse(Seq.empty), gs.levelColumns.getOrElse(Seq.empty))
//        case _ => None
//      }
//
//      //第二层分组字段
//      val grouping2 = toMakeColumn("GROUP BY ", groupColumn, gs.levelColumns.getOrElse(Seq.empty))
//      //第三层分组字段
//      val grouping3 = toMakeColumn("GROUP BY ", groupColumn)
//
//      val filterResp = getFilteringResp(grouping, selecting, filtering)
//
//      val level1SQL = sqlTemplate(s"${tableName}${gs.tableEx}", columns1, filterResp.filtering1, grouping1)
//
//      val level2SQL = isThirdLevel match {
//        case true =>
//          val havingFilter = gs.havingFilters.getOrElse("").split(",") match {
//            case seq if seq.size > 0 => Some(seq.mkString("HAVING ", " AND ", ""))
//            case _ => None
//          }
//          val _level2SQL = sqlTemplate(s"(${level1SQL}) A", columns2, filterResp.filtering2, grouping2, havingFilter)
//          sqlTemplate(s"(${_level2SQL}) B", columns3, None, grouping3)
//        case _ =>
//          sqlTemplate(s"(${level1SQL}) A", columns2, filterResp.filtering2, grouping2)
//
//      }
//
//      QuerySql(level2SQL, countSql = None, params = filterResp.params, groupingColumns = 0, headers)
//    }
//  }
//
//  /**
//   * 组合字段
//   */
//  def toMakeColumn(prefix: String, s: Seq[String]*): Option[String] = {
//    s.reduceLeft(_ ++ _).filterNot(_.isEmpty).distinct.filterNot(_.isEmpty) match {
//      case seq if seq.size > 0 => Some(prefix + seq.mkString(", "))
//      case _ => None
//    }
//  }
//
//  /**
//   * 组合过滤条件
//   */
//  def toMakeFilter(s: Seq[String]*): Option[String] = {
//    s.reduceLeft(_ ++ _).filterNot(_.isEmpty).distinct.filterNot(_.isEmpty) match {
//      case seq if seq.size > 0 => Some(seq.mkString("WHERE ", " AND ", ""))
//      case _ => None
//    }
//  }
//
//  /**
//   * 维度字段
//   */
//  def toGroupingColumn: PartialFunction[Option[Seq[Property]], Option[Seq[String]]] = {
//    case propertyOption => propertyOption.map(_.map(_.cellColumn))
//  }
//
//  /**
//   * 指标字段(第一层)
//   */
//  def toSelectingLabel: PartialFunction[Seq[Property], Seq[String]] = {
//    case properties =>
//      properties.map { p =>
//        val label = toCellLabel(p) match {
//          case s if !s.isEmpty => s" AS $s"
//          case _ => ""
//        }
//        s"${p.cellColumn}${label}"
//      } filter (!_.isEmpty)
//  }
//
//  /**
//   * 指标查询字段(带有聚合函数)(第二层)
//   * isAgg = true 第二层 或是 第三层
//   */
//  def toSelectingColumn(isAgg: Boolean = false): PartialFunction[Seq[Property], Seq[String]] = {
//    case properties =>
//      properties.map { p =>
//        p.support.map(_.inUsing) match {
//          case Some(true) =>
//            val label = toCellLabel(p)
//            isAgg match {
//              case true => s"SUM(${label}) AS ${label}"
//              case _ => s"${toSelectingMethod(p)}(${label}) as ${label}"
//            }
//
//          case _ => s"0"
//        }
//      } filter (!_.isEmpty)
//  }
//
//  def toCellLabel: PartialFunction[Property, String] = {
//    case p =>
//      p.support.flatMap(_.parentId) match {
//        case Some(s) => s"${p.cellLabel}_${s}"
//        case _ => p.cellLabel
//      }
//  }
//
//  def toSelectingMethod: PartialFunction[Property, String] = {
//    case p =>
//      p.aggregationMethod match {
//        case Some(m) if !m.isEmpty => m.toUpperCase
//        case _ => "SUM"
//      }
//  }
//
//  def getCellHeader(grouping: Option[Seq[Property]], selecting: Seq[Property]): Seq[CellHeader] = {
//
//    val groupHeaders = grouping.map(toCellHeader).getOrElse(Seq.empty)
//    val selectHeaders = toCellHeader(selecting)
//
//    groupHeaders ++ selectHeaders
//  }
//
//  /**
//   * 组合filter条件
//   */
//  def getFilteringResp(grouping: Option[Seq[Property]], selecting: Seq[Property], filtering: Option[Seq[Property]]): FilteringResp = {
//
//    // 维度条件
//    val groupFiltering = grouping.map(_.map(toCellFilter))
//
//    //指标条件
//    val selectFiltering = selecting.map(toCellFilter)
//
//    //过滤条件
//    val filterFiltering = filtering.map(_.map(toCellFilter))
//
//    //第一层过滤条件不能包括"C_"开头的
//    val select1 = selectFiltering.filterNot { _._1.startsWith(QueryPrefix) }
//    //第二层过滤条件只能是"C_"开头的
//    val select2 = selectFiltering.filter { _._1.startsWith(QueryPrefix) }
//
//    val filter1 = filterFiltering.map { _.filterNot { _._1.startsWith(QueryPrefix) } }
//    val filter2 = filterFiltering.map { _.filter { _._1.startsWith(QueryPrefix) } }
//
//    val groupParams = groupFiltering.map(_.flatMap(_._2).flatten)
//    val selectParams1 = filter1.map(_.flatMap(_._2).flatten)
//    val selectParams2 = filter2.map(_.flatMap(_._2).flatten)
//    val filterParams1 = filter1.map(_.flatMap(_._2).flatten)
//    val filterParams2 = filter2.map(_.flatMap(_._2).flatten)
//
//    val filtering1 = toMakeFilter(groupFiltering.map(_.map(_._1)).getOrElse(Seq.empty), select1.map(_._1), filter1.map(_.map(_._1)).getOrElse(Seq.empty))
//
//    val filtering2 = toMakeFilter(select2.map(_._1), filter2.map(_.map(_._1)).getOrElse(Seq.empty))
//
//    val params = groupParams.getOrElse(Seq.empty) ++ selectParams1.getOrElse(Seq.empty) ++
//      filterParams1.getOrElse(Seq.empty) ++ selectParams2.getOrElse(Seq.empty) ++ filterParams2.getOrElse(Seq.empty)
//
//    FilteringResp(filtering1, filtering2, Some(params))
//  }
//
//  /**
//   * 指标分组
//   */
//  def getSelectingGroup: PartialFunction[Seq[Property], Seq[GroupSelecting]] = {
//    case properties =>
//
//      properties.filterNot(_.propertyType == PropertyType.Combining).groupBy { p =>
//        s"${p.tableEx.getOrElse("")}#${p.uniqueColumns.getOrElse("")}#${p.levelColumns.getOrElse("")}#${p.havingFilters.getOrElse("")}#${p.cellFilters.getOrElse("")}"
//      }.map { x =>
//        //重新标记维度和指标
//        val selecing = properties.map { p =>
//          val inUsing = x._2.find(s => s.id == p.id).isDefined
//          val support = p.support match {
//            case Some(s) => s.copy(inUsing = inUsing)
//            case _ => PropertySupport(inUsing = inUsing)
//          }
//
//          p.copy(support = Some(support))
//        }
//
//        val splits = x._1.split("#", -1)
//
//        GroupSelecting(splits(0), Some(splits(1).split(",").filterNot(_.isEmpty)), Some(splits(2).split(",").filterNot(_.isEmpty)), Some(splits(3)), selecing)
//      } toSeq
//  }
//
//}
//
//case class GroupSelecting(tableEx: String, uniqueColumns: Option[Seq[String]], levelColumns: Option[Seq[String]], havingFilters: Option[String], selecting: Seq[Property])
//case class FilteringResp(filtering1: Option[String], filtering2: Option[String], params: Option[Seq[String]])
//case class GroupingAndSelectingResp(grouping1: String, grouping2: String, grouping3: Option[String], selecting1: String, selecting2: String, selecting3: Option[String])
