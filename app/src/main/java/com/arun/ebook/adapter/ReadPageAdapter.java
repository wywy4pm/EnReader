package com.arun.ebook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.fragment.ReadFragment;

import java.util.ArrayList;
import java.util.List;

public class ReadPageAdapter extends FragmentPagerAdapter {

    private List<BookDetailBean> data = new ArrayList<>();
    private int totalCount;

    public ReadPageAdapter(FragmentManager fm, List<BookDetailBean> data) {
        super(fm);
        this.data.addAll(data);
    }

    public void updateData(List<BookDetailBean> data, int totalCount) {
        this.totalCount = totalCount;
        if (this.data != null) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (data.get(position) != null) {
            data.get(position).totalPage = totalCount;
            data.get(position).currentPage = position;
        }
        return ReadFragment.newInstance(data.get(position));
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
