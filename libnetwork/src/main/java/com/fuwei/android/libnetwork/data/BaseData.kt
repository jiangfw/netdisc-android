package com.fuwei.android.libnetwork.data

/**
 * Created by fuwei on 4/25/22.
 */
open class BaseData<T> {
    var code: Int = 0
    var contentType: Int = -1
    var content: T? = null

    override fun toString(): String {
        return "BaseData(code=$code, contentType=$contentType, content=$content)"
    }


}