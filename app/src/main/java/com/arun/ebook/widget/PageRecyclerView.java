package com.arun.ebook.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.arun.ebook.listener.PageViewListener;

public class PageRecyclerView extends RecyclerView {
    private float downX = 0;
    private float downY = 0;
    private PageViewListener pageViewListener;

    public PageRecyclerView(Context context) {
        super(context);
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PageRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPageViewListener(PageViewListener pageViewListener) {
        this.pageViewListener = pageViewListener;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        Log.d("TAG", "onPageViewListenerDraw");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        /*float eventX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = eventX - downX;
                if (moveX > 50 || moveX < -50) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                float moveUpX = eventX - downX;
                if (pageViewListener != null) {
                    if (moveUpX > 50) {//上一页
                        pageViewListener.showPrePage();
                        return false;
                    } else if (moveUpX < -50) {
                        pageViewListener.showNextPage();
                        return false;
                    }
                }
                break;
        }*/
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d("TAG", "PageRecyclerView onTouchEvent");
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                downY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float moveUpX = Math.abs(eventX - downX);
                float moveUpY = Math.abs(eventY - downY);
                if (pageViewListener != null) {
                    if (moveUpX < 2 && moveUpY < 2) {
                        boolean isLeft = downX > 0 && downX < (getWidth() / 2);
                        if (isLeft) {
                            Log.d("TAG", "PageRecyclerView showPrePage");
                            pageViewListener.showPrePage();
                        } else {
                            Log.d("TAG", "PageRecyclerView showNextPage");
                            pageViewListener.showNextPage();
                        }
                        return false;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
