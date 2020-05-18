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

public class FolderSelectTextView extends LinearLayout {

    private TextView titleText;
    private ImageView ivIcon;
    public FolderSelectTextView(Context context) {
        super(context);
        init(context);
    }

    public FolderSelectTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initTypeArray(attrs);
    }

    public FolderSelectTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initTypeArray(attrs);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_select_textview, this, true);
        titleText = view.findViewById(R.id.title);
        ivIcon = view.findViewById(R.id.ivIcon);
    }

    private void initTypeArray(AttributeSet attrs) {
        TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.FolderSelectTextView);
        String text = typedArray.getString(R.styleable.FolderSelectTextView_foldertext);
        Drawable d = typedArray.getDrawable(R.styleable.FolderSelectTextView_foldersrc);
        if (d != null) {
            ivIcon.setImageDrawable(d);
        }
        setTitleText(text);
    }

    public void setTitleText(CharSequence text) {
        titleText.setText(text);
        invalidate();
    }
}
