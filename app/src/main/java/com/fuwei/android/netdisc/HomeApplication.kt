package com.fuwei.android.netdisc

import androidx.multidex.MultiDexApplication
import com.fuwei.android.libnetwork.NetWorkMonitorManager

/**
 * Created by fuwei on 4/24/22.
 */
class HomeApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        NetWorkMonitorManager.getInstance().init(this)
    }
}