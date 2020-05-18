package com.hyphenate.easeui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class OpenFileUtil {

    private static OpenFileUtil instance;
    private static Context context_;
    private static final HashMap<String, String> mapSimple = new HashMap<>();
    public static OpenFileUtil getInstance(Context context) {
        if (instance == null) {
            instance = new OpenFileUtil();
        }
        context_ = context;
        return instance;
    }
    public static Intent openFile(String filePath) {

        if (mapSimple.size() == 0) {
            mapSimple.put(".3gp", "video/3gpp");
            mapSimple.put(".apk", "application/vnd.android.package-archive");
            mapSimple.put(".asf", "video/x-ms-asf");
            mapSimple.put(".avi", "video/x-msvideo");
            mapSimple.put(".bin", "application/octet-stream");
            mapSimple.put(".bmp", "image/bmp");
            mapSimple.put(".c", "text/plain");
            mapSimple.put(".chm", "application/x-chm");
            mapSimple.put(".class", "application/octet-stream");
            mapSimple.put(".conf", "text/plain");
            mapSimple.put(".cpp", "text/plain");
            mapSimple.put(".doc", "application/msword");
            mapSimple.put(".docx", "application/msword");
            mapSimple.put(".exe", "application/octet-stream");
            mapSimple.put(".gif", "image/gif");
            mapSimple.put(".gtar", "application/x-gtar");
            mapSimple.put(".gz", "application/x-gzip");
            mapSimple.put(".h", "text/plain");
            mapSimple.put(".htm", "text/html");
            mapSimple.put(".html", "text/html");
            mapSimple.put(".jar", "application/java-archive");
            mapSimple.put(".java", "text/plain");
            mapSimple.put(".jpeg", "image/jpeg");
            mapSimple.put(".jpg", "image/jpeg");
            mapSimple.put(".js", "application/x-javascript");
            mapSimple.put(".log", "text/plain");
            mapSimple.put(".m3u", "audio/x-mpegurl");
            mapSimple.put(".m4a", "audio/mp4a-latm");
            mapSimple.put(".m4b", "audio/mp4a-latm");
            mapSimple.put(".m4p", "audio/mp4a-latm");
            mapSimple.put(".m4u", "video/vnd.mpegurl");
            mapSimple.put(".m4v", "video/x-m4v");
            mapSimple.put(".mov", "video/quicktime");
            mapSimple.put(".mp2", "audio/x-mpeg");
            mapSimple.put(".mp3", "audio/x-mpeg");
            mapSimple.put(".mp4", "video/mp4");
            mapSimple.put(".mpc", "application/vnd.mpohun.certificate");
            mapSimple.put(".mpe", "video/mpeg");
            mapSimple.put(".mpeg", "video/mpeg");
            mapSimple.put(".mpg", "video/mpeg");
            mapSimple.put(".mpg4", "video/mp4");
            mapSimple.put(".mpga", "audio/mpeg");
            mapSimple.put(".msg", "application/vnd.ms-outlook");
            mapSimple.put(".ogg", "audio/ogg");
            mapSimple.put(".pdf", "application/pdf");
            mapSimple.put(".png", "image/png");
            mapSimple.put(".pps", "application/vnd.ms-powerpoint");
            mapSimple.put(".ppt", "application/vnd.ms-powerpoint");
            mapSimple.put(".pptx", "application/vnd.ms-powerpoint");
            mapSimple.put(".prop", "text/plain");
            mapSimple.put(".rar", "application/x-rar-compressed");
            mapSimple.put(".rc", "text/plain");
            mapSimple.put(".rmvb", "audio/x-pn-realaudio");
            mapSimple.put(".rtf", "application/rtf");
            mapSimple.put(".sh", "text/plain");
            mapSimple.put(".tar", "application/x-tar");
            mapSimple.put(".tgz", "application/x-compressed");
            mapSimple.put(".txt", "text/plain");
            mapSimple.put(".json", "text/plain");
            mapSimple.put(".ovpn", "text/plain");
            mapSimple.put(".wav", "audio/x-wav");
            mapSimple.put(".wma", "audio/x-ms-wma");
            mapSimple.put(".wmv", "audio/x-ms-wmv");
            mapSimple.put(".wps", "application/vnd.ms-works");
            mapSimple.put(".xml", "text/plain");
            mapSimple.put(".xls", "application/vnd.ms-excel");
            mapSimple.put(".xlsx", "application/vnd.ms-excel");
            mapSimple.put(".z", "application/x-compress");
            mapSimple.put(".zip", "application/zip");
            mapSimple.put("", "*/*");
        }
        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
        String mimeType = getMimeTypeFromFile(end);
        if (end.contains("m4a") || end.contains("mp3") || end.contains("mid") || end.contains("xmf") || end.contains("ogg") || end.contains("wav") || end.contains("3gp") || end.contains("mp4")) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context_, "com.stratagile.pnrouter.fileprovider", new File(filePath));
            } else {
                uri = Uri.fromFile(new File(filePath));
            }
            intent.setDataAndType(uri, mimeType);
            return intent;
        }else{
            Intent intent = new Intent("android.intent.action.VIEW");
//            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context_, "com.stratagile.pnrouter.fileprovider", new File(filePath));
            } else {
                uri = Uri.fromFile(new File(filePath));
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if(new File(filePath).exists())
            {
                String aa = "";
            }
            intent.setDataAndType(uri, mimeType);
            return intent;
        }
        /* 依扩展名的类型决定MimeType */
       /* if (end.contains("m4a") || end.contains("mp3") || end.contains("mid") || end.contains("xmf") || end.contains("ogg") || end.contains("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.contains("3gp") || end.contains("mp4")) {
            return getVideoFileIntent(filePath);
        } else if (end.contains("jpg") || end.contains("gif") || end.contains("png") || end.contains("jpeg") || end.contains("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.contains("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.contains("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.contains("xls")) {
            return getExcelFileIntent(filePath);
        } else if (end.contains("doc")) {
            return getWordFileIntent(filePath);
        } else if (end.contains("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.contains("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.contains("txt")) {
            return getTextFileIntent(filePath, false);
        } else if (end.contains("ovpn")) {
            return getTextFileIntent(filePath, false);
        }else {
            return getAllIntent(filePath);
        }*/
    }
    /**
     * 使用自定义方法获得文件的MIME类型
     */
    public static String getMimeTypeFromFile(String end) {
        String type = "*/*";
        type = mapSimple.get("."+end);
        Log.i("bqt", "我定义的MIME类型为：" + type);
        if(type == null )
        {
            type = "*/*";
        }
        return type;
    }
    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            if (Build.VERSION.SDK_INT >= 24) {
                uri2 = FileProvider.getUriForFile(context_, "com.stratagile.pnrouter.fileprovider", new File(param));
            } else {
                uri2 = Uri.fromFile(new File(param));
            }
            intent.setDataAndType(uri2, "text/plain");
        }

        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

}
