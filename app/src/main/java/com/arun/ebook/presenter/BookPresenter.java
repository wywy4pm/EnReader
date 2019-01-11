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

    public BookPresenter() {
        super();
    }

    public void getBookDetail(String bookId, int pageSize, int currentPage) {
        BookModel.getInstance().getBookDetail(
                bookId, pageSize, currentPage, new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.errno == ErrorCode.SUC_NO) {
                            if (data.data instanceof CommonListData) {
                                CommonListData bean = (CommonListData) data.data;
                                if (getMvpView() instanceof BookActivity) {
                                    ((BookActivity) getMvpView()).setTotalCount(bean.total);
                                }
                                if (bean.current_page == 1) {
                                    getMvpView().refresh(bean.data);
                                } else {
                                    getMvpView().refreshMore(bean.data);
                                }
                            }
                        }
                    }
                });
    }

    public void bookEdit(int paragraphId, int type, String content, int styleId) {
        BookModel.getInstance().bookEdit(
                paragraphId, type, content, styleId, new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.errno == ErrorCode.SUC_NO) {
                            getMvpView().refresh(TYPE_BOOK_EDIT, data.data);
                        }
                    }
                });
    }
}
