package com.snow.yp.kgdemo.camera;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by y on 2017/7/13.
 */

public class MyCameraViewTest1 extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final String filePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "hehe" + ".jpg";

    private SurfaceHolder holder;
    private android.hardware.Camera mCamera;
    private MagicCameraInputFilter mCameraInputFilter;
    private int mTextureId;
    private float[] mSurfaceMatrix = new float[16];
    protected int[] mVertextBufferObject = new int[2];

    public MyCameraViewTest1(Context context) {
        this(context, null);
    }

    public MyCameraViewTest1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }


    private void init() {
        holder = getHolder();
        holder.addCallback(this);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "onSurfaceCreated: 1");
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void log(String s) {
        Log.d("mu_zi:", s);
    }


    String TAG = MyCameraViewTest1.class.getName();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated: ");
        if (mCameraInputFilter == null) {
            mCameraInputFilter = new MagicCameraInputFilter();
        }
        mTextureId = OpenGlUtils.getExternalOESTextureID();
        mCameraInputFilter.init();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onDrawFrame:");

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mCameraInputFilter.onDrawFrame(mTextureId, mVertextBufferObject[0], mVertextBufferObject[1]);

    }
}
