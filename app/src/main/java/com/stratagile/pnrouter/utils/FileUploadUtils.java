package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * Created by zl on 2019/1/28
 */
public class FileUploadUtils {
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
    public static void doUpLoadWork(String path, String to, Context context, int msgId, Handler handler, String key){
        ///data/data/com.johnny.testzipanddownload/files
        File destDir = new File(to);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        FileUpLoaderTask task = new FileUpLoaderTask(path, to, context,msgId,handler,key);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

}
