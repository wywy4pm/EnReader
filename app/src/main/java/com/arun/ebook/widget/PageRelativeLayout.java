package com.arun.ebook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class PageRelativeLayout extends RelativeLayout {
    public PageRelativeLayout(Context context) {
        super(context);
    }

    public PageRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
