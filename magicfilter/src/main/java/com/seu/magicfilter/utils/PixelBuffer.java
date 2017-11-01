/*
 * Copyright (C) 2012 CyberAgent
 * Copyright (C) 2010 jsemler 
 * 
 * Original publication without License
 * http://www.anddev.org/android-2d-3d-graphics-opengl-tutorials-f2/possible-to-do-opengl-off-screen-rendering-in-android-t13232.html#p41662
 */

package com.seu.magicfilter.utils;

import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.seu.magicfilter.jni.GLESNative;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE;
import static javax.microedition.khronos.opengles.GL10.GL_RGBA;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;

public class PixelBuffer {
    final static String TAG = "PixelBuffer";
    final static boolean LIST_CONFIGS = false;

    GLSurfaceView.Renderer mRenderer; // borrow this interface
    int mWidth;
    int mHeight;
    Bitmap mBitmap;

    EGL10 mEGL;
    EGLDisplay mEGLDisplay;
    EGLConfig[] mEGLConfigs;
    EGLConfig mEGLConfig;
    EGLContext mEGLContext;
    EGLSurface mEGLSurface;
    GL10 mGL;

    String mThreadOwner;


    public PixelBuffer(final int width, final int height) {
        mWidth = width;
        mHeight = height;
        this.init();
        mThreadOwner = Thread.currentThread().getName();
    }

