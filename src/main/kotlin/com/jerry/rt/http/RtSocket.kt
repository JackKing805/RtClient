package com.jerry.rt.http

import com.jerry.rt.core.http.protocol.RtContentType
import com.jerry.rt.core.http.protocol.RtMimeType
import com.jerry.rt.http.input.BasicInfoHandler
import com.jerry.rt.http.request.Request
import com.jerry.rt.http.response.Response
import com.jerry.rt.http.response.SocketData
import kotlinx.coroutines.*
import java.lang.Thread.sleep
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
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    })
    private var isAlive = AtomicBoolean(false)
    private lateinit var basicInfoHandler:BasicInfoHandler
    private lateinit var request:Request

    fun isAlive() = isAlive.get()

    fun connect(url:String,onConnect:(()->Unit)?=null,onMessage: ((Response) -> Unit)?=null, onClose:(()->Unit)?=null){
        request.setUrl(url)
        scope.launch(Dispatchers.IO) {
            basicInfoHandler = BasicInfoHandler(Socket(host,port))
            request = Request(basicInfoHandler.outputStream())

            startHeartbeat()
            onConnect?.invoke()
            waitMessage{
                onMessage?.invoke(it)
            }
            while (isAlive()){
                delay(500)
            }
            onClose?.invoke()
        }
    }

    private fun waitMessage(onMessage:(Response)->Unit){
        scope.launch(Dispatchers.IO) {
            while (isAlive.get()){
                try {
                    val messageRtProtocol = basicInfoHandler.getMessageRtProtocol()
                    val socketData = SocketData(messageRtProtocol,basicInfoHandler.inputStream())
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
            var isFirst = true
            isAlive.set(true)
            while (isAlive.get()){
                if (isFirst){
                    isFirst = false
                    delay(1000)
                }
                request.setContentType(RtContentType.RT_HEARTBEAT.content)
                try {
                    request.sendHeader()
                }catch (e:Exception){
                    e.printStackTrace()
                    break
                }
                delay(5000)
            }
            basicInfoHandler.close()
            isAlive.set(false)
        }
    }

    fun getRequest() = if (isAlive()) request else null

    fun sendMessage(content: String){
        request.setContentType(RtContentType.TEXT_PLAIN.content)
        request.write(content)
    }

    fun sendMessage(byteArray: ByteArray){
        request.setContentType(RtMimeType.STREAM.mimeType)
        request.write(byteArray)

    }
}