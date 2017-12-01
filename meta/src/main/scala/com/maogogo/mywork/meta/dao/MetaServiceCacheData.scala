package com.maogogo.mywork.meta.dao

import com.maogogo.mywork.common._
import com.maogogo.mywork.common.cache._
import javax.inject.Inject
import com.twitter.scrooge.BinaryThriftStructSerializer
import com.maogogo.mywork.thrift._
import com.twitter.util.Future
import com.maogogo.mywork.common.utils.UUID
import com.maogogo.mywork.common.utils.DateUtil
import org.slf4j.LoggerFactory

class MetaServiceCacheData @Inject() (dao: MetaServiceDao) {

  lazy val Log = LoggerFactory.getLogger(getClass)
  lazy val TablePropertyKey = UUID.uuid()

  def findTableProperty(id: String): Future[TableProperty] = {

    //    for {
    //      tablePropertyDataOption <- caForTableProperty.getOrElse(TablePropertyKey) {
    //        findTablePropertyData.map(Some(_))
    //      }
    //      _ = if (tablePropertyDataOption.isEmpty) throw new ServiceException(s"can not found table by id ${id}", Some(ErrorCode.MetaError))
    //      tablePropertyData = tablePropertyDataOption.get
    //      //k <- caForTableProperty.put(TablePropertyKey, tablePropertyData, DateUtil.getDayLastTime)
    //      //_ = Log.info("put TablePropertyData to the Redis")
    //    } yield {
    //      val tableProperty = tablePropertyData.tableProperties.find(_.table.id == id)
    //      if (tableProperty.isEmpty)
    //        throw new ServiceException(s"can not found table by id ${id}", Some(ErrorCode.MetaError))
    //      tableProperty.get
    //    }

    ???
  }

  def findTablePropertyData: Future[TablePropertyData] = {

    for {
      tableProperties <- dao.findTableProperties
      tables <- dao.findTables
      properties <- dao.findProperties
    } yield {

      val resp = tableProperties.groupBy(_._1).map { kv =>
        val tableOption = tables.find(_.id == kv._1)
        if (tableOption.isEmpty)
          throw new ServiceException(s"can not found table by id ${kv._1}", Some(ErrorCode.MetaError))
        val propertyIds = kv._2.map(_._2)
        val propertySeq = properties.filter(x => propertyIds.contains(x.id))

        TableProperty(tableOption.get, propertySeq)
      } toSeq

      TablePropertyData(resp)
    }

  }

  implicit val ks = new Murmurhash128StringBytesKeySerializer
  implicit val vsForTableProperty = new ValueSerializer[TablePropertyData, Bytes] {
    def serialize(t: TablePropertyData) = BinaryThriftStructSerializer(TablePropertyData).toBytes(t)
    def deserialize(bytes: Bytes) = BinaryThriftStructSerializer(TablePropertyData).fromBytes(bytes)
  }
  //  implicit val ca = cacher
  //
  //  val caForTableProperty = new SimpleStringBytesCacher[TablePropertyData]("TableProperty", 60 * 60 * 24 /*24 hours*/ )

}