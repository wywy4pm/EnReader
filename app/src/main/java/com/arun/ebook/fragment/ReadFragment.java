package com.arun.ebook.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.arun.ebook.R;
import com.arun.ebook.adapter.BookDetailAdapter;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookDetailItemBean;
import com.arun.ebook.common.Constant;
import com.arun.ebook.event.LongPressEvent;
import com.arun.ebook.widget.ReadRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ReadFragment extends BaseFragment {
    private ReadRecyclerView recyclerView;
    private BookDetailAdapter bookDetailAdapter;
    private List<BookDetailItemBean> bookDetailList = new ArrayList<>();
    private boolean isPressPopShow;

    public static ReadFragment newInstance(BookDetailBean bean) {
        ReadFragment readFragment = new ReadFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.INTENT_BOOK_DETAIL, bean);
        readFragment.setArguments(bundle);
        return readFragment;
    }

    @Override
    protected int preparedCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return R.layout.layout_book_detail;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        bookDetailAdapter = new BookDetailAdapter(getActivity(), bookDetailList);
        recyclerView.setAdapter(bookDetailAdapter);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLongPressPopChange(LongPressEvent longPressEvent) {
        isPressPopShow = !longPressEvent.isHide;
        recyclerView.setLongPressPopShow(isPressPopShow);
    }
}
