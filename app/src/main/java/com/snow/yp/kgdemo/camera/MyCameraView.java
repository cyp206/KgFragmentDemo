package com.snow.yp.kgdemo.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by y on 2017/7/13.
 */

public class MyCameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
//    public static final String filePath = Environment. + File.separator + "hehe" + ".jpg";

    private SurfaceHolder holder;
    private android.hardware.Camera mCamera;

    //-------
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;
    //------

    public MyCameraView(Context context) {
        this(context, null);
    }

    public MyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }


    private void init() {
        holder = getHolder();
        holder.addCallback(this);
//        holder.set
        initWhats();
    }

    private void initWhats() {
        rs = RenderScript.create(getContext());
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = android.hardware.Camera.open(FindFrontCamera());
        try {
            setCameraParams(mCamera, 0, 0);
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.startPreview();

        mCamera.autoFocus(new android.hardware.Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, android.hardware.Camera camera) {
                if (success) {
                    initCamera();
                    log("autoFocus:" + "success");
                    camera.cancelAutoFocus();
                }
            }
        });
    }

    //相机参数的初始化设置
    private void initCamera() {
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。
//        parameters.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
        parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
//        setDispaly(parameters,camera);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        holder = null;
        mCamera = null;
    }

    public void log(String s) {
        Log.d("mu_zi:", s);
    }

    private void setCameraParams(android.hardware.Camera camera, int width, int height) {

        android.hardware.Camera.Parameters parameters = camera.getParameters();

        List<android.hardware.Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        for (android.hardware.Camera.Size size : supportedPictureSizes) {
            log("picturesize:" + "width:" + size.width + "___height" + size.height);
        }
        List<android.hardware.Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (android.hardware.Camera.Size size : supportedPreviewSizes) {
            log("previewesize:" + "width:" + size.width + "___height" + size.height);

        }

        log("previewsize" + parameters.getPreviewSize().width + "_____" + parameters.getPreviewSize().height);
        log("pictureSize" + parameters.getPictureSize().width + "_____" + parameters.getPictureSize().height);
//        parameters.setPictureSize(4128, 2322);
//        camera.setParameters(parameters);
//        camera.setDisplayOrientation(90);

    }

    private Bitmap mBitmap;

    public void takePhotoView(final Context context) {
        mCamera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {


            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                MuLog.iTime("onPictureTaken");

                long s = System.currentTimeMillis();

                BufferedOutputStream bufferedOutputStream = null;
                try {
//                      mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    mBitmap = getBitmap(data.length, 4128, 2322, data);
//                    MuLog.i(mBitmap.getWidth()+"___"+mBitmap.getHeight() );
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90, mBitmap.getWidth() / 2, getHeight() / 2);
                    Bitmap aimBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);


                    File file = new File(new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM + File.separator + "Camera"), System.currentTimeMillis() + ".jpg");
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));

                    aimBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);

                    try {
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
                                file.getName(), null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // 最后通知图库更新
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                MuLog.iTime("onPictureTaken==> end");

            }
        });

    }

    String TAG = MyCameraView.class.getName();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated: ");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    public Bitmap getBitmap(int dataLength, int prevSizeW, int prevSizeH, byte[] data) {
        if (yuvType == null) {
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(dataLength);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(prevSizeW).setY(prevSizeH);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        in.copyFrom(data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        mBitmap = Bitmap.createBitmap(prevSizeW, prevSizeH, Bitmap.Config.ARGB_8888);
        out.copyTo(mBitmap);
        return mBitmap;
    }

    private int FindFrontCamera() {
        int cameraCount = 0;
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

}
