package com.jerry.rt.model

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * @className: MessageRtProtocol
 * @author: Jerry
 * @date: 2023/3/2:19:46
 **/
data class MessageRtProtocol(
    val code: Int,
    val msg:String,
    var protocolString: String,
    val header: Map<String, String>
) {
    private var charset: Charset?=null
    private val CHARSET_PATTERN = Pattern.compile("charset\\s*=\\s*([a-z0-9-]*)", Pattern.CASE_INSENSITIVE)

    init {
        initCharset()
    }

    private fun initCharset(){
        val contentType = getContentType()

        if (contentType.isNotEmpty()){
            val matcher = CHARSET_PATTERN.matcher(contentType)
            if (matcher.find()){
                charset = Charset.forName(matcher.group(1))
            }
        }

        if (charset ==null){
            charset = StandardCharsets.UTF_8
        }
    }


    private fun getValue(key: String, default: String = ""): String {
        return header.entries.find { it.key.trim().lowercase() == key.lowercase() }?.value?.trim()?:default
    }

    //获取内容类型:
    fun getContentType() = getValue("Content-Type", "none")

    //获取内容长度
    fun getContentLength() = try {
        val value = getValue("Content-Length")
        if (value.isEmpty()) {
            0L
        } else {
            value.toLong()
        }
    } catch (e: Exception) {
        0L
    }

    fun getCharset() = charset!!


}