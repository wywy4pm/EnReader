package com.arun.ebook.retrofit;

import com.arun.ebook.bean.AppBean;
import com.arun.ebook.bean.BookListData;
import com.arun.ebook.bean.CommonApiResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApi {

    @GET(RetrofitUrl.USER_REGISTER)
    Observable<CommonApiResponse<AppBean>> userRegister();

    @GET(RetrofitUrl.BOOK_LIST)
    Observable<CommonApiResponse<BookListData>> getBookList(@Query("page") int page, @Query("pageSize") int pageSize);

}
