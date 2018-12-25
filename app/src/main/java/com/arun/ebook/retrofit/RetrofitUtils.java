/*
package com.arun.ebook.retrofit;

import com.arun.ebook.bean.ConfigResponse;
import com.arun.ebook.bean.CommonResponse;
import com.arun.ebook.bean.book.BookThreeResponse;
import com.arun.ebook.bean.book.NewBookResponse;
import com.arun.ebook.bean.booklist.BookListResponse;
import com.arun.ebook.utils.Utils;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {
    public static final String BASE_URL = "http://fd.link365.cn/";
    public static final String baseUrl = "http://tps.link365.cn/wx/article/";
    private static RetrofitUtils self;
    private Retrofit mRetrofit;
    private Map<String, Subscription> subscriptionMap = new HashMap<>();
    //public static boolean isDebugger = false;

    private RetrofitUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                */
/*.addInterceptor(httpLoggingInterceptor)
                .addInterceptor(mInterceptor)*//*

                .build();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                //JSON转化器
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static RetrofitUtils getInstance() {
        if (self == null) {
            synchronized (RetrofitUtils.class) {
                if (self == null) {
                    self = new RetrofitUtils();
                }
            }
        }
        return self;
    }


    public void getBookList(Subscriber<BookListResponse> subscriber, int page) {
        Subscription subscription = mRetrofit.create(ApiService.class).getBookList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        subscriptionMap.put("getBookList", subscription);
    }

    public void getBookContent(Subscriber<NewBookResponse> subscriber, int bookId, int page) {
        Subscription subscription = mRetrofit.create(ApiService.class).getBookContent(bookId, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        subscriptionMap.put("getBookContent", subscription);
    }

    public void getBookThreeContent(Subscriber<BookThreeResponse> subscriber, int bookId, int page) {
        Subscription subscription = mRetrofit.create(ApiService.class).getBookThreeContent(bookId, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        subscriptionMap.put("getBookThreeContent", subscription);
    }

    public void paraEdit(Subscriber<CommonResponse> subscriber, int op_type, int bookId, String cnseq) {
        Subscription subscription = mRetrofit.create(ApiService.class).paraEdit(op_type, bookId, cnseq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        subscriptionMap.put("paraEdit", subscription);
    }

    public void getConfig(Subscriber<ConfigResponse> subscriber) {
        Subscription subscription = mRetrofit.create(ApiService.class).getConfig(Utils.getDeviceId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        subscriptionMap.put("getConfig", subscription);
    }

    public void unSubscribe(String name) {
        Subscription subscription = subscriptionMap.get(name);
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
*/
