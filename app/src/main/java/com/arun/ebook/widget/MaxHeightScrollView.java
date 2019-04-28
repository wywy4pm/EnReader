package com.arun.ebook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.arun.ebook.utils.DensityUtil;

public class MaxHeightScrollView extends ScrollView {

    private int maxHeight;

    public MaxHeightScrollView(Context context) {
        super(context);
        setMaxHeight(DensityUtil.getScreenHeight(context) / 2);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaxHeight(DensityUtil.getScreenHeight(context) / 2);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMaxHeight(DensityUtil.getScreenHeight(context) / 2);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}