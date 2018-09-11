package com.stratagile.pnrouter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stratagile.pnrouter.R;

/**
 * Created by huzhipeng on 2018/1/2.
 */

public class DetailView extends LinearLayout {

    private TextView titleText;
    private ImageView ivNext;
    private ImageView ivContent;
    private TextView tvContent;
    public DetailView(Context context) {
        super(context);
        init(context);
    }

    public DetailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initTypeArray(attrs);
    }

    public DetailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initTypeArray(attrs);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_textview, this, true);
        titleText = view.findViewById(R.id.title);
        tvContent = view.findViewById(R.id.tvContent);
        ivNext = view.findViewById(R.id.ivNext);
        ivContent = view.findViewById(R.id.ivContent);

    }

    private void initTypeArray(AttributeSet attrs) {
        TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.DetailView);
        String text = typedArray.getString(R.styleable.DetailView_text1);
        String contentStr = typedArray.getString(R.styleable.DetailView_contentStr);
        boolean isShowNext = typedArray.getBoolean(R.styleable.DetailView_showNext1, true);
        boolean isShowImg = typedArray.getBoolean(R.styleable.DetailView_showImg, false);
        Drawable d = typedArray.getDrawable(R.styleable.DetailView_src);
        if (d != null) {
            ivContent.setImageDrawable(d);
        }
        setShowNext(isShowNext);
        setTitleText(text);
        setContentVisible(isShowImg);
        tvContent.setText(contentStr);
    }

    public void setTitleText(CharSequence text) {
        titleText.setText(text);
        invalidate();
    }

    public void setShowNext(boolean showNext) {
        ivNext.setVisibility(showNext? VISIBLE : INVISIBLE);
        invalidate();
    }

    public void setContentVisible(boolean showImg) {
        if (showImg) {
            ivContent.setVisibility(VISIBLE);
            tvContent.setVisibility(GONE);
        } else {
            ivContent.setVisibility(GONE);
            tvContent.setVisibility(VISIBLE);
        }
        invalidate();
    }
}
