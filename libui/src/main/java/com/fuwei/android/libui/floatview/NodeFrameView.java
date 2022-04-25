package com.fuwei.android.libui.floatview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.fuwei.android.libcommon.logger.AILog;
import com.fuwei.android.libcommon.logger.LogLevel;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by fuwei on 4/8/22.
 */
public abstract class NodeFrameView extends FrameLayout {

    protected static String TAG = NodeFrameView.class.getSimpleName();

    private final Queue<NodeBundle> mNodeBundles = new ConcurrentLinkedQueue<>();

    public NodeFrameView(Context context) {
        this(context, null);
    }

    public NodeFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造函数完成以后，创建view
     *
     * @param context
     */
    public void onCreateView(Context context) {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        TAG = getClass().getSimpleName();
        AILog.d(TAG, "onAttachedToWindow. " + this, LogLevel.RELEASE);
        if (!isEmpty()) {
            update();
        }
    }

    protected abstract void update(NodeBundle nodeBundle);

    public void setNodeFrameViewBundleArguments(NodeBundle nodeBundle) {
        if (nodeBundle != null) {
            AILog.d(TAG, "setNodeFrameViewBundleArguments. Bundle = " + nodeBundle + ". " + this, LogLevel.RELEASE);
            this.mNodeBundles.add(nodeBundle);
        }

        if (!isEmpty()) {
            update();
        }
    }

    private void update() {
        Iterator<NodeBundle> iterator = mNodeBundles.iterator();
        while (iterator.hasNext()) {
            NodeBundle bundle = iterator.next();
            AILog.d(TAG, "update. NodeBundle = " + bundle + ". " + this, LogLevel.RELEASE);
            update(bundle);
            iterator.remove();
        }
    }

    private boolean isEmpty() {
        return mNodeBundles == null || mNodeBundles.isEmpty();
    }

}
