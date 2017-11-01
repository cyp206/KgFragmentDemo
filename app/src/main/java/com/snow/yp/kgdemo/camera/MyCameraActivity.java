package com.snow.yp.kgdemo.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.snow.yp.kgdemo.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyCameraActivity extends AppCompatActivity {

    Button btnTakPhoto;
    private MyCameraView mCameraView;
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;


    //todo  butterknife 无法使用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        ButterKnife.bind(this);
        btnTakPhoto = (Button) findViewById(R.id.btn_take_photo);

        mCameraView = (MyCameraView) findViewById(R.id.cameraview);
        btnTakPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "take photo", Toast.LENGTH_LONG).show();
                MuLog.iTime("setOnClickListener");
//                mCameraView.takePhotoView(getApplication());


            }
        });
    }

    private void initWhats() {
        rs = RenderScript.create(this);

        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }


//    @OnClick(R.id.btn_take_photo)
//    public void takePhoto() {
//        Log.i("snow","--sfds");
//        Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG).show();
//    }


    //-----------------


//    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        yuvimage.compressToJpeg(new Rect(0, 0, yuvimage.getWidth(), yuvimage.getHeight()), 100, baos);// 80--JPG图片的质量[0-100],100最高
//        byte[] rawImage = baos.toByteArray();
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
////options.inPreferredConfig = Bitmap.Config.RGB_565;   //默认8888
////options.inSampleSize = 8;
//        SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options));//方便回收
//        Bitmap bitmap = (Bitmap) softRef.get();
//    }


    {


        // 在onCreate方法中创建：
        // 在onPreviewFrame方法中调用以下方法：
//        if (yuvType == null)
//        {
//            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(dataLength);
//            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
//
//            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(prevSizeW).setY(prevSizeH);
//            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
//        }
//
//        in.copyFrom(data);
//
//        yuvToRgbIntrinsic.setInput(in);
//        yuvToRgbIntrinsic.forEach(out);
//
//        Bitmap bmpout = Bitmap.createBitmap(prevSizeW, prevSizeH, Bitmap.Config.ARGB_8888);
//        out.copyTo(bmpout);
//    }
    }
}
