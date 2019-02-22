package com.stratagile.pnrouter.view

import android.content.Context
import android.os.Environment
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.utils.GlideCircleTransform
import com.stratagile.pnrouter.utils.GlideCircleTransformMainColor
import com.stratagile.pnrouter.utils.SpUtil
import java.io.File
import java.util.*

class ImageButtonWithText(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    var imageView: ImageView
    var textView: TextView
    var withShape = false
    var options = RequestOptions()
            .centerCrop()
            .transform(GlideCircleTransformMainColor(context))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .priority(Priority.HIGH)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ImageButtonWithText)
        val view = LayoutInflater.from(context).inflate(R.layout.image_button_with_text, this, true)
        val textSize = a.getFloat(R.styleable.ImageButtonWithText_imageButtonTextSize, 16F)
        //是否包含边框
        withShape = a.getBoolean(R.styleable.ImageButtonWithText_withShape, false)
        /**
         * Recycle the TypedArray, to be re-used by a later caller. After calling
         * this function you must not ever touch the typed array again.
         */
        imageView = view.findViewById(R.id.ibIv)

        a.recycle()


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
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        textView = view.findViewById(R.id.ibTv)
        textView.background = getContext().resources.getDrawable(R.drawable.nick_text_bg)

        setTextSize(textSize)
        /**
         * Sets the horizontal alignment of the text and the
         * vertical gravity that will be used when there is extra space
         * in the TextView beyond what is required for the text itself.
         */
        //水平居中
        val ll = RelativeLayout.LayoutParams(getContext().resources.getDimension(R.dimen.x100).toInt(), getContext().resources.getDimension(R.dimen.x100).toInt())
        //        imageView.setLayoutParams(ll);
        textView.layoutParams = ll!!
        textView.gravity = Gravity.CENTER
        textView.setTextColor(getContext().resources.getColor(R.color.white))
        textView.setPadding(0, 0, 0, 0)
        imageView.visibility = View.INVISIBLE
    }

    fun setText(resId: Int) {
        textView.setText(resId)
    }

    fun setText(buttonText: CharSequence?) {
//        KLog.i(buttonText)
        val strings = buttonText.toString().toUpperCase().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringArrayList = Arrays.asList(*strings)
        val itTemp = stringArrayList.iterator()
        val realList = ArrayList<String>()
        while (itTemp.hasNext()) {
            val next = itTemp.next() as String
            if ("" != next) {
                realList.add(next)
            }
        }
        var showText = ""
        for (i in realList.indices) {
            if (i < 2) {
                showText += realList[i].substring(0, 1)
            }
        }
        if (withShape) {
            textView.background = context.resources.getDrawable(R.drawable.imagebutton_withtext_shape_bg)
        }
        textView.text = showText
    }

    fun setTextColor(color: Int) {
        textView.setTextColor(color)
    }

    fun setImage(url: String) {
        if ("" == url) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
            imageView.visibility = View.VISIBLE
            Glide.with(context)
                    .load(url)
                    .apply(AppConfig.instance.options)
                    .into(imageView)
        }
    }

    fun setTextSize(size : Float) {
        textView.setTextSize(size)
    }

    fun setImageFile(url: String) {
        if ("" == url) {
            textView.visibility = View.VISIBLE
        } else {
            if (SpUtil.getString(context, ConstantValue.selfImageName, "") != "") {
                val lastFile = File(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath+"/" + SpUtil.getString(context, ConstantValue.selfImageName, ""), "")
                if (lastFile.exists()) {
                    textView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    if (withShape) {
                        var options1 = RequestOptions()
                                .centerCrop()
                                .transform(GlideCircleTransform())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .priority(Priority.HIGH)
                        Glide.with(this)
                                .load(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath+"/" + SpUtil.getString(context, ConstantValue.selfImageName, ""))
                                .apply(options1)
                                .into(imageView)
                    } else {
                        Glide.with(this)
                                .load(Environment.getExternalStorageDirectory().toString() + ConstantValue.localPath+"/" + SpUtil.getString(context, ConstantValue.selfImageName, ""))
                                .apply(options)
                                .into(imageView)
                    }
                } else {

                }
            }
        }
    }
}