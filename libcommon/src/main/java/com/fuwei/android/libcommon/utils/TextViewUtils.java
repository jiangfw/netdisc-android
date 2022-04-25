package com.fuwei.android.libcommon.utils;

import android.graphics.Rect;
import android.text.Layout;
import android.widget.TextView;

/**
 * Created by fuwei on 3/10/22.
 */
public class TextViewUtils {

    /**
     * 获取TextView某一个字符的坐标位置
     *
     * @return 返回的是相对坐标
     * @parms tv
     * @parms index 字符索引
     */
    public static Rect getTextViewSelectionRect(TextView tv, int index) {
        Layout layout = tv.getLayout();
        Rect bound = new Rect();
        int line = layout.getLineForOffset(index);
        layout.getLineBounds(line, bound);
        int yAxisBottom = bound.bottom;//字符底部y坐标
        int yAxisTop = bound.top;//字符顶部y坐标
        int xAxisLeft = (int) layout.getPrimaryHorizontal(index);//字符左边x坐标
        int xAxisRight = (int) layout.getSecondaryHorizontal(index);//字符右边x坐标
        //xAxisRight 位置获取后发现与字符左边x坐标相等，如知道原因请告之。暂时加上字符宽度应对。
        if (xAxisLeft == xAxisRight) {
            String s = tv.getText().toString().substring(index, index + 1);//当前字符
            xAxisRight = xAxisRight + (int) tv.getPaint().measureText(s);//加上字符宽度
        }
        int tvTop = tv.getScrollY();//tv绝对位置
        return new Rect(xAxisLeft, yAxisTop + tvTop, xAxisRight, yAxisBottom + tvTop);

    }

    /**
     * 获取TextView触点坐标下的字符
     *
     * @param tv tv
     * @param x  触点x坐标
     * @param y  触点y坐标
     * @return 当前字符
     */
    public static String getTextViewSelectionByTouch(TextView tv, int x, int y) {
        String s = "";
        for (int i = 0; i < tv.getText().length(); i++) {
            Rect rect = getTextViewSelectionRect(tv, i);
            if (x < rect.right && x > rect.left && y < rect.bottom && y > rect.top) {
//                s = tv.getText().toString().substring(i, i + 1);//当前字符
                s = tv.getText().toString().substring(i);//当前字符到最后
                break;
            }
        }
        return s;
    }


    public static String getTextViewSelection(TextView tv) {
        Rect visibleRect = new Rect();
        tv.getLocalVisibleRect(visibleRect);

        visibleRect.top -= tv.getLineHeight() / 4;

        String s = "";
        for (int i = 0; i < tv.getText().length(); i++) {
            Rect rect = getTextViewSelectionRect(tv, i);

            visibleRect.contains(rect);

//            if (x < rect.right && x > rect.left && y < rect.bottom && y > rect.top)
            if (visibleRect.contains(rect)) {
                s = tv.getText().toString().substring(i);//当前字符到最后
                break;
            }
        }
        return s;
    }

    public static String getTextViewSelectionPrefix(TextView tv) {
        Rect visibleRect = new Rect();
        tv.getLocalVisibleRect(visibleRect);
        visibleRect.top -= tv.getLineHeight() / 4;
        String s = "";
        for (int i = 0; i < tv.getText().length(); i++) {
            Rect rect = getTextViewSelectionRect(tv, i);
            if (visibleRect.contains(rect)) {
                s = tv.getText().toString().substring(0, i);//当前字符到最后
                break;
            }
        }
        return s;
    }

    public static int getTextViewSelectionPrefixIndex(TextView tv) {
        Rect visibleRect = new Rect();
        tv.getLocalVisibleRect(visibleRect);
        visibleRect.top -= tv.getLineHeight() / 4;
        int index = -1;
        for (int i = 0; i < tv.getText().length(); i++) {
            Rect rect = getTextViewSelectionRect(tv, i);
            if (visibleRect.contains(rect)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static int getTextViewSelectionSuffixIndex(TextView tv) {
        Rect visibleRect = new Rect();
        tv.getLocalVisibleRect(visibleRect);
        visibleRect.top -= tv.getLineHeight() / 4;
        int index = 0;
        boolean isHas = false;
        for (int i = 0; i < tv.getText().length(); i++) {
            Rect rect = getTextViewSelectionRect(tv, i);
            if (visibleRect.contains(rect)) {
                isHas = true;
                index = i;
            } else if (isHas) {
                break;
            }
        }
        return index;
    }

}
