package com.fuwei.android.libui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fuwei.android.libcommon.context.GlobalApplication

/**
 * Created by fuwei on 3/10/22.
 */
abstract class BaseViewModel : ViewModel() {
    val TAG = "ViewModel-" + javaClass.simpleName

    var action: MutableLiveData<String> = MutableLiveData()

    abstract fun start(bundle: Bundle?)
    abstract fun stop()

    fun getContext(): Context {
        return GlobalApplication.getApplication()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        Log.i(TAG, "onCleared action = ${action.value}")
    }
}