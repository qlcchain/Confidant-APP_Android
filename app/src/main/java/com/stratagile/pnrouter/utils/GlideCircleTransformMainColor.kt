package com.stratagile.pnrouter.utils

import android.content.Context
import android.graphics.*

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig

import java.security.MessageDigest

/**
 * Created by huzhipeng on 2018/1/3.
 */

class GlideCircleTransformMainColor(private val context: Context)//        super(context);
    : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
        return circleCrop(pool, toTransform, context)
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?, context: Context): Bitmap? {
        if (source == null) {
            return null
        }
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        // TODO this could be acquired from the pool too
        val squared = Bitmap.createBitmap(source, x, y, size, size)
        var result: Bitmap? = pool.get(size, size, Bitmap.Config.ARGB_8888)
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result!!)
        val paint = Paint()
        paint.shader = BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true

        //绘制边框
        val mBorderPaint = Paint()
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = UIUtils.dip2px(2f, AppConfig.instance).toFloat()//画笔宽度为4px
        mBorderPaint.color = context.resources.getColor(R.color.mainColor)//边框颜色
        mBorderPaint.strokeCap = Paint.Cap.ROUND
        mBorderPaint.isAntiAlias = true
        val r = size / 2f
        val r1 = (size - 2 * 4) / 2f
        canvas.drawCircle(r, r, r1, paint)
//        canvas.drawCircle(r, r, r1, mBorderPaint)//画边框
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }
}