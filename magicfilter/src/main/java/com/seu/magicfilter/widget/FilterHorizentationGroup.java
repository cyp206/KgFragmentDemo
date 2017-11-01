package com.seu.magicfilter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.FilterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by hujinrong on 17/5/15.
 *
 */

public class FilterHorizentationGroup extends FrameLayout {

    public interface OnFilterSelectedListener {
        void onFilterSelected(FilterModel filterModel);
    }


    private RecyclerView mRecyclerView ;
    private CustomAdapter mAdapter ;
    private OnFilterSelectedListener mOnFilterSelectedListener;

    public FilterHorizentationGroup(Context context) {
        this(context,null);
    }
    public FilterHorizentationGroup(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public FilterHorizentationGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.widget_filter_group,this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.setUpRecyclerView();
    }

    public void setOnFilterSelectedListener(OnFilterSelectedListener onFilterSelectedListener) {
        this.mOnFilterSelectedListener = onFilterSelectedListener ;
    }
    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new CustomAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 设置FilterModels.
     * @param filterModels
     */
    public void addFilterModels(List<FilterModel> filterModels) {
        mAdapter.addAll(filterModels);
        mAdapter.notifyDataSetChanged();
    }



    public static class FilterModel {
        public FilterFactory.Filter mFilter ;
        public int mBackgroundResource = -1  ;
        public String mTitle;
        public Bitmap mBackgroundBitmap ;
        public boolean mSelected = false ;


        public FilterModel(FilterFactory.Filter filter,int backgroud,String name) {
            this(filter,backgroud,name,false);
        }

        public FilterModel(FilterFactory.Filter filter,int backgroud,String name,boolean selected) {
            this.mFilter = filter ;
            this.mBackgroundResource = backgroud ;
            this.mTitle = name ;
            this.mSelected = selected ;
        }

        public FilterModel(FilterFactory.Filter filter, Bitmap drawable, String name) {
            this.mFilter = filter ;
            this.mBackgroundBitmap = drawable ;
            this.mTitle = name ;
        }
    }

    /**
     *
     **/
    private class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle ;
        private ImageView mImageView ;
        private ImageView mImageViewSelect ;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mImageView = (ImageView) itemView.findViewById(R.id.img_cover);
            mImageViewSelect = (ImageView) itemView.findViewById(R.id.img_select);
        }

        public void bindData(FilterModel filter) {
            mTitle.setText(filter.mTitle);
            Glide.with(itemView.getContext()).load(filter.mBackgroundResource).into(mImageView);
            Glide.with(itemView.getContext()).load(R.mipmap.img_selected).into(mImageViewSelect);
            if( filter.mSelected ) {
                mImageViewSelect.setVisibility(View.VISIBLE);
            } else {
                mImageViewSelect.setVisibility(View.GONE);
            }
        }
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        private Context mCtx ;
        private List<FilterModel> mFilterModels ;
        private FilterModel mFilterModelSelected ;

        public CustomAdapter(Context ctx) {
            this.mCtx = ctx ;
            this.mFilterModels = new ArrayList<>();
        }

        /**
         *
         * @param filterModel
         */
        public void add(FilterModel filterModel) {
            this.mFilterModels.add(filterModel) ;
        }

        /**
         *
         * @param filterModels
         *
         */
        public void addAll(List<FilterModel> filterModels) {
            mFilterModels.clear();
            this.mFilterModels.addAll(filterModels);
            mFilterModelSelected = filterModels.get(0);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.recycler_item_filter,null);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            final FilterModel filterModel = mFilterModels.get(position);
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( mFilterModelSelected != null ) {
                        mFilterModelSelected.mSelected = !mFilterModelSelected.mSelected ;
                    }

                    if( mOnFilterSelectedListener != null ) {
                        mOnFilterSelectedListener.onFilterSelected(filterModel);
                    }

                    mFilterModelSelected = filterModel ;
                    mFilterModelSelected.mSelected = true ;
                    notifyDataSetChanged();
                }
            });
            holder.bindData(mFilterModels.get(position));
        }

        @Override
        public int getItemCount() {
            return mFilterModels.size() ;
        }
    }


}
