package com.snow.yp.kgdemo.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.snow.yp.kgdemo.R;

/**
 * Created by y on 2017/11/1.
 */

public class CustomViewHolder extends RecyclerView.ViewHolder {

    public TextView tvShow;

    public CustomViewHolder(View itemView) {
        super(itemView);
        tvShow = (TextView) itemView.findViewById(R.id.item_tv);
    }
}