package com.arun.ebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.bean.BookItemBean;
import com.bumptech.glide.Glide;

import java.util.List;

public class MainListAdapter extends BaseRecyclerAdapter<BookItemBean> {
    public MainListAdapter(Context context, List<BookItemBean> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexts.get()).inflate(R.layout.layout_book_item, parent, false);
        return new MainListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainListHolder) {
            ((MainListHolder) holder).setData(contexts.get(), getItem(position));
        }
    }

    private static class MainListHolder extends RecyclerView.ViewHolder {
        private ImageView book_bg;
        //private ImageView book_cover;
        //private TextView book_name, book_author;

        private MainListHolder(View itemView) {
            super(itemView);
            book_bg = itemView.findViewById(R.id.book_bg);
            /*book_cover = itemView.findViewById(R.id.book_cover);
            book_name = itemView.findViewById(R.id.book_name);
            book_author = itemView.findViewById(R.id.book_author);*/
        }

        private void setData(Context context, BookItemBean item) {
            if (item != null) {
                Glide.with(context).load(item.listImage).into(book_bg);
            }
        }
    }
}
