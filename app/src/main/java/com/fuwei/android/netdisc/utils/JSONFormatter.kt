package com.fuwei.android.netdisc.utils

import com.alibaba.fastjson.JSON
import java.lang.reflect.Type

/**
 * Created by fuwei on 4/25/22.
 */
object JSONFormatter {

    fun format(content: String, any: Any): Any {
        return JSON.parseObject(content, any.javaClass)
    }

    fun format(content: String, type: Type): Any {
        return JSON.parseObject(content, type)
    }
}