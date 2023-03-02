package com.jerry.rt.http.response

import com.jerry.rt.http.response.exceptions.LimitLengthException
import com.jerry.rt.http.response.exceptions.NoLengthReadException
import java.nio.charset.Charset


/**
 * @className: Request
 * @description: 请求
 * @author: Jerry
 * @date: 2023/1/6:19:47
 **/
data class Response(
    private val socketData: SocketData
) {
    private var bodyCache:ByteArray? = null

    fun getCharset(): Charset = socketData.getProtocol().getCharset()

    fun getByteBody():ByteArray?{
        if (bodyCache==null){
            bodyCache = ByteArray(socketData.getProtocol().getContentLength().toInt())
            try {
                socketData.readData(bodyCache!!,0,bodyCache!!.size)
            }catch (_: NoLengthReadException){
            }catch (_: LimitLengthException){
            }
        }
        return bodyCache!!
    }

    fun getBody() = getBody(getCharset())

    fun getBody(charset: Charset):String?{
        val body = getByteBody()
        if (body!=null){
            return String(body,charset)
        }
        return null
    }

}