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
import com.arun.ebook.presenter.BookPresenter;
import com.arun.ebook.view.CommonView4;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends BaseActivity implements CommonView4<List<BookDetailBean>> {
    private BookItemBean bookItem;
    private String bookId;
    private static final int PAGE_SIZE = 3;
    private BookPresenter bookPresenter;
    private ViewPager viewPager;
    private ReadPageAdapter readPageAdapter;
    private int currentPage = 1;
    private int totalCount;
    private List<BookDetailBean> pageList = new ArrayList<>();

    public static void jumpToBook(Context context, BookItemBean item) {
        Intent intent = new Intent(context, BookActivity.class);
        intent.putExtra(Constant.INTENT_BOOK_ITEM, item);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_book);
        initData();
        initView();
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
                currentPage = position + 1;
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
            pageList.addAll(data);
            readPageAdapter.updateData(pageList, totalCount);
        }
    }

    @Override
    public void refresh(int type, Object data) {

    }
}
