package com.jerry.rt

import com.jerry.rt.http.RtSocket

/**
 * @className: RtClient
 * @author: Jerry
 * @date: 2023/2/28:19:51
 **/
object RtClient {
    fun connect(host:String,port:Int,):RtSocket{
        return RtSocket(host,port)
    }
}