package com.seu.magicfilter.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import com.seu.magicfilter.camera.BaseEngine;
import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OnBitmapListener;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.Timer;



/**
 * Created by hujinrong on 17/4/18.
 */
public class MagicCameraView extends MagicBaseCameraView {

    public static final String TAG = "MagicCameraView";

    private BaseEngine.CameraInfo mCameraInfo;
    private CameraEngine mCameraEngine;


    public MagicCameraView(Context context) {
        this(context, null);
    }

    public MagicCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCameraEngine = new CameraEngine();
    }

    @Override
    protected void setUpCamera() {
        openCamera(mCameraId);
    }

    private int getActivityRotation() {
        Activity activity = (Activity) getContext();
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    Timer timer;

    /**
     * 拍照
     *
     * @param listener 监听
     */
    public void takePhoto(final PhotoTakeListener listener) {
        if (!mCameraEngine.canTakePhoto()) {
            Log.d(TAG, "Camera Engine is not avaliable.");
            return;
        }
        mCameraEngine.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] bytes, Camera camera) {
                timer = new Timer("TakePhoto");
                mCameraEngine.stopPreview();
                if (mFilter == null) {

                    mCameraEngine.startPreview();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {


                            BitmapFactory.Options options = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                            bitmap = OpenGlUtils.rotaingImageView(mCameraInfo.mOrientation + getActivityRotation(), bitmap);
                            Log.d("Jerome", String.format("Bitmap size=[%d,%d]", bitmap.getWidth(), bitmap.getHeight()));
                            listener.onTakedPhoto(bitmap);
                        }
                    });
                    thread.start();
                } else {
                    mCameraEngine.startPreview();
                    GPUImageFilter filter = mFilter.clone();
                    OpenGlUtils.renderScene(bytes,
                            mCameraInfo.mOrientation + getActivityRotation(),
                            mImageWidth,
                            mImageHeight,
                            filter,
                            new OnBitmapListener() {
                                @Override
                                public void onBitmap(Bitmap bitmap) {
                                    Log.d("Jerome", String.format("Bitmap size=[%d,%d]", bitmap.getWidth(), bitmap.getHeight()));
                                    listener.onTakedPhoto(bitmap);
                                }
                            });
                }
            }
        });
    }


    /**
     * 切换相机
     */
    public void switchCamera() {
        Log.d(TAG, "switchCamera");
        releaseCamera();
        mCameraId = (mCameraId == CameraEngine.CAMERA_FRONT) ? CameraEngine.CAMERA_BACK : CameraEngine.CAMERA_FRONT;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mTextureId = OpenGlUtils.getExternalOESTextureID();
                openCamera(mCameraId);
            }
        });
    }

    public void switchTorch() {
        mCameraEngine.switchTorch();
    }

    @Override
    public boolean isTorchOpen() {
        return mCameraEngine.isTorchOpen();
    }

    /**
     * 切换相机.
     * @param cameraId 相机ID
     */
    private void openCamera(int cameraId) {
        Log.d(TAG, "openCamera.");

        initSurfaceTexture();
        mCameraEngine.setPreviewDisplay(null, mSurfaceTexture);
        mCameraEngine.openCamera(getContext(),cameraId, mSurfaceWidth, mSurfaceHeight);

        final BaseEngine.CameraInfo cameraInfo = mCameraEngine.getCameraInfo(mCameraId, mSurfaceWidth, mSurfaceHeight);
        if (cameraInfo.mOrientation == 90 || cameraInfo.mOrientation == 270) {
            //imageWidth相当于预览图.
            mImageWidth = cameraInfo.mPreviewHeight;
            mImageHeight = cameraInfo.mPreviewWidth;
            mPictureWidth = cameraInfo.mPictureHeight;
            mPictureHeight = cameraInfo.mPictureWidth;
        } else {
            mImageWidth = cameraInfo.mPreviewWidth;
            mImageHeight = cameraInfo.mPreviewHeight;
            mPictureWidth = cameraInfo.mPictureWidth;
            mPictureHeight = cameraInfo.mPictureHeight;
        }
        Log.d("MagicCameraView", String.format("[%d,%d]", mImageWidth, mImageHeight));

        mCameraInputFilter.onInputSizeChanged(mImageWidth, mImageHeight);
        adjustSize(cameraInfo.mOrientation, cameraInfo.mIsFront, true);
        this.mCameraInfo = cameraInfo;
        mSurfaceTexture.setDefaultBufferSize(mImageWidth, mImageHeight);
        mCameraEngine.startPreview();
    }

    /**
     * 释放相机
     */
    public void releaseCamera() {
        Log.d(TAG, "releaseCamera");
        mCameraEngine.releaseCamera();
        mSurfaceTexture = null;
        mTextureId = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onFilterChanged() {
        super.onFilterChanged();
        mCameraInputFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
        if (mFilter != null) {
            mCameraInputFilter.initOffscreenFrameBuffer();
        } else {
            mCameraInputFilter.destroyOffscreenBuffer();
        }
    }

    /**
     * 触摸屏幕自动聚焦
     *
     * @param touchRect 点击的区域
     * @param width     sufaceview 的宽度
     * @param height    sufaceview 的高度
     */
    public void touchFocus(Rect touchRect, int width, int height) {
        mCameraEngine.touchFocus(touchRect, width, height);
    }
}
