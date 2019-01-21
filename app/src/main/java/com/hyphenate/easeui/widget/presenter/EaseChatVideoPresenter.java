package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSONObject;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.easeui.ui.EaseShowVideoActivity;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVideo;
import com.hyphenate.util.EMLog;
import com.noober.menu.FloatMenu;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.tox.toxcore.ToxCoreJni;

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
    @Override
    public void onBubbleLongClick(EMMessage message, View view) {
        super.onBubbleLongClick(message,view);
        if(!message.isAcked())
            return;
        String fromID = message.getFrom();
        viewRoot = view;
        String userId =   SpUtil.INSTANCE.getString(AppConfig.instance.getApplicationContext(), ConstantValue.INSTANCE.getUserId(), "");
        if(fromID.equals(userId) && false)
        {
            FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
            floatMenu.inflate(R.menu.popup_menu_file);
            //floatMenu.items(AppConfig.instance.getResources().getString(R.string.withDraw), AppConfig.instance.getResources().getString(R.string.cancel));
            int[] loc1=new int[2];
            view.getLocationOnScreen(loc1);
            KLog.i(loc1[0]);
            KLog.i(loc1[1]);
            floatMenu.show(new Point(loc1[0],loc1[1]),0,65);
            floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    switch (position)
                    {
                        case 0:
                            Intent intent = new Intent(getContext(), selectFriendActivity.class);
                            intent.putExtra("fromId", message.getTo());
                            intent.putExtra("message",message);
                            getContext().startActivity(intent);
                            break;
                        case 1:
                            DelMsgReq msgData = new DelMsgReq( message.getFrom(), message.getTo(),Integer.valueOf(message.getMsgId()) ,"DelMsg");
                            if(ConstantValue.INSTANCE.isWebsocketConnected())
                            {
                                AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(msgData));
                            }else if(ConstantValue.INSTANCE.isToxConnected())
                            {
                                BaseData baseData = new BaseData(msgData);
                                String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                                //FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            }
                            String  aa = message.getMsgId();
                            ConstantValue.INSTANCE.setDeleteMsgId(message.getMsgId());
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
}
