
package com.stratagile.pnrouter.utils;

/**
 * Created by huzhipeng on 2018/2/23.
 */

public class LogUtil {
    public static StringBuilder mLogInfo = new StringBuilder();
    public static boolean isShowLog = true;
    public static void addLog(String logInfo, String classInfo) {
        if(mLogInfo.length() >  2 * 1024 * 1024)
        {
            mLogInfo.setLength(0);
        }
        if (isShowLog) {
            mLogInfo.append(classInfo + "  " + TimeUtil.getTime() + "  " + logInfo + "\n\n");
        }

    }
    public static void addLog(String logInfo) {
        if(mLogInfo.length() >  2 * 1024 * 1024)
        {
            mLogInfo.setLength(0);
        }
        if (isShowLog) {
            mLogInfo.append(TimeUtil.getTime() + "  " + logInfo + "\n\n");
        }
    }

}
