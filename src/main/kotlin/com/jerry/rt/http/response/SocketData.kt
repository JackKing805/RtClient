package com.jerry.rt.http.response

import com.jerry.rt.core.http.request.interfaces.DataReadListener
import com.jerry.rt.model.MessageRtProtocol
import java.io.InputStream
import java.io.OutputStream

/**
 * @className: SocketData
 * @author: Jerry
 * @date: 2023/2/12:13:50
 **/
class SocketData(
    private val messageRtProtocol: MessageRtProtocol,
    inputStream: InputStream,
    outputStream: OutputStream
) : DataReadListener {
    private val socketBody = SocketBody(messageRtProtocol.getContentLength(),inputStream,outputStream)

    fun getProtocol() = messageRtProtocol

    fun getSocketBody() = socketBody

    override fun readData(byteArray: ByteArray, len: Int) {
        socketBody.readData(byteArray, len)
    }

    override fun readData(byteArray: ByteArray, offset: Int, len: Int) {
        socketBody.readData(byteArray,offset, len)
    }

    override fun readAllData(): ByteArray {
        return socketBody.readAllData()
    }

    override fun skipData() {
        socketBody.skipData()
    }
}