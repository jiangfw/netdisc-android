package com.fuwei.android.libcommon.logger;

import android.os.Process;
import android.util.Log;

import com.fuwei.android.libcommon.BasePrefHelper;
import com.fuwei.android.libcommon.context.GlobalApplication;
import com.fuwei.android.libcommon.utils.DateUtil;
import com.fuwei.android.libcommon.utils.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AILog {
    private static boolean USE_FORMAT_LOGGER = false;
    public static final String LOG_FILE = "Log.txt";

    public static void v(String tag, String msg) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).v(msg);
        } else {
            if (msg != null) {
                Log.v(tag, msg);
            }
        }
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        writeFile(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).d(msg);
        } else {
            if (msg != null) {
                Log.d(tag, msg);
            }
        }
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        writeFile(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).i(msg);
        } else {
            if (msg != null) {
                Log.i(tag, msg);
            }
        }
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        writeFile(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).w(msg);
        } else {
            if (msg != null) {
                Log.w(tag, msg);
            }
        }
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        writeFile(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).e(msg);
        } else {
            if (msg != null) {
                Log.e(tag, msg);
            }
        }
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        writeFile(tag, msg);
    }

    public static void e(String tag, String msg, Throwable ex) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).e(ex, msg);
        } else {
            if (msg != null) {
                Log.e(tag, msg, ex);
            }
        }
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        String stackTrace = LogUtils.getStackTrace(ex);
        writeFile(tag, msg + "\n" + stackTrace);
    }

    public static void j(String tag, String msg) {
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        Logger.t(tag).json(msg);
        writeFile(tag, msg);
    }


    public static void x(String tag, String msg) {
        if (Logger.init().getLogLevel() != LogLevel.FULL)
            return;
        Logger.t(tag).xml(msg);
        writeFile(tag, msg);
    }

    /**
     * @param tag
     * @param msg
     */

    public static void v(String tag, String msg, LogLevel logLevel) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).v(msg);
        } else {
            if (msg != null) {
                Log.v(tag, msg);
            }
        }
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        writeFile(tag, msg);
    }

    public static void d(String tag, String msg, LogLevel logLevel) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).d(msg);
        } else {
            if (msg != null) {
                Log.d(tag, msg);
            }
        }
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        writeFile(tag, msg);
    }

    public static void i(String tag, String msg, LogLevel logLevel) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).i(msg);
        } else {
            if (msg != null) {
                Log.i(tag, msg);
            }
        }
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        writeFile(tag, msg);
    }

    public static void w(String tag, String msg, LogLevel logLevel) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).w(msg);
        } else {
            if (msg != null) {
                Log.w(tag, msg);
            }
        }
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        writeFile(tag, msg);
    }

    public static void e(String tag, String msg, LogLevel logLevel) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).e(msg);
        } else {
            if (msg != null) {
                Log.e(tag, msg);
            }
        }
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        writeFile(tag, msg);
    }

    public static void e(String tag, String msg, Throwable ex, LogLevel logLevel) {
        if (USE_FORMAT_LOGGER) {
            Logger.t(tag).e(ex, msg);
        } else {
            if (msg != null) {
                Log.e(tag, msg, ex);
            }
        }
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        String stackTrace = LogUtils.getStackTrace(ex);
        writeFile(tag, msg + "\n" + stackTrace);
    }

    public static void j(String tag, String msg, LogLevel logLevel) {
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        Logger.t(tag).json(msg);
        writeFile(tag, msg);
    }


    public static void x(String tag, String msg, LogLevel logLevel) {
        if (Logger.init().getLogLevel().canLog(logLevel) == false)
            return;
        Logger.t(tag).xml(msg);
        writeFile(tag, msg);
    }

    /**
     * @param tag
     * @param msg
     */
    public static void writeFile(final String tag, final String msg) {

        FileUtil.getSaveFileThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                try {
                    if (logOutofDate() && logOutofSize()) {
                        boolean flag = new File(FileUtil.getExternalCacheDir(GlobalApplication.getApplication()) + File.separator + LOG_FILE).delete();//rm ai log
                        String newLine = "flag = " + flag + ":delete Old LogFile==============================================";
                        FileUtil.syncSaveStringToFile(FileUtil.getExternalCacheDir(GlobalApplication.getApplication()) + File.separator + LOG_FILE, sdf.format(System.currentTimeMillis()) + " [" + Process.myPid() + "] (" + tag + ") " + newLine + "\n", flag);
                    }
                    FileUtil.syncSaveStringToFile(FileUtil.getExternalCacheDir(GlobalApplication.getApplication()) + File.separator + LOG_FILE, sdf.format(System.currentTimeMillis()) + " [" + Process.myPid() + "] (" + tag + ") " + msg + "\n", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final long SecondOfDay = 60 * 60 * 24;
    private static final long MaxOfSize = 1024 * 1024 * 20;

    private static long oldTime = 0;
    private static long curTime = 0;

    private static boolean logOutofSize() {
        File logFile = new File(FileUtil.getExternalCacheDir(GlobalApplication.getApplication()) + File.separator + LOG_FILE);
        if (logFile.exists() == false) {
            return false;
        }

        if (logFile.length() < MaxOfSize) {
            return false;
        }

        return true;
    }

    private static boolean logOutofDate() {
        if (curTime == 0) {
            curTime = DateUtil.getTimeSecondFrom2011();
        }

        if (oldTime == 0) {
            oldTime = BasePrefHelper.getLogTimeStamp(GlobalApplication.getApplication());

            if (oldTime == 0) {
                BasePrefHelper.setLogTimeStamp(GlobalApplication.getApplication(), curTime);
            }
        }

        if (oldTime > 0 && curTime - oldTime > SecondOfDay) {
            BasePrefHelper.setLogTimeStamp(GlobalApplication.getApplication(), curTime);
            return true;
        }
        return false;
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);

}