package com.arun.ebook.presenter;

import com.arun.ebook.bean.BookListData;
import com.arun.ebook.bean.CommonApiResponse;
import com.arun.ebook.bean.CommonListData;
import com.arun.ebook.common.ErrorCode;
import com.arun.ebook.listener.RequestListenerImpl;
import com.arun.ebook.model.MainModel;
import com.arun.ebook.view.CommonView4;

public class MainPresenter extends BasePresenter<CommonView4> {
    public static final int TYPE_REGISTER = 1;

    public MainPresenter() {
        super();
    }

    public void userRegister() {
        MainModel.getInstance().userRegister(
                new RequestListenerImpl(getMvpView(), this) {
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.code == ErrorCode.SUC_NO) {
                            getMvpView().refresh(TYPE_REGISTER, data.data);
                        }
                    }
                });
    }

    public void getBookList(final int page) {
        MainModel.getInstance().getBookList(page,
                new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null) {
                            if (data.code == ErrorCode.SUC_NO) {
                                if (data.data instanceof BookListData) {
                                    BookListData bean = (BookListData) data.data;
                                    if (page == 1) {
                                        getMvpView().refresh(bean.book_list);
                                    } else {
                                        getMvpView().refreshMore(bean.book_list);
                                    }
                                }
                            } else if (data.code == ErrorCode.NO_DATA) {
                                getMvpView().refreshMore(null);
                            }
                        }
                    }
                });
    }
}
