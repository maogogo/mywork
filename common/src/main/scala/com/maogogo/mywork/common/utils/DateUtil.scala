package com.maogogo.mywork.common.utils

import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.sql.Timestamp

object DateUtil {

  def getDayLastTime: Long = {
    val d = Calendar.getInstance()
    d.set(Calendar.HOUR, 23)
    d.set(Calendar.MINUTE, 59)
    d.set(Calendar.SECOND, 59)
    d.set(Calendar.MILLISECOND, 999)
    ((d.getTimeInMillis - System.currentTimeMillis()) / 1000)
  }

  def format(date: Date, fmt: String): String = {
    new SimpleDateFormat(fmt).format(date)
  }

  def format(time: Timestamp, fmt: String) = {
    new SimpleDateFormat(fmt).format(time)
  }

}