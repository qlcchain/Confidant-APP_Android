package com.stratagile.pnrouter.utils;

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

    public static String getTimestampString(Date var0) {
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        long var4 = var0.getTime();
        if(isSameDay(var4)) {
            var1 = "hh:mm aa";
        } else if(isYesterday(var4)) {
            return (new SimpleDateFormat("hh:mm aa", Locale.ENGLISH)).format(var0) +" Yesterday" ;
        } else if(var3) {
            var1 = "dd hh:mm aa MMM";
        } else {
            var1 = "dd hh:mm aa MMM";
        }

        return (new SimpleDateFormat(var1, Locale.ENGLISH)).format(var0);
    }

    private static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }
}
