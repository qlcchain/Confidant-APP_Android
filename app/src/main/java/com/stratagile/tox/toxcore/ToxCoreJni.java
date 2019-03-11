package com.stratagile.tox.toxcore;

import android.os.Environment;

import com.google.gson.Gson;
import com.socks.library.KLog;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.FileUtil;
import com.stratagile.pnrouter.utils.LogUtil;
import events.ToxReceiveFileFinishedEvent;
import events.ToxReceiveFileNoticeEvent;
import events.ToxReceiveFileProgressEvent;
import events.ToxSendFileFinishedEvent;
import events.ToxSendFileProgressEvent;

import com.stratagile.tox.entity.DhtJson;
import com.stratagile.tox.entity.DhtNode;
import com.stratagile.tox.toxcallback.ToxCallbackListener;
import com.stratagile.tox.toxcallback.ToxConnection;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;

public class ToxCoreJni {
    //tox单例的实例
    private static ToxCoreJni instance;
    //tox的连接状态
    public static ToxConnection connectStatus;
    //tox回调的监听
    private ToxCallbackListener toxCallbackListener;

    public ToxCallbackListener getToxCallbackListener() {
        return toxCallbackListener;
    }

    public void setToxCallbackListener(ToxCallbackListener toxCallbackListener) {
        this.toxCallbackListener = toxCallbackListener;
    }
    private HashMap<String,String> sendFileRouterMap = new HashMap<>();
    private HashMap<String,String> reveiveFileNumberMap = new HashMap<>();
    private HashMap<String,Boolean> progressSendMap = new HashMap<>();
    private HashMap<String,Boolean> progressReceiveMap = new HashMap<>();
    private int progressBarMaxSeg = 25;
    /**
     * 获取单例
     * @return toxCoreJni实例
     */
    public static ToxCoreJni getInstance() {
        if (instance == null) {
            instance = new ToxCoreJni();
            connectStatus = ToxConnection.NONE;
            File dataFile = new File(Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath(), "");
            if (!dataFile.exists()) {
                dataFile.mkdir();
            }
        }
        return instance;
    }

    /**
     * 新建tox实例
     * @param dataPath tox保存的toxid的文件路径
     */
    public native void createTox(String dataPath);

    /**
     * 主动获取自己的tox状态的方法
     * c层会回调自己tox状态改变的方法
     */
    public native void getToxStatus();

    /**
     * 好友连接状态改变的回调。
     * @param friendId
     * @param status
     */
    public void freindStatus(String friendId, int status) {
        KLog.i("好友的状态为：" + status);
        KLog.i("好友的toxId为：" + friendId);
        if (toxCallbackListener!= null) {
            toxCallbackListener.toxFreindConnectionStatus(friendId, ToxConnection.Companion.parseToxConnect(status));
        }
    }

    /**
     * c层打印log到Androidstudio控制台
     * @param string
     */
    public void showLog(String string) {
        EventBus.getDefault().post(string);
        KLog.i("c层需要打印的log：" + string);
    }

    /**
     * 删除好友，改应用不需要调用
     * @param friendId
     * @return
     */
    public native int deleteFriend(String friendId);

    /**
     * 添加好友
     * @param friendId
     * @return 返回大于0，则是添加成功，改好友在tox中的list集合的索引
     * 小于0，则是添加失败。
     */
    public native int addFriend(String friendId);

    public int senToxMessage(String message, String friendId)
    {
       int result =  sendMessage(message, friendId);
        if(message.indexOf("HeartBeat") < 0)
        {
            LogUtil.addLog("Tox发送消息:"+message +"  result:" +result);
        }
       return result;
    }

    /**
     * 发送文字消息给好友。
     * @param message 文字消息内容
     * @param friendId 好友id
     * @return 返回大于0，发送成功，返回的是该消息在消息中的索引
     * 小于0，发送失败
     */
    public native int sendMessage(String message, String friendId);

    public int senToxFile(String filePath, String friendId)
    {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        KLog.i(filePath);
        int result =  sendFile(filePath, friendId, fileName);
        KLog.i("发送Tox文件:" + fileName +"  result:" +result);
        LogUtil.addLog("发送Tox文件:",filePath +"  result:" +result);
        return result;
    }
    public int senToxFileInManger(String filePath, String friendId,String msgId)
    {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        KLog.i(filePath);
        int result =  sendFile(filePath, friendId, "u:"+fileName);
        KLog.i("发送Tox文件:" + fileName +"  result:" +result);
        LogUtil.addLog("发送Tox文件:",filePath +"  result:" +result);
        return result;
    }
    public int senToxAvatarFile(String filePath, String friendId,String msgId)
    {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        KLog.i(filePath);
        int result =  sendFile(filePath, friendId, "a:"+fileName);
        KLog.i("发送Tox文件:" + fileName +"  result:" +result);
        LogUtil.addLog("发送Tox文件:",filePath +"  result:" +result);
        return result;
    }
    public native int sendFile(String filePath, String friendId, String fileName);

    /**
     * 关闭tox。app退出时调用。
     */
    public native void toxKill();


    public native byte[] sodiumCryptoSeedKeyPair(byte[] publicKey, byte[] privateKey, byte[] seed);

