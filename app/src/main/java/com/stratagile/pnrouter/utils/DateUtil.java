package com.stratagile.pnrouter.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;

import com.hyphenate.util.TimeInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static com.hyphenate.util.DateUtils.getTodayStartAndEndTime;
import static com.hyphenate.util.DateUtils.getYesterdayStartAndEndTime;

/**
 * Created by hjk on 2018/9/14.
 */

public class DateUtil {

    public DateUtil() {
    }

    public static String getTimestampString(Date var0, Context context) {
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        long var4 = var0.getTime();
        if(isSameDay(var4)) {//同一天
            if (is24Time(context)) {
                var1 = "HH:mm";
            } else {
                var1 = "hh:mm aa";
            }
        }  else if(isSameWeek(var4) && isSameYear(var4) ) {//同一周且在同一年
            var1 = "EEE";
        } else if(isDifferentWeek(var4) && isSameYear(var4) ) {//不在同一周，但在同一年
            var1 = "dd MMM";
        }else if(var3) {//不在同一年
            var1 = "dd/MM/yyyy";
        }

        return (new SimpleDateFormat(var1, Locale.ENGLISH)).format(var0);
    }

    public static String getTimestampString(long var0, Context context) {
        long result = var0 / 1000000000;
        Date date;
        if (result > 10) {
            date = new Date(var0);
        } else {
            date = new Date(var0 * 1000);
        }
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        long var4 = date.getTime();
        if(isSameDay(var4)) {
            if (is24Time(context)) {
                var1 = "HH:mm";
            } else {
                var1 = "hh:mm aa";
            }
        } else if(isYesterday(var4)) {
            if (is24Time(context)) {
                return (new SimpleDateFormat("HH:mm", Locale.ENGLISH)).format(date) +" Yesterday" ;
            } else {
                return (new SimpleDateFormat("hh:mm aa", Locale.ENGLISH)).format(date) +" Yesterday" ;
            }
        } else if(var3) {
            if (is24Time(context)) {
                var1 = "HH:mm MMM dd";
            } else {
                var1 = "hh:mm aa MMM dd";
            }
        } else {
            if (is24Time(context)) {
                var1 = "HH:mm MMM dd";
            } else {
                var1 = "hh:mm aa MMM dd";
            }
        }

        return (new SimpleDateFormat(var1, Locale.ENGLISH)).format(date);
    }

    private static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean is24Time(Context context) {
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
        if ("24".equals(strTimeFormat)) {
            return true;
        }
        return false;
    }
    /**
     * 时间格式转换方法，格式为 yyyy-MM-dd
     * @param dateStr
     * @return
     */
    public static Date getDate(String dateStr){
        try
        {
            SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (dateStr != null){
                return SimpleDateFormat.parse(dateStr);
            }else {
                return null;
            }
        }catch (Exception e)
        {

        }
        return new Date();
    }
    /**
     * 时间格式转换方法，格式为 yyyy-MM-dd
     * @param dateStr
     * @return
     */
    public static Long getDateTimeStame(String dateStr){
        try
        {
            SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (dateStr != null){
                return SimpleDateFormat.parse(dateStr).getTime();
            }else {
                return null;
            }
        }catch (Exception e)
        {

        }
        return new Date().getTime();
    }
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * start
     * 本周开始时间戳
     */
    public static long getWeekStartTime() {
        Calendar cal = Calendar.getInstance();
        // 获取星期日开始时间戳
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    //获取本年的开始时间
    public static long getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        return getDayStartTime(cal.getTime());
    }
    //获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(Integer.valueOf(1)));
    }
    //获取某个日期的开始时间
    public static long getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if(null != d){
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    private static boolean isSameWeek(long var0) {
        long var2 =  getWeekStartTime();
        return var0 >= var2;
    }
    private static boolean isDifferentWeek(long var0) {
        long var2 =  getWeekStartTime();
        return var0 < var2;
    }
    private static boolean isSameYear(long var0) {
        long var2 =  getBeginDayOfYear();
        return var0 >= var2;
    }
    private static boolean isDifferentYear(long var0) {
        long var2 =  getBeginDayOfYear();
        return var0 < var2;
    }
}
