package com.fuwei.android.libcommon.logger;

import android.content.Context;
import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtils {

    private static File mLogFile;

    public static final int MAX_FILE_WRITE_SIZE = 1024 * 1024;
    private static final String KEY_LOG_TIMESTAMP = "log_timestamp";
    private final static String logFileName = "hud_bug.txt";

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        if (throwable != null) {
            throwable.printStackTrace(pw);
        }
        return sw.getBuffer().toString();
    }

    /**
     * @param context
     * @param content
     */
    public synchronized static void saveLog(Context context, String content) {
        boolean isAppend = true;
        if (mLogFile == null || !mLogFile.exists()) {
            mLogFile = createLogFile(context, logFileName);
        } else {
            if (mLogFile.length() > MAX_FILE_WRITE_SIZE) {
                isAppend = false;
            }
        }

        if (mLogFile != null && mLogFile.canWrite()) {
            try {
                FileWriter fw = new FileWriter(mLogFile, isAppend);

                StringBuffer sb = new StringBuffer();
                JSONObject jsonLog = new JSONObject();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm",
                        Locale.getDefault());
                jsonLog.put("time", sdf.format(new Date()));
                jsonLog.put("content", content);
                sb.append(jsonLog.toString() + "\n\n\n");
                fw.write(sb.toString());
                fw.close();
                AILog.i("AIUncaughtExceptionHandler",
                        "path:" + mLogFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建log文件
     */
    private static File createLogFile(Context mContext, String fileName) {
        File logFile = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            logFile = new File(mContext.getExternalFilesDir(null), fileName);
        }
        if (logFile != null && !logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logFile;
    }

    public synchronized static File getLogFile(Context context) {
        if (mLogFile != null && mLogFile.canWrite()) {
            return mLogFile;
        }
        return null;
    }

}