package com.arun.ebook.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.BookActivity;
import com.arun.ebook.adapter.BookDetailAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookDetailItemBean;
import com.arun.ebook.bean.BookEditBean;
import com.arun.ebook.bean.FontBean;
import com.arun.ebook.bean.TranslateData;
import com.arun.ebook.common.Constant;
import com.arun.ebook.dialog.NewTranslateDialog;
import com.arun.ebook.dialog.TranslateDialog;
import com.arun.ebook.event.EditPageEvent;
import com.arun.ebook.event.LongPressEvent;
import com.arun.ebook.listener.BookEditListener;
import com.arun.ebook.listener.DialogListener;
import com.arun.ebook.model.BookModel;
import com.arun.ebook.presenter.BookPresenter;
import com.arun.ebook.utils.Utils;
import com.arun.ebook.view.CommonView4;
import com.arun.ebook.widget.ReadRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadFragment extends BaseFragment implements CommonView4, BookEditListener {
    private ReadRecyclerView recyclerView;
    private BookDetailAdapter bookDetailAdapter;
    private List<BookDetailItemBean> bookDetailList = new ArrayList<>();
    private boolean isPressPopShow;
    private BookPresenter bookPresenter;
    public int currentPage;
    private NewTranslateDialog translateDialog;
    private TextView pageNum;
    private int bookId;

    public static ReadFragment newInstance(BookDetailBean bean, int bookId) {
        ReadFragment readFragment = new ReadFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.INTENT_BOOK_ID, bookId);
        bundle.putSerializable(Constant.INTENT_BOOK_DETAIL, bean);
        readFragment.setArguments(bundle);
        return readFragment;
    }

    @Override
    protected int preparedCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return R.layout.layout_book_detail;
    }

    @Override
    protected void initView() {
        pageNum = findViewById(R.id.pageNum);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        bookDetailAdapter = new BookDetailAdapter(getActivity(), bookDetailList);
        bookDetailAdapter.setBookEditListener(this);
        recyclerView.setAdapter(bookDetailAdapter);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            if (getArguments().containsKey(Constant.INTENT_BOOK_DETAIL)) {
                BookDetailBean bean = (BookDetailBean) getArguments().get(Constant.INTENT_BOOK_DETAIL);
                BookDetailItemBean<BookDetailBean> itemBean = new BookDetailItemBean<>();
                itemBean.type = BookDetailAdapter.DATA_TYPE_CONTENT;
                itemBean.content = bean;
                File[] files = Utils.readFontsFile(getActivity(), "new_fonts", Constant.PATH_FONT_NEW);
                if (files != null && bean != null) {
                    bean.file = files[0];
                }
                if (bean != null) {
                    currentPage = bean.seq;
                }
                bookDetailList.add(itemBean);
                bookPresenter = new BookPresenter();
                bookPresenter.attachView(this);
                if (bean != null) {
                    if (bean.file != null) {
                        Typeface typeface = Typeface.createFromFile(bean.file);
                        if (typeface != null) {
                            pageNum.setTypeface(typeface);
                        }
                    }
                    pageNum.setText(bean.seq + "/" + bean.totalPage);
                }
            }
            if (getArguments().containsKey(Constant.INTENT_BOOK_ID)) {
                bookId = getArguments().getInt(Constant.INTENT_BOOK_ID);
                if (bookDetailAdapter != null) {
                    bookDetailAdapter.setBook_id(bookId);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLongPressPopChange(LongPressEvent longPressEvent) {
        isPressPopShow = !longPressEvent.isHide;
        recyclerView.setLongPressPopShow(isPressPopShow);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEditBack(EditPageEvent editPageEvent) {
        if (currentPage == editPageEvent.currentPage) {
            BookEditBean bean = null;
            if (!TextUtils.isEmpty(editPageEvent.content)) {
                if (editPageEvent.isEdit) {
                    bean = new BookEditBean(editPageEvent.paragraphId, BookEditBean.TYPE_EDIT, editPageEvent.content);
                } else {
                    bean = new BookEditBean(editPageEvent.paragraphId, BookEditBean.TYPE_INSERT, editPageEvent.content);
                }
            }
            if (bean != null) {
                onBookEdit(bean);
            }
        }
    }

    @Override
    public void refresh(Object data) {
    }

    @Override
    public void refreshMore(Object data) {
    }

    @Override
    public void refresh(int type, Object data) {
        if (type == BookPresenter.TYPE_BOOK_EDIT) {
            /*if (data instanceof Boolean) {
                Boolean bool = (Boolean) data;
                if (bool) {
                    Log.d("TAG", "TYPE_BOOK_EDIT SUCCESS");
                    if (getActivity() instanceof BookActivity) {
                        ((BookActivity) getActivity()).refreshData(true);
                    }
                }
            }*/
            Log.d("TAG", "TYPE_BOOK_EDIT SUCCESS");
            if (getActivity() instanceof BookActivity) {
                ((BookActivity) getActivity()).refreshData(true);
            }
        } else if (type == BookPresenter.TYPE_BOOK_TRANSLATE) {
            if (data instanceof TranslateData) {
                TranslateData translateData = (TranslateData) data;
                translateDialog = new NewTranslateDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("TranslateData", translateData);
                /*bundle.putInt("bgColor", bgColor);
                bundle.putInt("textColor", textColor);*/
                translateDialog.setArguments(bundle);
                translateDialog.show(getFragmentManager(), "dialog");
                translateDialog.setListener(new DialogListener() {
                    @Override
                    public void onDismiss() {
                    }
                });
            }
        }
    }

    @Override
    public void onBookEdit(BookEditBean bean) {
        if (bookPresenter != null) {
            bookPresenter.bookEdit(bean.pageId, bean.type, bean.content, bean.styleId);
        }
        //((BookActivity) getActivity()).removeList(currentPage);
    }


    @Override
    public void translateWord(int book_id, String keyword, int page_id) {
        if (translateDialog != null
                && translateDialog.getDialog() != null
                && translateDialog.getDialog().isShowing()) {
        } else {
            if (bookPresenter != null) {
                bookPresenter.bookTranslate(book_id, keyword, page_id);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (translateDialog != null) {
            translateDialog = null;
        }
    }
}
