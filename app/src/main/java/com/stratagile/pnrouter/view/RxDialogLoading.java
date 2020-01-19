package com.stratagile.pnrouter.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stratagile.pnrouter.R;

/**
 * 作者：Android on 2017/9/6
 * 邮箱：365941593@qq.com
 * 描述：
 */

public class RxDialogLoading {
    private Context mContext;
    private Dialog mDialog;
    private View mDialogContentView;
    private ProgressBar progressBar;
    private TextView tvProgress;


    public RxDialogLoading(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mDialog = new Dialog(mContext, R.style.tran_dialog);
        mDialogContentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_progress, null);
        progressBar = mDialogContentView.findViewById(R.id.progress);
        tvProgress = mDialogContentView.findViewById(R.id.tv_progress);
        mDialog.setContentView(mDialogContentView);
        mDialog.setCanceledOnTouchOutside(false);
    }

    public void setDialogText(CharSequence text) {
        tvProgress.setText(text);
        if ("".equals(text.toString())) {
            tvProgress.setVisibility(View.GONE);
        } else {
            tvProgress.setVisibility(View.VISIBLE);
        }
    }
    public void setBackground(int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) mDialogContentView.getBackground();
        gradientDrawable.setColor(color);
    }

    public void setmDialogColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
        }
    }
    public void setLoadingText(CharSequence charSequence) {
//        mLoadingView.setLoadingText(charSequence);
    }

    public void show() {
        mDialog.show();

    }
    public boolean isShow() {
        return mDialog.isShowing();
    }
    public void hide() {
        if (mDialog.getWindow().getDecorView() != null) {
            mDialog.dismiss();
        }
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void setOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        mDialog.setCancelable(true);
    }
    public void setNoCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        mDialog.setCancelable(false);
    }
    public void setCanceledOnBack(boolean cancel,DialogInterface.OnKeyListener onKeyListener) {
        mDialog.setCanceledOnTouchOutside(cancel);
        mDialog.setCancelable(true);
        mDialog.setOnKeyListener(onKeyListener);
        /*mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK ) {
                    return true;
                }
                return false;
            }
        });*/
    }

}
