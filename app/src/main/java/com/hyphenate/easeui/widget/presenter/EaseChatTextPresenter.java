package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.ui.EaseDingAckUserListActivity;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowText;
import com.hyphenate.exceptions.HyphenateException;
import com.noober.menu.FloatMenu;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.utils.SpUtil;
import com.vondear.rxtools.view.popupwindows.tools.RxPopupView;
import com.vondear.rxtools.view.popupwindows.tools.RxPopupViewManager;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatTextPresenter extends EaseChatRowPresenter implements RxPopupViewManager.TipListener {
    private static final String TAG = "EaseChatTextPresenter";
    private RxPopupViewManager mRxPopupViewManager;
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
            /*RxPopupView.Builder builder;
            if (mRxPopupViewManager == null) {
                mRxPopupViewManager = new RxPopupViewManager(this);
                //mRxPopupViewManager.findAndDismiss(tvQlc);
            }
            builder = new RxPopupView.Builder(context, view, null, "withdraw", RxPopupView.POSITION_ABOVE);
            builder.setBackgroundColor(R.color.white);
            builder.setTextColor(R.color.mainColor);
            builder.setGravity(RxPopupView.GRAVITY_CENTER);
            builder.setTextSize(12);
            view = mRxPopupViewManager.show(builder.build());
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRxPopupViewManager.dismiss(viewRoot, true);
                }
            }, 3000);*/

            FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
            floatMenu.items(AppConfig.instance.getResources().getString(R.string.withDraw), AppConfig.instance.getResources().getString(R.string.cancel));
            int[] loc1=new int[2];
            view.getLocationOnScreen(loc1);
            floatMenu.show(new Point(loc1[0],loc1[1]));
            floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    switch (position)
                    {
                        case 0:
                            DelMsgReq msgData = new DelMsgReq( message.getFrom(), message.getTo(),Integer.valueOf(message.getMsgId()) ,"DelMsg");
                            AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(msgData));
                            ConstantValue.INSTANCE.setMsgId(message.getMsgId());
                            break;
                        case 1:
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

    @Override
    public void onTipDismissed(View view, int anchorViewId, boolean byUser) {

    }
}
