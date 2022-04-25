package com.fuwei.android.libnetwork;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuwei on 3/25/22.
 */
public class NetWorkMonitorManager {
    protected static final String TAG = "NetWorkMonitorManager";

    /**
     * 网络状态改变回调
     *
     * @author zhengrong
     */
    public interface NetWorkListener {
        enum NetState {
            /**
             * 无网络
             **/
            NO_CONNECT,
            /**
             * 3G网络
             **/
            MOBILE_CONNECT,
            /**
             * WIFI
             **/
            WIFI_CONNECT,
            /**
             * 以太网
             **/
            WIFI_ETHERNET,
        }

        /**
         * 网络已连接
         */
        void onNetWorkConnected(NetState netState);

        /**
         * 网络已断开
         */
        void onNetWorkDisConnected();
    }

    private volatile static NetWorkMonitorManager mInstance = null;

    private final List<NetWorkListener> mListeners = new ArrayList<>();

    private NetWorkMonitorManager() {

    }

    public static NetWorkMonitorManager getInstance() {
        if (mInstance == null) {
            synchronized (NetWorkMonitorManager.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkMonitorManager();
                }
            }
        }

        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        registerNetWorkReceiver(context);
    }

    /**
     * 反初始化
     *
     * @param context
     */
    public void release(Context context) {
        unregisterNetWorkReceiver(context);
    }

    /**
     * 注册监听网络状态改变
     *
     * @param listener
     */
    public void registerListener(NetWorkListener listener) {
        synchronized (mListeners) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    /**
     * 反注册监听网络状态改变
     *
     * @param listener
     */
    public void unregisterListener(NetWorkListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private boolean isRegisteredNetWorkReceiver;

    private void registerNetWorkReceiver(Context context) {
        if (!isRegisteredNetWorkReceiver) {
            Log.d(TAG, "registerNetWorkReceiver");
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。这个广播的最大弊端是比上边下面两个广播的反应要慢.

            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);// 这个监听wifi的打开与关闭，与wifi的连接无关
            // filterc.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//
            // 这个监听wifi的连接状态即是否连上了一个有效无线路由，
            context.getApplicationContext().registerReceiver(mConnectivityCheckReceiver, filter);
            isRegisteredNetWorkReceiver = true;
        }
    }

    /**
     * 恢复
     *
     * @param context
     */
    public void restoreAndNotify(Context context) {
        mIsMobileConnected = isMobileConnected(context);
        mIsWifiConnected = isWifiConnected(context);
        Log.d(TAG, "restoreAndNotify, isMobileConnected:" + mIsMobileConnected + ", mIsWifiConnected:" + mIsWifiConnected);
        notifyConnectedListener();
    }

    private void unregisterNetWorkReceiver(Context context) {
        if (isRegisteredNetWorkReceiver) {
            Log.d(TAG, "unregisterNetWorkReceiver");
            if (context != null) {
                context.getApplicationContext().unregisterReceiver(mConnectivityCheckReceiver);
            }
            isRegisteredNetWorkReceiver = false;
        }
    }

    private boolean mIsWifiConnected = false;
    private boolean mIsMobileConnected = false;
    private boolean mIsEthernetConnected = false;
    public final BroadcastReceiver mConnectivityCheckReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

                NetworkInfo mobile = connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifi = connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //以太网
                NetworkInfo ethernet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
                boolean isWifiConnected = false;
                if (wifi != null) {
                    isWifiConnected = wifi.isConnected();
                }

                boolean isMobileConnected = false;
                if (mobile != null) {
                    isMobileConnected = mobile.isConnected();
                }

                boolean isEthernetConnected = false;
                if (ethernet != null) {
                    isEthernetConnected = ethernet.isConnected();
                }

                boolean changed = false;
                if (mIsWifiConnected != isWifiConnected) {
                    mIsWifiConnected = isWifiConnected;
                    changed = true;
                }
                if (mIsMobileConnected != isMobileConnected) {
                    mIsMobileConnected = isMobileConnected;
                    changed = true;
                }
                if (mIsEthernetConnected != isEthernetConnected) {
                    mIsEthernetConnected = isEthernetConnected;
                    changed = true;
                }

                if (changed) {
                    if (mIsWifiConnected || mIsMobileConnected || mIsEthernetConnected) {
                        notifyConnectedListener();
                    } else {
                        notifyDisConnectedListener();
                    }
                }
                Log.d(TAG, "changed:" + changed + " , isWifiConnected:" + mIsWifiConnected + " , isMobileConnected:" + mIsMobileConnected + " , isEthernetConnected:" + mIsEthernetConnected);
            }
        }
    };

    private void notifyConnectedListener() {
        for (NetWorkListener listener : mListeners) {
            if (listener != null) {
                if (mIsMobileConnected) {
                    listener.onNetWorkConnected(NetWorkListener.NetState.MOBILE_CONNECT);
                } else if (mIsWifiConnected) {
                    listener.onNetWorkConnected(NetWorkListener.NetState.WIFI_CONNECT);
                } else if (mIsEthernetConnected) {
                    listener.onNetWorkConnected(NetWorkListener.NetState.WIFI_ETHERNET);
                }
            }
        }
    }

    private void notifyDisConnectedListener() {
        for (NetWorkListener listener : mListeners) {
            if (listener != null) {
                listener.onNetWorkDisConnected();
            }
        }
    }

    /**
     * wifi网络 是否连接
     *
     * @param context
     * @return
     */
    public boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.i(TAG, "TYPE_WIFI CONNECTED");
                return true;
            }
        }
        return false;
    }

    /**
     * 数据流量网络 是否连接
     *
     * @param context
     * @return
     */
    public boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isConnected()) {
            if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.i(TAG, "TYPE_MOBILE CONNECTED");
                return true;
            }
        }
        return false;
    }

    /**
     * 网络是否可用(wifi&mobile)
     *
     * @param context
     * @return
     */
    public boolean isNetWorkConnected(Context context) {
        return isWifiConnected(context) || isMobileConnected(context);
    }

}
