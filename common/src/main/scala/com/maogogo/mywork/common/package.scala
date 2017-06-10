package com.maogogo.mywork

import com.twitter.finagle.mysql._

package object common {

  type Bytes = Array[Byte]
  type TransactionsClient = Client with Transactions

}