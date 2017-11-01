package com.seu.magicfilter.camera;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * Created by hujinrong on 17/4/17.
 */

public interface ICameraEngine {

    /**
     * Created by hujinrong on 17/4/18.
     */

    interface CameraOpenListener {
        /**
         * 监听Camera状态
         * @param success 是否成功
         * @param e 异常信息
         */
        void onOpenCamera(boolean success,Exception e);
    }
    /**
     * 相机是否打开
     * @return true当前有相机处于打开状态.
     */
    boolean isCameraOpend();

    /**
     * 打开相机
     * @param id
     */
    void openCamera(int id);

    /**
     * 打开相机
     * @param id
     * @param width width
     * @param height height
     *
     */
    void openCamera(int id,int width,int height);

    /**
     * 打开相机
     * @param id 相机ID，代表前后
     * @param cameraOpenListener 改变监听器
     */
    void openCamera(int id,CameraOpenListener cameraOpenListener);

    /**
     * 释放相机资源
     */
    void releaseCamera();

    /**
     * 设置预览
     * @param surfaceHolder 关联SurfaceView
     * @param surfaceTexture 关联GLSurfaceView
     */
    void setPreviewDisplay(SurfaceHolder surfaceHolder, SurfaceTexture surfaceTexture);

    /**
     * 开启预览
     */
    void startPreview();

    /**
     * 关闭预览
     */
    void stopPreview() ;

    /**
     * 是否可以拍照。依据为当前是否进行preview.
     * @return true 可以takephoto,false 不可以takephoto
     */
    boolean canTakePhoto();
    /**
     * 拍照
     * @param shutterCallback 快门按下callback
     * @param rawCallback 原始数据callback
     * @param jpgCallback jpg数据callback
     */
    void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback, Camera.PictureCallback jpgCallback);

    /**
     * 获取相机信息
     * @param cameraId 相机
     * @param previewWidth 宽度
     * @param previewHeight 长度
     * @return 相机信息
     */
    BaseEngine.CameraInfo getCameraInfo(int cameraId,int previewWidth,int previewHeight);

    /**
     * 触摸屏幕自动聚焦
     * @param touchRect 点击的区域
     * @param width sufaceview 的宽度
     * @param height sufaceview 的高度
     */
    void touchFocus(Rect touchRect, int width, int height);
}
