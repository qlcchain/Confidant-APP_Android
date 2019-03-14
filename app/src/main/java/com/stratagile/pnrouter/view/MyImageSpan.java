package com.stratagile.pnrouter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import com.stratagile.pnrouter.R;

public class MyImageSpan extends ImageSpan {
    private Context context;
    public MyImageSpan(Context arg0, int arg1) {
        super(arg0, arg1);
        this.context = arg0;
    }

    public MyImageSpan(Drawable drawable, int arg1) {
        super(drawable, arg1);

    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
//        d.setBounds(0, 0, (int) context.getResources().getDimension(R.dimen.x28), (int) context.getResources().getDimension(R.dimen.x28));//这里设置图片的大小
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
