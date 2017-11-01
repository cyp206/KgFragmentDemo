package com.seu.magicfilter.camera;

/**
 * Created by hujinrong on 17/4/25.
 */

public class BaseEngine {


    public static class CameraInfo {
        public int mPreviewWidth;

        public int mPreviewHeight;

        public int mOrientation;

        public boolean mIsFront;

        public int mPictureWidth;

        public int mPictureHeight;

        @Override
        public String toString() {
            return "CameraInfo{" +
                    "mPreviewWidth=" + mPreviewWidth +
                    ", mPreviewHeight=" + mPreviewHeight +
                    ", mOrientation=" + mOrientation +
                    ", mIsFront=" + mIsFront +
                    ", mPictureWidth=" + mPictureWidth +
                    ", mPictureHeight=" + mPictureHeight +
                    '}';
        }
    }

    protected ICameraEngine.CameraOpenListener mCameraOpenListener ;

    /**
     * 设置打开监听器
     * @param cameraOpenListener 设置打开监听器
     */
    public void setCameraOpenListener(ICameraEngine.CameraOpenListener cameraOpenListener) {
        this.mCameraOpenListener = cameraOpenListener ;
    }

    /**
     * 打开成功
     */
    protected void notifyCameraOpened() {
        if( mCameraOpenListener != null ) {
            mCameraOpenListener.onOpenCamera(true,null);
        }
    }

    /**
     * 打开失败
     * @param e 异常信息
     */
    protected void notifyCameraClosed(Exception e) {
        if( mCameraOpenListener != null ) {
            mCameraOpenListener.onOpenCamera(false,e);
        }
    }
}
