
package com.stratagile.pnrouter.utils;

import java.util.ArrayList;

/**
 * Created by huzhipeng on 2018/2/23.
 */

public class LogUtil {
    public static ArrayList<String> logList = new ArrayList<>();
    public static OnLogListener onLogListener;
    public static boolean isShowLog = true;
    public static void addLog(String logInfo, String classInfo) {
        if (isShowLog) {
            if (onLogListener != null) {
                onLogListener.onLog(TimeUtil.getTime() + "  " + classInfo + "  " + logInfo);
            } else {
                logList.add(TimeUtil.getTime() + "  " + classInfo + "  " + logInfo);
            }
        }
        if (logList.size() > 5000) {
            logList.clear();
        }

    }
    public static void addLog(String logInfo) {
        if (isShowLog) {
            if (onLogListener != null) {
                onLogListener.onLog(TimeUtil.getTime() + "  " + logInfo);
            } else {
                logList.add(TimeUtil.getTime() + "  " + logInfo);
            }
        }
        if (logList.size() > 5000) {
            logList.clear();
        }
    }

    public interface OnLogListener {
        void onLog(String logInfo);
    }

}
