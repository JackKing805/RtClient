package com.jerry.rt

import com.jerry.rt.http.RtSocket
import com.jerry.rt.i.StateListener

/**
 * @className: RtClient
 * @author: Jerry
 * @date: 2023/2/28:19:51
 **/
object RtClient {
    fun connect(host:String,port:Int,stateListener: StateListener):RtSocket{
        return RtSocket(host,port)
    }
}