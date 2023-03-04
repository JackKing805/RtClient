package com.jerry.rt.http.input

import com.jerry.rt.http.input.model.BasicHeader
import com.jerry.rt.model.MessageRtProtocol
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket

/**
 * @className: BasicInfoHandler
 * @author: Jerry
 * @date: 2023/2/15:19:39
 **/
class BasicInfoHandler(private val socket: Socket) {
    private var startLine: String? = null
    private var `is`: InputStream
    private var os: OutputStream
    private var buf = CharArray(2048)
    private var pos = 0
    private var lineBuf: StringBuffer? = null
    private var hdrs: BasicHeader? = null


    init {
        socket.keepAlive = true
        `is` = socket.getInputStream()
        os = socket.getOutputStream()
    }

    fun close(){
        try {
            socket.shutdownInput()
            socket.shutdownOutput()
            socket.close()
        }catch (e:Exception){
        }
    }


    private fun process() {
        buf = CharArray(2048)
        pos = 0
        startLine = null
        lineBuf = null
        hdrs= null
        parseBasicLine()
        parseHeader()
    }

    private fun parseBasicLine() {
        do {
            startLine = readLine()
            if (startLine == null) {
                return
            }
        } while (startLine != null && startLine == "")
    }

    private fun parseHeader() {
        hdrs = BasicHeader()
        var s = CharArray(10)
        var len = 0
        var firstc = `is`.read()
        var keyend: Int
        if (firstc == 13 || firstc == 10) {
            keyend = `is`.read()
            if (keyend == 13 || keyend == 10) {
                return
            }
            s[0] = firstc.toChar()
            len = 1
            firstc = keyend
        }

        while (firstc != 10 && firstc != 13 && firstc >= 0) {
            keyend = -1
            var inKey = firstc > 32
            s[len++] = firstc.toChar()
            label116@ while (true) {
                var c: Int
                if (`is`.read().also { c = it } < 0) {
                    firstc = -1
                    break
                }
                when (c) {
                    9 -> {
                        c = 32
                        inKey = false
                    }
                    32 -> inKey = false
                    10, 13 -> {
                        firstc = `is`.read()
                        if (c == 13 && firstc == 10) {
                            firstc = `is`.read()
                            if (firstc == 13) {
                                firstc = `is`.read()
                            }
                        }
                        if (firstc == 10 || firstc == 13 || firstc > 32) {
                            break@label116
                        }
                        c = 32
                    }

                    58 -> {
                        if (inKey && len > 0) {
                            keyend = len
                        }
                        inKey = false
                    }
                }

                if (len >= s.size) {
                    val ns = CharArray(s.size * 2)
                    System.arraycopy(s, 0, ns, 0, len)
                    s = ns
                }
                s[len++] = c.toChar()
            }

            while (len > 0 && s[len - 1] <= ' ') {
                --len
            }
            var k: String?
            if (keyend <= 0) {
                k = null
                keyend = 0
            } else {
                k = String(s, 0, keyend)
                if (keyend < len && s[keyend] == ':') {
                    ++keyend
                }
                while (keyend < len && s[keyend] <= ' ') {
                    ++keyend
                }
            }
            val v: String = if (keyend >= len) {
                String()
            } else {
                String(s, keyend, len - keyend)
            }
            if (hdrs!!.size() >= 200) {
                throw IOException("Maximum number of request headers (sun.net.httpserver.maxReqHeaders) exceeded, " + 200 + ".")
            }
            if (k == null) {
                k = ""
            }
            hdrs!!.add(k, v)
            len = 0
        }
    }

    @Throws(IOException::class)
    fun readLine(): String? {
        var gotCR = false
        var gotLF = false
        pos = 0
        lineBuf = StringBuffer()
        while (!gotLF) {
            val c = `is`.read()
            if (c == -1) {
                return null
            }
            if (gotCR) {
                if (c == 10) {
                    gotLF = true
                } else {
                    gotCR = false
                    consume(13)
                    consume(c)
                }
            } else if (c == 13) {
                gotCR = true
            } else {
                consume(c)
            }
        }
        lineBuf!!.append(buf, 0, pos)
        return String(lineBuf!!)
    }

    private fun consume(c: Int) {
        if (pos == 2048) {
            lineBuf!!.append(buf)
            pos = 0
        }
        buf[pos++] = c.toChar()
    }

    fun inputStream() = `is`

    fun outputStream() = os


    fun requestLine() = startLine!!

    fun headers() = hdrs!!

    fun getMessageRtProtocol() :MessageRtProtocol{
        process()


        val headers = headers()
        val requestLine = requestLine()
        println("requ:$requestLine")

        val split = requestLine.split(" ")
        val version = split[0]
        val code = split[0].toInt()
        val msg = split[1]

        return MessageRtProtocol(code,msg,version,headers.getHeaders())
    }
}

