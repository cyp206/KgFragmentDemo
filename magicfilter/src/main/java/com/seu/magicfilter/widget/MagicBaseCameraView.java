package com.seu.magicfilter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.camera.CustomSurfaceTexture;
import com.seu.magicfilter.filter.base.MagicCameraInputFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hujinrong on 17/4/26.
 * 拍照CameraView基类.试图兼容Camera以及CameraManager.
 */

public abstract class MagicBaseCameraView extends MagicBaseView {


    public interface PhotoTakeListener {
        void onTakedPhoto(Bitmap bitmap);
    }

    public static final String TAG = "MagicBaseCameraView";
    protected MagicCameraInputFilter mCameraInputFilter;
    protected volatile SurfaceTexture mSurfaceTexture;
    private float[] mSurfaceMatrix = new float[16];

    //输出的图片宽高.
    protected int mPictureWidth;
    protected int mPictureHeight;

    protected int mCameraId = CameraEngine.CAMERA_BACK;

    protected SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener = new SurfaceTexture.OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            requestRender();
        }
    };

    public MagicBaseCameraView(Context context) {
        this(context, null);
    }

    public MagicBaseCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        mScaleType = ScaleType.CENTER_CROP;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        if (mCameraInputFilter == null) {
            mCameraInputFilter = new MagicCameraInputFilter();
        }
        mTextureId = OpenGlUtils.getExternalOESTextureID();
        mCameraInputFilter.init();

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        super.onSurfaceChanged(gl10, width, height);
        setUpCamera();
        if (mFilter != null) {
            mFilter.initOffscreenFrameBuffer();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        releaseCamera();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        super.onDrawFrame(gl10);
        if (mSurfaceTexture == null || mTextureId == OpenGlUtils.NO_TEXTURE) {
            Log.d(TAG, "surface texture is empty.");
            return;
        }
        try {
            mSurfaceTexture.updateTexImage();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        mSurfaceTexture.getTransformMatrix(mSurfaceMatrix);
        mCameraInputFilter.setTextureTransformMatrix(mSurfaceMatrix);
        if (mFilter == null) {
            mCameraInputFilter.onDrawFrame(mTextureId, mVertextBufferObject[0], mVertextBufferObject[1]);
        } else {
            int textureId = mCameraInputFilter.onDrawToFrameBuffer(this.mTextureId);
            mFilter.onDrawFrame(textureId, mVertextBufferObject[0], mVertextBufferObject[1]);
        }
        GLES20.glFlush();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        if (mCameraInputFilter != null) {
            mCameraInputFilter.destroyOffscreenBuffer();
        }
    }

    protected abstract void setUpCamera();

    protected abstract void releaseCamera();

    public abstract void switchCamera();

    public abstract void takePhoto(final PhotoTakeListener listener);

    /**
     * 初始化SurfaceTexture
     */
    protected void initSurfaceTexture() {

        if (mTextureId != OpenGlUtils.NO_TEXTURE && mSurfaceTexture == null) {
            Log.d(this.getClass().getSimpleName(), "initSurfaceTexture");
            mSurfaceTexture = new CustomSurfaceTexture(mTextureId);
            mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);
        }
    }

    /**
     * 触摸屏幕自动聚焦
     *
     * @param touchRect 点击的区域
     * @param width     sufaceview 的宽度
     * @param height    sufaceview 的高度
     */
    public abstract void touchFocus(Rect touchRect, int width, int height);

    public abstract void switchTorch();

    public abstract boolean isTorchOpen();


}
