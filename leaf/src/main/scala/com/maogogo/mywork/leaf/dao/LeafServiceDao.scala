package com.maogogo.mywork.leaf.dao

import com.maogogo.mywork.common.jdbc.ConnectionBuilder
import javax.inject.{ Inject, Named }
import com.maogogo.mywork.common.modules.DataSourcePool

class LeafServiceDao @Inject() (@Named("connections") val connections: Seq[DataSourcePool], @Named("shardId") val shardId: Int)
  extends ConnectionBuilder {

}