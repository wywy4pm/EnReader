package com.arun.ebook.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.BookActivity;
import com.arun.ebook.activity.FontActivity;
import com.arun.ebook.adapter.BookDetailAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookDetailItemBean;
import com.arun.ebook.bean.BookEditBean;
import com.arun.ebook.bean.FontBean;
import com.arun.ebook.bean.TranslateData;
import com.arun.ebook.common.Constant;
import com.arun.ebook.dialog.StyleBottomDialog;
import com.arun.ebook.dialog.TranslateController;
import com.arun.ebook.event.EditPageEvent;
import com.arun.ebook.event.EnFontEvent;
import com.arun.ebook.event.EnSizeEvent;
import com.arun.ebook.event.LongPressEvent;
import com.arun.ebook.event.TextColorEvent;
import com.arun.ebook.listener.BookEditListener;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.presenter.BookPresenter;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.utils.StringUtils;
import com.arun.ebook.utils.Utils;
import com.arun.ebook.view.CommonView4;
import com.arun.ebook.widget.ReadRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadFragment extends BaseFragment implements CommonView4, BookEditListener, View.OnClickListener, PageViewListener {
    private ReadRecyclerView recyclerView;
    private BookDetailAdapter bookDetailAdapter;
    private List<BookDetailItemBean> bookDetailList = new ArrayList<>();
    private boolean isPressPopShow;
    private BookPresenter bookPresenter;
    public int currentPage;
    //private NewTranslateDialog translateDialog;
    private TranslateController translateController;
    private StyleBottomDialog styleBottomDialog;
    private TextView pageNum, pageFont;
    private int bookId;
    private int textColor;
    private double textScale;
    private FontBean fontBean;

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
        pageFont = findViewById(R.id.pageFont);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        bookDetailAdapter = new BookDetailAdapter(getActivity(), bookDetailList);
        bookDetailAdapter.setBookEditListener(this);
        recyclerView.setAdapter(bookDetailAdapter);

        pageFont.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            if (getArguments().containsKey(Constant.INTENT_BOOK_DETAIL)) {
                BookDetailBean bean = (BookDetailBean) getArguments().get(Constant.INTENT_BOOK_DETAIL);
                if (bean != null) {
                    //content
                    BookDetailItemBean<BookDetailBean> contentBean = new BookDetailItemBean<>();
                    contentBean.type = BookDetailAdapter.DATA_TYPE_CONTENT;
                    File[] files = Utils.readFontsFile(getActivity(), "new_fonts", Constant.PATH_FONT_NEW);
                    if (files != null) {
                        bean.file = files[0];
                    }
                    contentBean.content = bean;
                    bookDetailList.add(contentBean);

                    //translate
                    if (!TextUtils.isEmpty(bean.cn)) {
                        BookDetailItemBean<BookDetailBean> translateBean = new BookDetailItemBean<>();
                        translateBean.type = BookDetailAdapter.DATA_TYPE_TRANSLATE;
                        translateBean.content = bean;
                        bookDetailList.add(translateBean);
                    }

                    currentPage = bean.seq;
                    /*if (bean.file != null) {
                        Typeface typeface = Typeface.createFromFile(bean.file);
                        if (typeface != null) {
                            pageNum.setTypeface(typeface);
                        }
                    }*/
                    pageNum.setText(bean.seq + "/" + bean.totalPage);
                }
            }
            if (getArguments().containsKey(Constant.INTENT_BOOK_ID)) {
                bookId = getArguments().getInt(Constant.INTENT_BOOK_ID);
                if (bookDetailAdapter != null) {
                    bookDetailAdapter.setBook_id(bookId);
                }
            }

            bookPresenter = new BookPresenter();
            bookPresenter.attachView(this);
            setTextColor();
            setEnSize();
            setEnFont();
        }
    }

    private void setTextColor() {
        if (getActivity() instanceof BookActivity && ((BookActivity) getActivity()).textColor != 0) {
            textColor = ((BookActivity) getActivity()).textColor;
            if (textColor != 0) {
                bookDetailAdapter.setTextColor(textColor);
            }
            pageNum.setTextColor(textColor);
            pageFont.setTextColor(textColor);
        }
    }

    private void setEnSize() {
        if (getActivity() instanceof BookActivity && ((BookActivity) getActivity()).textScale > 0) {
            textScale = ((BookActivity) getActivity()).textScale;
            if (textScale > 0) {
                bookDetailAdapter.setEnTextSize(textScale);
            }
        }
    }

    private void setEnFont() {
        if (getActivity() instanceof BookActivity && ((BookActivity) getActivity()).fontBean != null) {
            fontBean = ((BookActivity) getActivity()).fontBean;
            if (fontBean != null) {
                setFont(fontBean);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTextColorChange(TextColorEvent textColorEvent) {
        setTextColor();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnSizeChange(EnSizeEvent enSizeEvent) {
        setEnSize();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnFontChange(EnFontEvent enFontEvent) {
        setEnFont();
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
                translateController = new TranslateController(getActivity());
                translateController.setTranslateData(translateData);
                translateController.showDialog();
                if (getActivity() instanceof BookActivity) {
                    if (((BookActivity) getActivity()).readBg != 0) {
                        translateController.setReadBg(((BookActivity) getActivity()).readBg);
                    }
                    if (((BookActivity) getActivity()).textColor != 0) {
                        translateController.setTextColor(((BookActivity) getActivity()).textColor);
                    }
                }
                /*translateDialog = new NewTranslateDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("TranslateData", translateData);
                translateDialog.setArguments(bundle);
                translateDialog.show(getFragmentManager(), "dialog");
                translateDialog.setListener(new DialogListener() {
                    @Override
                    public void onDismiss() {
                    }
                });*/
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
        if (translateController != null
                && translateController.isShowing()) {
        } else {
            if (bookPresenter != null) {
                bookPresenter.bookTranslate(book_id, keyword, page_id);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (translateController != null) {
            translateController = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pageFont:
                styleBottomDialog = new StyleBottomDialog();
                styleBottomDialog.show(getFragmentManager(), "dialog");
                styleBottomDialog.setListener(this);
                if (getActivity() instanceof BookActivity) {
                    if (((BookActivity) getActivity()).readBg != 0) {
                        styleBottomDialog.setBgColor(((BookActivity) getActivity()).readBg);
                    }
                    if (((BookActivity) getActivity()).textColor != 0) {
                        styleBottomDialog.setTextColor(((BookActivity) getActivity()).textColor);
                    }
                    if (((BookActivity) getActivity()).textScale > 0) {
                        styleBottomDialog.setScale(((BookActivity) getActivity()).textScale);
                    }
                    if (((BookActivity) getActivity()).fontBean != null) {
                        styleBottomDialog.setFont_name(((BookActivity) getActivity()).fontBean.fontName);
                    }
                }
                break;
        }
    }


    @Override
    public void setTextSize(double scale) {
        SharedPreferencesUtils.setConfigString(getActivity(), SharedPreferencesUtils.KEY_READ_EN_SIZE, String.valueOf(scale));
        if (bookDetailAdapter != null) {
            bookDetailAdapter.setEnTextSize(scale);
        }
        if (getActivity() != null && getActivity() instanceof BookActivity) {
            ((BookActivity) getActivity()).setEnSize(scale);
        }
        EventBus.getDefault().post(new EnSizeEvent(scale));
    }

    @Override
    public void setReadBackground(int bgColor) {
        SharedPreferencesUtils.setConfigInt(getActivity(), SharedPreferencesUtils.KEY_READ_BG, bgColor);
        if (translateController != null) {
            translateController.setReadBg(bgColor);
        }
        if (getActivity() != null && getActivity() instanceof BookActivity) {
            ((BookActivity) getActivity()).setReadBg(bgColor);
        }
    }

    @Override
    public void setTextColor(int textColor) {
        SharedPreferencesUtils.setConfigInt(getActivity(), SharedPreferencesUtils.KEY_READ_TEXT_COLOR, textColor);
        if (bookDetailAdapter != null) {
            bookDetailAdapter.setTextColor(textColor);
        }
        if (translateController != null) {
            translateController.setTextColor(textColor);
        }
        if (getActivity() != null && getActivity() instanceof BookActivity) {
            ((BookActivity) getActivity()).setTextColor(textColor);
        }
        EventBus.getDefault().post(new TextColorEvent(textColor));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FontActivity.REQUEST_CODE_FOR_FONT) {
            if (resultCode == FontActivity.RESULT_CODE_TO_READ) {
                if (data != null && data.getExtras() != null && data.getExtras().containsKey("switchFont")) {
                    FontBean bean = (FontBean) data.getExtras().getSerializable("switchFont");
                    if (bean != null) {
                        setFont(bean);
                        String saveFileName = "";
                        if (bean.file == null) {
                            saveFileName = "默认";
                        } else {
                            saveFileName = bean.file.getAbsolutePath();
                        }
                        if (getActivity() != null) {
                            if (getActivity() instanceof BookActivity) {
                                ((BookActivity) getActivity()).setEnFont(bean);
                            }
                            SharedPreferencesUtils.setConfigString(getActivity(), SharedPreferencesUtils.KEY_READ_EN_FONT, saveFileName);
                        }
                        EventBus.getDefault().post(new EnFontEvent());
                    }
                }
            }
        }
    }

    public void setFont(FontBean fontBean) {
        if (fontBean != null) {
            if (bookDetailAdapter != null) {
                bookDetailAdapter.setEnTextFont(fontBean);
            }
            if (styleBottomDialog != null) {
                styleBottomDialog.setFontName(fontBean.fontName);
            }
            //StringUtils.setEnTextFont(pageNum, fontBean.file);
        }
    }

    @Override
    public void setReadProgress(int progress) {
    }

    @Override
    public void setScreenLight(int light) {
    }

    @Override
    public void setLineSpace(int lineSpace) {
    }

    @Override
    public void setEdgeSpace(int edgeSpace) {
    }

    @Override
    public void setParaSpace(int paraSpace) {
    }

    @Override
    public void showNextPage() {
    }

    @Override
    public void showPrePage() {
    }

    @Override
    public void showTransDialog(String word, String seq) {
    }

    @Override
    public void setTextSize(int spSize) {
    }
}
