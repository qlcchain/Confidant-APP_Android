package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.ui.EaseShowVideoActivity;
import com.hyphenate.easeui.utils.PathUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVideo;
import com.hyphenate.util.EMLog;
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
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.DeleteUtils;
import com.stratagile.pnrouter.utils.FileDownloadUtils;
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

public class EaseChatVideoPresenter extends EaseChatFilePresenter {
    private static final String TAG = "EaseChatVideoPresenter";
    private Context context;
    private View viewRoot;
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowVideo(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();
        EMLog.d(TAG, "video view is on click");
        String localUrl = videoBody.getLocalUrl();
        if(localUrl.contains("ease_default_vedio"))
        {
            return;
        }
        if(localUrl.contains("ease_default_fileForward_vedio"))
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
        if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {

        }else{
            if(videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                        videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message);
                return;
            }
        }
        Intent intent = new Intent(getContext(), EaseShowVideoActivity.class);
        intent.putExtra("msg", message);
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
