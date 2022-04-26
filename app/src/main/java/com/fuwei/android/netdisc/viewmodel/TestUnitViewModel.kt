package com.fuwei.android.netdisc.viewmodel

import android.os.Bundle
import com.fuwei.android.libcommon.logger.AILog
import com.fuwei.android.libui.base.BaseViewModel
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Created by fuwei on 4/24/22.
 */
class TestUnitViewModel : BaseViewModel() {

    override fun start(bundle: Bundle?) {
        val action = bundle?.getString("action", "")
        when (action) {
            "file_list" -> {
                fetchFileList()
            }

            "file_upload" -> {

            }
        }

        AILog.i(TAG, "action = $action")
    }

    override fun stop() {
    }


    private fun testRequestTranslate() {
        val httpParams = HttpParams()
        val from = "zh"
        val to = "en"
        val jsonArray = JSONArray()
        jsonArray.put("我叫什么名字")
        httpParams.put("from", from)
        httpParams.put("to", to)
        httpParams.put("contents", jsonArray.toString())
        val jsonObject = JSONObject()
        try {
            jsonObject.put("from", from)
            jsonObject.put("to", to)
            jsonObject.put("contents", jsonArray)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requestUrl = "http://test.ileja.com/ar/server/dui/translate"
        OkGo.post<String>(requestUrl)
            .upJson(jsonObject)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                }

                override fun onError(response: Response<String>) {
                }
            })

    }


    private fun fetchFileList() {
        //http://106.12.132.116:8088/test/get/file/list?dir=myfile
        val requestUrl = "http://106.12.132.116:8088/test/get/file/list"
//        val requestUrl = "http://192.168.1.116:8088/test/get/file/list"
        val httpParams = HttpParams()
        httpParams.put("dir", "myfile")
        OkGo.get<String>(requestUrl)
            .params(httpParams)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    AILog.i(
                        TAG,
                        "onSuccess body = ${response?.body()} ,code = ${response?.code()} , message = ${response?.message()}"
                    )

                    action.postValue(response?.body())
                }

                override fun onError(response: Response<String>?) {
                    AILog.i(
                        TAG,
                        "onError body = ${response?.body()} ,code = ${response?.code()} , message = ${response?.message()}"
                    )
                    action.postValue("onError body = ${response?.body()} ,code = ${response?.code()} , message = ${response?.message()}")


                }

            })
    }

    private fun uploadFile(file: File) {
        //http://106.12.132.116:8088/test/post/upload?dir=myfile
        val requestUrl = "http://106.12.132.116:8088/test/post/upload"
        val httpParams = HttpParams()
        httpParams.put("dir", "myfile")
        OkGo.post<String>(requestUrl)
            .params(httpParams)
            .params("file", file)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    AILog.i(
                        TAG,
                        "onSuccess body = ${response?.body()} ,code = ${response?.code()} , message = ${response?.message()}"
                    )
                }

                override fun onError(response: Response<String>) {
                    AILog.i(
                        TAG,
                        "onError body = ${response?.body()} ,code = ${response?.code()} , message = ${response?.message()}"
                    )
                }
            })

    }


}