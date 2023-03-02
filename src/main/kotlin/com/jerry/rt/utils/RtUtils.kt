package com.jerry.rt.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @className: RtUtils
 * @author: Jerry
 * @date: 2023/1/26:15:14
 **/
object RtUtils {
    //获取局域网ip加端口


    fun dateToFormat(date: Date,format:String):String{
        val simpleDateFormat = SimpleDateFormat(format, Locale.US)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return simpleDateFormat.format(date)
    }

}