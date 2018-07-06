package com.arun.ebook.retrofit;

import com.arun.ebook.bean.CommonResponse;
import com.arun.ebook.bean.book.NewBookResponse;
import com.arun.ebook.bean.booklist.BookListResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiService {

    @GET("booklist/")
    Observable<BookListResponse> getBookList(@Query("page") int page);

    /*@GET("getbook/")
    Observable<NewBookResponse> getBookContent(@Query("bookid") int bookId, @Query("page") int page);*/

    @GET("getbookv2/")
    Observable<NewBookResponse> getBookContent(@Query("book_id") int bookId, @Query("page") int page);

    @GET("param_handle/")
    Observable<CommonResponse> paraEdit(@Query("op_type") int op_type, @Query("bookid") int bookid, @Query("cnseq") String cnseq);
}
