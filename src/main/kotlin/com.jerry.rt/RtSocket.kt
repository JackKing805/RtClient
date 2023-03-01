package com.jerry.rt

import com.jerry.rt.i.StateListener
import com.jerry.rt.input.BasicInfoHandler
import kotlinx.coroutines.*
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @className: RtSocket
 * @author: Jerry
 * @date: 2023/2/28:20:04
 **/
class RtSocket(private val host:String,private val port:Int) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable ->  })
    private val socket = Socket(host,port)
    private val basicInfoHandler = BasicInfoHandler(socket)
    private var isAlive = AtomicBoolean(false)

    fun connect(){

        startHeartbeat()
    }

    private fun startHeartbeat(){
        scope.launch {
            isAlive.set(true)
            while (isAlive.get()){

            }
        }
    }




    fun getInputStream() = basicInfoHandler.inputStream()

    fun getOutputStream() = basicInfoHandler.outputStream()
}