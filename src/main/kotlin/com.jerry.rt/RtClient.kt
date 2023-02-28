package com.jerry.rt

import com.jerry.rt.i.StateListener
import java.net.Socket

/**
 * @className: RtClient
 * @author: Jerry
 * @date: 2023/2/28:19:51
 **/
object RtClient {
    fun connect(host:String,port:Int,stateListener: StateListener){
        val socket = RtSocket(host,port)
        socket.connect()
    }
}