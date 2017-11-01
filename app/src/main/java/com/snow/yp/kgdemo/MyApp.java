package com.snow.yp.kgdemo;

import android.app.Application;
import android.content.Context;

import com.seu.magicfilter.utils.MagicParams;

/**
 * Created by y on 2017/7/17.
 */

public class MyApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        init();
    }

    private void init() {

        MagicParams.init(this);
    }


    public Context getContext() {
        return mContext;
    }
}
