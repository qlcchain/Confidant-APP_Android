package com.hyphenate.easeui.widget.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.ui.EaseShowNormalFileActivity;
import com.hyphenate.easeui.utils.OpenFileUtil;
import com.hyphenate.easeui.utils.PathUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
import com.hyphenate.exceptions.HyphenateException;
import com.message.Message;
import com.noober.menu.FloatMenu;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.entity.GroupDelMsgReq;
import com.stratagile.pnrouter.entity.JPullFileListRsp;
import com.stratagile.pnrouter.entity.PullFileReq;
import com.stratagile.pnrouter.entity.events.BeginDownloadForwad;
import com.stratagile.pnrouter.entity.events.DeleteMsgEvent;
import com.stratagile.pnrouter.entity.events.DownloadForwadSuccess;
import com.stratagile.pnrouter.entity.events.SaveMsgEvent;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.utils.AlbumNotifyHelper;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.DeleteUtils;
import com.stratagile.pnrouter.utils.FileDownloadUtils;
import com.stratagile.pnrouter.utils.FileUtil;
import com.stratagile.pnrouter.utils.GsonUtil;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.tox.toxcore.ToxCoreJni;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;
import im.tox.tox4j.core.enums.ToxMessageType;
import scala.App;
import scalaz.Alpha;


