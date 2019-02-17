package com.arun.ebook.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arun.ebook.R;
import com.arun.ebook.adapter.MineAdapter;
import com.arun.ebook.bean.MineDataBean;
import com.arun.ebook.bean.MineGroupBean;
import com.arun.ebook.bean.MineMenuItem;
import com.arun.ebook.presenter.MinePresenter;
import com.arun.ebook.view.CommonView3;

import java.util.ArrayList;
import java.util.List;

public class MineFragment extends BaseFragment implements CommonView3 {
    private RecyclerView recyclerView;
    private MineAdapter mineAdapter;
    private List<MineMenuItem> list = new ArrayList<>();
    private MinePresenter minePresenter;

    @Override
    protected int preparedCreate(Bundle savedInstanceState) {
        return R.layout.layout_mine;
    }

    @Override
    protected void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        mineAdapter = new MineAdapter(getActivity(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mineAdapter);
    }

    @Override
    protected void initData() {
        minePresenter = new MinePresenter();
        minePresenter.attachView(this);
        minePresenter.getMineData();
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
}
