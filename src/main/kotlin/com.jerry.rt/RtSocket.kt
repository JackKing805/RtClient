package com.jerry.rt

import com.jerry.rt.http.response.Response
import com.jerry.rt.input.BasicInfoHandler
import com.jerry.rt.http.response.SocketData
import kotlinx.coroutines.*
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
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
        waitMessage{

        }
    }

    private fun waitMessage(onMessage:(Response)->Unit){
        scope.launch {
            val basicInfo = BasicInfoHandler(socket)
            while (isAlive.get()){
                try {
                    val messageRtProtocol = basicInfo.getMessageRtProtocol()
                    val socketData = SocketData(messageRtProtocol,basicInfo.inputStream(),basicInfo.outputStream())
                    onMessage(Response(socketData))
                    socketData.skipData()
                }catch (e: SocketException){
                    break
                }catch (e: SocketTimeoutException){
                    break
                }catch (e:NullPointerException){
                    break
                }catch (e:Exception){
                    e.printStackTrace()
                    break
                }
            }
        }
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