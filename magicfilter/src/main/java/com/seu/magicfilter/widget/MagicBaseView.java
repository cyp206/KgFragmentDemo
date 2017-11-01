package com.seu.magicfilter.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;

/**
 * Created by hujinrong on 17/4/17.
 */

public class MagicBaseView extends GLSurfaceView implements GLSurfaceView.Renderer{


    public enum  ScaleType{
        CENTER_INSIDE,
        CENTER_CROP,
        FIT_XY;
    }

    protected GPUImageFilter mFilter ;

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

    protected int mTextureId = OpenGlUtils.NO_TEXTURE;

    /**
     * 顶点坐标
     */
    protected  FloatBuffer mGLCubeBuffer;


    /**
     * 纹理坐标
     */
    protected  FloatBuffer mGLTextureBuffer;



    protected ScaleType mScaleType = ScaleType.FIT_XY;

    protected int[] mVertextBufferObject = new int[2];


    public MagicBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public MagicBaseView(Context context) {
        this(context,null);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);

        //需要在合适的时候初始化Filter.
        if( mFilter != null ) {
            mFilter.init();
        }


    }

    public void prepareVBO(FloatBuffer vertex,int vertextCount,FloatBuffer texture,int textureCount,int[] vbo) {
        //数据绑定到VBO.
        GLES20.glGenBuffers(2,vbo,0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,vertextCount*4,vertex,GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GL_ARRAY_BUFFER,vbo[1]);
        GLES20.glBufferData(GL_ARRAY_BUFFER,textureCount*4,texture,GL_STATIC_DRAW);
        GLES20.glBindBuffer(GL_ARRAY_BUFFER,0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        onFilterChanged();
    }

    /**
     * onSurfaceChanged调用
     */
    protected void onFilterChanged(){
        if(mFilter != null) {
            mFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
            mFilter.onInputSizeChanged(mImageWidth, mImageHeight);
        }
    }


    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 调整大小
     * @param rotation 旋转角度
     * @param flipHorizontal 水平翻转
     * @param flipVertical 垂直翻转
     */
    protected void adjustSize(int rotation, boolean flipHorizontal, boolean flipVertical){
        float[] textureCords = TextureRotationUtil.getRotation(Rotation.fromInt(rotation),
                flipHorizontal, flipVertical);
        float[] cube = TextureRotationUtil.CUBE;
        float ratio1 = (float)mSurfaceWidth / mImageWidth;
        float ratio2 = (float)mSurfaceHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float)mSurfaceWidth;
        float ratioHeight = imageHeightNew / (float)mSurfaceHeight;

        if(mScaleType == ScaleType.CENTER_INSIDE){
            cube = new float[]{
                    TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                    TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                    TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                    TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
            };
        }else if(mScaleType == ScaleType.FIT_XY){

        }else if(mScaleType == ScaleType.CENTER_CROP){
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distVertical), addDistance(textureCords[1], distHorizontal),
                    addDistance(textureCords[2], distVertical), addDistance(textureCords[3], distHorizontal),
                    addDistance(textureCords[4], distVertical), addDistance(textureCords[5], distHorizontal),
                    addDistance(textureCords[6], distVertical), addDistance(textureCords[7], distHorizontal),
            };
        }
        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);

        prepareVBO(mGLCubeBuffer,TextureRotationUtil.CUBE.length,mGLTextureBuffer,TextureRotationUtil.TEXTURE_NO_ROTATION.length,mVertextBufferObject);
    }

    /**
     * 重置距离
     * @param coordinate 坐标
     * @param distance 距离
     * @return 重置距离
     */
    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    /**
     * 设置滤镜Filter.
     * @param filter 滤镜
     */
    public void setFilter(final GPUImageFilter filter){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mFilter != null) {
                    mFilter.destroy();
                }
                mFilter = null ;
                mFilter = filter ;
                if (mFilter != null) {
                    mFilter.init();
                }
                onFilterChanged();
            }
        });
    }
}
