package com.arun.ebook.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.adapter.MineAdapter;
import com.arun.ebook.bean.MineDataBean;
import com.arun.ebook.bean.MineGroupBean;
import com.arun.ebook.bean.MineMenuItem;
import com.arun.ebook.event.UidEvent;
import com.arun.ebook.helper.AppHelper;
import com.arun.ebook.presenter.MinePresenter;
import com.arun.ebook.utils.AppUtils;
import com.arun.ebook.utils.DeviceUtils;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.view.CommonView3;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends BaseFragment implements CommonView3 {
    private RecyclerView recyclerView;
    private MineAdapter mineAdapter;
    private List<MineMenuItem> list = new ArrayList<>();
    private MinePresenter minePresenter;
    private TextView text_bottom;

    @Override
    protected int preparedCreate(Bundle savedInstanceState) {
        return R.layout.layout_mine;
    }

    @Override
    protected void initView() {
        text_bottom = findViewById(R.id.text_bottom);
        recyclerView = findViewById(R.id.recyclerView);
        mineAdapter = new MineAdapter(getActivity(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mineAdapter);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        minePresenter = new MinePresenter();
        minePresenter.attachView(this);
        minePresenter.getMineData();

        text_bottom.setText(getResources().getString(R.string.bottom_tips, AppUtils.getAppVersion(getActivity()), SharedPreferencesUtils.getUid(getActivity())));
    }

    @Override
    public void refresh(int type, Object data) {
        if (type == MinePresenter.TYPE_MINE_DATA) {
            if (data instanceof MineDataBean) {
                List<MineGroupBean> groupBeans = ((MineDataBean) data).menu_group;
                formatData(groupBeans);
                mineAdapter.notifyDataSetChanged();
            }
        }
    }

    public void formatData(List<MineGroupBean> groupBeans) {
        if (groupBeans != null && groupBeans.size() > 0) {
            for (int i = 0; i < groupBeans.size(); i++) {
                List<MineMenuItem> menuItems = groupBeans.get(i).menu_list;
                if (menuItems != null && menuItems.size() > 0) {
                    for (int j = 0; j < menuItems.size(); j++) {
                        MineMenuItem item = menuItems.get(j);
                        if (item != null) {
                            if (j == 0) {
                                item.isGroupFirst = true;
                            }
                            if (j < menuItems.size() - 1) {
                                item.isHasNext = true;
                            }
                        }
                        list.add(item);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetUid(UidEvent uidEvent) {
        if (uidEvent != null) {
            text_bottom.setText(getResources().getString(R.string.bottom_tips, AppUtils.getAppVersion(getActivity()), uidEvent.uid));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
