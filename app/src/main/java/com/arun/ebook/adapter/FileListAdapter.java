package com.arun.ebook.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.NewReadActivity;
import com.arun.ebook.bean.booklist.BookListBean;

import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class FileListAdapter extends BaseAdapter {

    private Context context;
    private List<BookListBean> books;

    /*private OnClickItem onClickItem;

    public interface OnClickItem {
        void clickItem(int pos);
    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }*/

    public FileListAdapter(Context context, List<BookListBean> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_txt_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BookListBean bean = books.get(position);
        if (bean != null) {
            holder.mFileName.setText(bean.name);
            holder.mFileReadTime.setText(!TextUtils.isEmpty(bean.lastReadTime) ? bean.lastReadTime : "");
            holder.fileDetail.setText(bean.desc);
            if (!TextUtils.isEmpty(bean.readProgress)) {
                holder.mFilePage.setText(String.valueOf(bean.readProgress + "%"));
            } else {
                holder.mFilePage.setText("");
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean != null) {
                    NewReadActivity.jumpToRead(context, bean);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private TextView mFileName;
        private TextView mFileReadTime;
        private TextView fileDetail;
        private TextView mFilePage;

        public ViewHolder(View itemView) {
            mFileName = itemView.findViewById(R.id.fileName);
            mFileReadTime = itemView.findViewById(R.id.fileReadTime);
            fileDetail = itemView.findViewById(R.id.fileDetail);
            mFilePage = itemView.findViewById(R.id.filePage);
        }
    }
}
