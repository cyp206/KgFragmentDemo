package com.snow.yp.kgdemo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.seu.magicfilter.utils.MagicParams;
import com.snow.commonlibrary.log.MyLog;

/**
 * Created by y on 2017/7/17.
 */

public class MyApp extends Application {
    public  String TAG =this.getClass().getSimpleName();
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        MyLog.i(TAG+"onCreate");
        init();
    }

    private void init() {

        MagicParams.init(this);
    }


    public static  Context getContext() {
        return mContext;
    }
}
