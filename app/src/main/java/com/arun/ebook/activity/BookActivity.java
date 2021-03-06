package com.arun.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.arun.ebook.R;
import com.arun.ebook.adapter.ReadPageAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookItemBean;
import com.arun.ebook.bean.BookPageIdsData;
import com.arun.ebook.bean.ConfigData;
import com.arun.ebook.bean.FontBean;
import com.arun.ebook.bean.PageStyleData;
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

import java.io.File;
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
    private View groupView;
    public int readBg;
    public int textColor;
    public double textScale = 1;
    public FontBean fontBean;

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
        groupView = LayoutInflater.from(this).inflate(R.layout.activity_book, null);
        setContentView(groupView);
        EventBus.getDefault().register(this);
        initData();
        initView();
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    public void setReadBg(int bgColor) {
        if (bgColor != 0) {
            this.readBg = bgColor;
            groupView.setBackgroundColor(bgColor);
        }
    }

    public void setTextColor(int textColor) {
        if (textColor != 0) {
            this.textColor = textColor;
        }
    }

    public void setEnSize(double enSize) {
        this.textScale = enSize;
    }

    public void setEnFont(FontBean bean) {
        this.fontBean = bean;
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
        currentPosStr = SharedPreferencesUtils.getPageIds(this, String.valueOf(bookId));
        if (TextUtils.isEmpty(currentPosStr)) {
            if (bookItem != null && bookItem.readSeq > 0) {
                currentPosStr = String.valueOf(bookItem.readSeq - 1);
            }
        }
        readBg = SharedPreferencesUtils.getConfigInt(this, SharedPreferencesUtils.KEY_READ_BG);
        if (readBg != 0) {
            setReadBg(readBg);
        }
        textColor = SharedPreferencesUtils.getConfigInt(this, SharedPreferencesUtils.KEY_READ_TEXT_COLOR);
        String scaleText = SharedPreferencesUtils.getConfigString(this, SharedPreferencesUtils.KEY_READ_EN_SIZE);
        if (!TextUtils.isEmpty(scaleText)) {
            textScale = Double.valueOf(scaleText);
        }
        String fontFilePath = SharedPreferencesUtils.getConfigString(this, SharedPreferencesUtils.KEY_READ_EN_FONT);
        if (!TextUtils.isEmpty(fontFilePath)) {
            fontBean = new FontBean();
            File fontFile = null;
            String fontName = "";
            if (!"默认".equals(fontFilePath)) {
                fontFile = new File(fontFilePath);
                fontName = fontFile.getName();
            } else {
                fontName = "默认";
            }
            fontBean.fontName = fontName;
            fontBean.file = fontFile;
        }
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
                SharedPreferencesUtils.setPageIds(BookActivity.this, String.valueOf(bookId), String.valueOf(currentPos));
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
        getPageStyle();
    }

    private void getPageStyle() {
        if (bookPresenter != null) {
            bookPresenter.getPageStyle();
        }
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
        } else if (type == BookPresenter.TYPE_PAGE_STYLE) {
            if (data instanceof PageStyleData) {
                PageStyleData pageStyleData = (PageStyleData) data;
                ConfigData.bgColor = pageStyleData.bg_color;
                ConfigData.textColor = pageStyleData.text_color;
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
        //取消保持屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //SharedPreferencesUtils.setPageIds(this, String.valueOf(bookId), String.valueOf(currentPos));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getSupportFragmentManager().getFragments() != null && getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
