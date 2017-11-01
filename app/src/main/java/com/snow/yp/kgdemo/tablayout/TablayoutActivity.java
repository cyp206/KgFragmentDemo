package com.snow.yp.kgdemo.tablayout;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.snow.yp.kgdemo.R;

import java.lang.reflect.Field;

public class TablayoutActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MainTabLayout mainTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentViewPagerAdapter(getSupportFragmentManager()));
        mainTabLayout = (MainTabLayout) findViewById(R.id.main_tab_layout);
        mainTabLayout.mTablatyout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mainTabLayout.initTabLayout(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
        mainTabLayout.initTabLayout(0);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mainTabLayout.initTabIndicatorWidth();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class FragmentViewPagerAdapter extends FragmentStatePagerAdapter {

        private BlankFiFragment fiFragment;
        private BlankSeFragment seFragment;

        public FragmentViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fiFragment = new BlankFiFragment();
            seFragment = new BlankSeFragment();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fiFragment;
                case 1:
                    return seFragment;
            }
            return fiFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    public void setIndicatorShader(TabLayout tabs) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        tabStrip.setAccessible(true);
        Class<?> slidingTabStrip = tabStrip.getDeclaringClass();
        Field mSelectedIndicatorPaint = null;
        try {
            mSelectedIndicatorPaint = slidingTabStrip.getDeclaredField("mSelectedIndicatorPaint");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        mSelectedIndicatorPaint.setAccessible(true);
        Paint paint = null;
        try {
            paint = (Paint) mSelectedIndicatorPaint.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        paint.setColor(Color.BLACK);
    }


}
