package com.arun.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.arun.ebook.R;
import com.arun.ebook.adapter.ReadPageAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookItemBean;
import com.arun.ebook.common.Constant;
import com.arun.ebook.event.HidePopEvent;
import com.arun.ebook.event.LongPressEvent;
import com.arun.ebook.presenter.BookPresenter;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.view.CommonView4;
import com.arun.ebook.widget.ReadViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends BaseActivity implements CommonView4<List<BookDetailBean>> {
    private BookItemBean bookItem;
    private int bookId;
    private static final int PAGE_SIZE = 3;
    private BookPresenter bookPresenter;
    private ReadViewPager viewPager;
    private ReadPageAdapter readPageAdapter;
    private int currentPage = 1;
    private int posOne = 0;
    private int currentShowPage = 1;
    private int totalCount;
    //private List<BookDetailBean> cacheThreePageList = new ArrayList<>();
    private List<BookDetailBean> pageList = new ArrayList<>();
    //private PageViewGroup groupView;
    private boolean isPressPopShow;
    private boolean isEdit;
    private int currentPos;

    public static void jumpToBook(Context context, BookItemBean item) {
        Intent intent = new Intent(context, BookActivity.class);
        intent.putExtra(Constant.INTENT_BOOK_ITEM, item);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        //groupView = (PageViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_book, null);
        setContentView(R.layout.activity_book);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Constant.INTENT_BOOK_ITEM)) {
                bookItem = (BookItemBean) getIntent().getExtras().getSerializable(Constant.INTENT_BOOK_ITEM);
                if (bookItem != null) {
                    bookId = bookItem.book_id;
                }
            }
        }
        currentPos = SharedPreferencesUtils.getConfigInt(this, SharedPreferencesUtils.KEY_READ_POS);
        currentPage = currentPos / PAGE_SIZE + 1;
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        readPageAdapter = new ReadPageAdapter(getSupportFragmentManager(), pageList);
        viewPager.setAdapter(readPageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (pageList != null && pageList.get(position) != null) {
                    currentPos = pageList.get(position).seq - 1;
                }
                currentShowPage = position / PAGE_SIZE + 1;
                posOne = (currentShowPage - 1) * PAGE_SIZE;
                EventBus.getDefault().post(new HidePopEvent());
                if (position == pageList.size() - 1) {
                    getNextData();
                } else if (position == 0) {
                    getPreData();
                }
            }
        });
        bookPresenter = new BookPresenter();
        bookPresenter.attachView(this);
        getData();
    }

    private void getData() {
        currentPage = 1;
        getBookDetail(true);
    }

    private void getNextData() {
        currentPage++;
        getBookDetail(true);
    }

    private void getPreData() {
        currentPage--;
        getBookDetail(false);
    }

    private void getBookDetail(boolean isNext) {
        if (bookPresenter != null) {
            bookPresenter.getBookDetail(bookId, "", isNext);
        }
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void refreshMoreData(List<BookDetailBean> data, boolean isNext) {
        if (data != null && data.size() > 0) {
            if (isEdit) {
                if (data.size() == 3) {
                    addList(data, posOne, posOne + 1, posOne + 2);
                }
                isEdit = false;
            } else {
                if (isNext) {
                    pageList.addAll(data);
                } else {
                    pageList.addAll(0, data);
                }
            }
            readPageAdapter.updateData(pageList, totalCount);
        }
    }

    @Override
    public void refresh(List<BookDetailBean> data) {
        if (data != null && data.size() > 0) {
            pageList.clear();
            pageList.addAll(data);
            readPageAdapter.updateData(pageList, totalCount);
        }
    }

    @Override
    public void refreshMore(List<BookDetailBean> data) {
       /* if (data != null && data.size() > 0) {
            if (isEdit) {
                if (data.size() == 3) {
                    addList(data, posOne, posOne + 1, posOne + 2);
                }
                isEdit = false;
            } else {
                pageList.addAll(data);
            }
            readPageAdapter.updateData(pageList, totalCount);
        }*/
    }

    @Override
    public void refresh(int type, Object data) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLongPressPopChanged(LongPressEvent longPressEvent) {
        isPressPopShow = !longPressEvent.isHide;
        viewPager.setLongPressPopShow(isPressPopShow);
    }

    public void refreshData(boolean isEdit) {
        this.isEdit = isEdit;
        removeList(posOne, posOne + 1, posOne + 2);
        if (bookPresenter != null) {
            bookPresenter.getBookDetail(bookId, "", true);
        }
    }

    public void addList(List<BookDetailBean> beanList, int... position) {
        for (int i = 0; i < position.length; i++) {
            pageList.add(position[i], beanList.get(i));
        }
    }

    public void removeList(int... position) {
        List<BookDetailBean> deleteList = new ArrayList<>();
        for (int i = 0; i < position.length; i++) {
            deleteList.add(pageList.get(i));
        }
        pageList.removeAll(deleteList);
    }

    public void backMain(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferencesUtils.setConfigInt(this, SharedPreferencesUtils.KEY_READ_POS, currentPos);
    }
}
