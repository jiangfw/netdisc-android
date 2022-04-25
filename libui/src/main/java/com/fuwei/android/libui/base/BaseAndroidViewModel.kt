package com.fuwei.android.libui.base

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * Created by fuwei on 3/10/22.
 */
abstract class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = "ViewModel" + this.javaClass.simpleName


    var action: MutableLiveData<String> = MutableLiveData()
    abstract fun start()
    abstract fun stop()


    fun getContext(): Context {
        return getApplication()
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        Log.i(TAG, "onCleared action = ${action.value}")
    }
}