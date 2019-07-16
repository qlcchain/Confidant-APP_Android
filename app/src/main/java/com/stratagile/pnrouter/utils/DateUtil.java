package com.stratagile.pnrouter.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;

import com.hyphenate.util.TimeInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        if(isSameDay(var4)) {
            if (is24Time(context)) {
                var1 = "HH:mm";
            } else {
                var1 = "hh:mm aa";
            }
        } else if(isYesterday(var4)) {
            if (is24Time(context)) {
                return (new SimpleDateFormat("HH:mm", Locale.ENGLISH)).format(var0) +" Yesterday" ;
            } else {
                return (new SimpleDateFormat("hh:mm aa", Locale.ENGLISH)).format(var0) +" Yesterday" ;
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
}
