package com.hyphenate.easeui.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.easeui.EaseUI;
import com.stratagile.pnrouter.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.domain.EaseAvatarOptions;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseChatMessageList.MessageListItemClickListener;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.DateUtils;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.db.UserEntityDao;
import com.stratagile.pnrouter.utils.DateUtil;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.pnrouter.view.ImageButtonWithText;

import java.util.Date;
import java.util.List;

public abstract class EaseChatRow extends LinearLayout {
    public interface EaseChatRowActionCallback {
        void onResendClick(EMMessage message);

        void onBubbleClick(EMMessage message);

        void onBubbleLongClick(EMMessage message,View view);

        void onDetachedFromWindow();
    }

    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected ImageButtonWithText userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;

    protected ImageView sendStatusView;

    protected MessageListItemClickListener itemClickListener;
    protected EaseMessageListItemStyle itemStyle;

    private EaseChatRowActionCallback itemActionCallback;

    public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        this.activity = (Activity) context;
        inflater = LayoutInflater.from(context);

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        itemActionCallback.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    public void updateView(final EMMessage msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onViewUpdate(msg);
            }
        });
    }

    private void initView() {
        onInflateView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageButtonWithText) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        sendStatusView = (ImageView) findViewById(R.id.msg_sendstatus);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * set property according message and postion
     *
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position,
            EaseChatMessageList.MessageListItemClickListener itemClickListener,
                          EaseChatRowActionCallback itemActionCallback,
                          EaseMessageListItemStyle itemStyle) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;
        this.itemActionCallback = itemActionCallback;
        this.itemStyle = itemStyle;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
    	// set nickname, avatar and background of bubble
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(DateUtil.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
            	// show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtil.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        if(userAvatarView != null) {
            //set nickname and avatar
            if (message.direct() == Direct.SEND) {
//                EaseUserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
                //设置自己的头像
                userAvatarView.setText(SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUsername(), ""));
            } else {
                List<UserEntity> user = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(message.getFrom())).list();
                if (user.size() != 0) {
                    String usernameSouce = new String(RxEncodeTool.base64Decode(user.get(0).getNickName()));
                    EaseUserUtils.setUserAvatar(usernameSouce, userAvatarView);
                } else {
                    EaseUserUtils.setUserAvatar(message.getFrom(), userAvatarView);
                }
            }
        }
        if (EMClient.getInstance().getOptions().getRequireDeliveryAck()) {
            if(deliveredView != null){
                if (message.isDelivered()) {
                    deliveredView.setVisibility(View.VISIBLE);
                } else {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (EMClient.getInstance().getOptions().getRequireAck()) {
            if (ackedView != null) {
                if (message.isAcked()) {
                    if (deliveredView != null) {
                        deliveredView.setVisibility(View.INVISIBLE);
                    }
                    ackedView.setVisibility(View.VISIBLE);
                } else {
                    ackedView.setVisibility(View.INVISIBLE);
                }
            }
            if (sendStatusView != null) {
                if (message.isAcked()) {
                    sendStatusView.setImageResource(R.drawable.sendout);
                    sendStatusView.setVisibility(View.VISIBLE);
                } else {
                    sendStatusView.setImageResource(R.drawable.send);
                    sendStatusView.setVisibility(View.VISIBLE);
                }
            }
        }
        if (itemStyle != null) {
            if (userAvatarView != null) {
                if (itemStyle.isShowAvatar()) {
                    userAvatarView.setVisibility(View.VISIBLE);
//                    EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();
//                    if(avatarOptions != null && userAvatarView instanceof EaseImageView){
//                        EaseImageView avatarView = ((EaseImageView)userAvatarView);
//                        if(avatarOptions.getAvatarShape() != 0)
//                            avatarView.setShapeType(avatarOptions.getAvatarShape());
//                        if(avatarOptions.getAvatarBorderWidth() != 0)
//                            avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
//                        if(avatarOptions.getAvatarBorderColor() != 0)
//                            avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
//                        if(avatarOptions.getAvatarRadius() != 0)
//                            avatarView.setRadius(avatarOptions.getAvatarRadius());
//                    }
                } else {
                    userAvatarView.setVisibility(View.GONE);
                }
            }
            if (usernickView != null) {
                if (itemStyle.isShowUserNick())
                    usernickView.setVisibility(View.VISIBLE);
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (bubbleLayout != null) {
                if (message.direct() == Direct.SEND) {
                    if (itemStyle.getMyBubbleBg() != null) {
                        bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                    }
                } else if (message.direct() == Direct.RECEIVE) {
                    if (itemStyle.getOtherBubbleBg() != null) {
                        bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBubbleBg());
                    }
                }
            }
        }

    }

    private void setClickListener() {
        if(bubbleLayout != null){
            bubbleLayout.setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onBubbleClick(message)){
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleClick(message);
                    }
                }
            });
    
            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {
    
                @Override
                public boolean onLongClick(View v) {
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleLongClick(message,v);
                    }
                    return true;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onResendClick(message)){
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onResendClick(message);
                    }
                }
            });
        }

        if(userAvatarView != null){
            userAvatarView.setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarClick(message.getFrom());
                        }
                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    if(itemClickListener != null){
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarLongClick(message.getFrom());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh view when message status change
     */
    protected abstract void onViewUpdate(EMMessage msg);

    /**
     * setup view
     * 
     */
    protected abstract void onSetUpView();
}
