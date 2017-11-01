package com.snow.yp.kgdemo.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.seu.magicfilter.camera.CustomSurfaceTexture;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.TextureRotationUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by y on 2017/7/13.
 */

public class MyCameraViewTest extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final String filePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "hehe" + ".jpg";

    private SurfaceHolder holder;
    private android.hardware.Camera mCamera;
    protected int mTextureId = OpenGlUtils.NO_TEXTURE;
    private CustomSurfaceTexture mSurfaceTexture;
    protected GPUImageFilter mFilter;
    protected MagicCameraInputFilter mCameraInputFilter;
    private float[] mSurfaceMatrix = new float[16];
    protected int[] mVertextBufferObject = new int[2];

    protected FloatBuffer mGLCubeBuffer;


    /**
     * 纹理坐标
     */
    protected FloatBuffer mGLTextureBuffer;
    private int mSurfaceWidth;
    private int mSurfaceHeight;


    public MyCameraViewTest(Context context) {
        this(context, null);
    }

    public MyCameraViewTest(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }


    private void init() {
        //***************
        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        //*************
        holder = getHolder();
        getHolder().setFormat(PixelFormat.RGBA_8888);
        holder.addCallback(this);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        //************


    }

    private void cameraCreate(SurfaceHolder holder) {

        mCamera = android.hardware.Camera.open();
        try {
            setCameraParams(mCamera, 0, 0);
            android.hardware.Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。
//        parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
//        setDispaly(parameters,camera);
            mCamera.setParameters(parameters);
//            mCamera.startPreview();
            mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void log(String s) {
        Log.d("mu_zi:", s);
    }

    private void setCameraParams(android.hardware.Camera camera, int width, int height) {
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureSize(4128, 2322);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);

    }


    String TAG = MyCameraViewTest.class.getName();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated: 1");
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);

        //需要在合适的时候初始化Filter.
        if (mFilter != null) {
            mFilter.init();
        }

        if (mCameraInputFilter == null) {
            mCameraInputFilter = new MagicCameraInputFilter();
        }
        mTextureId = OpenGlUtils.getExternalOESTextureID();
        mCameraInputFilter.init();


        //*********


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged:1");
        GLES20.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        onFilterChanged();
        initSurfaceTexture();
        //********

        mCamera = android.hardware.Camera.open();
        try {
            setCameraParams(mCamera, 0, 0);
            android.hardware.Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。
//        parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
//        setDispaly(parameters,camera);
            mCamera.setParameters(parameters);
//            mCamera.startPreview();
            mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

            mCamera.setPreviewTexture(mSurfaceTexture);
            mCameraInputFilter.onInputSizeChanged(1920, 1080);

            mSurfaceTexture.setDefaultBufferSize(1920, 1080);

            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //*********
        if (mFilter != null) {
            mFilter.initOffscreenFrameBuffer();
        }
        //*******


    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onDrawFrame:");

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //**************
        if (mSurfaceTexture == null || mTextureId == OpenGlUtils.NO_TEXTURE) {
            Log.d(TAG, "surface texture is empty.");
            return;
        }
        try {
            mSurfaceTexture.updateTexImage();
            Log.d(TAG, "onDrawFrame: 1");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, "mSurfaceTexture.getTransformMatrix");

        mSurfaceTexture.getTransformMatrix(mSurfaceMatrix);
        mCameraInputFilter.setTextureTransformMatrix(mSurfaceMatrix);
        if (mFilter == null) {
            mCameraInputFilter.onDrawFrame(mTextureId, mVertextBufferObject[0], mVertextBufferObject[1]);
            Log.d(TAG, "mFilter == null");

        } else {
            Log.d(TAG, "mFilter ！= null");

            int textureId = mCameraInputFilter.onDrawToFrameBuffer(this.mTextureId);
            mFilter.onDrawFrame(textureId, mVertextBufferObject[0], mVertextBufferObject[1]);
        }
        Log.d(TAG, "GLES20.glFlush()");

        GLES20.glFlush();
    }


    /**
     * onSurfaceChanged调用
     */
    protected void onFilterChanged() {
        if (mFilter != null) {
            mFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
            mFilter.onInputSizeChanged(mSurfaceWidth, mSurfaceHeight);
        }
    }


    protected void initSurfaceTexture() {
        if (mTextureId != OpenGlUtils.NO_TEXTURE && mSurfaceTexture == null) {
            Log.d(this.getClass().getSimpleName(), "initSurfaceTexture");
            mSurfaceTexture = new CustomSurfaceTexture(mTextureId);
            log("mTextureId:" + mTextureId);

            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        }
    }


    protected SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };
}
