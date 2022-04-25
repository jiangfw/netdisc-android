package com.fuwei.android.libui.floatview;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.fuwei.android.libcommon.logger.AILog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuwei on 4/8/22.
 */
public class FloatWindowManager {

    private static final String TAG = "FloatWindowManager";

    private static FloatWindowManager mFloatWindowManager;

    private WindowManager mWindowManager;

    private final List<View> mFloatWindowLayout = new ArrayList<>();

    private FloatWindowManager() {
    }

    public static synchronized FloatWindowManager getInstance() {
        if (mFloatWindowManager == null) {
            mFloatWindowManager = new FloatWindowManager();

        }
        return mFloatWindowManager;
    }

    /**
     * 创建悬浮框
     *
     * @param context
     */
    public boolean createFloatWindow(final Context context, View view, int layoutParamWidth, int layoutParamHeight) {
        return createFloatWindow(context, view, layoutParamWidth, layoutParamHeight, true);
    }

    private boolean createFloatWindow(final Context context, View view, int layoutParamWidth, int layoutParamHeight, boolean isfocus) {
        if (mFloatWindowLayout.contains(view)) {
            AILog.e(TAG, "createFloatWindow(), the view " + view + " already added!");
            return false;
        }
        mFloatWindowLayout.add(view);
        AILog.i(TAG, "add view : " + view + ", after: " + mFloatWindowLayout);
        WindowManager windowManager = getWindowManager(context);
        LayoutParams layoutParam = new LayoutParams();

        layoutParam.format = PixelFormat.RGBA_8888;

        layoutParam.type = LayoutParams.TYPE_PHONE;
//        layoutParam.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        if (isfocus) {
            layoutParam.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN | LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        } else {
            layoutParam.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        }
        layoutParam.gravity = Gravity.START | Gravity.TOP;
        layoutParam.width = layoutParamWidth;
        layoutParam.height = layoutParamHeight;
        layoutParam.x = 0;
        layoutParam.y = 0;

        windowManager.addView(view, layoutParam);
        return true;
    }

    public boolean createFloatWindowType(final Context context, View view, int layoutParamWidth, int layoutParamHeight, boolean isfocus) {
        if (mFloatWindowLayout.contains(view)) {
            AILog.e(TAG, "createFloatWindow(), the view " + view + " already added!");
            return false;
        }
        mFloatWindowLayout.add(view);
        AILog.i(TAG, "add view : " + view + ", after: " + mFloatWindowLayout);
        WindowManager windowManager = getWindowManager(context);
        LayoutParams layoutParam = new LayoutParams();

        layoutParam.format = PixelFormat.RGBA_8888;

        layoutParam.type = LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParam.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        if (isfocus) {
            layoutParam.flags = LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        } else {
            layoutParam.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        }
        layoutParam.gravity = Gravity.CENTER | Gravity.BOTTOM;
        layoutParam.width = layoutParamWidth;
        layoutParam.height = layoutParamHeight;
        layoutParam.x = 0;
        layoutParam.y = 0;

        windowManager.addView(view, layoutParam);
        return true;
    }


    /**
     * 移除悬浮框
     *
     * @param context
     */
    public void removeFloatWindow(Context context, View view) {
        if (mFloatWindowLayout.remove(view)) {//contains
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeViewImmediate(view);
            AILog.i(TAG, "removeFloatWindow : " + view);
        }
    }

    public boolean isTop(View view) {
        if (mFloatWindowLayout.size() > 0) {
            return mFloatWindowLayout.indexOf(view) == mFloatWindowLayout.size() - 1;
        }
        return false;
    }

    private WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

}
