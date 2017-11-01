package com.seu.magicfilter.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 *
 * Created by hujinrong on 17/4/25.
 * 参考：https://github.com/googlesamples/android-Camera2Basic
 *
 */

@SuppressWarnings("NewApi")
public class Camera2Engine extends BaseEngine implements ICameraEngine {


    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;


    private Context mCtx ;
    private CameraManager mCameraManager ;
    private Surface mSurface ;
    private HandlerThread mBackgroundThread;
    private Handler mMainHandler ;
    private Handler mChildHandler ;
    private String mCameraId ;
    private CameraCharacteristics mCameraCharacteristics ;
    private CaptureRequest mPreviewRequest;
    private Rect mActiveArraySize = new Rect(0, 0, 1, 1);
    //设备信息
    private CameraDevice mCameraDevice ;
    private CameraCaptureSession mCameraCaptureSession ;
    //可用的CameraId.
    private String[] mAvaliableCameras = new String[0] ;

    private CameraDevice.StateCallback mOpenCameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice ;
            notifyCameraOpened();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraDevice = null ;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {
            notifyCameraClosed(new RuntimeException(String.format("OpenCamera Error=%d",i)));
            cameraDevice.close();
            mCameraDevice = null ;
        }
    };

    private CameraCaptureSession.CaptureCallback mAfCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
            if (null == afState) {
                return;
            }

            if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                    CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                startNormalPreview();
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
    };

    private CameraInfo mCameraInfo;
    private CaptureRequest.Builder mPreviewRequestBuilder;

    public Camera2Engine(Context ctx) {
        this.mCtx = ctx.getApplicationContext() ;
        this.mCameraManager = (CameraManager) this.mCtx.getSystemService(Context.CAMERA_SERVICE);
        this.mMainHandler = new Handler(Looper.getMainLooper());
        try {
            this.mAvaliableCameras = this.mCameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }



    @Override
    public boolean isCameraOpend() {
        return false;
    }

    @Override
    public void openCamera(int id) {
        openCamera(id);
    }

    @Override
    public void openCamera(int id, int width, int height) {

    }

    @Override
    public void openCamera(int id,CameraOpenListener listener) {

        setCameraOpenListener(listener);
        //准备线程
        startBackgroundThread();

        this.mCameraId = convertCameraId(id);

        try {
            mCameraManager.openCamera(mCameraId,mOpenCameraStateCallback,mMainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }


    /**
     * 转换CameraId
     * @param id CameraId.
     * @return CameraId
     */
    private String convertCameraId(int id) {
        if( id == 0 && mAvaliableCameras.length > 0 ) {
            return mAvaliableCameras[0];
        }
        if( id == 1 && mAvaliableCameras.length > 1 ){
            return mAvaliableCameras[1];
        }
        return null ;
    }

    /**
     * 开启后台线程
     */
    private void startBackgroundThread() {
        if( mBackgroundThread == null ) {
            mBackgroundThread = new HandlerThread("ChildThread");
            mBackgroundThread.start();
            this.mChildHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    /**
     * 关闭后台线程
     */
    private void stopBackgroundThread() {
        if( mBackgroundThread != null ) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mChildHandler = null;
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void releaseCamera() {
        stopBackgroundThread();

        //释放相机资源
        if( null != mCameraCaptureSession ) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null ;
        }

        if( null != mCameraDevice ) {
            mCameraDevice.close();
            mCameraDevice = null ;
        }
    }

    @Override
    public void setPreviewDisplay(SurfaceHolder surfaceHolder, SurfaceTexture surfaceTexture) {
        if( surfaceHolder != null ) {
            mSurface = surfaceHolder.getSurface();
        }

        if( surfaceTexture != null ) {
            mSurface = new Surface(surfaceTexture);
        }


    }

    @Override
    public void startPreview() {
        try {

            // 创建预览需要的CaptureRequest.Builder
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // SurfaceTexture surfaceTexture = null ;
            mPreviewRequestBuilder.addTarget(mSurface);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(mSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) {
                        return;
                    }
                    mCameraCaptureSession = cameraCaptureSession ;
                    try {
                        // 自动对焦
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 打开闪光灯
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 显示预览
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        cameraCaptureSession.setRepeatingRequest(mPreviewRequest, null, mChildHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                        mCameraCaptureSession = null ;
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    mCameraCaptureSession = null ;
                }
            }, mChildHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPreview() {
        try {
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.close();
            mCameraCaptureSession = null ;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean canTakePhoto() {
        return false;
    }

    @Override
    public void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback, Camera.PictureCallback jpgCallback) {

    }

    @Override
    public BaseEngine.CameraInfo getCameraInfo(int cameraId,int surfaceWidth,int surfaceHeight) {
        String mappingCameraId = convertCameraId(cameraId);
        if( mCameraCharacteristics == null || !mappingCameraId.equals(this.mCameraId) ) {
            //获取相机信息
            try {
                mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mappingCameraId);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                return null ;
            }
        }
        mActiveArraySize = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);


        StreamConfigurationMap configurationMap = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (configurationMap == null) {
            return null;
        }
        //获取图片输出的尺寸
        Size[] size = configurationMap.getOutputSizes(ImageFormat.JPEG);
        printSizeArray(size,"JPEG size");

        Size[] sizePreview = configurationMap.getOutputSizes(SurfaceTexture.class);
        printSizeArray(sizePreview,"SurfaceTexture size");
        mCameraInfo = new CameraInfo();
        mCameraInfo.mPictureWidth = size[0].getWidth() ;
        mCameraInfo.mPictureHeight = size[0].getHeight() ;

        // For still image captures, we use the largest available size.
        Size largest = Collections.max(
                Arrays.asList(size),
                new CompareSizesByArea());

        Size chooseOptimalSize =  chooseOptimalSize(sizePreview,surfaceWidth,surfaceHeight,MAX_PREVIEW_WIDTH,MAX_PREVIEW_HEIGHT,largest);
        mCameraInfo.mPreviewWidth = chooseOptimalSize.getWidth() ;
        mCameraInfo.mPreviewHeight = chooseOptimalSize.getHeight() ;

        mCameraInfo.mOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        mCameraInfo.mIsFront = mCameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT ? true:false ;
        Log.d("CameraEngine", mCameraInfo.toString());
        return mCameraInfo;
    }

    @Override
    public void touchFocus(Rect touchRect, int width, int height) {
        if (null == mCameraDevice || null == mCameraCaptureSession || null == mPreviewRequest || null == mCameraInfo) {
            return;
        }

        double x = touchRect.centerX();
        double y = touchRect.centerY();
        double tmp;
        int realPreviewWidth = mCameraInfo.mPreviewWidth;
        int realPreviewHeight = mCameraInfo.mPreviewHeight;
        if (90 == mCameraInfo.mOrientation || 270 == mCameraInfo.mOrientation) {
            realPreviewWidth = mCameraInfo.mPreviewHeight;
            realPreviewHeight = mCameraInfo.mPreviewWidth;
        }

        double imgScale = 1.0;
        double verticalOffset = 0;
        double horizontalOffset = 0;
        if (realPreviewHeight * width > realPreviewWidth * height) {
            imgScale = width * 1.0 / realPreviewWidth;
            verticalOffset = (realPreviewHeight - height / imgScale) / 2;
        } else {
            imgScale = height*1.0/realPreviewHeight;
            horizontalOffset = (realPreviewWidth - width / imgScale) / 2;
        }

        x = x / imgScale + horizontalOffset;
        y = y / imgScale + verticalOffset;

        if (90 == mCameraInfo.mOrientation) {
            tmp = x;
            x = y;
            y = mCameraInfo.mPreviewHeight - tmp;
        } else if (270 == mCameraInfo.mOrientation) {
            tmp = x;
            x = mCameraInfo.mPreviewWidth - y;
            y = tmp;
        }

        Rect cropRegion = mPreviewRequest.get(CaptureRequest.SCALER_CROP_REGION);
        if (null == cropRegion) {
            cropRegion = mActiveArraySize;
        }

        int cropWidth = cropRegion.width();
        int cropHeight = cropRegion.height();
        if (mCameraInfo.mPreviewHeight * cropWidth > mCameraInfo.mPreviewWidth * cropHeight) {
            imgScale = cropHeight * 1.0 / mCameraInfo.mPreviewHeight;
            verticalOffset = 0;
            horizontalOffset = (cropWidth - imgScale * mCameraInfo.mPreviewWidth) / 2;
        } else {
            imgScale = cropWidth * 1.0 / mCameraInfo.mPreviewWidth;
            horizontalOffset = 0;
            verticalOffset = (cropHeight - imgScale * mCameraInfo.mPreviewHeight) / 2;
        }

        // 将点击区域相对于图像的坐标，转化为相对于成像区域的坐标
        x = x * imgScale + horizontalOffset + cropRegion.left;
        y = y * imgScale + verticalOffset + cropRegion.top;

        double tapAreaRatio = 0.1;
        Rect rect = new Rect();
        rect.left = clamp((int) (x - tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
        rect.right = clamp((int) (x + tapAreaRatio / 2 * cropRegion.width()), 0, cropRegion.width());
        rect.top = clamp((int) (y - tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());
        rect.bottom = clamp((int) (y + tapAreaRatio / 2 * cropRegion.height()), 0, cropRegion.height());

        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, new MeteringRectangle[] {new MeteringRectangle(rect, 1000)});
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, new MeteringRectangle[] {new MeteringRectangle(rect, 1000)});
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);

        mPreviewRequest = mPreviewRequestBuilder.build();
        try {
            mCameraCaptureSession.setRepeatingRequest(mPreviewRequest, mAfCaptureCallback, mChildHandler);
        } catch (CameraAccessException e) {
            Log.e("Camera2Engine", "setRepeatingRequest failed, " + e.getMessage());
        }
    }

    /**
     * 手动对焦结束后,回复原始自动对焦状态
     */
    private void startNormalPreview() {
        // Auto focus should be continuous for camera preview.
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);

        mPreviewRequest = mPreviewRequestBuilder.build();
        try {
            mCameraCaptureSession.setRepeatingRequest(mPreviewRequest, null, mChildHandler);
        } catch (CameraAccessException e) {
            Log.e("Camera2Engine", "setRepeatingRequest failed, " + e.getMessage());
        }
    }

    /**
     * 限定值
     * @param x 要限定的值
     * @param min 最小值
     * @param max 最大值
     * @return 返回的结果
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

    /**
     * 打印Sizes.
     * @param sizes 打印Sizes.
     * @param message 错误信息
     */
    private void printSizeArray(Size[] sizes,String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("--------%s begin-----------\n",message));
        for(int i = 0 ; i < sizes.length ;i++ ) {
            Size size = sizes[i];
            stringBuilder.append(String.format("[%d,%d]",size.getWidth(),size.getHeight()));
        }
        stringBuilder.append(String.format("--------%s end-----------\n",message));
        Log.d("Camera2Engine",stringBuilder.toString());

    }

    /**
     * 返回最合适的预览尺寸
     *
     * @param choices           相机希望输出类支持的尺寸list
     * @param textureViewWidth  texture view 宽度
     * @param textureViewHeight texture view 高度
     * @param maxWidth          能够选择的最大宽度
     * @param maxHeight         能够选择的醉倒高度
     * @param aspectRatio       图像的比例(pictureSize, 只有当pictureSize和textureSize保持一致, 才不会失真)
     * @return 最合适的预览尺寸
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e("Camera2Engine", "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }
}
