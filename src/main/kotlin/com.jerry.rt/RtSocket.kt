package com.jerry.rt

import com.jerry.rt.i.StateListener
import java.net.Socket

/**
 * @className: RtSocket
 * @author: Jerry
 * @date: 2023/2/28:20:04
 **/
class RtSocket(private val host:String,private val port:Int) {
    private val socket = Socket(host,port)
    private val inputStream = socket.getInputStream()
    private val outputStream = socket.getOutputStream()


    fun connect(){
        socket.keepAlive = true
    }

    fun getInputStream() = inputStream

    fun getOutputStream() = outputStream
}