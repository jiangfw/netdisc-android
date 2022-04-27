package com.fuwei.android.netdisc.viewmodel

import android.os.Bundle
import android.text.TextUtils
import com.fuwei.android.libcommon.logger.AILog
import com.fuwei.android.libui.base.BaseViewModel
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.model.Response
import java.io.File

/**
 * Created by fuwei on 4/24/22.
 */
class TestUnitViewModel : BaseViewModel() {

    override fun start(bundle: Bundle?) {
        val action = bundle?.getString("action", "")
        val data = bundle?.getString("data", "")
        when (action) {
            "file_list" -> {
                fetchFileList(data)
            }

            "file_upload" -> {

            }
        }

        AILog.i(TAG, "action = $action")
    }

    override fun stop() {
    }

    fun fetchFileList(dir: String?) {
        //http://106.12.132.116:8088/test/get/file/list?dir=myfile
        val requestUrl = "http://106.12.132.116:8088/test/get/file/list"
//        val requestUrl = "http://192.168.1.104:8088/test/get/file/list"
        val httpParams = HttpParams()
        if (TextUtils.isEmpty(dir)) {
            httpParams.put("dir", "myfile")
        } else {
            httpParams.put("dir", dir)
        }
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
                        "onError code = ${response?.code()},error = ${response?.exception}"
                    )
                    action.postValue("onError code = ${response?.code()},error = ${response?.exception} ")


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
                        "onError code = ${response?.code()},error = ${response?.exception}"
                    )
                }
            })

    }


}