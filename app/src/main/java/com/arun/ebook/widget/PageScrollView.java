package com.arun.ebook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class PageScrollView extends ScrollView {

    private float downX = 0;

    public PageScrollView(Context context) {
        super(context);
    }

    public PageScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = eventX - downX;
                if (moveX > 8 || moveX < -8) {
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }
}
