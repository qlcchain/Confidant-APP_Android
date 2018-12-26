package com.stratagile.pnrouter.utils;

/**
 * Created by zhang on 2018/12/27.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * SharedPreferences工具类，可以保存object对象
 */
public class SharedPreferenceUtil {

    /**
     * 存放实体类以及任意类型
     *
     * @param context 上下文对象
     * @param key
     * @param obj
     */
    public static void putParcelable(Context context, String key, Parcelable obj) {
        if (obj instanceof Parcelable) {// obj必须实现Serializable接口，否则会出问题
            try {
                // 1.序列化
                Parcel p = Parcel.obtain();
                obj.writeToParcel(p, 0);
                byte[] bytes = p.marshall();
                p.recycle();

                String string64 = new String(Base64.encode(bytes, 0));
                SharedPreferences.Editor editor = context.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                editor.putString(key, string64).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Parcelable");
        }

    }

    public static Parcel getBean(Context context, String key) {
        Parcel obj = null;
        try {
            String base64 = context.getSharedPreferences("config", Context.MODE_PRIVATE).getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            // 2.反序列化
            obj = unmarshall(base64Bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    private static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // this is extremely important!
        return parcel;
    }
}
