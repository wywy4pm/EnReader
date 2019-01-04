package com.arun.ebook.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.arun.ebook.event.HidePopEvent;

import org.greenrobot.eventbus.EventBus;

public class ReadViewPager extends ViewPager{
    private boolean isLongPressPopShow;
    private float downX = 0;
    private float downY = 0;

    public ReadViewPager(Context context) {
        super(context);
    }

    public ReadViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLongPressPopShow(boolean longPressPopShow) {
        isLongPressPopShow = longPressPopShow;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                downY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveUpX = Math.abs(eventX - downX);
                float moveUpY = Math.abs(eventY - downY);
                if (moveUpX > 10 && moveUpY > 10) {
                    if (isLongPressPopShow) {
                        Log.d("TAG", "ReadRecyclerView isLongPressPopShow");
                        EventBus.getDefault().post(new HidePopEvent());
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }
}
