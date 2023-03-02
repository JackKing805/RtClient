package com.jerry.rt.http.request.impl

import com.jerry.rt.http.request.RequestWriter
import java.io.OutputStream
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * @className: StringResponseWriter
 * @description: string 结果
 * @author: Jerry
 * @date: 2023/1/1:16:21
 **/
class ByteRequestWriter(outputStream: OutputStream): RequestWriter<ByteArray>(outputStream) {
    @Throws(Exception::class)
    override fun writeFirstLine(url:String) {
        super.writeFirstLine(url)
        writeLine("RT $url rt/1.0")
    }

    @Throws(Exception::class)
    override fun writeLine(line: String) {
        super.writeLine(line)
        if (line.endsWith("\r\n")){
            outputStream.write(line.toByteArray())
        }else{
            outputStream.write((line+"\r\n").toByteArray())
        }
        outputStream.flush()
    }

    @Throws(Exception::class)
    override fun writeHeader(key: String, value: Any) {
        super.writeHeader(key, value)
        writeLine("$key: $value")
    }


    @Throws(Exception::class)
    override fun writeBody(content: ByteArray) {
        super.writeBody(content)
        if (isFirstBody()){
            writeLine("\r\n")
        }

        outputStream.write(content)
        outputStream.flush()
    }

    @Throws(Exception::class)
    override fun writeBody(content: ByteArray, offset: Int, size: Int) {
        super.writeBody(content, offset, size)
        if (isFirstBody()){
            writeLine("\r\n")
        }

        outputStream.write(content,offset,size)
        outputStream.flush()
    }

    @Throws(Exception::class)
    override fun endWrite() {
        super.endWrite()
        if (isFirstBody()){
            writeLine("\r\n")
        }
        outputStream.flush()
        reset()
    }
}