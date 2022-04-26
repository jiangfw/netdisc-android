package com.fuwei.android.netdisc.utils

import com.alibaba.fastjson.JSON
import java.lang.Exception
import java.lang.reflect.Type

/**
 * Created by fuwei on 4/25/22.
 */
object JSONFormatter {

    fun format(content: String, type: Type): Any {
        try {
            return JSON.parseObject(content, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return content
    }
}