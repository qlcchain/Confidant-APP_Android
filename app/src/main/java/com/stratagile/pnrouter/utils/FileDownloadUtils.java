package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.socks.library.KLog;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.MyFile;
import com.stratagile.pnrouter.entity.events.FileStatus;
import com.stratagile.pnrouter.entity.file.UpLoadFile;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;

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
        KLog.i("ChatdoDownLoadWork:"+path+"_TO::"+to);
        FileDownLoaderTask task = new FileDownLoaderTask(path, to, context,msgId,handler,key);
        task.execute();
    }

}
