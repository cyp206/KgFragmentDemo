package com.snow.yp.kgdemo.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by y on 2017/7/17.
 */

public class CameraUtils {

    public static final String filePath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "hehe" + ".jpg";

    public static void takePhotoView(final Context context, android.hardware.Camera mCamera) {
        mCamera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                BufferedOutputStream bufferedOutputStream = null;
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                    Bitmap aimBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    File file = new File(filePath);
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
            }
        });

    }
}
