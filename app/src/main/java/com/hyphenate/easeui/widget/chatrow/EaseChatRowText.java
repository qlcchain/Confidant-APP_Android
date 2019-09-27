package com.hyphenate.easeui.widget.chatrow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.entity.events.AddEmailEvent;
import com.stratagile.pnrouter.entity.events.ReplyMsgEvent;
import com.stratagile.pnrouter.ui.activity.email.EmailChooseActivity;
import com.stratagile.pnrouter.ui.activity.email.EmailSendActivity;
import com.stratagile.pnrouter.ui.activity.main.MainActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class EaseChatRowText extends EaseChatRow{

	private TextView contentView;
    private TextView tv_chatAssoccontent;
    private View tv_chatcontentDash;
    private String phone;

    public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
				R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
	}

    @Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
        tv_chatAssoccontent = (TextView) findViewById(R.id.tv_chatAssoccontent);
        tv_chatcontentDash = (View) findViewById(R.id.tv_chatcontentDash);
	}

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String AssocContent = "";
        String AssocUserName = "";
        try {
            AssocContent = message.getStringAttribute("AssocContent");
            AssocUserName = message.getStringAttribute("username");
            String aa = "";
        }catch (Exception e)
        {

        }
        if(!AssocContent.equals(""))
        {
            if(!AssocContent.equals(""))
            {
                AssocContent = AssocUserName +": “"+AssocContent+"”";
            }

            Spannable span = EaseSmileUtils.getSmiledTextInput(context, AssocContent);
            // 设置内容
            tv_chatAssoccontent.setText(span, BufferType.SPANNABLE);
            tv_chatAssoccontent.setVisibility(VISIBLE);
            tv_chatcontentDash.setVisibility(VISIBLE);
        }else{
            tv_chatAssoccontent.setVisibility(GONE);
            tv_chatcontentDash.setVisibility(GONE);
        }
        Spannable span = EaseSmileUtils.getSmiledTextInput(context, txtBody.getMessage());
        // 设置内容
        contentView.setText(span, BufferType.SPANNABLE);
        setClickListener();
    }
    private void setClickListener() {

        CharSequence text = contentView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) contentView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            //style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            contentView.setText(style);
        }
        if(contentView != null){
            /*contentView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (itemClickListener != null && itemClickListener.onBubbleClick(message)){
                        return false;
                    }
                    return  false;
                }
            });
            contentView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onBubbleClick(message)){
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleClick(message);
                    }
                }
            });*/

            contentView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleLongClick(message,v);
                    }
                    v.setTag("onLongClick");
                    return true;
                }
            });
        }
    }
    private  class MyURLSpan extends ClickableSpan {

        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            if (widget.getTag() != null) {
                widget.setTag(null);
                return;
            }
            if(mUrl.contains("http:") || mUrl.contains("https:"))
            {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri url = Uri.parse(mUrl);
                intent.setData(url);
                getContext().startActivity(intent);
            }else if(mUrl.contains("tel:"))
            {
                phone = mUrl;
                AndPermission.with(AppConfig.instance)
                        .requestCode(101)
                        .permission(
                                Manifest.permission.CALL_PHONE
                        )
                        .callback(permission)
                        .start();
            }else if(mUrl.contains("mailto:"))
            {
                 if(AppConfig.instance.getEmailConfig() != null && AppConfig.instance.getEmailConfig().getAccount() != null)
                 {
                     String emailAdress = mUrl.replace("mailto:","");
                     Intent intent = new Intent(AppConfig.instance, EmailSendActivity.class);
                     intent.putExtra("flag",255);
                     intent.putExtra("menu","");
                     intent.putExtra("emailAdress",emailAdress);
                     getContext().startActivity(intent);
                 }else{
                     getContext().startActivity(new Intent(AppConfig.instance, MainActivity.class));
                     String emailAdress = mUrl.replace("mailto:","");
                     EventBus.getDefault().post(new AddEmailEvent(emailAdress));
                 }
            }
            //Toast.makeText(AppConfig.instance, mUrl, Toast.LENGTH_SHORT).show();
            //widget.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }
    private PermissionListener permission = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {

            // 权限申请成功回调。
            if (requestCode == 101) {
                Intent intentPhone = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));
                getContext().startActivity(intentPhone);
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败");

            }
        }
    };
    @Override
    protected void onViewUpdate(EMMessage msg) {
        switch (msg.status()) {
            case CREATE:
                onMessageCreate();
                break;
            case SUCCESS:
                onMessageSuccess();
                break;
            case FAIL:
                onMessageError();
                break;
            case INPROGRESS:
                onMessageInProgress();
                break;
        }
    }

    public void onAckUserUpdate(final int count) {
        if (ackedView != null) {
            ackedView.post(new Runnable() {
                @Override
                public void run() {
                    ackedView.setVisibility(VISIBLE);
                    ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
                }
            });
        }
    }

    private void onMessageCreate() {
        //progressBar.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }

    private void onMessageSuccess() {
        //progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);

        // Show "1 Read" if this msg is a ding-type msg.
        if (EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
            ackedView.setVisibility(VISIBLE);
            List<String> userList = EaseDingMessageHelper.get().getAckUsers(message);
            int count = userList == null ? 0 : userList.size();
            ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
        }

        // Set ack-user list change listener.
        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    private void onMessageError() {
        //progressBar.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
        //progressBar.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener =
            new EaseDingMessageHelper.IAckUserUpdateListener() {
                @Override
                public void onUpdate(List<String> list) {
                    onAckUserUpdate(list.size());
                }
            };
}
