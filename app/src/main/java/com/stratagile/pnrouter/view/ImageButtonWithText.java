package com.stratagile.pnrouter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.utils.GlideCircleTransform;
import com.stratagile.pnrouter.utils.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ImageButtonWithText extends RelativeLayout {
    public ImageView imageView;
    public TextView textView;

    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .transform(new GlideCircleTransform())
            .priority(Priority.HIGH);

    public ImageButtonWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageButtonWithText);
        /*
        * 在attrs.xml添加属性：
        *   <declare-styleable name="ImageButtonWithText">
             <attr name="picture" format="reference"/>
            </declare-styleable>
        * */
        int picture_id = a.getResourceId(R.styleable.ImageButtonWithText_picture, -1);
        /**
         * Recycle the TypedArray, to be re-used by a later caller. After calling
         * this function you must not ever touch the typed array again.
         */
        a.recycle();
        imageView = new ImageView(context, attrs);

        /**
         * Sets a drawable as the content of this ImageView.
         * This does Bitmap reading and decoding on the UI
         * thread, which can cause a latency hiccup.  If that's a concern,
         * consider using setImageDrawable(android.graphics.drawable.Drawable) or
         * setImageBitmap(android.graphics.Bitmap) instead.
         * 直接在UI线程读取和解码Bitmap，可能会存在潜在的性能问题
         * 可以考虑使用 setImageDrawable(android.graphics.drawable.Drawable)
         * 或者setImageBitmap(android.graphics.Bitmap) 代替
         */
//        imageView.setImageResource(picture_id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        textView = new TextView(context, attrs);
        textView.setBackground(getContext().getResources().getDrawable(R.drawable.nick_text_bg));
        /**
         * Sets the horizontal alignment of the text and the
         * vertical gravity that will be used when there is extra space
         * in the TextView beyond what is required for the text itself.
         */
        //水平居中
        RelativeLayout.LayoutParams ll = new RelativeLayout.LayoutParams((int) getContext().getResources().getDimension(R.dimen.x100), (int) getContext().getResources().getDimension(R.dimen.x100));
//        imageView.setLayoutParams(ll);
        textView.setLayoutParams(ll);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        textView.setPadding(0, 0, 0, 0);
        setClickable(true);
        setFocusable(true);
        imageView.setVisibility(INVISIBLE);
        addView(imageView);
        addView(textView);
    }

    public void setText(int resId) {
        textView.setText(resId);
    }

    public void setText(CharSequence buttonText) {
        String[] strings = buttonText.toString().toUpperCase().split(" ");
        List<String> stringArrayList = Arrays.asList(strings);
        Iterator itTemp = stringArrayList.iterator();
        ArrayList<String> realList = new ArrayList<>();
        while (itTemp.hasNext()) {
            String next = (String) itTemp.next();
            if (!"".equals(next)) {
                realList.add(next);
            }
        }
        String showText = "";
        for (int i = 0; i < realList.size(); i++) {
            if (i < 2) {
                showText += realList.get(i).substring(0, 1);
            }
        }
        textView.setText(showText);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
    }

    public void setImage(String url) {
        if ("".equals(url)) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(url)
                    .apply(options)
                    .into(imageView);
        }
    }
}