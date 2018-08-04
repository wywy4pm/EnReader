package com.arun.ebook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TextGroupView extends RelativeLayout {
    public TextGroupView(Context context) {
        super(context);
    }

    public TextGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAG", "TextGroupView onTouchEvent");
        return super.onTouchEvent(event);
    }
}
