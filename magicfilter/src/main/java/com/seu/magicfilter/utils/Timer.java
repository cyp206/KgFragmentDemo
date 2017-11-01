package com.seu.magicfilter.utils;

import android.util.Log;

/**
 * Created by hujinrong on 17/5/9.
 */

public class Timer {

    long mStart ;
    String mTag ;
    public Timer(String tag) {
        this.mStart = System.currentTimeMillis() ;
        this.mTag = tag ;
    }

    public void logTime(String message) {
        Log.d(getClass().getSimpleName(),String.format("%s-[%s:time is %d]",mTag,message,(System.currentTimeMillis()-mStart)));
        this.mStart = System.currentTimeMillis() ;
    }
}