    /**
     * 自己的状态改变，由c调用
     * 0 无连接
     * 1 tcp
     * 2 udp
     */
    public void callSelfChange(int status) {
        KLog.i("tox状态改变！！！！！！！！！！");
        if (status == 1) {
            if (toxCallbackListener!= null) {
                toxCallbackListener.toxSelfConnectionStatus(ToxConnection.TCP);
            }
            connectStatus = ToxConnection.TCP;
        } else if (status == 2){
            if (toxCallbackListener!= null) {
                toxCallbackListener.toxSelfConnectionStatus(ToxConnection.UDP);
            }
            connectStatus = ToxConnection.UDP;
        } else {
            if (toxCallbackListener!= null) {
                toxCallbackListener.toxSelfConnectionStatus(ToxConnection.NONE);
            }
            connectStatus = ToxConnection.NONE;
        }
        KLog.i("tox状态改变！！！！！！！！！！");
    }

    public native int bootStrap(String address, int port, String publicKey);

    public native void iterate();

    /**
     * tox消息的回调
     * @param message
     */
    public void receivedMessage(String friendNumber, String message) {
        if (toxCallbackListener != null) {
            toxCallbackListener.toxOnMessage(friendNumber, message);
        }
    }

    public void startReceiveFile(int fileNumber, String fileName, String routerId) {
        KLog.i("开始接收的文件序号：" + fileNumber);
        KLog.i("开始接收的文件名：" + fileName);
        KLog.i("路由器的Id是：" + routerId);
        reveiveFileNumberMap.put(routerId,fileNumber+"");
        EventBus.getDefault().post(new ToxReceiveFileNoticeEvent(routerId,fileNumber,fileName));
    }

    public void starSendFile(int fileNumber, String routerId, int index) {
        KLog.i("开始发送的文件序号：" + fileNumber);
        KLog.i("路由器的Id是：" + routerId);
        KLog.i("pptox的index为：" + index);
        sendFileRouterMap.put(fileNumber+"",routerId);
    }

    /**
     * 发送了多少字节的回调
     * @param position
     * @param filesize
     */
    public void sendFileRate(int fileNumber, int position, int filesize, int index) {
        KLog.i("fileNumber：" + fileNumber);
        KLog.i("发送了：" + position);
        KLog.i("总共：" + filesize);
        String key = sendFileRouterMap.get(fileNumber +"");
        int average = filesize / progressBarMaxSeg;
        if(average <= 0)
        {
            average = 1;
        }
        if(position == filesize)
        {
            int num = (int)(position / average) + 1;
            progressSendMap.put(index+"_"+num,null);
            EventBus.getDefault().post(new ToxSendFileFinishedEvent(key,fileNumber));
        }else{
            int num = (int)(position / average) + 1;
            if(progressSendMap.get(index+"_"+num) == null)
            {
                KLog.i("抛出EventBus:sendFileRate"+filesize+"_"+num);
                EventBus.getDefault().post(new ToxSendFileProgressEvent(key,fileNumber,position,filesize));
                progressSendMap.put(index+"_"+num,true);
            }
        }
    }
    /**
     * 接收了多少字节的回调
     * @param position
     * @param filesize
     */
    public void receivedFileRate(int position, int filesize, String routerId, int fileNum) {
        KLog.i("接收了：" + position);
        KLog.i("总共：" + filesize);
        KLog.i("路由Id为：" + routerId);
        KLog.i("fileNum为：" + fileNum);
        int fileNumber = Integer.valueOf(reveiveFileNumberMap.get(routerId));
        int average = filesize / progressBarMaxSeg;
        if(position == filesize)
        {
            int num = (int)(position / average) + 1;
            progressReceiveMap.put(fileNum+"_"+num,null);
            KLog.i("抛出EventBus:receivedFileFinish"+fileNum+"_"+num);
            EventBus.getDefault().post(new ToxReceiveFileFinishedEvent(routerId,fileNumber));
        }else{
            int num = (int)(position / average) + 1;
            if(progressReceiveMap.get(fileNum+"_"+num) == null)
            {
                KLog.i("抛出EventBus:receivedFileRate"+fileNum+"_"+num);
                EventBus.getDefault().post(new ToxReceiveFileProgressEvent(routerId,fileNumber,position,filesize));
                progressReceiveMap.put(fileNum+"_"+num,true);
            }

        }
    }

    public void bootStrapJava() {
        KLog.i("引导");
        String toxJson = FileUtil.getAssetJson(AppConfig.instance, "tox.json");
        DhtJson dhtJson = new Gson().fromJson(toxJson, DhtJson.class);
        for (DhtNode dhtNode : dhtJson.getNodes()) {
            int result = ToxCoreJni.getInstance().bootStrap(dhtNode.getIpv4(), dhtNode.getPort(), dhtNode.getPublic_key());
            KLog.i("引导 " + dhtNode.getIpv4() + " " + result);
        }
        KLog.i("引导 完成");
    }

    /**
     * 主动取消文件的接受
     * @param fileNumber
     */
    public native void cancelFileReceive(int fileNumber);

    /**
     * 主动取消文件的发送
     * @param fileNumber
     */
    public native void cancelFileSend(int fileNumber);

    public String setFileSavePath(String oldName) {
        //todo
        String origainName = "";
//        if(oldName.contains(":"))
//        {
//            oldName = oldName.substring(oldName.indexOf(":")+1,oldName.length());
//            oldName = oldName.substring(0,oldName.indexOf(":"));
//            origainName = new String(Base58.decode(oldName));
//        }
        return AppConfig.instance.getFilesDir().getAbsolutePath() + "/temp/" + oldName;
    }
}
