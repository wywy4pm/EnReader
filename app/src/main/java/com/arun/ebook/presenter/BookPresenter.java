package com.arun.ebook.presenter;

import com.arun.ebook.activity.BookActivity;
import com.arun.ebook.bean.CommonApiResponse;
import com.arun.ebook.bean.CommonListData;
import com.arun.ebook.common.ErrorCode;
import com.arun.ebook.listener.RequestListenerImpl;
import com.arun.ebook.model.BookModel;
import com.arun.ebook.view.CommonView4;

public class BookPresenter extends BasePresenter<CommonView4> {
    public static final int TYPE_BOOK_EDIT = 1;
    public static final int TYPE_BOOK_TRANSLATE = 2;

    public BookPresenter() {
        super();
    }

    public void getBookDetail(int bookId, String page_ids, final boolean isNext) {
        BookModel.getInstance().getBookDetail(
                bookId, page_ids, new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.code == ErrorCode.SUC_NO) {
                            if (data.data instanceof CommonListData) {
                                CommonListData bean = (CommonListData) data.data;
                                if (getMvpView() instanceof BookActivity) {
                                    ((BookActivity) getMvpView()).setTotalCount(bean.total);
                                }
                                if (bean.current_page == 1) {
                                    getMvpView().refresh(bean.data);
                                } else if (getMvpView() instanceof BookActivity) {
                                    ((BookActivity) getMvpView()).refreshMoreData(bean.data, isNext);
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
