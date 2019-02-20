package com.arun.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.arun.ebook.R;
import com.arun.ebook.adapter.ReadPageAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookItemBean;
import com.arun.ebook.bean.BookPageIdsData;
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
    //private int currentPage = 1;
    private int posOne = 0;
    private int currentShowPage = 1;
    private int totalCount;
    //private List<BookDetailBean> cacheThreePageList = new ArrayList<>();
    private List<BookDetailBean> pageList = new ArrayList<>();
    //private PageViewGroup groupView;
    private boolean isPressPopShow;
    private boolean isEdit;
    private String currentPosStr;
    private int currentPos;
    private List<Integer> page_ids;
    private String pageIds;

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
        //currentPos = SharedPreferencesUtils.getConfigInt(this, SharedPreferencesUtils.KEY_READ_POS);
        //currentPage = currentPos / PAGE_SIZE + 1;

        currentPosStr = SharedPreferencesUtils.getPageIds(this, String.valueOf(bookId));
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        readPageAdapter = new ReadPageAdapter(getSupportFragmentManager(), pageList, bookId);
        viewPager.setAdapter(readPageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("TAG", "onPageSelected position = " + position);
                if (pageList != null && pageList.get(position) != null) {
                    currentPos = pageList.get(position).seq - 1;
                }
                currentShowPage = position / PAGE_SIZE + 1;
                posOne = (currentShowPage - 1) * PAGE_SIZE;
                EventBus.getDefault().post(new HidePopEvent());
                if (position == pageList.size() - 1) {
                    if (getPageIds(true, currentPos)) {
                        getBookDetail(true, false);
                    }
                } else if (position == 0) {
                    if (getPageIds(false, currentPos)) {
                        getBookDetail(false, false);
                    }
                }
            }
        });
        bookPresenter = new BookPresenter();
        bookPresenter.attachView(this);
        getBookPageIds();

    }

    private void getBookPageIds() {
        if (bookPresenter != null) {
            bookPresenter.getBookPageIds(bookId);
        }
    }

    private void getBookDetail(boolean isNext, boolean isFirst) {
        if (bookPresenter != null) {
            bookPresenter.getBookDetail(bookId, pageIds, isNext, isFirst);
        }
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void refreshData(List<BookDetailBean> data, boolean isNext, boolean isFirst) {
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
            if (isFirst) {
                if (getPageIds(false, currentPos)) {
                    getBookDetail(false, false);
                }
            }
        }
    }

    @Override
    public void refresh(List<BookDetailBean> data) {
    }

    @Override
    public void refreshMore(List<BookDetailBean> data) {
    }

    @Override
    public void refresh(int type, Object data) {
        if (type == BookPresenter.TYPE_BOOK_PAGE_IDS) {
            if (data instanceof BookPageIdsData) {
                page_ids = ((BookPageIdsData) data).page_ids;
                if (TextUtils.isEmpty(currentPosStr)) {
                    if (page_ids.size() >= 3) {
                        pageIds = page_ids.get(0) + "," + page_ids.get(1) + "," + page_ids.get(2);
                    } else if (page_ids.size() == 2) {
                        pageIds = page_ids.get(0) + "," + page_ids.get(1);
                    } else if (page_ids.size() == 1) {
                        pageIds = String.valueOf(page_ids.get(0));
                    }
                    getBookDetail(true, false);
                } else {
                    currentPos = Integer.parseInt(currentPosStr);
                    if (getPageIds(true, currentPos - 1)) {
                        getBookDetail(true, true);
                    }
                }
            }
        }
    }

    private boolean getPageIds(boolean isNext, int currentPos) {
        boolean isLoad = true;
        if (isNext) {
            if (page_ids != null && page_ids.size() > 0) {
                if (currentPos < page_ids.size() - 1) {
                    if (currentPos == page_ids.size() - 2) {
                        pageIds = String.valueOf(page_ids.get(currentPos + 1));
                    } else if (currentPos == page_ids.size() - 3) {
                        pageIds = page_ids.get(currentPos + 1) + "," + page_ids.get(currentPos + 2);
                    } else {
                        pageIds = page_ids.get(currentPos + 1) + "," + page_ids.get(currentPos + 2) + "," + page_ids.get(currentPos + 3);
                    }
                } else {
                    isLoad = false;
                }
            }
        } else {
            if (page_ids != null && page_ids.size() > 0) {
                if (currentPos > 0) {
                    if (currentPos == 1) {
                        pageIds = String.valueOf(page_ids.get(currentPos - 1));
                    } else if (currentPos == 2) {
                        pageIds = page_ids.get(currentPos - 2) + "," + page_ids.get(currentPos - 1);
                    } else {
                        pageIds = page_ids.get(currentPos - 3) + "," + page_ids.get(currentPos - 2) + "," + page_ids.get(currentPos - 1);
                    }
                } else {
                    isLoad = false;
                }
            }
        }
        return isLoad;
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
            bookPresenter.getBookDetail(bookId, "", true, false);
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
        SharedPreferencesUtils.setPageIds(this, String.valueOf(bookId), String.valueOf(currentPos));
    }

}
