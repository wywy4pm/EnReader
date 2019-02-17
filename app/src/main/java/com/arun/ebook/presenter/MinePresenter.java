package com.arun.ebook.presenter;

import com.arun.ebook.bean.CommonApiResponse;
import com.arun.ebook.common.ErrorCode;
import com.arun.ebook.listener.RequestListenerImpl;
import com.arun.ebook.model.MineModel;
import com.arun.ebook.view.CommonView3;

public class MinePresenter extends BasePresenter<CommonView3> {
    public static final int TYPE_MINE_DATA = 1;

    public MinePresenter() {
        super();
    }

    public void getMineData() {
        MineModel.getInstance().getMineData(
                new RequestListenerImpl(getMvpView(), this) {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(CommonApiResponse data) {
                        if (getMvpView() != null && data != null && data.code == ErrorCode.SUC_NO) {
                            getMvpView().refresh(TYPE_MINE_DATA, data.data);
                        }
                    }
                });
    }
}
