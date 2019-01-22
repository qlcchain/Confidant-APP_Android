package com.stratagile.tox.toxcore;

import android.os.Environment;

import com.socks.library.KLog;
import com.stratagile.pnrouter.utils.LogUtil;
import events.ToxReceiveFileFinishedEvent;
import events.ToxReceiveFileNoticeEvent;
import events.ToxSendFileFinishedEvent;
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
    /**
     * 获取单例
     * @return toxCoreJni实例
     */
    public static ToxCoreJni getInstance() {
        if (instance == null) {
            instance = new ToxCoreJni();
            connectStatus = ToxConnection.NONE;
            File dataFile = new File(Environment.getExternalStorageDirectory() + "/RouterData13", "");
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
        int result =  sendFile(filePath, friendId);
        LogUtil.addLog("发送Tox文件:",filePath +"  result:" +result);
        return result;
    }
    public native int sendFile(String fileName, String friendId);

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

    public void starSendFile(int fileNumber, String routerId) {
        KLog.i("开始发送的文件序号：" + fileNumber);
        KLog.i("路由器的Id是：" + routerId);
        sendFileRouterMap.put(fileNumber+"",routerId);
    }

    /**
     * 发送了多少字节的回调
     * @param position
     * @param filesize
     */
    public void sendFileRate(int fileNumber, int position, int filesize) {
        KLog.i("fileNumber：" + fileNumber);
        KLog.i("发送了：" + position);
        KLog.i("总共：" + filesize);
        String key = sendFileRouterMap.get(fileNumber +"");
        if(position == filesize)
        {
            EventBus.getDefault().post(new ToxSendFileFinishedEvent(key,fileNumber));
        }
    }
    /**
     * 接收了多少字节的回调
     * @param position
     * @param filesize
     */
    public void receivedFileRate(int position, int filesize, String routerId) {
        KLog.i("接收了：" + position);
        KLog.i("总共：" + filesize);
        KLog.i("路由Id为：" + routerId);
        int fileNumber = Integer.valueOf(reveiveFileNumberMap.get(routerId));
        if(position == filesize)
        {
            EventBus.getDefault().post(new ToxReceiveFileFinishedEvent(routerId,fileNumber));
        }
    }
}
