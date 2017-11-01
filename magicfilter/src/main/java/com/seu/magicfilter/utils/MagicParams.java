package com.seu.magicfilter.utils;

import android.content.Context;

/**
 * Created by hujinrong on 17/4/17.
 */

public class MagicParams {
    private static Context sContext;

    public static int sBeautyLevel = 5;

    /**
     * 初始化
     * @param ctx Context
     */
    public static void init(Context ctx) {
        sContext = ctx ;
    }

    /**
     * 获取Context.
     * @return Context
     */
    public static Context getContext() {
        return sContext ;
    }
}
