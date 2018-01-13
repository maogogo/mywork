package com.maogogo.mywork.common

import com.twitter.finagle.mysql._

package object jdbc {

  type TransactionsClient = Client with Transactions

}

