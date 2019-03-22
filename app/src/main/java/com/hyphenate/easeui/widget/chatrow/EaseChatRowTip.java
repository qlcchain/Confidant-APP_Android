package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.utils.NetUtils;

import java.io.File;

public class EaseChatRowTip extends EaseChatRow{
    private static final String TAG = "EaseChatRowTip";

    protected TextView content;

    private EMTextMessageBody tipMessageBody;

    public EaseChatRowTip(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
	protected void onInflateView() {
	    inflater.inflate(R.layout.ease_row_tip, this);
	}

	@Override
	protected void onFindViewById() {
        content = (TextView) findViewById(R.id.content);
	}


	@Override
	protected void onSetUpView() {
        tipMessageBody = (EMTextMessageBody) message.getBody();
        content.setText(tipMessageBody.getMessage());
	}

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

    private void onMessageCreate() {
       /* if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);*/
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    private void onMessageSuccess() {
       /* if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);*/
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }

    private void onMessageError() {
       /* if (percentageView != null)
            percentageView.setVisibility(View.INVISIBLE);*/
        if (statusView != null)
            statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
       /* if (percentageView != null) {
            percentageView.setVisibility(View.VISIBLE);
            percentageView.setText(message.progress() + "%");
        }*/
        if (statusView != null)
            statusView.setVisibility(View.INVISIBLE);
    }
}
