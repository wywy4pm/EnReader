package com.arun.ebook.retrofit;


import com.arun.ebook.bean.AppBean;
import com.arun.ebook.common.Constant;
import com.arun.ebook.helper.AppHelper;
import com.arun.ebook.interceptor.LoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInit {

    private static OkHttpClient client;

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.API_BASE_URL)
            .client(getClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static OkHttpClient getClient() {
        if (client == null) {
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    String appVersion = "";
                    String deviceName = "";
                    String platform = "android";
                    String deviceId = "";
                    /*String osVersion = "";
                    String isRelease = "";
                    String uid = "";
                    String apiVersion = "";*/
                    if (getAppBean() != null) {
                        appVersion = getAppBean().version;
                        deviceName= getAppBean().device_name;
                        platform = getAppBean().platform;
                        deviceId = getAppBean().device_id;
                        /*osVersion = getAppBean().osVersion;
                        isRelease = String.valueOf(getAppBean().isRelease);
                        uid = getAppBean().uid;
                        apiVersion = getAppBean().apiVersion;*/
                    }
                    Request originalRequest = chain.request();
                    Request authorised = originalRequest.newBuilder()
                            .addHeader("VERSION", appVersion)
                            .addHeader("DEVICENAME", deviceName)
                            .addHeader("PLATFORM", platform)
                            .addHeader("DEVICEID", deviceId)
                            .build();
                    return chain.proceed(authorised);
                }
            };

            client = new OkHttpClient
                    .Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new LoggingInterceptor())
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    private RetrofitInit() {

    }

    private static RetrofitApi retrofitApi;

    public static RetrofitApi getApi() {
        if (retrofitApi == null) {
            retrofitApi = retrofit.create(RetrofitApi.class);
        }
        return retrofitApi;
    }

    public static <T> T createApi(Class<T> mClass) {
        return retrofit.create(mClass);
    }

    private static AppBean getAppBean() {
        return AppHelper.getInstance().getAppConfig();
    }

}
