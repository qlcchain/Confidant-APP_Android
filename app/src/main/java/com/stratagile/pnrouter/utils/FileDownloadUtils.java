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
        task = new ZipUnTask(from, to, context, replaceAll,handler,true);
        task.execute();
    }
    public static void destroyZipUnTask()
    {
        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
        }
    }

    /**
     *
     * @param path
     * @param to
     * @param context
     * @param msgId
     * @param handler
     * @param key
     * @param type 0 正常解密  1 群聊中收到文件转发的aes解密
     */
    public static void doDownLoadWork(String path,String fileName,String to,Context context,int msgId,Handler handler,String key,String type){
        ///data/data/com.johnny.testzipanddownload/files
        if(path.indexOf("https://:") > -1)
        {
            return;
        }
        try
        {
            File destDir = new File(to);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            KLog.i("ChatdoDownLoadWork:"+path+"_TO::"+to+"_key:"+key+"_type:"+type);
            FileDownLoaderTask task = new FileDownLoaderTask(path,fileName, to, context,msgId,handler,key,type);
            task.execute();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
