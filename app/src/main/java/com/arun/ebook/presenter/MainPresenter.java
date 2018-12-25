package com.arun.ebook.presenter;

import com.arun.ebook.bean.BookListData;
import com.arun.ebook.bean.CommonApiResponse;
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
                        if (getMvpView() != null && data != null && data.errno == ErrorCode.SUC_NO) {
                            getMvpView().refresh(TYPE_REGISTER, data.data);
                        }
                    }
                });
    }

    public void getBookList(int page, int pageSize) {
        MainModel.getInstance().getBookList(page, pageSize,
                new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.errno == ErrorCode.SUC_NO) {
                            if (data.data instanceof BookListData) {
                                BookListData bean = (BookListData) data.data;
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
}
