package com.arun.ebook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.fragment.ReadFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadPageAdapter extends OpenPagerAdapter<BookDetailBean> {
    private List<BookDetailBean> data = new ArrayList<>();
    private int totalCount;
    private int book_id;

    public ReadPageAdapter(FragmentManager fm, List<BookDetailBean> data, int book_id) {
        super(fm);
        this.book_id = book_id;
        if (this.data != null) {
            this.data.addAll(data);
        }
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
        }
        return ReadFragment.newInstance(data.get(position), book_id);
    }

    @Override
    public int getCount() {
        return data == null || data.size() == 0 ? 0 : data.size();
    }

    @Override
    protected BookDetailBean getItemData(int position) {
        return data == null || data.size() == 0 ? null : data.get(position);
    }

    @Override
    protected boolean dataEquals(BookDetailBean oldData, BookDetailBean newData) {
        return oldData.equals(newData);
    }

    @Override
    public int getDataPosition(BookDetailBean bean) {
        return data.indexOf(bean);
    }

    public ReadFragment getCurrentFragmentItem() {
        return (ReadFragment) getCurrentPrimaryItem();
    }

    public void setNewData(List<BookDetailBean> datas) {
        data.clear();
        data.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(BookDetailBean bean) {
        data.add(bean);
        notifyDataSetChanged();
    }

    public void addData(int position, BookDetailBean bean) {
        data.add(position, bean);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public void moveData(int from, int to) {
        if (from == to) return;
        Collections.swap(data, from, to);
        notifyDataSetChanged();
    }

    public void moveDataToFirst(int from) {
        BookDetailBean tempData = data.remove(from);
        data.add(0, tempData);
        notifyDataSetChanged();
    }

    public void updateByPosition(int position, BookDetailBean bean) {
        if (position >= 0 && data.size() > position) {
            data.set(position, bean);
            ReadFragment targetF = getCachedFragmentByPosition(position);
        }
    }

    public ReadFragment getCachedFragmentByPosition(int position) {
        return (ReadFragment) getFragmentByPosition(position);
    }

}
