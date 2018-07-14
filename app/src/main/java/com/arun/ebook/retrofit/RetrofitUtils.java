package com.arun.ebook.retrofit;

import com.arun.ebook.bean.CommonResponse;
import com.arun.ebook.bean.book.NewBookResponse;
import com.arun.ebook.bean.booklist.BookListResponse;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RetrofitUtils {
    public static final String baseUrl = "http://tps.link365.cn/wx/article/";
    private static RetrofitUtils self;
    private Retrofit mRetrofit;
    private Map<String, Subscription> subscriptionMap = new HashMap<>();
    //public static boolean isDebugger = false;

    private RetrofitUtils() {
        /*Interceptor mInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .build();
                return chain.proceed(request);
            }
        };*/
        /*//添加日志
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (isDebugger) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }*/
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                /*.addInterceptor(httpLoggingInterceptor)
                .addInterceptor(mInterceptor)*/
                .build();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
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

    public void getBookContent(Subscriber<NewBookResponse> subscriber, int booId, int page) {
        Subscription subscription = mRetrofit.create(ApiService.class).getBookContent(booId, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        subscriptionMap.put("getBookContent", subscription);
    }

    public void paraEdit(Subscriber<CommonResponse> subscriber, int op_type, int bookId, String cnseq) {
        Subscription subscription = mRetrofit.create(ApiService.class).paraEdit(op_type,bookId,cnseq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        subscriptionMap.put("paraEdit", subscription);
    }

    public void unSubscribe(String name) {
        Subscription subscription = subscriptionMap.get(name);
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
