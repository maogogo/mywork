package com.maogogo.mywork.common.jdbc

import com.maogogo.mywork.common._
import com.maogogo.mywork.common.modules.DataSourcePool
import com.twitter.util.Future

trait SQL {

  def apply[T](fallback: TransactionsClient ⇒ Future[T])(implicit builder: ConnectionBuilder): Future[T] = {
    this(0, 0)(fallback)
  }

  def apply[T](masterOrSlave: Int = 0, partitionRandomIndex: Int = 0)(fallback: TransactionsClient ⇒ Future[T])(implicit builder: ConnectionBuilder): Future[T] = {
    fallback(builder.client(masterOrSlave, partitionRandomIndex))
  }

}

object SQL extends SQL