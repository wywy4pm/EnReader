package com.arun.ebook.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.arun.ebook.R;
import com.arun.ebook.adapter.MainListAdapter;
import com.arun.ebook.bean.BookItemBean;
import com.arun.ebook.event.UidEvent;
import com.arun.ebook.helper.AppHelper;
import com.arun.ebook.presenter.MainPresenter;
import com.arun.ebook.view.CommonView4;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment implements CommonView4<List<BookItemBean>> {
    private RecyclerView recyclerView;
    private MainListAdapter mainListAdapter;
    private MainPresenter mainPresenter;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 15;
    private List<BookItemBean> bookList = new ArrayList<>();
    private String uid;

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        return mainFragment;
    }

    @Override
    protected int preparedCreate(Bundle savedInstanceState) {
        return R.layout.layout_main;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mainListAdapter = new MainListAdapter(getActivity(), bookList);
        recyclerView.setAdapter(mainListAdapter);
        setRecyclerViewScrollListener(recyclerView);
    }

    @Override
    protected void initData() {
        mainPresenter = new MainPresenter();
        mainPresenter.attachView(this);
    }

    private void getData() {
        currentPage = 1;
        request();
    }

    private void getMoreData() {
        currentPage++;
        request();
    }

    private void request() {
        if (mainPresenter != null) {
            mainPresenter.getBookList(currentPage);
        }
    }

    @Override
    public void setLoadMore() {
        Log.d("TAG", "get more book list");
        getMoreData();
    }

    @Override
    public void refresh(List<BookItemBean> data) {
        if (data != null && data.size() > 0) {
            setHaveMore(true);
            bookList.clear();
            bookList.addAll(data);
            mainListAdapter.notifyDataSetChanged();
        } else {
            setHaveMore(false);
        }
    }

    @Override
    public void refreshMore(List<BookItemBean> data) {
        if (data != null && data.size() > 0) {
            setHaveMore(true);
            bookList.addAll(data);
            mainListAdapter.notifyDataSetChanged();
        } else {
            setHaveMore(false);
        }
    }

    @Override
    public void refresh(int type, Object data) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetUid(UidEvent uidEvent) {
        if (uidEvent != null) {
            uid = uidEvent.uid;
            AppHelper.getInstance().getAppConfig().setUid(uid);
            getData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
