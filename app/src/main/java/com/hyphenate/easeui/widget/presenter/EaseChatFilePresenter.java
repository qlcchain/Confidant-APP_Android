package com.hyphenate.easeui.widget.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.easeui.ui.EaseShowNormalFileActivity;
import com.hyphenate.easeui.utils.OpenFileUtil;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowFile;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.FileUtils;
import com.noober.menu.FloatMenu;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.utils.SpUtil;

import java.io.File;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;
import im.tox.tox4j.core.enums.ToxMessageType;

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
        if (file.exists()) {
            // open files if it exist
            try {
                Intent intent = OpenFileUtil.getInstance((Activity) getContext()).openFile(filePath);
                ((Activity) getContext()).startActivity(intent);
                //FileUtils.openFile(file, (Activity) getContext());
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        } else {
            // download the file
            getContext().startActivity(new Intent(getContext(), EaseShowNormalFileActivity.class).putExtra("msg", message));
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
    @Override
    public void onBubbleLongClick(EMMessage message, View view) {
        super.onBubbleLongClick(message,view);
        String fromID = message.getFrom();
        viewRoot = view;
        String userId =   SpUtil.INSTANCE.getString(AppConfig.instance.getApplicationContext(), ConstantValue.INSTANCE.getUserId(), "");
        if(fromID.equals(userId))
        {
            FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
            floatMenu.inflate(R.menu.popup_menu_file);
            //floatMenu.items(AppConfig.instance.getResources().getString(R.string.withDraw), AppConfig.instance.getResources().getString(R.string.cancel));
            int[] loc1=new int[2];
            view.getLocationOnScreen(loc1);
            KLog.i(loc1[0]);
            KLog.i(loc1[1]);
            floatMenu.show(new Point(450,loc1[1]-200));
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
                                FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                            }

                            String  aa = message.getMsgId();
                            ConstantValue.INSTANCE.setMsgId(message.getMsgId());
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
}
