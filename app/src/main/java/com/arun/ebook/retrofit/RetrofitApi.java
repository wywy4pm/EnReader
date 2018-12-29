package com.arun.ebook.retrofit;

import com.arun.ebook.bean.AppBean;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookItemBean;
import com.arun.ebook.bean.CommonApiResponse;
import com.arun.ebook.bean.CommonListData;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitApi {

    @GET(RetrofitUrl.USER_REGISTER)
    Observable<CommonApiResponse<AppBean>> userRegister();

    @GET(RetrofitUrl.BOOK_LIST)
    Observable<CommonApiResponse<CommonListData<BookItemBean>>> getBookList(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET(RetrofitUrl.BOOK_DETAIL)
    Observable<CommonApiResponse<CommonListData<BookDetailBean>>> getBookDetail(@Path("bookId") String bookId, @Path("pageSize") int pageSize, @Query("page") int currentPage);
}
