package com.jerry.rt.http.request

import com.jerry.rt.core.http.protocol.*
import com.jerry.rt.http.request.impl.ByteRequestWriter
import com.jerry.rt.utils.RtUtils
import java.io.*
import java.nio.charset.Charset
import java.util.*

/**
 * @className: Response
 * @description: 返回
 * @author: Jerry
 * @date: 2023/1/6:19:47
 **/
//todo contentType设置charset
class Request(
    private val output: OutputStream,
){
    private val byteResponseWriter = ByteRequestWriter(output)

    private val header = mutableMapOf<String, String>()

    private var url = "/"

    private var charset = Charsets.UTF_8

    init {
        reset()
    }


    fun setUrl(url:String){
        if (url.isEmpty()){
            throw IllegalArgumentException("not support empty url")
        }
        this.url = url
    }

    fun getUrl() = url

    fun setHeader(key: String, value: String) {
        header[key] = value
    }

    fun getHeader(key: String,default:String?=null):String?{
        return header[key]?:default
    }

    fun getHeaders() = header


    fun setHeaders(headers: MutableMap<String, String>) {
        header.putAll(headers.filter {
            it.key != "Content-Type" &&
                    it.key != "Content-Length"
        })
    }

    fun setCharset(charset: Charset) {
        this.charset = charset
    }

    fun getCharset() = charset

    fun setContentType(contentType: String) {
        val result = if (contentType.startsWith("text")) {
            if (contentType.contains(";")) {
                contentType
            } else {
                contentType + ";" + charset.name()
            }
        } else {
            contentType
        }
        header[RtHeader.CONTENT_TYPE.content] = result
    }


    fun setContentLength(length: Int) {
        header[RtHeader.CONTENT_LENGTH.content] = length.toString()
    }

    @Throws(Exception::class)
    fun sendHeader() {
        write("")
    }

    fun removeHeader(name:String){
        header.remove(name)
    }

    fun getByteWriter() = byteResponseWriter

    @Throws(IOException::class)
    fun write(body: ByteArray, contentType: String, length: Int = body.size) {
        send(start = {
            setContentType(contentType)
            setContentLength(length)
        },{
            byteResponseWriter.writeBody(body)
        }) {

        }
    }

    @Throws(IOException::class)
    fun write(body: ByteArray) {
        if (header[RtHeader.CONTENT_TYPE.content] ==null){
            throw NullPointerException("please provider contentType")
        }
        send(start = {
            setContentLength(body.size)
        },{
            byteResponseWriter.writeBody(body)
        }) {

        }
    }

    fun reset(){
        header.clear()
        setHeader(RtHeader.DATE.content, RtUtils.dateToFormat(Date(),"EEE, dd MMM yyyy HH:mm:ss 'GMT'"))
        url = "/"
    }

    @Throws(IOException::class)
    fun write(body: String, contentType: String, length: Int = body.length) {
        write(body.toByteArray(), contentType, length)
    }

    @Throws(IOException::class)
    fun write(body: String, contentType: String) {
        write(body.toByteArray(), contentType, body.length)
    }

    @Throws(IOException::class)
    fun write(body: String) {
        val contentType = header[RtHeader.CONTENT_TYPE.content]?:throw NullPointerException("please set Content-Type")
        write(body.toByteArray(), contentType, body.length)
    }




    fun getPrintWriter() = PrintWriter(output)


    /**
     * 发送，重要方法
     */
    @Synchronized
    @Throws(Exception::class)
    private fun send(start:()->Unit, body:(ByteRequestWriter)->Unit, complete:()->Unit){
        try {
            start()
            byteResponseWriter.writeFirstLine(url)
            header.entries.forEach {
                byteResponseWriter.writeHeader(it.key, it.value)
            }
            body(byteResponseWriter)
            byteResponseWriter.endWrite()
            reset()
        }catch (e:Exception){
            throw e
        }finally {
            complete()
        }
    }
}
