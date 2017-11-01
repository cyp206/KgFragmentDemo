package com.seu.magicfilter.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.seu.magicfilter.camera.BaseEngine;
import com.seu.magicfilter.camera.Camera2Engine;
import com.seu.magicfilter.camera.CameraEngine;
import com.seu.magicfilter.camera.ICameraEngine;

/**
 * Created by hujinrong on 17/4/26.
 *
 */

public class MagicCameraManagerView extends MagicBaseCameraView {

    private Camera2Engine mCamera2Engine ;

    public MagicCameraManagerView(Context context) {
        this(context,null);
    }

    public MagicCameraManagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCamera2Engine = new Camera2Engine(getContext());
    }

    @Override
    protected void setUpCamera() {
        initSurfaceTexture();

        final BaseEngine.CameraInfo cameraInfo = mCamera2Engine.getCameraInfo(mCameraId,mSurfaceWidth,mSurfaceHeight);
        if ( cameraInfo.mOrientation == 90 || cameraInfo.mOrientation == 270 ) {
            //imageWidth相当于预览图.
            mImageWidth  =   cameraInfo.mPreviewHeight ;
            mImageHeight = cameraInfo.mPreviewWidth ;
            mPictureWidth = cameraInfo.mPictureHeight ;
            mPictureHeight = cameraInfo.mPictureWidth ;
        } else {
            mImageWidth = cameraInfo.mPreviewWidth ;
            mImageHeight = cameraInfo.mPreviewHeight ;
            mPictureWidth = cameraInfo.mPictureWidth ;
            mPictureHeight = cameraInfo.mPictureHeight ;
        }

        mCameraInputFilter.onInputSizeChanged(mImageWidth,mImageHeight);
        adjustSize(cameraInfo.mOrientation,cameraInfo.mIsFront,true);
        mCamera2Engine.setPreviewDisplay(null,mSurfaceTexture);
        mCamera2Engine.openCamera(mCameraId, new ICameraEngine.CameraOpenListener() {
            @Override
            public void onOpenCamera(boolean success, Exception e) {
                mSurfaceTexture.setDefaultBufferSize(mImageWidth,mImageHeight);
                mCamera2Engine.startPreview();
            }
        });
    }

    @Override
    protected void releaseCamera() {
        mCamera2Engine.releaseCamera();
    }

    @Override
    public void switchCamera() {
        releaseCamera();
        mCameraId = (mCameraId == CameraEngine.CAMERA_FRONT)?CameraEngine.CAMERA_BACK:CameraEngine.CAMERA_FRONT ;
        mCamera2Engine = new Camera2Engine(getContext());
        mCamera2Engine.openCamera(mCameraId);

    }

    @Override
    public void takePhoto(PhotoTakeListener listener) {

    }

    @Override
    public void touchFocus(Rect touchRect, int width, int height) {
        mCamera2Engine.touchFocus(touchRect,width,height);
    }

    @Override
    public void switchTorch() {

    }

    @Override
    public boolean isTorchOpen() {
        return false;
    }
}
