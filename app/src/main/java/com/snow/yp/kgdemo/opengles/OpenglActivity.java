package com.snow.yp.kgdemo.opengles;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

//import com.example.android.opengl.MyGLSurfaceView;
import com.snow.yp.kgdemo.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenglActivity extends AppCompatActivity implements GLSurfaceView.Renderer {


    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        surfaceView = new com.example.android.opengl.MyGLSurfaceView(this);
        surfaceView = new MySurfaceView(this);
        setContentView(surfaceView);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }


}
