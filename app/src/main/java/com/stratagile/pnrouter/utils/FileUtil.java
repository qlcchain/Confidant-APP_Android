package com.stratagile.pnrouter.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;

import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.PathUtils;
import com.smailnet.eamil.Utils.AESCipher;
import com.smailnet.eamil.Utils.AESToolsCipher;
import com.socks.library.KLog;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.db.RecentFile;
import com.stratagile.pnrouter.db.RecentFileDao;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okio.ByteString;


/**
 * Created by huzhipeng on 2018/3/7.
 */

public class FileUtil {

    private final int PLOY =  0X1021;
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] HEX_CHARKong = {' '};
    public static void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dataFile = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath(), "");
                if (!dataFile.exists()) {
                    dataFile.mkdir();
                }
                File routerList = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/RouterList", "");
                if (!routerList.exists()) {
                    routerList.mkdir();
                }
                File UserID = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/UserID", "");
                if (!UserID.exists()) {
                    UserID.mkdir();
                }
                File rsa = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/RA", "");
                if (!rsa.exists()) {
                    rsa.mkdir();
                }
                File temp = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/temp", "");
                if (!temp.exists()) {
                    temp.mkdir();
                }
                File Avatar = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/Avatar", "");
                if (!Avatar.exists()) {
                    Avatar.mkdir();
                }
                File picAndVideoTemp = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/PicAndVideoTemp", "");
                if (!picAndVideoTemp.exists()) {
                    picAndVideoTemp.mkdir();
                }
                KLog.i("文件夹初始化成功。。。。。。。。。。。。。");
            }
        }).start();
    }

    /**
     * 保存自己的User数据到本地sd卡
     * @param userData  用户数据
     * @param from 用途
     * @return
     */
    public static String saveUserData2Local(String userData, String from) {
        if(userData == null)
            return "";
        String lastP2pId = getLocalUserData(from);
        copyDataFile();
        String jsonPath = Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/UserID/"+from+".json";
        File jsonFile = new File(jsonPath);
        FileWriter fw = null;
        BufferedWriter out = null;
        try {
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }
            fw = new FileWriter(jsonFile);
            out = new BufferedWriter(fw);
            out.write(userData, 0, userData.length());
            out.close();
        } catch (Exception e) {
            System.out.println("保存数据异常" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return "";
    }
    /**
     * 获取sd卡已经保存的User数据
     * @param from 用途
     * @return
     */
    public static String getLocalUserData(String from) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        String userIdJson = "";
        try {
            File file = new File(Environment.getExternalStorageDirectory(), ConstantValue.INSTANCE.getLocalPath()+"/UserID/"+from+".json");
            if (!file.exists()) {
                return userIdJson;
            }
            fis = new FileInputStream(file);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            userIdJson = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
                if (ois != null) ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userIdJson;
    }

    /**
     * 复制data文件到backup文件夹
     */
    public static void copyDataFile() {
        copyFile(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/data", Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/backup/data");
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static int copyFile(String oldPath, String newPath) {
        KLog.i("复制文件。。。。。。");
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public static String getAssetJson(Context mContext, String fileName) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }
    public static String getJson(Context mContext, String fileName) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }

    /**
     * 保存路由器数据到sd卡
     *
     * @param userId  用户id
     * @param jsonStr 数据
     */
    public static void saveRouterData(String userId, String jsonStr) {
        String jsonPath = Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/RouterList/" + userId + ".json";
        File jsonFile = new File(jsonPath);

        FileWriter fw = null;
        BufferedWriter out = null;
        try {
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }
            fw = new FileWriter(jsonFile);
            out = new BufferedWriter(fw);
            out.write(jsonStr, 0, jsonStr.length());
            out.close();
        } catch (Exception e) {
            System.out.println("保持资产数据异常" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 读取本地SDk卡路由器数据
     *
     * @param userId 用户id
     * @return
     */
    public static String readRoutersData(String userId) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory(), ConstantValue.INSTANCE.getLocalPath()+"/RouterList/" + userId + ".json");
                if (!file.exists()) {
                    return "";
                }
                fis = new FileInputStream(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                String res = new String(buffer);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (ois != null) ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
    /**
     * 保存RA数据到sd卡
     *
     * @param jsonStr 数据
     */
    public static void saveKeyData(String jsonStr,String fileName) {
        String jsonPath = Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/RA/"+fileName+".json";
        File jsonFile = new File(jsonPath);

        FileWriter fw = null;
        BufferedWriter out = null;
        try {
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }
            fw = new FileWriter(jsonFile);
            out = new BufferedWriter(fw);
            out.write(jsonStr, 0, jsonStr.length());
            out.close();
        } catch (Exception e) {
            System.out.println("保持资产数据异常" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 读取本地SDk卡路由器数据
     *
     * @return
     */
    public static String readKeyData(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory(), ConstantValue.INSTANCE.getLocalPath()+"/RA/"+fileName+".json");
                if (!file.exists()) {
                    return "";
                }
                fis = new FileInputStream(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                String res = new String(buffer);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (ois != null) ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
    /**
     * 读取本地SDk卡所有路由器数据
     *
     * @return
     */
    public static String getAllRouterJsonNames() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory(), ConstantValue.INSTANCE.getLocalPath()+"/RouterList/");
                if (!file.exists()) {
                    return "";
                }
                File[] files = file.listFiles();
                String name = "";
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        if (name.equals("")) {
                            name += files[i].getName().replace(".json", "");
                        } else {
                            name += "," + files[i].getName().replace(".json", "");
                        }

                    }
                    return name;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (ois != null) ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 读取本地SDk卡所有钱包的地址
     *
     * @return
     */
    public static String getAllAddressNames() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory(), ConstantValue.INSTANCE.getLocalPath()+"/Address/");
                if (!file.exists()) {
                    return "";
                }
                File[] files = file.listFiles();
                String name = "";
                if (files != null && files.length > 0) {

                    for (int i = 0; i < files.length; i++) {
                        String data = getDataFromFile(files[i]);
                        String addData = data;
                        if (!"".equals(data) && data.length() >= 42) {
                            boolean isBase64 = StringUitl.isBase64(data);
                            if (!isBase64) {
                                try {
                                    FileOutputStream fos = new FileOutputStream(files[i]);
                                    OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                                    byte[] bytes = data.getBytes();
                                    String encryptPrivateKey = RxEncodeTool.base64Encode2String(bytes);
                                    addData = encryptPrivateKey;
                                    osw.write(encryptPrivateKey);
                                    osw.flush();
                                    fos.flush();
                                    osw.close();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            isBase64 = StringUitl.isBase64(addData);
                            if (isBase64) {
                                byte[] bytesAddData = RxEncodeTool.base64Decode(addData);
                                byte[] bytes = bytesAddData;
                                String decryptPrivateKey = new String(bytes);
                                addData = decryptPrivateKey;
                            }
                            if (!name.contains(addData)) {
                                if (name.equals("")) {
                                    name += addData;
                                } else {
                                    name += "," + addData;
                                }
                            }
                        }
                    }
                    return name;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (ois != null) ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 读取文件数据
     *
     * @param file
     * @return
     */
    public static String getDataFromFile(File file) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                if (!file.exists()) {
                    return "";
                }
                fis = new FileInputStream(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                String res = new String(buffer);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (ois != null) ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 保存数据到sd卡
     *
     * @param path 路径包括文件名称
     * @param data 数据
     */
    public static void savaData(String path, String data) {
        File walletFile = new File(Environment.getExternalStorageDirectory() + path, "");//ConstantValue.INSTANCE.getLocalPath()+"/Address/index.txt"
        if (!walletFile.exists()) {
            try {
                walletFile.createNewFile();
                try {
                    FileOutputStream fos = new FileOutputStream(walletFile);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                    osw.write(data);
                    osw.flush();
                    fos.flush();
                    osw.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(walletFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                osw.write(data);
                osw.flush();
                fos.flush();
                osw.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据路径获取sd卡数据
     *
     * @param path
     * @return
     */
    public static String readData(String path) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory(), path);
                if (!file.exists()) {
                    return "";
                }

                fis = new FileInputStream(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                String res = new String(buffer);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (ois != null) ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 保持vpn server数据到sd卡
     *
     * @param name    文件名称
     * @param jsonStr 数据内容
     */
    public static void saveVpnServerData(String name, String jsonStr) {
        String jsonPath = Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/vpn/" + name + ".ovpn";
        File jsonFile = new File(jsonPath);

        FileWriter fw = null;
        BufferedWriter out = null;
        try {
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }
            fw = new FileWriter(jsonFile);
            out = new BufferedWriter(fw);
            out.write(jsonStr, 0, jsonStr.length());
            out.close();
        } catch (Exception e) {
            System.out.println("保持vpn sever数据异常" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    /**
     * 获取单个文件的MD5值！
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024 * 1024 * 100];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024 * 1024 * 100)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String result =  bigInt.toString(16);
        if(result.length() != 32)
        {
            int count = 32-result.length();
            String addPre = "";
            for(int i = 0; i < count ;i++)
            {
                addPre +="0";
            }
            result  = addPre+ result;
        }
        return result;
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param file
     * @param listChild ;true递归子目录中的文件
     * @return
     */
    public static Map<String, String> getDirMD5(File file, boolean listChild) {
        if (!file.isDirectory()) {
            return null;
        }
        int leng = (int) file.length();
        Map<String, String> map = new HashMap<String, String>();
        String md5;
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory() && listChild) {
                map.putAll(getDirMD5(f, listChild));
            } else {
                md5 = getFileMD5(f);
                if (md5 != null) {
                    map.put(f.getPath(), md5);
                }
            }
        }
        return map;
    }

    public static ByteString readFile(File file) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        byte[] buffer = null;
        try {
            fis = new FileInputStream(file);
            buffer = new byte[fis.available()];
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) fis.close();
                if (ois != null) ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ByteString.of(buffer);
    }
    public static byte[] file2Byte(String filePath) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("file not exists");
            }
            bos = new ByteArrayOutputStream((int) file.length());
            in = new BufferedInputStream(new FileInputStream(file));
            int buf_size = (int) file.length();
            if(buf_size == 0 )
                buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
     * 在字节数组中截取指定长度数组
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        if (src.length < count) {
            System.arraycopy(src, begin, bs, 0, src.length);
        } else {
            System.arraycopy(src, begin, bs, 0, count);
        }
        return bs;
    }
    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }
    public static ByteString toByteString(byte[] bytes) {
        return ByteString.of(bytes);
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param  str 待转换的ASCII字符串
     * @param  str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param  src Byte字符串，每个Byte之间没有分隔符
     * @param  src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        int i=0;
        for(byte bt: bt1){
            bt3[i]=bt;
            i++;
        }

        for(byte bt: bt2){
            bt3[i]=bt;
            i++;
        }
        return bt3;
    }
    /*public int getCRS16(int[] bytes,int size) {
        int crc = 0;
        short i;
        for (; size > 0; size--) {
            crc = crc ^ (bytes++ <<8);
            for (i = 0; i < 8; i++) {
                if ((crc & 0X8000) != 0) {
                    crc = (crc << 1) ^ PLOY;
                }else {
                    crc <<= 1;
                }
            }
            crc &= 0XFFFF;
        }
        return crc;
    }*/

    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(AppConfig.instance, AppConfig.instance.getPackageName() + ".utils.MyFileProvider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(AppConfig.instance, AppConfig.instance.getPackageName() + ".utils.MyFileProvider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(String Path) {
        File file = new File(Path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(AppConfig.instance, AppConfig.instance.getPackageName() + ".utils.MyFileProvider", file);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }
    /**
     * bytes 转十六进制
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }
        return new String(buf);
    }
    /**
     * drawable转为file
     * @param mContext
     * @param drawableId  drawable的ID
     * @param fileName   转换后的文件名
     * @return
     */
    public static File drawableToFile(Context mContext,int drawableId,String fileName,int fileType){
//        InputStream is = view.getContext().getResources().openRawResource(R.drawable.logo);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
//        Bitmap bitmap = BitmapFactory.decodeStream(is);
        String defaultPath = PathUtils.getInstance().getFilePath()+"";
       /* switch (fileType)
        {
            case 1:
                defaultPath = PathUtils.getInstance().getImagePath()+"";
                break;
            case 2:
                defaultPath = PathUtils.getInstance().getVoicePath()+"";
                break;
            case 3:
                defaultPath = PathUtils.getInstance().getVideoPath()+"";
                break;
        }*/
        File file = new File(defaultPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String defaultImgPath = defaultPath + "/"+fileName;
        file = new File(defaultImgPath);
        if(!file.exists())
        {
            try {
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//            is.close();
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File getKongFile(String fileName) {
        // TODO Auto-generated method stub
        String defaultPath = PathUtils.getInstance().getFilePath()+"/" + fileName;
        File file = new File(defaultPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return file;
    }


    /**
     * 拷贝本地文件
     * @param fromFile
     * @param toFile
     * @return
     */
    public static void copySdcardFile(String fromFile, String toFile)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    InputStream fosfrom = new FileInputStream(fromFile);
                    OutputStream fosto = new FileOutputStream(toFile);
                    byte bt[] = new byte[1024 * 1024 * 10];
                    int c;
                    while ((c = fosfrom.read(bt)) > 0)
                    {
                        fosto.write(bt, 0, c);
                    }
                    fosfrom.close();
                    fosto.close();

                } catch (Exception ex)
                {

                }
            }
        }).start();

    }
    /**
     * 拷贝本地图片并压缩处理
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copySdcardPicAndCompress(String fromFile, String toFile,Boolean isCompress)
    {
        if(!isCompress)
        {
            try
            {
                InputStream fosfrom = new FileInputStream(fromFile);
                OutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0)
                {
                    fosto.write(bt, 0, c);
                }
                fosfrom.close();
                fosto.close();
                return 1;
            } catch (Exception ex)
            {

            }
        }else{
            //压缩图片..
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            int degree = EaseImageUtils.readPictureDegree(fromFile);
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile, bitmapOptions);
            if(degree != 0)
            {
                bitmap = EaseImageUtils.rotateToDegrees(bitmap, degree);
            }
            int code = saveBitmap(bitmap,toFile,true);
            return code;
        }
        return 0;
    }
    /**
     * 拷贝本地图片压缩并加密存储
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copySdcardToxPicAndEncrypt(String fromFile, String toFile, String aesKey,Boolean isCompress)
    {
        if(!isCompress)
        {
            try
            {
                InputStream fosfrom = new FileInputStream(fromFile);
                byte[] fileBufferMi =  FileUtil.InputStreamTOByte(fosfrom);
                byte [] miFile = AESToolsCipher.aesEncryptBytes(fileBufferMi,aesKey.getBytes("UTF-8"));
                fosfrom = FileUtil.byteTOInputStream(miFile);
                OutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0)
                {
                    fosto.write(bt, 0, c);
                }
                fosfrom.close();
                fosto.close();
                return 1;
            } catch (Exception ex)
            {

            }
        }else{
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile, bitmapOptions);
            String tempName = fromFile.substring(fromFile.lastIndexOf("/")+1);
            String tempPath = PathUtils.getInstance().getTempPath().toString()+"/" + tempName;
            int degree = EaseImageUtils.readPictureDegree(fromFile);
            if(degree != 0)
            {
                bitmap = EaseImageUtils.rotateToDegrees(bitmap, degree);
            }
            int code = saveBitmap(bitmap,tempPath,true);
            if(code == 1)
            {
                try
                {
                    InputStream fosfrom = new FileInputStream(tempPath);
                    byte[] fileBufferMi =  FileUtil.InputStreamTOByte(fosfrom);
                    byte [] miFile = AESToolsCipher.aesEncryptBytes(fileBufferMi,aesKey.getBytes("UTF-8"));
                    fosfrom = FileUtil.byteTOInputStream(miFile);
                    OutputStream fosto = new FileOutputStream(toFile);
                    byte bt[] = new byte[1024];
                    int c;
                    while ((c = fosfrom.read(bt)) > 0)
                    {
                        fosto.write(bt, 0, c);
                    }
                    fosfrom.close();
                    fosto.close();
                    return 1;
                } catch (Exception exx)
                {

                }
            }
            return 0;
        }
        return 0;
    }
    /**
     * 拷贝本地文件并加密存储
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copySdcardToxFileAndEncrypt(String fromFile, String toFile, String aesKey)
    {
        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            byte[] fileBufferMi =  FileUtil.InputStreamTOByte(fosfrom);
            byte [] miFile = AESToolsCipher.aesEncryptBytes(fileBufferMi,aesKey.getBytes("UTF-8"));
            fosfrom = FileUtil.byteTOInputStream(miFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();

        } catch (Exception ex)
        {
            return 0;
        }
        return 1;
    }
    /**
     * 拷贝本地文件并解密存储
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copySdcardToxFileAndDecrypt(String fromFile, String toFile, String aesKey)
    {
        File from = new File(fromFile);
        if(from.exists())
        {
            try
            {
                InputStream fosfrom = new FileInputStream(fromFile);
                byte[] fileBufferMi =  FileUtil.InputStreamTOByte(fosfrom);
                KLog.i("copySdcardToxFileAndDecrypt:"+aesKey);
                byte [] miFile = AESCipher.aesDecryptBytes(fileBufferMi,aesKey.getBytes("UTF-8"));
                fosfrom = FileUtil.byteTOInputStream(miFile);
                OutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0)
                {
                    fosto.write(bt, 0, c);
                }
                fosfrom.close();
                fosto.close();

            } catch (Exception ex)
            {
                return 0;
            }
            return 1;
        }else{
            return 0;
        }

    }
    /**
     * 拷贝下载的临时文件并解密到toFile文件
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copyTempFiletoFileAndDecrypt(String fromFile, String toFile, String aesKey)
    {
        File from = new File(fromFile);
        if(from.exists())
        {
            try
            {
                long size = from.length();
                InputStream fosfrom = new FileInputStream(fromFile);
                byte[] fileBufferMi =  FileUtil.InputStreamTOByte(fosfrom);
                byte [] miFile = AESCipher.aesDecryptBytes(fileBufferMi,aesKey.getBytes("UTF-8"));
                fosfrom = FileUtil.byteTOInputStream(miFile);
                OutputStream fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0)
                {
                    fosto.write(bt, 0, c);
                }
                fosfrom.close();
                fosto.close();

            } catch (Exception ex)
            {
                return 0;
            }
            return 1;
        }else{
            return 0;
        }

    }
    /**
     * 得到amr的时长
     *
     * @param file
     * @return amr文件时间长度
     * @throws IOException
     */
    public static int getAmrDuration(File file) {
        long duration = -1;
        int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0,
                0, 0 };
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            // 文件的长度
            long length = file.length();
            // 设置初始位置
            int pos = 6;
            // 初始帧数
            int frameCount = 0;
            int packedPos = -1;
            // 初始数据值
            byte[] datas = new byte[1];
            while (pos <= length) {
                randomAccessFile.seek(pos);
                if (randomAccessFile.read(datas, 0, 1) != 1) {
                    duration = length > 0 ? ((length - 6) / 650) : 0;
                    break;
                }
                packedPos = (datas[0] >> 3) & 0x0F;
                pos += packedSize[packedPos] + 1;
                frameCount++;
            }
            // 帧数*20
            duration += frameCount * 20;
        } catch (IOException e)
        {
            return  0;
        }
        finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
        return (int)((duration/1000)+1);
    }

    /**
     * 保存bitmap到本地
     * @param bitmap
     * @param path
     */
    public static void saveBitmpToFileOnThread(Bitmap bitmap, String path) {
        if(new File(path).exists())
        {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    try {
                        // 图片文件路径
                        File file = new File(path);
                        FileOutputStream os = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.flush();
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
    /**
     * 保存bitmap到本地
     * @param bitmap
     * @param path
     */
    public static void saveBitmpToFileNoThread(Bitmap bitmap, String path,int quality) {
        if(new File(path).exists())
        {
            return;
        }
        if (bitmap != null) {
            try {
                // 图片文件路径
                File file = new File(path);
                FileOutputStream os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    final static int BUFFER_SIZE = 4096;

    /**
     * 将InputStream转换成String
     * @param in InputStream
     * @return String
     * @throws Exception
     *
     */
    public static String InputStreamTOString(InputStream in) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),"ISO-8859-1");
    }

    /**
     * 将InputStream转换成某种字符编码的String
     * @param in
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in,String encoding) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),encoding);
    }

    /**
     * 将String转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream StringTOInputStream(String in) throws Exception{

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("ISO-8859-1"));
        return is;
    }

    /**
     * 将InputStream转换成byte数组
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }

    /**
     * 将byte数组转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream byteTOInputStream(byte[] in) throws Exception{

        ByteArrayInputStream is = new ByteArrayInputStream(in);
        return is;
    }
    /**
     * 将byte数组转换成InputStream
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream byteTOFileInputStream(byte[] in) throws Exception{

        FileInputStream is = new FileInputStream(new String(in));
        return is;
    }
    /**
     * 将byte数组转换成String
     * @param in
     * @return
     * @throws Exception
     */
    public static String byteTOString(byte[] in) throws Exception{

        InputStream is = byteTOInputStream(in);
        return InputStreamTOString(is);
    }

    /**
     *  删除单个文件
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                return file.delete();
            }
        }catch (Exception e)
        {

        }
        return false;
    }
    /**
     * 将图片压缩并且保存到本地
     */
    public static int saveBitmap(Bitmap bm,String dir,Boolean isCompress) {
        try {
            File f = new File(dir);
            Uri uri;
            if (!f.exists()) {
                f.createNewFile();
            }
            int quality = 100;
            if(isCompress)
            {
                quality = 70;
            }
            FileOutputStream out = new FileOutputStream(f);
            if(dir.endsWith(".jpg"))
            {
                bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            }else if(dir.endsWith(".png")){
                bm.compress(Bitmap.CompressFormat.PNG, quality, out);
            }
            out.flush();
            out.close();
            return 1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 将头像图片存到本地
     */
    public static int saveAvatarBitmap(Bitmap bm,String dir,int size) {
        try {
            File f = new File(dir);
            Uri uri;
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            if(dir.endsWith(".jpg"))
            {
                bm = compressImage(bm,size,Bitmap.CompressFormat.JPEG);
            }else if(dir.endsWith(".png")){
                bm = compressImage(bm,size,Bitmap.CompressFormat.PNG);
            }
            if(dir.endsWith(".jpg"))
            {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }else if(dir.endsWith(".png")){
                bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            out.flush();
            out.close();
            return 1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image ,int size,Bitmap.CompressFormat compressFormat) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(compressFormat, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(compressFormat, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    /**
     * 拷贝本地文件
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copyAppFileToSdcard(String fromFile, String toFile)
    {
        File file = new File(fromFile);
        if(!file.exists())
        {
            return 0;
        }
        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();

            return 1;
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    public static void recordRecentFile(String fileName, int opreateType, int fileType, String friendName) {
        RecentFile recentFile = AppConfig.instance.getMDaoMaster().newSession().getRecentFileDao().queryBuilder().where(RecentFileDao.Properties.FileName.eq(fileName), RecentFileDao.Properties.OpreateType.eq(opreateType)).unique();
        if (recentFile != null) {
            recentFile.setTimeStamp(Calendar.getInstance().getTimeInMillis());
            AppConfig.instance.getMDaoMaster().newSession().getRecentFileDao().update(recentFile);
        } else {
            recentFile = new RecentFile();
            recentFile.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
            recentFile.setFileName(fileName);
            recentFile.setFileType(fileType);
            recentFile.setFriendName(friendName);
            recentFile.setOpreateType(opreateType);
            recentFile.setTimeStamp(Calendar.getInstance().getTimeInMillis());
            AppConfig.instance.getMDaoMaster().newSession().getRecentFileDao().insert(recentFile);
        }
        EventBus.getDefault().post(recentFile);
    }
    public static boolean writeStr_to_txt(String path,String content) throws IOException{

        File F=new File(path);
        //如果文件不存在,就动态创建文件
        if(!F.exists()){
            F.createNewFile();
        }
        FileWriter fw=null;
        try {
            //设置为:True,表示写入的时候追加数据
            fw=new FileWriter(F);
            //回车并换行
            fw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fw!=null){
                fw.close();
            }
        }
        return false;
    }
    public static String readTxtFile(File file){
        String content = "";
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int len=inputStream.available();
            byte []buffer=new byte[len];
            inputStream.read(buffer);
            inputStream.close();
            content=new String(buffer);
            return content;
        }catch (Exception e)
        {

        }
        return "";
    }
    /** Exporting contacts from the phone */
    public static boolean exportContacts(Context context,String path) throws Exception {
        try{
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            int index = cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
            FileOutputStream fout = new FileOutputStream(path);
            byte[] data = new byte[1024 * 1];
            while (cur.moveToNext()) {
                String lookupKey = cur.getString(index);
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
                FileInputStream fin = fd.createInputStream();
                int len = -1;
                while ((len = fin.read(data)) != -1) {
                    fout.write(data, 0, len);
                }
                fin.close();
            }
            fout.close();
        }catch (Exception e)
        {
            return false;
        }

        return true;
    }

}