/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatFilePresenter extends EaseChatRowPresenter {

    private Context context;
    private View viewRoot;
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowFile(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMNormalFileMessageBody fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        File file = new File(filePath);
        if(file.getName().contains("file_fileForward"))
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
        if(!message.getStringAttribute("kong","").equals(""))
        {
            Toast.makeText(AppConfig.instance, R.string.downwaiting, Toast.LENGTH_SHORT).show();
            return;
        }
        if (file.exists() && !file.getName().contains("file_downloading")) {
            String newFilePath = Environment.getExternalStorageDirectory() + ConstantValue.INSTANCE.getLocalPath()+"/temp/"+file.getName();
            int result = FileUtil.copyAppFileToSdcard(filePath,newFilePath);
            if(result == 1)
            {
                try {
                    Intent intent = OpenFileUtil.getInstance((Activity) getContext()).openFile(newFilePath);
                    ((Activity) getContext()).startActivity(intent);
                    //FileUtils.openFile(file, (Activity) getContext());
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(AppConfig.instance, R.string.open_error, Toast.LENGTH_SHORT).show();
            }

        } else {
            // download the file

                Toast.makeText(AppConfig.instance, R.string.open_error, Toast.LENGTH_SHORT).show();

            //getContext().startActivity(new Intent(getContext(), EaseShowNormalFileActivity.class).putExtra("msg", message));
        }
        if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
        String fromID = message.getFrom();
        viewRoot = view;
        String userId =   SpUtil.INSTANCE.getString(AppConfig.instance.getApplicationContext(), ConstantValue.INSTANCE.getUserId(), "");
        if( message.getType() != EMMessage.Type.VOICE)
        {
            FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
            if(fromID.equals(userId))
            {
                if( message.getType() == EMMessage.Type.IMAGE || message.getType() == EMMessage.Type.VIDEO)
                {
                    floatMenu.inflate(R.menu.popup_menu_pic_video);
                }else{
                    floatMenu.inflate(R.menu.popup_menu_file);
                }

            }else{
                if(message.getChatType().equals( EMMessage.ChatType.GroupChat))
                {
                    if(UserDataManger.currentGroupData.getGAdmin().equals(userId))//如果是群管理员
                    {
                        if( message.getType() == EMMessage.Type.IMAGE || message.getType() == EMMessage.Type.VIDEO)
                        {
                            floatMenu.inflate(R.menu.popup_menu_pic_video);
                        }else{
                            floatMenu.inflate(R.menu.popup_menu_file);
                        }

                    }else{
                        if( message.getType() == EMMessage.Type.IMAGE || message.getType() == EMMessage.Type.VIDEO)
                        {
                            floatMenu.inflate(R.menu.friendpopup_menu_pic_video);
                        }else{
                            floatMenu.inflate(R.menu.friendpopup_menu_file);
                        }

                    }

                }else{
                    if( message.getType() == EMMessage.Type.IMAGE || message.getType() == EMMessage.Type.VIDEO)
                    {
                        floatMenu.inflate(R.menu.friendpopup_menu_pic_video);
                    }else{
                        floatMenu.inflate(R.menu.friendpopup_menu_file);
                    }


                }

            }
            int[] loc1=new int[2];
            view.getLocationOnScreen(loc1);
            KLog.i(loc1[0]);
            KLog.i(loc1[1]);
            floatMenu.show(new Point(loc1[0],loc1[1]),60,65);
            floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {

                @Override
                public void onClick(View v, int position,String name) {
                    switch (name)
                    {
                        case "Forward":
                            Intent intent = new Intent(getContext(), selectFriendActivity.class);
                            intent.putExtra("fromId", message.getTo());
                            intent.putExtra("message",message);
                            getContext().startActivity(intent);
                            ((Activity) getContext()).overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                            break;
                        case "Withdraw":
                            String  msgId = message.getMsgId();
                            ConstantValue.INSTANCE.setDeleteMsgId(message.getMsgId());
                            if(message.isAcked())
                            {
                                if(message.getChatType().equals( EMMessage.ChatType.GroupChat))
                                {
                                    GroupDelMsgReq msgData = new GroupDelMsgReq(0, userId, message.getTo(),Integer.valueOf(message.getMsgId()) ,"GroupDelMsg");
                                    BaseData baseData = new BaseData(4,msgData);
                                    if(ConstantValue.INSTANCE.isWebsocketConnected())
                                    {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(baseData);
                                    }else if(ConstantValue.INSTANCE.isToxConnected())
                                    {
                                        String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                                        if (ConstantValue.INSTANCE.isAntox()) {
                                            FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                                        }else{
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                        }
                                    }
                                }else{
                                    DelMsgReq msgData = new DelMsgReq( message.getFrom(), message.getTo(),Integer.valueOf(message.getMsgId()) ,"DelMsg");
                                    if(ConstantValue.INSTANCE.isWebsocketConnected())
                                    {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(msgData));
                                    }else if(ConstantValue.INSTANCE.isToxConnected())
                                    {
                                        BaseData baseData = new BaseData(msgData);
                                        String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                                        if (ConstantValue.INSTANCE.isAntox()) {
                                            FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                                        }else{
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                        }
                                    }
                                }

                            }else{
                                EventBus.getDefault().post(new DeleteMsgEvent(msgId));
                            }
                            break;
                        case "Save":
                            String galleryPath = Environment.getExternalStorageDirectory()
                                    + File.separator + Environment.DIRECTORY_DCIM
                                    + File.separator + "Confidant" + File.separator;
                            File galleryPathFile = new File(galleryPath);
                            if(!galleryPathFile.exists())
                            {
                                galleryPathFile.mkdir();
                            }
                            if( message.getType() == EMMessage.Type.IMAGE)
                            {
                                EMImageMessageBody eMImageMessageBody  = (EMImageMessageBody)message.getBody();
                                String imagePath = eMImageMessageBody.getLocalUrl();
                                galleryPath += System.currentTimeMillis()+imagePath.substring(imagePath.lastIndexOf("."),imagePath.length());
                                int result = FileUtil.copyAppFileToSdcard(imagePath, galleryPath);
                                if (result == 1) {

                                    AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance,galleryPath,System.currentTimeMillis());
                                }
                                EventBus.getDefault().post(new SaveMsgEvent("",result));
                            }else if(message.getType() == EMMessage.Type.VIDEO){
                                EMVideoMessageBody eMVideoMessageBody  = (EMVideoMessageBody)message.getBody();
                                String videoPath = eMVideoMessageBody.getLocalUrl();
                                galleryPath +=  System.currentTimeMillis()+videoPath.substring(videoPath.lastIndexOf("."),videoPath.length());
                                int result = FileUtil.copyAppFileToSdcard(videoPath, galleryPath);
                                if (result == 1) {
                                    AlbumNotifyHelper.insertVideoToMediaStore(AppConfig.instance,galleryPath,0,5000);
                                }
                                EventBus.getDefault().post(new SaveMsgEvent("",result));
                            }

                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
}
