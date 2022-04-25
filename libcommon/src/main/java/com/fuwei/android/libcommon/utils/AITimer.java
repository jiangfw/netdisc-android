package com.fuwei.android.libcommon.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fuwei on 7/14/21.
 */
public class AITimer extends Timer {
    private static final String TAG = "AITimer";
    private static AITimer mTimer;
    private static Map<String, TimerTask> mTaskMap = new HashMap<String, TimerTask>();

    private AITimer() {
        super("aitimer_used_thread");
    }

    public static AITimer getInstance() {
        if (mTimer == null) {
            mTimer = new AITimer();
        }
        return mTimer;
    }

    public void startTimer(TimerTask task, String taskName, long millisec) {
        Log.i(TAG, "startTimer,taskName:" + taskName);
        TimerTask timerTask = mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }
        mTaskMap.put(taskName, task);
        try {
            mTimer.schedule(task, millisec);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void startTimer(TimerTask task, String taskName, int firstDelay, int millisecInterval) {
        Log.i(TAG, "startTimer,timer num" + mTaskMap.size());
        TimerTask timerTask = (TimerTask) mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }
        mTaskMap.put(taskName, task);
        try {
            mTimer.schedule(task, firstDelay, millisecInterval);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void cancelTimer(String taskName) {
        Log.i(TAG, "cancelTimer,taskName:" + taskName);
        TimerTask timerTask = (TimerTask) mTaskMap.get(taskName);
        if (timerTask != null) {
            timerTask.cancel();
            mTaskMap.remove(taskName);
        }
    }
}
