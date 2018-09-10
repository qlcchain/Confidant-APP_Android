package com.stratagile.pnrouter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Image;
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

public class DrawableTextView extends LinearLayout {

    private TextView titleText;
    private ImageView ivNext;
    public DrawableTextView(Context context) {
        super(context);
        init(context);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initTypeArray(attrs);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initTypeArray(attrs);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.drawble_textview, this, true);
        titleText = view.findViewById(R.id.title);
        ivNext = view.findViewById(R.id.ivNext);
    }

    private void initTypeArray(AttributeSet attrs) {
        TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.DrawableTextView);
        String text = typedArray.getString(R.styleable.DrawableTextView_text);
        boolean isShowNext = typedArray.getBoolean(R.styleable.DrawableTextView_showNext, true);
        setShowNext(isShowNext);
        setTitleText(text);
    }

    public void setTitleText(CharSequence text) {
        titleText.setText(text);
        invalidate();
    }

    public void setShowNext(boolean showNext) {
        ivNext.setVisibility(showNext? VISIBLE : INVISIBLE);
    }
}
