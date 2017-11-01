package com.seu.magicfilter.utils;

import android.graphics.Bitmap;
import android.media.ImageReader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.seu.magicfilter.filter.base.MagicImageInputFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.widget.MagicBaseView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hujinrong on 17/5/8.
 */

public class ImageRender implements GLSurfaceView.Renderer {

    private Bitmap mBitmap ;
    private MagicImageInputFilter mMagicImageInputFilter ;
    protected GPUImageFilter mFilter ;
    protected int mTextureId = OpenGlUtils.NO_TEXTURE;
    /**
     * 顶点坐标
     */
    protected FloatBuffer mGLCubeBuffer;
    /**
     * 纹理坐标
     */
    protected  FloatBuffer mGLTextureBuffer;
    /**
     * GLSurfaceView的宽高
     */
    protected int mSurfaceWidth;
    protected int mSurfaceHeight;

    /**
     * 图像宽高
     */
    protected int mImageWidth;

    protected int mImageHeight;

    public ImageRender(Bitmap bitmap,GPUImageFilter filter) {

        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        this.mFilter = filter ;
        this.setBitmap(bitmap);
    }



    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap ;
        this.mImageWidth = bitmap.getWidth() ;
        this.mImageHeight = bitmap.getHeight() ;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);

        Timer timer = new Timer("ImageRender");
        if( mMagicImageInputFilter == null ) {
            mMagicImageInputFilter = new MagicImageInputFilter();
        }
        mMagicImageInputFilter.init();
        timer.logTime("Init MagicInputFilter");
        if( mFilter != null ) {
            mFilter.init();
            timer.logTime("Init Filter.");
        }

        if( mTextureId != OpenGlUtils.NO_TEXTURE ) {
            GLES20.glDeleteTextures(1,new int[]{mTextureId},0);
            mTextureId = OpenGlUtils.NO_TEXTURE ;
        }

        mTextureId = OpenGlUtils.loadTexture(this.mBitmap,OpenGlUtils.NO_TEXTURE);
        timer.logTime("loadTexture");
        OpenGlUtils.checkGlError("loadTexture");


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width, height);
        mSurfaceWidth = width ;
        mSurfaceHeight = height ;
        onFilterChanged();
        MathUtils.adjustSize(
                mSurfaceWidth,mSurfaceHeight,
                mImageWidth,mImageHeight,
                MagicBaseView.ScaleType.CENTER_CROP,
                mGLCubeBuffer,mGLTextureBuffer
                ,0,false,false);
        mMagicImageInputFilter.initOffscreenFrameBuffer();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if( mTextureId == OpenGlUtils.NO_TEXTURE ) {
            return  ;
        }
        if( mFilter == null ) {
            mMagicImageInputFilter.onDrawFrame(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
        } else {
            int texureId = mMagicImageInputFilter.onDrawToFrameBuffer(mTextureId);
            mFilter.onDrawFrame(texureId,mGLCubeBuffer,mGLTextureBuffer);
        }
    }



    public ByteBuffer onDrawFrameWithByteBuffer(GL10 gl10) {
        if( mTextureId == OpenGlUtils.NO_TEXTURE ) {
            return  null;
        }
        if( mFilter == null ) {
            return mMagicImageInputFilter.onDrawToFrameBufferPixelData(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
        } else {
            int texureId = mMagicImageInputFilter.onDrawToFrameBuffer(mTextureId);
            return mFilter.onDrawToFrameBufferPixelData(texureId,mGLCubeBuffer,mGLTextureBuffer);
        }
    }


    /**
     * onSurfaceChanged调用
     */
    protected void onFilterChanged(){
        if(mFilter != null) {
            mFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
            mFilter.onInputSizeChanged(mImageWidth, mImageHeight);
        }

        mMagicImageInputFilter.onDisplaySizeChanged(mSurfaceWidth,mSurfaceHeight);
        mMagicImageInputFilter.onInputSizeChanged(mSurfaceWidth,mSurfaceHeight);
        this.mMagicImageInputFilter.initOffscreenFrameBuffer();
    }
}
