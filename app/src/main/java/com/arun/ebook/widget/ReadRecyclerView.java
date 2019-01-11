package com.arun.ebook.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.arun.ebook.event.HidePopEvent;
import com.arun.ebook.event.RemoveLongPressEvent;

import org.greenrobot.eventbus.EventBus;

public class ReadRecyclerView extends RecyclerView {
    private boolean isLongPressPopShow;
    private float downX = 0;
    private float downY = 0;

    public ReadRecyclerView(Context context) {
        super(context);
    }

    public ReadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
                Log.d("TAG", "ReadRecyclerView ACTION_DOWN");
                downX = eventX;
                downY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = Math.abs(eventX - downX);
                float moveY = Math.abs(eventY - downY);
                Log.d("TAG", "ReadRecyclerView ACTION_MOVE");
                if (moveY > 10) {
                    if (isLongPressPopShow) {
                        Log.d("TAG", "ReadRecyclerView isLongPressPopShow");
                        EventBus.getDefault().post(new HidePopEvent());
                    } else {
                        EventBus.getDefault().post(new RemoveLongPressEvent());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                float moveUpX = Math.abs(eventX - downX);
                float moveUpY = Math.abs(eventY - downY);
                if (moveUpX < 10 && moveUpY < 10) {
                    Log.d("TAG", "ReadRecyclerView ACTION_UP");
                    if (isLongPressPopShow) {
                        Log.d("TAG", "ReadRecyclerView isLongPressPopShow");
                        EventBus.getDefault().post(new HidePopEvent());
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
