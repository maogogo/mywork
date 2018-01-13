package com.maogogo.mywork.common.jdbc.twttr

import javax.inject.{ Inject, Named }
import com.maogogo.mywork.common.modules.DataSourcePool
import com.maogogo.mywork.common.jdbc._

class MySqlDataSourcePool @Inject() (
  @Named("connections") connections: Seq[DataSourcePool],
  @Named("shardId") shardId: Int) {

  def client: TransactionsClient = {
    connections(0).clients(0)
  }

  //  def client(ma: Int = 0, pa: Int = 0): TransactionsClient = {
  //
  //    ???
  //  }

  def client(masterOrSlave: Int = 0, partitionRandomIndex: Int = 0): TransactionsClient = {
    connections(0).clients(0)
  }

}