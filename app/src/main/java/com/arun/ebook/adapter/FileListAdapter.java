package com.arun.ebook.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.ReadActivity;
import com.arun.ebook.bean.BookBean;

import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class FileListAdapter extends BaseAdapter {

    private Context context;
    private List<BookBean> books;

    public FileListAdapter(Context context, List<BookBean> books) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_txt_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BookBean bean = books.get(position);
        if (bean != null) {
            if (bean.txtFile != null) {
                holder.mFileName.setText(bean.txtFile.getName());
                holder.mFilePath.setText(bean.txtFile.getAbsolutePath());
            }
            holder.mFileReadTime.setText(!TextUtils.isEmpty(bean.lastReadTime) ? bean.lastReadTime : "");
            holder.mFilePage.setText(bean.allPages > 0 && bean.currentPage > 0 ? String.valueOf(bean.currentPage + "/" + bean.allPages) : "");
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean != null && bean.txtFile != null && bean.txtFile.length() > 0) {
                    ReadActivity.jumpToRead(context, bean.txtFile.getPath());
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        private TextView mFileName;
        private TextView mFileReadTime;
        private TextView mFilePath;
        private TextView mFilePage;

        public ViewHolder(View itemView) {
            mFileName = itemView.findViewById(R.id.fileName);
            mFileReadTime = itemView.findViewById(R.id.fileReadTime);
            mFilePath = itemView.findViewById(R.id.filePath);
            mFilePage = itemView.findViewById(R.id.filePage);
        }
    }
}
