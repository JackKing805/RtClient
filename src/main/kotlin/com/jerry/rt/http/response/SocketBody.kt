package com.jerry.rt.http.response

import com.jerry.rt.http.response.exceptions.LimitLengthException
import com.jerry.rt.http.response.exceptions.NoLengthReadException
import com.jerry.rt.core.http.request.interfaces.DataReadListener
import com.jerry.rt.utils.readLength
import com.jerry.rt.utils.skipNotConsumptionByte
import com.jerry.rt.utils.toByteArray
import java.io.InputStream
import java.io.OutputStream

/**
 * @className: SocketBody
 * @author: Jerry
 * @date: 2023/2/12:14:52
 **/
class SocketBody(private val maxSize:Long,private val inputStream: InputStream):DataReadListener {
    private var readSize = 0L

    @Throws(exceptionClasses = [NoLengthReadException::class, LimitLengthException::class])
    override fun readData(byteArray: ByteArray,len: Int){
        readData(byteArray,0,len)
    }

    @Throws(exceptionClasses = [NoLengthReadException::class, LimitLengthException::class])
    override fun readData(byteArray: ByteArray,offset:Int,len:Int){
        val total = offset + len
        if (readSize == maxSize){
            throw NoLengthReadException()
        }

        if (readSize+total>maxSize){
            throw LimitLengthException()
        }
        val read = inputStream.read(byteArray,offset,len)
        if (read!=-1){
            readSize+=read
        }
    }

    @Throws(exceptionClasses = [LimitLengthException::class])
    override fun readAllData():ByteArray{
        if (readSize!=0L){
            return ByteArray(0)
        }
        val byteArray = inputStream.readLength(maxSize).toByteArray()
        readSize = maxSize
        return byteArray
    }

    @Throws(exceptionClasses = [Exception::class])
    override fun skipData(){
        val skip = maxSize - readSize
        val skipNotConsumptionByte = inputStream.skipNotConsumptionByte(skip)
    }

    fun getInputStream() = inputStream
}
