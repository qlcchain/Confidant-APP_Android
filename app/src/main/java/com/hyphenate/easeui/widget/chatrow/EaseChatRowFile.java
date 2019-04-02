package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMNormalFileMessageBody;
import com.stratagile.pnrouter.R;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.TextFormater;
import com.stratagile.pnrouter.utils.NetUtils;

import java.io.File;

public class EaseChatRowFile extends EaseChatRow{
    private static final String TAG = "EaseChatRowFile";

    protected TextView fileNameView;
	protected TextView fileSizeView;
    protected TextView fileStateView;
    private ProgressBar progressBarShelf;
    protected LinearLayout ll_loading;
    
    private EMNormalFileMessageBody fileMessageBody;

    public EaseChatRowFile(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
	protected void onInflateView() {
	    inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? 
	            R.layout.ease_row_received_file : R.layout.ease_row_sent_file, this);
	}

	@Override
	protected void onFindViewById() {
	    fileNameView = (TextView) findViewById(R.id.tv_file_name);
        fileSizeView = (TextView) findViewById(R.id.tv_file_size);
        fileStateView = (TextView) findViewById(R.id.tv_file_state);
        percentageView = (TextView) findViewById(R.id.percentage);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        progressBarShelf = (ProgressBar) findViewById(R.id.progress_bar);
	}


	@Override
	protected void onSetUpView() {
	    fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        String filePath = fileMessageBody.getLocalUrl();
        fileNameView.setText(fileMessageBody.getFileName());
        if(ll_loading !=null)
        {
            if(filePath.contains("ease_default_file"))
            {
                ll_loading.setVisibility(View.VISIBLE);
            }else{
                ll_loading.setVisibility(View.INVISIBLE);
            }
        }
        File file = new File(filePath);
        if (file.exists() && !file.getName().contains("file_downloading")) {
            if(!message.getStringAttribute("kong","").equals(""))
            {
                fileSizeView.setText(NetUtils.INSTANCE.parseSize(0));
                progressBarShelf.setVisibility(View.VISIBLE);
            }else{
                fileSizeView.setText(NetUtils.INSTANCE.parseSize(fileMessageBody.getFileSize()));
                progressBarShelf.setVisibility(View.INVISIBLE);
            }
        }else{
            progressBarShelf.setVisibility(View.VISIBLE);
            fileSizeView.setText(NetUtils.INSTANCE.parseSize(0));
        }
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (file.exists() && !file.getName().contains("file_downloading")) {
                fileStateView.setText(R.string.Have_downloaded);
            } else {
                fileStateView.setText(R.string.Did_not_download);
            }
            return;
        }
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
