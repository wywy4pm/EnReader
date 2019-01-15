package com.arun.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.arun.ebook.R;
import com.arun.ebook.adapter.ReadPageAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookItemBean;
import com.arun.ebook.common.Constant;
import com.arun.ebook.event.HidePopEvent;
import com.arun.ebook.event.LongPressEvent;
import com.arun.ebook.presenter.BookPresenter;
import com.arun.ebook.view.CommonView4;
import com.arun.ebook.widget.ReadViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends BaseActivity implements CommonView4<List<BookDetailBean>> {
    private BookItemBean bookItem;
    private String bookId;
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
                    bookId = bookItem.bookId;
                }
            }
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        readPageAdapter = new ReadPageAdapter(getSupportFragmentManager(), pageList);
        viewPager.setAdapter(readPageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentShowPage = position / 3 + 1;
                posOne = (currentShowPage - 1) * 3;
                EventBus.getDefault().post(new HidePopEvent());
                if (position == pageList.size() - 1) {
                    getMoreData();
                }
            }
        });
        bookPresenter = new BookPresenter();
        bookPresenter.attachView(this);
        getData();
    }

    private void getData() {
        currentPage = 1;
        getBookDetail();
    }

    private void getMoreData() {
        currentPage++;
        getBookDetail();
    }

    private void getBookDetail() {
        if (bookPresenter != null) {
            bookPresenter.getBookDetail(bookId, PAGE_SIZE, currentPage);
        }
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
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
        if (data != null && data.size() > 0) {
            if (isEdit) {
                if (data.size() == 3) {
                    addList(data, posOne, posOne + 1, posOne + 2);
                }
                isEdit = false;
            } else {
                pageList.addAll(data);
            }
            readPageAdapter.updateData(pageList, totalCount);
        }
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
            bookPresenter.getBookDetail(bookId, PAGE_SIZE, currentShowPage);
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
}
