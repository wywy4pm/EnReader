package com.arun.ebook.model;

import android.text.TextUtils;

import com.arun.ebook.bean.CommonApiResponse;
import com.arun.ebook.common.ErrorCode;
import com.arun.ebook.exception.ApiException;
import com.arun.ebook.helper.ExceptionHelper;
import com.arun.ebook.listener.CommonRequestListener;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wy on 2017/5/22.
 */

public class BaseModel {

    @SuppressWarnings("unchecked")
    public Observer<CommonApiResponse> request(Observable observable, final CommonRequestListener listener) {
        if (observable != null) {
            Observer<CommonApiResponse> observer = new Observer<CommonApiResponse>() {
                @Override
                public void onError(Throwable e) {
                    if (listener != null) {
                        ApiException apiException = ExceptionHelper.handleException(e);
                        if (apiException != null) {
                            listener.onError(apiException.code, apiException.errorMsg);
                        }
                        listener.onComplete();
                    }
                }

                @Override
                public void onComplete() {
                    if (listener != null) {
                        listener.onComplete();
                    }
                }

                @Override
                public void onSubscribe(Disposable d) {
                    listener.onStart(d);
                }

                @Override
                public void onNext(CommonApiResponse object) {
                    if (listener != null) {
                        if (object != null) {
                            if (object.code == ErrorCode.SUC_NO || object.code == ErrorCode.NO_DATA) {
                                listener.onSuccess(object);
                                if (!TextUtils.isEmpty(object.msg)) {
                                    listener.onError(ErrorCode.COMMON_ERROR, object.msg);
                                }
                            }
                        }
                    }
                }
            };
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
            return observer;
        }
        return null;
    }

}
