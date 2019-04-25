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
import com.luck.picture.lib.PicturePreviewActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.observable.ImagesObservable;
import com.message.Message;
import com.noober.menu.FloatMenu;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.db.MessageEntity;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
            String fileMiName = data.getFileName().substring(data.getFileName().lastIndexOf("/") + 1, data.getFileName().length());
            String fileOrginName = new String(Base58.decode(fileMiName));
            String fileLocalPath = PathUtils.getInstance().getFilePath().toString() + "/" + fileOrginName;
            String fileLocalMiPath = PathUtils.getInstance().getTempPath().toString() + "/" + fileOrginName;
            File fileLocal = new File(fileLocalPath);

            if(fileLocal.exists())
            {
                DeleteUtils.deleteFile(fileLocalPath);
            }
            File fileMi = new File(fileLocalMiPath);
            if(fileMi.exists())
            {
                DeleteUtils.deleteFile(fileLocalMiPath);
            }
            EventBus.getDefault().post(new BeginDownloadForwad(data.getMsgId()+"",messageData,data));
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
        //getContext().startActivity(intent);

        List<LocalMedia> selectedImages = new ArrayList<LocalMedia>();
        List<LocalMedia> previewImages =  ImagesObservable.getInstance().readLocalMedias("chat");
        if(previewImages != null && previewImages.size() >0)
        {
            Collections.sort(previewImages, new Comparator<LocalMedia>() {
                @Override
                public int compare(LocalMedia lhs, LocalMedia rhs) {
                    int lsize = lhs.getTimeStamp();
                    int rsize = rhs.getTimeStamp();
                    return lsize == rsize ? 0 : (lsize > rsize ? 1 : -1);
                }
            });
            ImagesObservable.getInstance().saveLocalMedia(previewImages,"chat");
            int postion = 0;
            int size = previewImages.size();
            int msgId = Integer.valueOf(message.getMsgId()) ;
            for(int i = 0 ; i < size ;i ++)
            {
                if(previewImages.get(i).getTimeStamp() == msgId)
                {
                    postion = i;
                }
                previewImages.get(i).setPosition(i);
            }
            Intent intentPicturePreviewActivity = new Intent(getContext(), PicturePreviewActivity.class);
            Bundle bundle = new Bundle();
            //ImagesObservable.getInstance().saveLocalMedia(previewImages);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
            bundle.putInt(PictureConfig.EXTRA_POSITION, postion);
            bundle.putString("from","chat");
            intentPicturePreviewActivity.putExtras(bundle);
            getContext().startActivity(intentPicturePreviewActivity);
        }
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
