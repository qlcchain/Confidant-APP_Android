package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareUtil {

    public ShareUtil() {
    }

    public static void share(Context context, File file) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent,"share"));
    }

    /**
     * 将图片存到本地
     */
    public static Uri saveBitmap(Context context,Bitmap bm,String dir) {
        try {
            File f = new File(Environment.getExternalStorageDirectory() + dir);
            Uri uri;
            if (!f.exists()) {
                f.createNewFile();
            }else{
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(context, "com.stratagile.pnrouter.fileprovider", f);
                } else {
                    uri = Uri.fromFile(f);
                }
                return uri;
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, "com.stratagile.pnrouter.fileprovider", f);
            } else {
                uri = Uri.fromFile(f);
            }
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
