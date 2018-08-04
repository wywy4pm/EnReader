package com.arun.ebook.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.arun.ebook.listener.PageViewListener;

public class PageReadViewGroup extends RelativeLayout {
    private float downX = 0;
    private PageViewListener pageViewListener;

    public PageReadViewGroup(Context context) {
        super(context);
    }

    public PageReadViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageReadViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPageViewListener(PageViewListener pageViewListener) {
        this.pageViewListener = pageViewListener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                break;
            case MotionEvent.ACTION_MOVE:
                /*float moveX = eventX - downX;
                if (moveX > 50 || moveX < -50) {
                    return false;
                }*/
                break;
            case MotionEvent.ACTION_UP:
                /*float moveUpX = eventX - downX;
                if (pageViewListener != null) {
                    if (moveUpX > 50) {//上一页
                        pageViewListener.showPrePage();
                        return false;
                    } else if (moveUpX < -50) {
                        pageViewListener.showNextPage();
                        return false;
                    }
                }*/
                /*boolean isLeft = downX > 0 && downX < (getWidth() / 2);
                if (isLeft) {
                    pageViewListener.showPrePage();
                    return false;
                } else {
                    pageViewListener.showNextPage();
                    return false;
                }*/
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAG", "PageReadViewGroup onTouchEvent");
        boolean isLeft = downX > 0 && downX < (getWidth() / 2);
        if (isLeft) {
            pageViewListener.showPrePage();
        } else {
            pageViewListener.showNextPage();
        }
        return super.onTouchEvent(event);
    }*/
}
