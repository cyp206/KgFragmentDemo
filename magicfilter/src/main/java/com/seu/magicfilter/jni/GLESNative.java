package com.seu.magicfilter.jni;

/**
 * Created by hujinrong on 17/4/28.
 */

public class GLESNative {
    static {
        System.loadLibrary("magicfilter-lib");
    }
    public static native void glReadPixels(int width,int height,int format,int type,int offset);
}
