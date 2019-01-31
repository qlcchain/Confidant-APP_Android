package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

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
public class FileMangerDownloadUtils {
    private static ZipUnTask task;
    private static HashMap<String, String> downFilePathMap = new HashMap<>();
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
        if(!downFilePathMap.containsKey(msgId+""))
        {
            String fileNiName = path.substring(path.lastIndexOf("/")+1,path.length());
            UpLoadFile uploadFile = new UpLoadFile(fileNiName,path,0, true, false, false,0,1,0,false,key);
            MyFile myRouter = new MyFile();
            myRouter.setType(0);
            myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
            myRouter.setUpLoadFile(uploadFile);
            LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
            EventBus.getDefault().post(new FileStatus(fileNiName,0, true, false, false,0,1,0,false,0));

            downFilePathMap.put(msgId+"",path);
            FileMangerDownLoaderTask task = new FileMangerDownLoaderTask(path, to, context,msgId,handler,key,downFilePathMap);
            task.execute();
        }
    }

}
