package com.fuwei.android.libui.floatview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.fuwei.android.libcommon.logger.AILog;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by fuwei on 4/8/22.
 */
public class NodeFrameViewContainer extends FrameLayout {

    private static final String TAG = "NodeFrameViewContainer";

    static final Class<?>[] mConstructorSignature = new Class[] {Context.class, AttributeSet.class};

    final Object[] mConstructorArgs = new Object[2];

    private final Stack<NodeFrameView> mNodeFrameViewLists = new Stack<>();

    public NodeFrameViewContainer(Context context) {
        this(context, null);
    }

    public NodeFrameViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 增加view
     *
     * @param clazz
     */
    public void addFrameView(Class<? extends NodeFrameView> clazz, int animateIn, int animateOut) {
        this.addFrameView(clazz, null, animateIn, animateOut);
    }

    /**
     * 增加view
     *
     * @param clazz
     * @param bundle 传输的数据
     */
    public void addFrameView(Class<? extends NodeFrameView> clazz, NodeBundle bundle, int animateIn, int animateOut) {
        if (isTopFrameView(clazz)) {
            getTopFrameView().setNodeFrameViewBundleArguments(bundle);
        } else {
            createIfNotExist(clazz, bundle, animateIn, animateOut);
        }
    }

    private NodeFrameView inflateView(Class<? extends NodeFrameView> clazz) {
        mConstructorArgs[0] = getContext();
        NodeFrameView targetFrameView = null;
        try {
            Object[] args = mConstructorArgs;
            args[1] = null;
            targetFrameView = clazz.getConstructor(mConstructorSignature).newInstance(args);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (targetFrameView != null) {
            targetFrameView.onCreateView(getContext());
        }
        return targetFrameView;
    }

    /**
     * 替换view,其他的view均不可见或被移除
     *
     * @param clazz
     */
    public void replaceFrameView(Class<? extends NodeFrameView> clazz, int animateIn, int animateOut) {
        this.replaceFrameView(clazz, null, animateIn, animateOut);
    }

    /**
     * 替换view,其他的view均不可见或被移除
     *
     * @param clazz
     * @param bundle 传输的数据
     */
    public void replaceFrameView(Class<? extends NodeFrameView> clazz, NodeBundle bundle, int animateIn, int animateOut) {
        if (isTopFrameView(clazz)) {
            getTopFrameView().setNodeFrameViewBundleArguments(bundle);
        } else {
            //remove all frame view
            Iterator<NodeFrameView> iter = mNodeFrameViewLists.iterator();
            while (iter.hasNext()) {
                NodeFrameView fm = iter.next();
                if (animateOut > 0) {
                    fm.setAnimation(AnimationUtils.loadAnimation(getContext(), animateOut));
                }
                this.removeView(fm);
                iter.remove();
            }

            NodeFrameView targetFrameView = create(clazz, bundle);
            if (animateIn > 0) {
                targetFrameView.setAnimation(AnimationUtils.loadAnimation(getContext(), animateIn));
            }
            targetFrameView.setFocusable(true);
            targetFrameView.setFocusableInTouchMode(true);
        }
    }

    private NodeFrameView create(Class<? extends NodeFrameView> clazz, NodeBundle bundle) {
        NodeFrameView targetFrameView = inflateView(clazz);
        this.addView(targetFrameView);
        mNodeFrameViewLists.add(targetFrameView);
        AILog.d(TAG, "create targetFrameView=" + targetFrameView);

        targetFrameView.setNodeFrameViewBundleArguments(bundle);

        return targetFrameView;
    }

    private NodeFrameView createIfNotExist(Class<? extends NodeFrameView> clazz, NodeBundle bundle, int animateIn, int animateOut) {
        Iterator<NodeFrameView> iter = mNodeFrameViewLists.iterator();
        NodeFrameView targetFrameView = null;
        while (iter.hasNext()) {
            targetFrameView = iter.next();
            if (targetFrameView != null && targetFrameView.getClass() == clazz) {
                this.removeView(targetFrameView);
                iter.remove();
                break;
            }
        }

        AILog.d(TAG, "createIfNotExist targetFrameView=" + targetFrameView);
        if (targetFrameView == null) {
            targetFrameView = create(clazz, bundle);
        } else {//add to top
            this.addView(targetFrameView);
            mNodeFrameViewLists.add(targetFrameView);
            targetFrameView.setNodeFrameViewBundleArguments(bundle);
        }
        if (animateIn > 0) {
            targetFrameView.setAnimation(AnimationUtils.loadAnimation(getContext(), animateIn));
        }
        return targetFrameView;
    }

    /**
     * 移除所有的view
     */
    public void removeAllFrameView() {
        Iterator<NodeFrameView> iter = mNodeFrameViewLists.iterator();
        while (iter.hasNext()) {
            NodeFrameView fm = iter.next();
            this.removeView(fm);
            iter.remove();
        }
    }

    /**
     * 是否包含view
     *
     * @param clazz
     * @return
     */
    public boolean containsFrameView(Class<? extends NodeFrameView> clazz) {
        return queryCache(clazz) != null;
    }

    /**
     * 移除view
     *
     * @param clazz
     */
    public void removeFrameView(Class<? extends NodeFrameView> clazz) {
        NodeFrameView targetFrameView = queryCache(clazz);
        if (targetFrameView != null) {
            this.removeView(targetFrameView);
            mNodeFrameViewLists.remove(targetFrameView);
        }
    }

    private NodeFrameView queryCache(Class<? extends NodeFrameView> clazz) {
        for (NodeFrameView fm : mNodeFrameViewLists) {
            if (fm != null && fm.getClass() == clazz) {
                return fm;
            }
        }
        return null;
    }


    private boolean isTopFrameView(Class<? extends NodeFrameView> clazz) {
        NodeFrameView topFragment = getTopFrameView();
        return topFragment != null && topFragment.getClass() == clazz;
    }


    /**
     * 获取最上层的view
     *
     * @return
     */
    public NodeFrameView getTopFrameView() {
        return mNodeFrameViewLists.isEmpty() ? null : mNodeFrameViewLists.peek();
    }

}
