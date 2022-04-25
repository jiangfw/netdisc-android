package com.fuwei.android.libui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.webkit.WebSettings
import android.webkit.WebViewClient
import com.fuwei.android.libui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web_view.*


/**
 * Created by fuwei on 11/16/21.
 */
class WebViewActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_web_view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        wv_content.settings.javaScriptEnabled = true //支持js脚本
        wv_content.settings.cacheMode = WebSettings.LOAD_NO_CACHE //关闭webview中缓存
        wv_content.settings.allowFileAccess = true //设置可以访问文件
        wv_content.settings.setNeedInitialFocus(true) //当webview调用requestFocus时为webview设置节点
        wv_content.settings.setSupportZoom(true) //支持缩放
        wv_content.settings.builtInZoomControls = true
        wv_content.settings.loadWithOverviewMode = true
        wv_content.settings.useWideViewPort = true
        wv_content.settings.displayZoomControls = false
        wv_content.settings.javaScriptCanOpenWindowsAutomatically = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wv_content.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        wv_content.webViewClient = WebViewClient()
        update(intent);

        iv_back.setOnClickListener() {
            finish()
        }
    }

    private fun update(intent: Intent?) {
        if (intent != null) {
            val url = intent.getStringExtra("url")
            if (!TextUtils.isEmpty(url)) {
                wv_content.loadUrl(url)
            }
            val title = intent.getStringExtra("title")
            if (!TextUtils.isEmpty(title)) {
                tv_help_title.text = title
            }
        }
    }
}