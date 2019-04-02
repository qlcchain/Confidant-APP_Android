package com.hyphenate.easeui.widget.presenter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.ui.EaseDingAckUserListActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowText;
import com.hyphenate.exceptions.HyphenateException;
import com.noober.menu.FloatMenu;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.entity.GroupDelMsgReq;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.tox.toxcore.ToxCoreJni;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;
import im.tox.tox4j.core.enums.ToxMessageType;


/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatTextPresenter extends EaseChatRowPresenter {
    private static final String TAG = "EaseChatTextPresenter";
    private Context context;
    private View viewRoot;
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        context = cxt;
        return new EaseChatRowText(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);

        if (!EaseDingMessageHelper.get().isDingMessage(message)) {
            return;
        }

        // If this msg is a ding-type msg, click to show a list who has already read this message.
        Intent i = new Intent(getContext(), EaseDingAckUserListActivity.class);
        i.putExtra("msg", message);
        getContext().startActivity(i);
    }
    @Override
    public void onBubbleLongClick(EMMessage message, View view) {
        super.onBubbleLongClick(message,view);
        /*if(!message.isAcked())
            return;*/
        String fromID = message.getFrom();
        viewRoot = view;
        String userId =   SpUtil.INSTANCE.getString(AppConfig.instance.getApplicationContext(), ConstantValue.INSTANCE.getUserId(), "");

        FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
        if(fromID.equals(userId))
        {
            floatMenu.inflate(R.menu.popup_menu);
        }else{
            if(message.getChatType().equals( EMMessage.ChatType.GroupChat))
            {
                if(UserDataManger.currentGroupData.getGAdmin().equals(userId))//如果是群管理员
                {
                    floatMenu.inflate(R.menu.popup_menu);
                }else{
                    floatMenu.inflate(R.menu.friendpopup_menu);
                }
            }else {
                floatMenu.inflate(R.menu.friendpopup_menu);
            }

        }
        //floatMenu.items(AppConfig.instance.getResources().getString(R.string.withDraw), AppConfig.instance.getResources().getString(R.string.cancel));
        int[] loc1=new int[2];
        view.getLocationOnScreen(loc1);
        KLog.i(loc1[0]);
        KLog.i(loc1[1]);
        KLog.i(AppConfig.instance.getPoint().x + "");
        KLog.i(AppConfig.instance.getPoint().y + "");
        floatMenu.show(new Point(loc1[0], loc1[1]),0,65);
//        floatMenu.show(new Point(AppConfig.instance.getPoint().x, AppConfig.instance.getPoint().y),0,65);
        floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
            @Override
            public void onClick(View v, int position,String name) {
                switch (name)
                {
                    case "Copy":
                        ClipboardManager cm = (ClipboardManager) AppConfig.instance.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", EaseSmileUtils.getSmiledTextInput(getContext(), EaseCommonUtils.getMessageDigest(message, getContext())));
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(AppConfig.instance.getApplicationContext(), R.string.copy_success, Toast.LENGTH_SHORT).show();
                        break;
                    case "Forward":
                        Intent intent = new Intent(getContext(), selectFriendActivity.class);
                        intent.putExtra("fromId", message.getTo());
                        intent.putExtra("message",message);
                        getContext().startActivity(intent);
                        ((Activity) getContext()).overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                        break;
                    case "Withdraw":
                        if(message.getChatType().equals( EMMessage.ChatType.GroupChat))
                        {
                            int type = 0;
                            if(!fromID.equals(userId))
                            {
                                type = 1;
                            }
                            GroupDelMsgReq  msgData = new GroupDelMsgReq(type, userId, message.getTo(),Integer.valueOf(message.getMsgId()) ,"GroupDelMsg");
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


                        ConstantValue.INSTANCE.setDeleteMsgId(message.getMsgId());

                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return;
        }

        // Send the group-ack cmd type msg if this msg is a ding-type msg.
        EaseDingMessageHelper.get().sendAckMessage(message);
    }
}
