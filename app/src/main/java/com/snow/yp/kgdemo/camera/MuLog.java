package com.snow.yp.kgdemo.camera;

import android.util.Log;

/**
 * Created by y on 2017/7/18.
 */

public class MuLog {
    static long lastTime = 0;

    static final String TAG = "mu_zi";

    public static void i(String msg) {
        Log.d(TAG, msg);
    }

    public static void iTime(String msg) {
        long during = System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();
        Log.d(TAG, msg + ":" + "during:" + during);
    }

}
