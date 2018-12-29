package com.arun.ebook.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arun.ebook.R;
import com.arun.ebook.adapter.BookDetailAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookDetailItemBean;
import com.arun.ebook.common.Constant;

import java.util.ArrayList;
import java.util.List;

public class ReadFragment extends BaseFragment {
    //private SelectableTextHelper mSelectableTextHelper;
    private RecyclerView recyclerView;
    private BookDetailAdapter bookDetailAdapter;
    private List<BookDetailItemBean> bookDetailList = new ArrayList<>();

    public static ReadFragment newInstance(BookDetailBean bean) {
        ReadFragment readFragment = new ReadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.INTENT_BOOK_DETAIL, bean);
        readFragment.setArguments(bundle);
        return readFragment;
    }

    @Override
    protected int preparedCreate(Bundle savedInstanceState) {
        return R.layout.layout_book_detail;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        bookDetailAdapter = new BookDetailAdapter(getActivity(), bookDetailList);
        recyclerView.setAdapter(bookDetailAdapter);
        /*mSelectableTextHelper = new SelectableTextHelper.Builder(mTvTest)
                .setSelectedColor(getResources().getColor(R.color.text_green))
                .setCursorHandleSizeInDp(20)
                .setCursorHandleColor(getResources().getColor(R.color.red))
                .build();

        mSelectableTextHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {

            }
        });*/
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            if (getArguments().containsKey(Constant.INTENT_BOOK_DETAIL)) {
                BookDetailBean bean = (BookDetailBean) getArguments().get(Constant.INTENT_BOOK_DETAIL);
                BookDetailItemBean<BookDetailBean> itemBean = new BookDetailItemBean<>();
                itemBean.type = BookDetailAdapter.DATA_TYPE_CONTENT;
                itemBean.content = bean;
                bookDetailList.add(itemBean);
            }
        }
    }
}
