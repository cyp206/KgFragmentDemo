package com.snow.yp.kgdemo.tablayout;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snow.yp.kgdemo.R;

import java.lang.reflect.Field;

/**
 * Created by y on 2017/7/19.
 */

public class MainTabLayout extends FrameLayout {

    public TabLayout mTablatyout;
    public ImageButton settingsButton;
    private RelativeLayout rlBackground;

    public MainTabLayout(Context context) {
        this(context, null);
    }

    public MainTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.tab_layout_main, this);
        mTablatyout = (TabLayout) findViewById(R.id.tablayout);
        settingsButton = (ImageButton) findViewById(R.id.img_btn_camera_settings);
        rlBackground = (RelativeLayout) findViewById(R.id.rl_backgroud);

    }


    public void initTabLayout(int position) {
        int[] tabResource = {R.drawable.icon_camera_default, R.drawable.icon_store_default};
        int[] tabSelectedResource = {R.drawable.icon_camera_selected, R.drawable.icon_store_selected};
        for (int i = 0; i < mTablatyout.getTabCount(); i++) {
            if (mTablatyout.getTabAt(i).getCustomView() == null) {
                mTablatyout.getTabAt(i).setCustomView(R.layout.tab_custom);
            }
            ((ImageView) mTablatyout.getTabAt(i).getCustomView().findViewById(R.id.iv_tab_icon)).setImageDrawable(getResources().getDrawable(tabResource[i]));
        }
        ((ImageView) mTablatyout.getTabAt(position).getCustomView().findViewById(R.id.iv_tab_icon)).setImageDrawable(getResources().getDrawable(tabSelectedResource[position]));
        if(position==1){
//            rlBackground.setBackground();
            rlBackground.setBackgroundColor(getResources().getColor(R.color.transparent));

        }else {
//            rlBackground.setBackground(null);
            rlBackground.setBackgroundColor(getResources().getColor(R.color.main_store_bg));
        }

    }


    public void initTabIndicatorWidth() {
        int leftDip = 15;
        int rightDip = 15;
        Class<?> tabLayout = mTablatyout.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(mTablatyout);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }


}
