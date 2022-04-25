package com.fuwei.android.libcommon;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BasePrefHelper {

    private static final String TAG = "BasePrefHelper";
    private static final String KEY_MAC_ADDR = "mac_addr";
    private static final String KEY_LOG_TIMESTAMP = "log_timestamp";
    private static final String KEY_DEVICE_ID = "device_id";

    public static final String KEY_TOKEN = "token";
    private static final String KEY_REFRESH_RANDOM = "refreshRandom";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_AVATAR_URL = "user_avatarUrl";
    private static final String KEY_USER_NICK_NAME = "user_nickName";
    private static final String KEY_USER_PHONE_NUMBER = "user_phone_num";


    private static List<OnSharedPreferenceChangeListener> mOnSharedPreferenceChangeListeners = new CopyOnWriteArrayList();

    private static MMKV commonMMKV = null;

    private static MMKV userMMKV = null;

    private static MMKV getCommonMMKV(Context context) {

        if (commonMMKV == null) {
            try {
                commonMMKV = MMKV.mmkvWithID("common_mmkv", MMKV.MULTI_PROCESS_MODE);
            } catch (IllegalStateException e) {
                MMKV.initialize(context);
                commonMMKV = MMKV.mmkvWithID("common_mmkv", MMKV.MULTI_PROCESS_MODE);
            }
        }

        return commonMMKV;

    }

    /**
     * 存储用户相关的数据
     */
    private static MMKV getUserMMKV(Context context) {

        if (userMMKV == null) {

            try {
                userMMKV = MMKV.mmkvWithID("user_mmkv", MMKV.MULTI_PROCESS_MODE);
            } catch (IllegalStateException e) {
                MMKV.initialize(context);
                userMMKV = MMKV.mmkvWithID("user_mmkv", MMKV.MULTI_PROCESS_MODE);
            }
        }
        return userMMKV;
    }

    public static void setUserPhone(Context context, String phone) {
        if (context != null) {
            putString(getUserMMKV(context), KEY_USER_PHONE_NUMBER, phone);
        }
    }

    public static String getUserPhone(Context context) {
        if (context != null) {
            return getUserMMKV(context).getString(KEY_USER_PHONE_NUMBER, "");
        }
        return "";
    }

    public static void setRefreshRandom(Context context, String token) {
        if (context != null) {
            putString(getUserMMKV(context), KEY_REFRESH_RANDOM, token);
        }
    }

    public static String getRefreshRandom(Context context) {
        if (context != null) {
            return getUserMMKV(context).getString(KEY_REFRESH_RANDOM, "");
        }
        return "";
    }

    public static void setToken(Context context, String token) {
        if (context != null) {
            putString(getUserMMKV(context), KEY_TOKEN, token);
        }
    }

    public static String getToken(Context context) {
        if (context != null) {
            return getUserMMKV(context).getString(KEY_TOKEN, "");
        }
        return "";
    }

    public static void setUserId(Context context, String userID) {
        if (context != null) {
            putString(getUserMMKV(context), KEY_USER_ID, userID);
        }
    }

    public static String getUserID(Context context) {
        if (context != null) {
            return getUserMMKV(context).getString(KEY_USER_ID, "");
        }
        return "";
    }

    public static void setUserAvatarUrl(Context context, String url) {
        if (context != null) {
            putString(getUserMMKV(context), KEY_USER_AVATAR_URL, url);
        }
    }

    public static String getUserAvatarUrl(Context context) {
        if (context != null) {
            return getUserMMKV(context).getString(KEY_USER_AVATAR_URL, "");
        }
        return "";
    }

    public static void setUserNickName(Context context, String nickName) {
        if (context != null) {
            putString(getUserMMKV(context), KEY_USER_NICK_NAME, nickName);
        }
    }

    public static String getUserNickName(Context context) {
        if (context != null) {
            return getUserMMKV(context).getString(KEY_USER_NICK_NAME, "");
        }
        return "";
    }


    public static void loginOut(Context context) {
        setToken(context, "");
        setRefreshRandom(context, "");
        setUserId(context, "");
        setUserAvatarUrl(context, "");
        setUserNickName(context, "");
        setUserPhone(context, "");
    }

    public static void setMacAddr(Context context, String macAddr) {
        putString(getUserMMKV(context), KEY_MAC_ADDR, macAddr);
    }

    public static String getMacAddr(Context context) {
        return getUserMMKV(context).getString(KEY_MAC_ADDR, null);
    }

    public static void setLogTimeStamp(Context context, long timestamp) {
        putLong(getCommonMMKV(context), KEY_LOG_TIMESTAMP, timestamp);
    }

    public static long getLogTimeStamp(Context context) {
        return getCommonMMKV(context).getLong(KEY_LOG_TIMESTAMP, 0);
    }

    public static void setDeviceId(Context context, String deviceId) {
        putString(getUserMMKV(context), KEY_DEVICE_ID, deviceId);
    }

    public static String getDeviceId(Context context) {
        String deviceId = getUserMMKV(context).getString(KEY_DEVICE_ID, "");
        return deviceId;
    }


    private static void putString(MMKV mmkv, String key, String value) {
        if (mmkv != null) {
            mmkv.putString(key, value).commit();
            notifyChange(key, value);
        }
    }

    private static void putBoolean(MMKV mmkv, String key, boolean value) {
        if (mmkv != null) {
            mmkv.putBoolean(key, value).commit();
            notifyChange(key, value);
        }
    }

    private static void putLong(MMKV mmkv, String key, long value) {
        if (mmkv != null) {
            mmkv.putLong(key, value).commit();
            notifyChange(key, value);
        }
    }

    private static void putInt(MMKV mmkv, String key, int value) {
        if (mmkv != null) {
            mmkv.putInt(key, value).commit();
            notifyChange(key, value);
        }
    }


    private static void notifyChange(String key, Object value) {
        for (OnSharedPreferenceChangeListener listener : mOnSharedPreferenceChangeListeners) {
            listener.onSharedPreferenceChanged(key, value);
        }
    }

    public static void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        if (!mOnSharedPreferenceChangeListeners.contains(onSharedPreferenceChangeListener)) {
            mOnSharedPreferenceChangeListeners.add(onSharedPreferenceChangeListener);
        }
    }

    public static void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        mOnSharedPreferenceChangeListeners.remove(onSharedPreferenceChangeListener);
    }

    public interface OnSharedPreferenceChangeListener {
        void onSharedPreferenceChanged(String key, Object value);
    }


}
