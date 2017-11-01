package com.snow.yp.kgdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snow.commonlibrary.recycleview.BaseAdapter;
import com.snow.yp.kgdemo.R;
import com.snow.yp.kgdemo.adapter.holder.CustomViewHolder;
import com.snow.yp.kgdemo.adapter.holder.JMBean;

/**
 * Created by y on 2017/11/1.
 */

public class MainAdapter extends BaseAdapter<JMBean, CustomViewHolder> {


    public MainAdapter(Context ctx) {
        super(ctx);
        Log.i("heheda","MainAdapter");
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mCtx).inflate(R.layout.main_text_recycle_item, parent,false);
        Log.i("hehe","fsdf");
        return new CustomViewHolder(inflate);
    }

    @Override
    protected void bindViewHolderData(CustomViewHolder viewHolder, JMBean data, int position) {
        viewHolder.tvShow.setText(data.topic);
    }


}
