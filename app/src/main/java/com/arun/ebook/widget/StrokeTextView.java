package com.arun.ebook.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.arun.ebook.utils.DensityUtil;

/**
 * Created by Administrator on 2018/4/23.
 */

public class StrokeTextView extends AppCompatTextView {
    private int widthDp;
    private String colorString;
    private int radiusDp;
    private int strokeWidthDp;

    public StrokeTextView(Context context, int widthDp, int radiusDp, int strokeWidthDp, String colorString) {
        super(context);
        this.widthDp = widthDp;
        this.colorString = colorString;
        this.radiusDp = radiusDp;
        this.strokeWidthDp = strokeWidthDp;
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TextPaint paint = getPaint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#15140F"));
        paint.setStrokeWidth(DensityUtil.dp2px(strokeWidthDp));
        paint.setStyle(Paint.Style.STROKE);

        int rectRightBottom = DensityUtil.dp2px(widthDp - strokeWidthDp);
        RectF rectStroke = new RectF(DensityUtil.dp2px(strokeWidthDp), DensityUtil.dp2px(strokeWidthDp), rectRightBottom, rectRightBottom);
        canvas.drawRoundRect(rectStroke, DensityUtil.dp2px(radiusDp), DensityUtil.dp2px(radiusDp), paint);

        paint.setColor(Color.parseColor(colorString));
        paint.setStyle(Paint.Style.FILL);
        RectF rectInner = new RectF(DensityUtil.dp2px(strokeWidthDp), DensityUtil.dp2px(strokeWidthDp), rectRightBottom, rectRightBottom);
        canvas.drawRoundRect(rectInner, DensityUtil.dp2px(radiusDp), DensityUtil.dp2px(radiusDp), paint);
    }
}
