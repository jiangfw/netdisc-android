package com.fuwei.android.libui.view

import android.text.style.ClickableSpan
import android.text.TextPaint
import android.view.View

/**
 * Created by fuwei on 11/16/21.
 */
open class NoUnderlineClickSpan : ClickableSpan() {
    override fun onClick(widget: View) {}
    override fun updateDrawState(ds: TextPaint) {
        ds.color = ds.linkColor
        ds.bgColor = 0
        ds.isUnderlineText = false
    }
}