package com.arun.ebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.WebViewActivity;
import com.arun.ebook.bean.MineMenuItem;
import com.arun.ebook.utils.DensityUtil;

import java.util.List;

public class MineAdapter extends BaseRecyclerAdapter<MineMenuItem> {
    public MineAdapter(Context context, List<MineMenuItem> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(contexts.get()).inflate(R.layout.layout_mine_mune_item, parent, false);
        return new MineHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MineHolder) {
            ((MineHolder) holder).setData(contexts.get(), list.get(position));
        }
    }

    private static class MineHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView menu_name;
        private View line_view;

        private MineHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            menu_name = itemView.findViewById(R.id.menu_name);
            line_view = itemView.findViewById(R.id.line_view);
        }

        private void setData(final Context context, final MineMenuItem mineMenuItem) {
            if (mineMenuItem != null) {
                menu_name.setText(mineMenuItem.title);
                if (itemView.getLayoutParams() instanceof RecyclerView.LayoutParams) {
                    if (mineMenuItem.isGroupFirst) {
                        ((RecyclerView.LayoutParams) itemView.getLayoutParams()).setMargins(0, DensityUtil.dp2px(20), 0, 0);
                    } else {
                        ((RecyclerView.LayoutParams) itemView.getLayoutParams()).setMargins(0, 0, 0, 0);
                    }
                }
                if (mineMenuItem.isHasNext) {
                    line_view.setVisibility(View.VISIBLE);
                } else {
                    line_view.setVisibility(View.INVISIBLE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(mineMenuItem.url)) {
                            WebViewActivity.jumpToWebViewActivity(context, mineMenuItem.url);
                        }
                    }
                });
            }
        }
    }
}
