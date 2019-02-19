package com.arun.ebook.retrofit;

import com.arun.ebook.bean.AppBean;
import com.arun.ebook.bean.BookDetailData;
import com.arun.ebook.bean.BookPageIdsData;
import com.arun.ebook.bean.BookListData;
import com.arun.ebook.bean.CommonApiResponse;
import com.arun.ebook.bean.MineDataBean;
import com.arun.ebook.bean.TranslateData;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitApi {

    @GET(RetrofitUrl.USER_REGISTER)
    Observable<CommonApiResponse<AppBean>> userRegister();

    @GET(RetrofitUrl.BOOK_LIST)
    Observable<CommonApiResponse<BookListData>> getBookList(@Query("page") int page);

    @GET(RetrofitUrl.BOOK_PAGE_IDS)
    Observable<CommonApiResponse<BookPageIdsData>> getBookPageIds(@Query("book_id") int book_id);

    @GET(RetrofitUrl.BOOK_DETAIL)
    Observable<CommonApiResponse<BookDetailData>> getBookDetail(@Query("book_id") int bookId, @Query("page_ids") String page_ids);

    @GET(RetrofitUrl.BOOK_EDIT)
    Observable<CommonApiResponse<List<String>>> bookEdit(@Path("pageId") int pageId, @Path("type") int type, @Query("content") String content, @Query("styleId") int styleId);

    @GET(RetrofitUrl.BOOK_TRANSLATE)
    Observable<CommonApiResponse<TranslateData>> bookTranslate(@Query("keyword") String keyword, @Query("page_id") String page_id);

    @GET(RetrofitUrl.USER_CENTER)
    Observable<CommonApiResponse<MineDataBean>> getMineData();

}
