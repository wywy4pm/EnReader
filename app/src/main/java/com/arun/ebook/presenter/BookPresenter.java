package com.arun.ebook.presenter;

import com.arun.ebook.activity.BookActivity;
import com.arun.ebook.bean.BookDetailData;
import com.arun.ebook.bean.CommonApiResponse;
import com.arun.ebook.common.ErrorCode;
import com.arun.ebook.listener.RequestListenerImpl;
import com.arun.ebook.model.BookModel;
import com.arun.ebook.view.CommonView4;

public class BookPresenter extends BasePresenter<CommonView4> {
    public static final int TYPE_BOOK_EDIT = 1;
    public static final int TYPE_BOOK_TRANSLATE = 2;
    public static final int TYPE_BOOK_PAGE_IDS = 3;

    public BookPresenter() {
        super();
    }

    public void getBookPageIds(int bookId) {
        BookModel.getInstance().getBookPageIds(
                bookId, new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null) {
                            getMvpView().refresh(TYPE_BOOK_PAGE_IDS, data.data);
                        }
                    }
                });
    }

    public void getBookDetail(int bookId, String page_ids, final boolean isNext) {
        BookModel.getInstance().getBookDetail(
                bookId, page_ids, new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.code == ErrorCode.SUC_NO) {
                            if (data.data instanceof BookDetailData) {
                                BookDetailData bean = (BookDetailData) data.data;
                                if (getMvpView() instanceof BookActivity) {
                                    ((BookActivity) getMvpView()).setTotalCount(bean.total_page);
                                }
                                if (getMvpView() instanceof BookActivity) {
                                    ((BookActivity) getMvpView()).refreshData(bean.page_list, isNext);
                                }
                            }
                        }
                    }
                });
    }

    public void bookEdit(int pageId, int type, String content, int styleId) {
        BookModel.getInstance().bookEdit(
                pageId, type, content, styleId, new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.code == ErrorCode.SUC_NO) {
                            getMvpView().refresh(TYPE_BOOK_EDIT, data.data);
                        }
                    }
                });
    }

    public void bookTranslate(String keyword, int page_id) {
        BookModel.getInstance().bookTranslate(
                keyword, String.valueOf(page_id), new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.code == ErrorCode.SUC_NO) {
                            getMvpView().refresh(TYPE_BOOK_TRANSLATE, data.data);
                        }
                    }
                });
    }
}
