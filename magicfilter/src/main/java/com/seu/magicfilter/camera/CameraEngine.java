package com.seu.magicfilter.camera;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Camera
 * Created by hujinrong on 17/4/17.
 */

public class CameraEngine extends BaseEngine {

    public static final int CAMERA_FRONT = 1;
    public static final int CAMERA_BACK = 0;

    private static final String TAG = "CameraEngine";
    private Camera mCamera;
    private int mCameraId;
    private boolean mIsPreview;

    private SurfaceHolder mSurfaceHolder;
    private SurfaceTexture mSurfaceTexture;
    private Camera.CameraInfo mCameraInfo;
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                mCamera.cancelAutoFocus();
            }
        }
    };

    public boolean isCameraOpend() {
        return mCamera != null;
    }


    public void openCamera(int id) {
        if (mCamera == null) {
            this.mCameraId = id;
            mCamera = Camera.open(id);
            int[] range = new int[2];
            mCamera.getParameters().getPreviewFpsRange(range);

            Log.d(TAG, String.format("[%d,%d]", range[0], range[1]));
            notifyCameraOpened();
        }
    }

    public void openCamera(Context context, int id, int width, int height) {
        if (mCamera == null) {
            this.mCameraId = id;
            mCamera = Camera.open(id);
            if (width != 0 && height != 0) {
                Camera.Parameters parameters = mCamera.getParameters();

                List<Camera.Size> sizes = mCamera.getParameters().getSupportedPictureSizes();
                int screenWidth = context.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
                int screenHeight = context.getApplicationContext().getResources().getDisplayMetrics().heightPixels;
                //由于拍照默认是选择90度 在显示和保存的时候坐旋转 所以参数w h 换位

                Camera.Size targetPreviewSize = getAdapterSize(screenHeight, screenWidth, sizes);
                if (targetPreviewSize != null) {
                    parameters.setPictureSize(targetPreviewSize.width, targetPreviewSize.height);
                    Log.d("Jerome", String.format("targetPreviewSize:width=%d,height=%d", targetPreviewSize.width, targetPreviewSize.height));
                }
                mCamera.setParameters(parameters);
            }
            notifyCameraOpened();
        }
    }

    private Camera.Size getAdapterSize(int width, int height, List<Camera.Size> sizes) {
        if (sizes == null) return null;
        Camera.Size targetSize = null;
        float WHRetio = width * 1f / (height * 1f);
        for (Camera.Size size : sizes) {
            if (size != null) {
                if (size.width * 1f / (size.height * 1f) != WHRetio) continue;
                if (targetSize == null || targetSize.width < size.width) {
                    targetSize = size;
                }
            }
        }

        return targetSize;
    }

    private Camera.Size findPreviewSize(List<Camera.Size> sizes, int width) {
        int min = Integer.MAX_VALUE;
        Camera.Size sizeMin = null;
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            int gap = Math.abs(size.width - width);
            if (gap < min) {
                min = gap;
                sizeMin = size;
            }
        }
        return sizeMin;
    }

    private Camera.Size findPictureSize(List<Camera.Size> sizes, int width) {
        int min = Integer.MAX_VALUE;
        Camera.Size sizeMin = null;
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            int gap = Math.abs(size.width - width);
            if (gap < min) {
                min = gap;
                sizeMin = size;
            }
        }
        return sizeMin;
    }

    public void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    public void setPreviewDisplay(SurfaceHolder surfaceHolder, SurfaceTexture surfaceTexture) {
        this.mSurfaceHolder = surfaceHolder;
        this.mSurfaceTexture = surfaceTexture;
    }

    public void startPreview() {
        try {
            if (mSurfaceTexture != null) {
                mCamera.setPreviewTexture(mSurfaceTexture);
            } else {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mCamera.startPreview();
        mIsPreview = true;
    }

    public void stopPreview() {
        mCamera.stopPreview();
        mIsPreview = false;
    }

    public boolean canTakePhoto() {
        return mIsPreview;
    }

    public void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback, Camera.PictureCallback jpgCallback) {
        mCamera.takePicture(shutterCallback, rawCallback, jpgCallback);
    }

    public BaseEngine.CameraInfo getCameraInfo(int cameraId, int surfaceWidth, int surfaceHeight) {
        boolean needRelease = false;
        if (mCamera == null) {
            mCamera = Camera.open(cameraId);
            needRelease = true;
        }
        mCameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, mCameraInfo);

        BaseEngine.CameraInfo cameraInfoRet = new BaseEngine.CameraInfo();
        cameraInfoRet.mOrientation = mCameraInfo.orientation;

        Camera.Size previewSize = getPreviewSize();
        Camera.Size pictureSize = getPictureSize();
        cameraInfoRet.mPreviewWidth = previewSize.width;
        cameraInfoRet.mPreviewHeight = previewSize.height;

        cameraInfoRet.mPictureWidth = pictureSize.width;
        cameraInfoRet.mPictureHeight = pictureSize.height;

        cameraInfoRet.mIsFront = mCameraId == 1 ? true : false;

        Log.d(TAG, cameraInfoRet.toString());
        if (needRelease) {
            mCamera.release();
        }
        return cameraInfoRet;
    }

    public void touchFocus(Rect touchRect, int width, int height) {

        int orientation = mCameraInfo.orientation;

        Rect tfocusRect = calculateTapArea(touchRect, orientation, width, height);
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> modes = parameters.getSupportedFocusModes();

        if (!modes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            //不支持自动对焦
            return;
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        List<Camera.Area> focusAreas = new ArrayList<>();
        Camera.Area area = new Camera.Area(tfocusRect, 1000);
        focusAreas.add(area);

        if (parameters.getMaxNumFocusAreas() > 0) {
            parameters.setFocusAreas(focusAreas);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            parameters.setMeteringAreas(focusAreas);
        }

        try {
            mCamera.setParameters(parameters);
            mCamera.autoFocus(mAutoFocusCallback);
        } catch (Exception e) {
            Log.e(TAG, "autofocus failed, " + e.getMessage());
        }

    }

    /**
     * 把触摸的区域转换为识别的区域, 坐标系转换 x(-1000, 1000), y(-1000, 1000)
     *
     * @param tapRect     点击的区域
     * @param orientation camera 方向
     * @param width       sufaceview 宽度
     * @param height      sufaceview 高度
     * @return 转换后识别区域
     */
    private Rect calculateTapArea(Rect tapRect, int orientation, int width, int height) {
        int x = tapRect.centerX();
        int y = tapRect.centerY();

        int tempX = (int) (x * 1.0f / width * 2000 - 1000);
        int tempY = (int) (y * 1.0f / height * 2000 - 1000);

        int centerX = 0;
        int centerY = 0;
        if (90 == orientation) {
            centerX = tempY;
            centerY = (2000 - (tempX + 1000) - 1000);
        } else if (270 == orientation) {
            centerX = (2000 - (tempY + 1000)) - 1000;
            centerY = tempX;
        }

        int left = clamp(centerX - tapRect.width() / 2, -1000, 1000);
        int right = clamp(left + tapRect.width(), -1000, 1000);
        int top = clamp(centerY - tapRect.height() / 2, -1000, 1000);
        int bottom = clamp(top + tapRect.height(), -1000, 1000);

        return new Rect(left, top, right, bottom);
    }


    /**
     * 限定x 不超过 min max
     *
     * @param x   需要限定的值
     * @param min 最小值
     * @param max 最大值
     * @return 返回的值
     */
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }


    private Camera.Size getPreviewSize() {
        return mCamera.getParameters().getPreviewSize();
    }

    private Camera.Size getPictureSize() {
        return mCamera.getParameters().getPictureSize();
    }


    public void switchTorch() {
        if (mCameraId == CAMERA_FRONT) {
            return;
        }
        Camera.Parameters params = mCamera.getParameters();
        if (isTorchOpen()) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        } else {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
        mCamera.setParameters(params);
    }

    public boolean isTorchOpen() {
        if (mCameraId == CAMERA_FRONT) {
            return false;
        }
        Camera.Parameters params = mCamera.getParameters();
        String flashModel = params.getFlashMode();

        if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashModel)) {
            return true;
        }

        return false;
    }
}