    private void init() {
        mEGL = (EGL10) EGLContext.getEGL();
        mEGLDisplay = mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (EGL10.EGL_NO_DISPLAY == mEGLDisplay) {
            throw new RuntimeException("eglGetDisplay,failed:" + GLUtils.getEGLErrorString(mEGL.eglGetError()));
        }

        int versions[] = new int[2];
        if (!mEGL.eglInitialize(mEGLDisplay, versions)) {
            throw new RuntimeException("eglInitialize,failed:" + GLUtils.getEGLErrorString(mEGL.eglGetError()));
        }
        int configsCount[] = new int[1];
        EGLConfig configs[] = new EGLConfig[1];
        int configSpec[] = new int[]{
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE,8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
        };
        mEGL.eglChooseConfig(mEGLDisplay, configSpec, configs, 1, configsCount);
        if (configsCount[0] <= 0) {
            throw new RuntimeException("eglChooseConfig,failed:" + GLUtils.getEGLErrorString(mEGL.eglGetError()));
        }
        mEGLConfig = configs[0];
        int attr[] = new int[]{
                EGL10.EGL_WIDTH, mWidth,
                EGL10.EGL_HEIGHT, mHeight,
//                EGL10.EGL_LARGEST_PBUFFER, EGL14.EGL_TRUE,
                EGL10.EGL_NONE
        };
        mEGLSurface = mEGL.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, attr);
        if (null == mEGLSurface || EGL10.EGL_NO_SURFACE == mEGLSurface) {
            throw new RuntimeException("eglCreateWindowSurface,failed:" + GLUtils.getEGLErrorString(mEGL.eglGetError()));
        }
        int contextSpec[] = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                EGL14.EGL_NONE
        };
        mEGLContext = mEGL.eglCreateContext(mEGLDisplay, mEGLConfig, EGL10.EGL_NO_CONTEXT, contextSpec);
        if (EGL10.EGL_NO_CONTEXT == mEGLContext) {
            throw new RuntimeException("eglCreateContext,failed:" + GLUtils.getEGLErrorString(mEGL.eglGetError()));
        }
        if (!mEGL.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent,failed:" + GLUtils.getEGLErrorString(mEGL.eglGetError()));
        }
    }

    public void setRenderer(final GLSurfaceView.Renderer renderer) {
        mRenderer = renderer;

        // Does this thread own the OpenGL context?
        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e(TAG, "setRenderer: This thread does not own the OpenGL context.");
            return;
        }
        // Call the renderer initialization routines
        mRenderer.onSurfaceCreated(mGL, mEGLConfig);
        mRenderer.onSurfaceChanged(mGL, mWidth, mHeight);
    }

    public Bitmap getBitmap( ) {
        // Do we have a renderer?
        if (mRenderer == null) {
            Log.e(TAG, "getBitmap: Renderer was not set.");
            return null;
        }

        // Does this thread own the OpenGL context?
        if (!Thread.currentThread().getName().equals(mThreadOwner)) {
            Log.e(TAG, "getBitmap: This thread does not own the OpenGL context.");
            return null;
        }

        // Call the renderer draw routine (it seems that some filters do not
        // work if this is only called once)
        mRenderer.onDrawFrame(mGL);
//        mRenderer.onDrawFrame(mGL);
        if( OpenGlUtils.isGLES30Avaliable()) {
            convertToBitmapWithPixelBuffer();
        } else {
            convertToBitmap();
        }
        return mBitmap;
    }

    public void destroy() {
        mRenderer.onDrawFrame(mGL);
        mRenderer.onDrawFrame(mGL);
        mEGL.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);

        mEGL.eglDestroySurface(mEGLDisplay, mEGLSurface);
        mEGL.eglDestroyContext(mEGLDisplay, mEGLContext);
        mEGL.eglTerminate(mEGLDisplay);
    }

    private EGLConfig chooseConfig() {
        int[] attribList = new int[] {
                EGL_DEPTH_SIZE, 0,
                EGL_STENCIL_SIZE, 0,
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL_NONE
        };

        // No error checking performed, minimum required code to elucidate logic
        // Expand on this logic to be more selective in choosing a configuration
        int[] numConfig = new int[1];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, null, 0, numConfig);
        int configSize = numConfig[0];
        mEGLConfigs = new EGLConfig[configSize];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, mEGLConfigs, configSize, numConfig);

        if (LIST_CONFIGS) {
            listConfig();
        }

        return mEGLConfigs[0]; // Best match is probably the first configuration
    }

    private void listConfig() {
        Log.i(TAG, "Config List {");

        for (EGLConfig config : mEGLConfigs) {
            int d, s, r, g, b, a;

            // Expand on this logic to dump other attributes
            d = getConfigAttrib(config, EGL_DEPTH_SIZE);
            s = getConfigAttrib(config, EGL_STENCIL_SIZE);
            r = getConfigAttrib(config, EGL_RED_SIZE);
            g = getConfigAttrib(config, EGL_GREEN_SIZE);
            b = getConfigAttrib(config, EGL_BLUE_SIZE);
            a = getConfigAttrib(config, EGL_ALPHA_SIZE);
            Log.i(TAG, "    <d,s,r,g,b,a> = <" + d + "," + s + "," +
                    r + "," + g + "," + b + "," + a + ">");
        }

        Log.i(TAG, "}");
    }

    private int getConfigAttrib(final EGLConfig config, final int attribute) {
        int[] value = new int[1];
        return mEGL.eglGetConfigAttrib(mEGLDisplay, config,
                attribute, value) ? value[0] : 0;
    }

    private void convertToBitmap() {
        Log.d(PixelBuffer.TAG,String.format("width %d,height %d",mWidth,mHeight));

        IntBuffer ib = IntBuffer.allocate(mWidth * mHeight);
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        GLES20.glReadPixels(0, 0, mWidth, mHeight, GL_RGBA, GL_UNSIGNED_BYTE, ib);
        int[] iat = new int[mWidth * mHeight];
        int[] ia = ib.array();

        for (int i = 0; i < mHeight; i++) {
            for (int j = 0; j < mWidth; j++) {
                iat[(mHeight - i - 1) * mWidth + j] = ia[i * mWidth + j];
            }
        }
        mBitmap.copyPixelsFromBuffer(IntBuffer.wrap(iat));
//        mBitmap.copyPixelsFromBuffer(ib);

    }

    private void convertToBitmapWithPixelBuffer() {

        long start = System.currentTimeMillis() ;
        int []pob = new int[1];
        GLES30.glGenBuffers(1,pob,0);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, pob[0]);
        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, 4*mWidth*mHeight, null, GLES30.GL_DYNAMIC_READ);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER,0);


        GLES30.glPixelStorei(GLES30.GL_PACK_ALIGNMENT,1);
//        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0);
        GLES30.glReadBuffer(GLES30.GL_BACK);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, pob[0]);
        GLESNative.glReadPixels(mWidth,mHeight,GLES30.GL_RGBA,GLES30.GL_UNSIGNED_BYTE,0);
        Log.d(getClass().getSimpleName(),String.format("%d",(int)(System.currentTimeMillis()-start)));
        Buffer buf = GLES30.glMapBufferRange(GLES30.GL_PIXEL_PACK_BUFFER, 0, 4*mWidth*mHeight, GLES30.GL_MAP_READ_BIT);
        ByteBuffer byteBuffer = ((ByteBuffer) buf).order(ByteOrder.nativeOrder());
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER,0);

//        IntBuffer intBuffer = byteBuffer.asIntBuffer() ;
//
//        int[] ia = intBuffer.array();
//        int[] iat = new int[mWidth * mHeight];
//        //Stupid
//        for (int i = 0; i < mHeight; i++) {
//            for (int j = 0; j < mWidth; j++) {
//                iat[(mHeight - i - 1) * mWidth + j] = ia[i * mWidth + j];
//            }
//        }
        //

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(byteBuffer);

    }
}
