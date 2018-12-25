package com.arun.ebook.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arun.ebook.R;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.utils.DeviceUtils;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.utils.ToastUtils;
import com.arun.ebook.view.MvpView;

/**
 * Created by wy on 2017/4/13.
 */

public abstract class BaseFragment extends Fragment implements MvpView {
    protected Activity thisInstance;
    protected View rootView;
    private LayoutInflater inflater;
    private int layoutId;
    private View no_network;
    private boolean isLoading;
    public int screenWidth;
    public int screenHeight;
    public String userId;
    public String deviceId;
    public ProgressBar progressBar;

    private boolean haveMore = true;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            this.inflater = inflater;
            thisInstance = getActivity();
            layoutId = preparedCreate(savedInstanceState);
            int themeId = getTheme();
            if (themeId > 0) {
                final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), themeId);
                inflater = inflater.cloneInContext(contextThemeWrapper);
            }
            rootView = inflater.inflate(layoutId, null);

            initCommon();

            initView();
            initData();
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    private void initCommon() {
        screenWidth = DensityUtil.getScreenWidth(getActivity());
        screenHeight = DensityUtil.getScreenHeight(getActivity());
        deviceId = DeviceUtils.getMobileIMEI(getActivity());
        userId = SharedPreferencesUtils.getUid(getActivity());
        initProgressBar();
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isHaveMore() {
        return haveMore;
    }

    public void setHaveMore(boolean haveMore) {
        this.haveMore = haveMore;
    }

    /*public void showNetWorkErrorView(View view) {
        view.setVisibility(View.GONE);
        no_network = findViewById(R.id.no_network);
        if (no_network != null) {
            no_network.setVisibility(View.VISIBLE);
        }
        if (findViewById(R.id.not_network_btn) != null) {
            findViewById(R.id.not_network_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reloadData();
                }
            });
        }
    }

    public void hideNetWorkErrorView(View view) {
        view.setVisibility(View.VISIBLE);
        no_network = findViewById(R.id.no_network);
        if (no_network != null) {
            no_network.setVisibility(View.GONE);
        }
    }*/

    public void setRecyclerViewScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                        int visibleCount = linearLayoutManager.getChildCount();
                        int totalCount = linearLayoutManager.getItemCount();
                        int limitLoadMore = 0;
                        if (totalCount > 5) {
                            limitLoadMore = totalCount - 5;
                        } else {
                            limitLoadMore = totalCount;
                        }
                        synchronized (BaseFragment.this) {
                            if (firstVisiblePosition + visibleCount >= limitLoadMore) {
                                if (!isLoading && haveMore) {
                                    setLoadMore();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRefreshComplete() {
        setLoading(false);
        hideProgressBar();
    }

    @Override
    public void onError(int errorType, String errorMsg) {

    }

    @Override
    public void onLoadStart() {
        setLoading(true);
        showProgressBar();
    }

    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onError(int errorType, @StringRes int errorMsg) {
        //ShowTopToastHelper.showTopToastView(getActivity(), getString(errorMsg), R.color.red);
    }

    public void showToast(String toastMsg) {
        ToastUtils.getInstance(getActivity()).showToast(toastMsg);
    }

    public void showToast(int toastMsg) {
        ToastUtils.getInstance(getActivity()).showToast(toastMsg);
    }

    public void setLoadMore() {

    }

    public void reloadData() {

    }

    public int getTheme() {
        return -1;
    }

    public <T extends View> T findViewById(int id) {
        return rootView.findViewById(id);
    }

    protected abstract int preparedCreate(Bundle savedInstanceState);

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
