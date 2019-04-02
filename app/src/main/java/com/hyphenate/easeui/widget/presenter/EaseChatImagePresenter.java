package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.utils.PathUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowImage;
import com.message.Message;
import com.noober.menu.FloatMenu;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.entity.JPullFileListRsp;
import com.stratagile.pnrouter.entity.PullFileReq;
import com.stratagile.pnrouter.entity.events.BeginDownloadForwad;
import com.stratagile.pnrouter.entity.events.DownloadForwadSuccess;
import com.stratagile.pnrouter.entity.file.UpLoadFile;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.DeleteUtils;
import com.stratagile.pnrouter.utils.FileDownloadUtils;
import com.stratagile.pnrouter.utils.FileMangerDownloadUtils;
import com.stratagile.pnrouter.utils.GsonUtil;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.tox.toxcore.ToxCoreJni;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;
import im.tox.tox4j.core.enums.ToxMessageType;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatImagePresenter extends EaseChatFilePresenter {
    private Context context;
    private View viewRoot;
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowImage(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(final EMMessage message) {
        super.handleReceiveMessage(message);

        getChatRow().updateView(message);

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                getChatRow().updateView(message);
            }

            @Override
            public void onError(int code, String error) {
                getChatRow().updateView(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                getChatRow().updateView(message);
            }
        });
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
        String localUrl = imgBody.getLocalUrl();
        if(localUrl.contains("image_defalut_bg"))
        {
            return;
        }
        if(localUrl.contains("image_defalut_fileForward_bg"))
        {
            String fileDataJson = message.getStringAttribute("fileData","");
            String messageDataJson = message.getStringAttribute("Message","");
            Gson gson = GsonUtil.getIntGson();
            JPullFileListRsp.ParamsBean.PayloadBean data = gson.fromJson(fileDataJson, JPullFileListRsp.ParamsBean.PayloadBean.class);
            Message messageData = gson.fromJson(messageDataJson, Message.class);
            String aa = "";
            String fileMiName = data.getFileName().substring(data.getFileName().lastIndexOf("/") + 1, data.getFileName().length());
            String msgId = data.getMsgId()+"";
            String fileOrginName = new String(Base58.decode(fileMiName));
            String filePath = PathUtils.getInstance().getFilePath().toString() + "/" + fileOrginName;
            String fileMiPath = PathUtils.getInstance().getTempPath().toString() + "/" + fileOrginName;
            File file = new File(filePath);

            if(file.exists())
            {
                DeleteUtils.deleteFile(filePath);
            }
            File fileMi = new File(fileMiPath);
            if(fileMi.exists())
            {
                DeleteUtils.deleteFile(fileMiPath);
            }
            EventBus.getDefault().post(new BeginDownloadForwad(data.getMsgId()+"",messageData));
            String filledUri = "https://" + ConstantValue.INSTANCE.getCurrentRouterIp() + ConstantValue.INSTANCE.getPort() + data.getFileName();
            String files_dir = PathUtils.getInstance().getFilePath().toString() + "/";
            if (ConstantValue.INSTANCE.isWebsocketConnected()) {
                //receiveFileDataMap.put(data.getMsgId()+"", data);

                new Thread(new Runnable(){
                    public void run(){
                        FileDownloadUtils.doDownLoadWork(filledUri, files_dir, AppConfig.instance, data.getMsgId(), handler, data.getUserKey(),data.getFileFrom()+"");
                    }
                }).start();

            } else {
                //receiveToxFileDataMap.put(fileOrginName,data);
                ConstantValue.INSTANCE.getReceiveToxFileGlobalDataMap().put(fileMiName,data.getUserKey());
                String selfUserId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
                PullFileReq msgData = new PullFileReq(selfUserId, selfUserId, fileMiName, data.getMsgId(), data.getFileFrom(), 2,"PullFile");
                BaseData baseData = new BaseData(msgData);
                String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                if (ConstantValue.INSTANCE.isAntox()) {
                    FriendKey friendKey = new FriendKey(ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                    MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                } else {
                    ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                }
            }

            return;
        }
        if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
            if(imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
                getChatRow().updateView(message);
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message);
            }
        } else{
            if(imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message);
                getChatRow().updateView(message);
                return;
            }
        }
        Intent intent = new Intent(getContext(), EaseShowBigImageActivity.class);
        File file = new File(imgBody.getLocalUrl());
        long fileSize = file.length();
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
            intent.putExtra("fileUrl", imgBody.getLocalUrl());
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            String msgId = message.getMsgId();
            intent.putExtra("messageId", msgId);
            intent.putExtra("localUrl", imgBody.getLocalUrl());
        }
        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getContext().startActivity(intent);
    }
    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x404:
                    break;
                case 0x55:
                    Bundle data = msg.getData();
                    String msgId = data.getInt("msgID") + "";
                    EventBus.getDefault().post(new DownloadForwadSuccess(msgId));
                    break;
            }

        }
    };
    @Override
    public void onBubbleLongClick(EMMessage message, View view) {
        super.onBubbleLongClick(message,view);
        if(!message.isAcked())
            return;
        String fromID = message.getFrom();
        viewRoot = view;
        String userId =   SpUtil.INSTANCE.getString(AppConfig.instance.getApplicationContext(), ConstantValue.INSTANCE.getUserId(), "");

    }
}
