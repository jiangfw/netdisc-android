package com.fuwei.android.libcommon.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 应用中获取时间的工具类。
 *
 * @author aispeech
 */
public class DateUtil {

    /**
     * 返回时间,时间制与系统一致
     *
     * @return eg:11:11
     */
    public static String getTimeStr(Context context) {
        SimpleDateFormat format;
        if (DateFormat.is24HourFormat(context)) {
            format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            format = new SimpleDateFormat("hh:mm", Locale.getDefault());
        }

        return format.format(new Date());
    }

    /**
     * 返回日期
     *
     * @return eg: 11月11日 周一
     */
    public static String getDateStr(Context context) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日",
                Locale.getDefault());
        sb.append(format.format(new Date()));
        sb.append(" ");
        Calendar calendar = Calendar.getInstance();
        int weekField = calendar.get(Calendar.DAY_OF_WEEK);
        String dayOfWeekStr = null;
        switch (weekField) {
            case Calendar.SUNDAY:
                dayOfWeekStr = "周日";
                break;
            case Calendar.MONDAY:
                dayOfWeekStr = "周一";
                break;
            case Calendar.TUESDAY:
                dayOfWeekStr = "周二";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeekStr = "周三";
                break;
            case Calendar.THURSDAY:
                dayOfWeekStr = "周四";
                break;
            case Calendar.FRIDAY:
                dayOfWeekStr = "周五";
                break;
            case Calendar.SATURDAY:
                dayOfWeekStr = "周六";
                break;
        }
        sb.append(dayOfWeekStr);
        return sb.toString();
    }

    public static String getTodayDateStr() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",
                Locale.getDefault());
        return format.format(new Date());
    }

    /**
     * 把时间类型转换成字符串形式(yyyy-MM-dd HH:mm:ss)
     */
    public static String getNowDateString() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return f.format(new Date());
    }

    public static long getDayTimeSpan(long days) {
        return 1000 * 3600 * 24 * days;
    }

    public static String getTimeFromMS(long timeInMS) {
        long hours = timeInMS / 3600000;
        long minutes = (timeInMS % 3600000) / 60000;
        long seconds = (timeInMS % 60000) / 1000;
        if (hours > 0) {
            return String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 获取时间串
     *
     * @return
     */
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    /**
     * 获取自2011年到现在的秒数
     */
    public static long getTimeSecondFrom2011() {
        Date dt = new Date();

        long beginTime = 0;
        try {
            SimpleDateFormat formater = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = formater.parse("2011-01-01 00:00:00");
            beginTime = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long time = dt.getTime() - beginTime;

        return time / 1000;
    }
}