package com.hyphenate.easeui.widget.presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

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
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.utils.SpUtil;

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
        String fromID = message.getFrom();
        viewRoot = view;
        String userId =   SpUtil.INSTANCE.getString(AppConfig.instance.getApplicationContext(), ConstantValue.INSTANCE.getUserId(), "");
        if(fromID.equals(userId))
        {
            FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
            floatMenu.inflate(R.menu.popup_menu);
            //floatMenu.items(AppConfig.instance.getResources().getString(R.string.withDraw), AppConfig.instance.getResources().getString(R.string.cancel));
            int[] loc1=new int[2];
            view.getLocationOnScreen(loc1);
            KLog.i(loc1[0]);
            KLog.i(loc1[1]);
            floatMenu.show(new Point(110,loc1[1]-200));
            floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    switch (position)
                    {
                        case 0:
                            break;
                        case 1:
                            ClipboardManager cm = (ClipboardManager) AppConfig.instance.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            // 创建普通字符型ClipData
                            ClipData mClipData = ClipData.newPlainText("Label", EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(message, getContext())));
                            // 将ClipData内容放到系统剪贴板里。
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(AppConfig.instance.getApplicationContext(), R.string.copy_success, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            break;
                        case 3:
                            Intent intent = new Intent(getContext(), selectFriendActivity.class);
                            getContext().startActivity(intent);
                            break;
                        case 4:
                            DelMsgReq msgData = new DelMsgReq( message.getFrom(), message.getTo(),Integer.valueOf(message.getMsgId()) ,"DelMsg");
                            AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(msgData));
                            ConstantValue.INSTANCE.setMsgId(message.getMsgId());
                            break;
                        default:
                            break;
                    }
                }
            });
        }
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
