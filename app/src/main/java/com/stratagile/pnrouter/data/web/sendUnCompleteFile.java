package com.stratagile.pnrouter.data.web;

import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.PathUtils;
import com.message.Message;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.entity.ToxFileData;
import com.stratagile.pnrouter.entity.events.FileTransformEntity;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.FileUtil;
import com.stratagile.pnrouter.utils.LibsodiumUtil;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.utils.RxEncryptTool;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.tox.toxcore.ToxCoreJni;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;

public class sendUnCompleteFile {

    private HashMap<String, Boolean> sendMsgLocalMap = new HashMap<>();
    private HashMap<String, String> sendFilePathMap = new HashMap<>();
    private HashMap<String, Boolean> deleteFileMap = new HashMap<>();
    private HashMap<String, String> sendFileFriendKeyMap = new HashMap<>();
    private HashMap<String, String> sendFileKeyByteMap = new HashMap<>();
    private HashMap<String, byte[]> sendFileFriendKeyByteMap = new HashMap<>();
    private HashMap<String, byte[]> sendFileMyKeyByteMap = new HashMap<>();
    private HashMap<String, Boolean> sendFileResultMap = new HashMap<>();
    private HashMap<String, String> sendFileNameMap = new HashMap<>();
    private HashMap<String, Integer> sendFileLastByteSizeMap = new HashMap<>();
    private HashMap<String, byte[]> sendFileLeftByteMap = new HashMap<>();
    private HashMap<String, String> sendMsgIdMap = new HashMap<>();
    private HashMap<String, Message> receiveFileDataMap = new HashMap<>();
    private HashMap<String, Message> receiveToxFileDataMap = new HashMap<>();
    private HashMap<String, String> receiveToxFileIdMap = new HashMap<>();

    /**
     * 开始发送文件
     * @param fileTransformEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBeginSendFile(FileTransformEntity fileTransformEntity) {

    }
    protected void sendImageMessage(String userId,String friendId,String files_dir,String msgId,String friendSignPublicKey,String friendMiPublicKey) {
        new Thread(new Runnable() {
            public void run() {

                try {
                    File file = new File(files_dir);
                    boolean isHas = file.exists();
                    if (isHas) {
                        String fileName = ((int) (System.currentTimeMillis() / 1000)) + "_" + files_dir.substring(files_dir.lastIndexOf("/") + 1);
                        if (ConstantValue.INSTANCE.getCurreantNetworkType().equals("WIFI")) {

                            sendMsgLocalMap.put(msgId, false);
                            sendFilePathMap.put(msgId, files_dir);
                            deleteFileMap.put(msgId, false);
                            sendFileFriendKeyMap.put(msgId, friendSignPublicKey);

                            String fileKey = RxEncryptTool.generateAESKey();
                            byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
                            byte[] friend = RxEncodeTool.base64Decode(friendMiPublicKey);
                            byte[] SrcKey = new byte[256];
                            byte[] DstKey = new byte[256];
                            try {
                                if (ConstantValue.INSTANCE.getEncryptionType().equals("1")) {
                                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, ConstantValue.INSTANCE.getLibsodiumpublicMiKey()));
                                    DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.INSTANCE.EncryptShareKey(fileKey, friendMiPublicKey));
                                } else {
                                    SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), my));
                                    DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.getBytes(), friend));
                                }
                                sendFileKeyByteMap.put(msgId, fileKey.substring(0, 16));
                                sendFileMyKeyByteMap.put(msgId, SrcKey);
                                sendFileFriendKeyByteMap.put(msgId, DstKey);
                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String wssUrl = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getFilePort();
                            EventBus.getDefault().post(new FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"));

                        }
                        Gson gson = new Gson();
                        Message Message = new Message();
                        Message.setMsgType(1);
                        Message.setFileName(fileName);
                        Message.setMsg("");
                        Message.setFrom(userId);
                        Message.setTo(friendId);
                        Message.setTimeStatmp(System.currentTimeMillis() / 1000);
                        Message.setUnReadCount(0);
                        String baseDataJson = gson.toJson(Message);
                        SpUtil.INSTANCE.putString(AppConfig.instance, ConstantValue.INSTANCE.getMessage() + userId + "_" + friendId, baseDataJson);
                    }

                } catch (Exception e) {

                }
            }

        }).start();

    }
}
