package com.maogogo.mywork.common.utils

import java.text.MessageFormat

object FormulaUtil {

  def scriptFigure(fmt: String, arguments: String*): String = {
    //这里必须这么用,否则会报错
    val engine = new jdk.nashorn.api.scripting.NashornScriptEngineFactory().getScriptEngine
    val msg = MessageFormat.format(fmt, arguments: _*)
    engine.eval(msg).toString
  }

}