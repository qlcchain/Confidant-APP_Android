package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;

/**
 * Created by Administrator on 2016/6/30.
 */
public class FileDownloadUtils {
   private static ZipUnTask task;
    public static void doZipUnWork(String from, String to, Context context, Boolean replaceAll, Handler handler){
        task = new ZipUnTask(from, to, context, replaceAll,handler);
        task.execute();
    }
    public static void destroyZipUnTask()
    {
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
        }
    }
    public static void doDownLoadWork(String path,String to,Context context,int msgId,Handler handler,String key){
        ///data/data/com.johnny.testzipanddownload/files
        File destDir = new File(to);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        FileDownLoaderTask task = new FileDownLoaderTask(path, to, context,msgId,handler,key);
        task.execute();
    }
}
