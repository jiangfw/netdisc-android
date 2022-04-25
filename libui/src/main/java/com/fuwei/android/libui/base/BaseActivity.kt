package com.fuwei.android.libui.base

import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.fuwei.android.libui.R
import com.gyf.immersionbar.ImmersionBar

/**
 * Created by fuwei on 3/10/22.
 */
abstract class BaseActivity : AppCompatActivity() {

    val TAG: String = "Activity-" + this.javaClass.simpleName

    abstract fun getLayoutId(): Int
    abstract fun initView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//默认添加屏幕常亮
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(getLayoutId())
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .applySystemFits(true)
            .statusBarDarkFont(true)
            .init()
        initView()
    }
    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
}