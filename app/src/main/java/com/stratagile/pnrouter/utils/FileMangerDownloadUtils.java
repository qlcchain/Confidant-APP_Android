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
import java.util.Iterator;

/**
 * Created by Administrator on 2016/6/30.
 */
public class FileMangerDownloadUtils {
    private static ZipUnTask task;
    private static HashMap<String, String> downFilePathMap = new HashMap<>();
    private static HashMap<String, FileMangerDownLoaderTask> taskListMap = new HashMap<>();
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
    public static void init() {
        downFilePathMap = new HashMap<>();
        for (String key : taskListMap.keySet()) {
            FileMangerDownLoaderTask FileMangerDownLoaderTask = taskListMap.get(key);
            FileMangerDownLoaderTask.cancel(true);
        }
    }

    public static void doDownLoadWork(String path,String fileName,String to,Context context,int msgId,Handler handler,String key,int FileFrom){
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
            if(!downFilePathMap.containsKey(msgId+""))
            {
                KLog.i("FiledoDownLoadWork:"+path+"_TO::"+to);
                String fileNiName = fileName;
                UpLoadFile uploadFile = new UpLoadFile(fileNiName,path,0, true, false, "0",0,1,0,false,key,FileFrom,0,msgId+"",false);
                MyFile myRouter = new MyFile();
                myRouter.setType(0);
                myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
                myRouter.setUpLoadFile(uploadFile);
                LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
                EventBus.getDefault().post(new FileStatus(fileNiName +"__"+msgId,0, true, false, false,0,1,0,false,0));

                downFilePathMap.put(msgId+"",path);
                FileMangerDownLoaderTask task = new FileMangerDownLoaderTask(path,fileName, to, context,msgId,handler,key,downFilePathMap,FileFrom);
                task.execute();
                taskListMap.put(msgId+"",task);
            }else{

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void cancelWork(int msgId){

        try
        {
            FileMangerDownLoaderTask task = taskListMap.get(msgId+"");
            if(task != null)
            {
                task.cancelWork();
                taskListMap.remove(msgId+"");
                downFilePathMap.remove(msgId+"");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

}
