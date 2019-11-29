package com.hyphenate.easeui.utils;

/**
 * Created by hjk on 2018/10/17.
 */

import android.content.Context;
import android.os.Environment;
import java.io.File;

public class PathUtils {
    public static String pathPrefix;
    public static final String historyPathName = "/chat/";
    public static final String imagePathName = "/image/";
    public static final String voicePathName = "/voice/";
    public static final String filePathName = "/file/";
    public static final String videoPathName = "/video/";
    public static final String netdiskDownloadPathName = "/netdisk/";
    public static final String meetingPathName = "/meeting/";
    private static File storageDir = null;
    private static PathUtils instance = null;
    private File voicePath = null;
    private File imagePath = null;
    private File historyPath = null;
    private File videoPath = null;
    private File filePath;
    private File tempPath = null;
    private File emailPath = null;
    private File encryptionPath = null;
    private PathUtils() {
    }

    public static PathUtils getInstance() {
        if(instance == null) {
            instance = new PathUtils();
        }

        return instance;
    }

    public void initDirs(String var1, String var2, Context var3) {
        String var4 = var3.getPackageName();
        pathPrefix =  var3.getFilesDir().getAbsolutePath();
        this.voicePath = generateVoicePath(var1, var2, var3);
        if(!this.voicePath.exists()) {
            this.voicePath.mkdirs();
        }

        this.imagePath = generateImagePath(var1, var2, var3);
        if(!this.imagePath.exists()) {
            this.imagePath.mkdirs();
        }
        this.tempPath = generateTempPath(var1, var2, var3);
        if(!this.tempPath.exists()) {
            this.tempPath.mkdirs();
        }
        this.emailPath = generateEmailPath(var1, var2, var3);
        if(!this.emailPath.exists()) {
            this.emailPath.mkdirs();
        }

        this.historyPath = generateHistoryPath(var1, var2, var3);
        if(!this.historyPath.exists()) {
            this.historyPath.mkdirs();
        }

        this.videoPath = generateVideoPath(var1, var2, var3);
        if(!this.videoPath.exists()) {
            this.videoPath.mkdirs();
        }

        this.filePath = generateFiePath(var1, var2, var3);
        if(!this.filePath.exists()) {
            this.filePath.mkdirs();
        }
        this.encryptionPath=  generateEncryptionPath(var1, var2, var3);
        if(!this.encryptionPath.exists()) {
            this.encryptionPath.mkdirs();
        }
    }

    public File getImagePath() {
        return this.filePath;//统一一个文件夹，方便tox处理
    }

    public File getVoicePath() {
        return this.filePath;//统一一个文件夹，方便tox处理
    }

    public File getFilePath() {
        return this.filePath;//统一一个文件夹，方便tox处理
    }
    public File getTempPath() {
        return this.tempPath;
    }
    public File getVideoPath() {
        return this.filePath;
    } //统一一个文件夹，方便tox处理

    public File getHistoryPath() {
        return this.historyPath;
    }

    public File getEncryptionPath(){return  this.encryptionPath;}

    private static File getStorageDir(Context var0) {
        if(storageDir == null) {
            File var1 = Environment.getExternalStorageDirectory();
            if(var1.exists()) {
                return var1;
            }

            storageDir = var0.getFilesDir();
        }

        return storageDir;
    }

    private static File generateImagePath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/image/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/image/";
        }

        return new File(var3);
    }
    private static File generateTempPath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/temp/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/temp/";
        }

        return new File(var3);
    }
    private static File generateEmailPath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/email/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/email/";
        }

        return new File(var3);
    }
    public static String generateEmailMessagePath(String name) {
        String var3 = null;
        var3 = pathPrefix + "/email/"+name+"/";
        File newFile = new File(var3);
        if(!newFile.exists()) {
            newFile.mkdirs();
        }
        return var3;
    }
    private static File generateVoicePath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/voice/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/voice/";
        }

        return new File(var3);
    }

    private static File generateFiePath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/file/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/file/";
        }

        return new File(var3);
    }
    private static File generateEncryptionPath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/encryption/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/encryption/";
        }

        return new File(var3);
    }
    private static File generateVideoPath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/video/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/video/";
        }

        return new File(var3);
    }

    private static File generateHistoryPath(String var0, String var1, Context var2) {
        String var3 = null;
        if(var0 == null) {
            var3 = pathPrefix + var1 + "/chat/";
        } else {
            var3 = pathPrefix + var0 + "/" + var1 + "/chat/";
        }

        return new File(var3);
    }

    public static File getTempPath(File var0) {
        return new File(var0.getAbsoluteFile() + ".tmp");
    }
}
