package com.seu.magicfilter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.AttributeSet;
import android.util.Log;

import com.seu.magicfilter.filter.adavanced.MagicSketchFilter;
import com.seu.magicfilter.filter.base.MagicImageInputFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hujinrong on 17/4/19.
 */

public class MagicImageView extends MagicBaseView {


    private static final String TAG = "MagicImageView";

    private Bitmap mBitmap ;

    private MagicImageInputFilter mMagicImageInputFilter ;

    public MagicImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MagicImageView(Context context) {
        super(context);
    }

    /**
     * 设置待显示的图片.
     * @param bitmap 图片
     * @param scaleType 缩放类型
     */
    public void setBitmap(final Bitmap bitmap,ScaleType scaleType) {
        this.mBitmap = bitmap                   ;
        this.mImageWidth = bitmap.getWidth()   ;
        this.mImageHeight = bitmap.getHeight() ;
        this.mScaleType = scaleType ;
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        super.onSurfaceChanged(gl10, width, height);
        Log.d(TAG,"onSurfaceChanged");
        onFilterChanged();
        adjustSize(0,false,false);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        Log.d(TAG,"onSurfaceCreated");
        if( mMagicImageInputFilter == null ) {
            mMagicImageInputFilter = new MagicImageInputFilter();
        }
        mMagicImageInputFilter.init();
        if( mFilter != null ) {
            mFilter.init();
        }

        if( mTextureId != OpenGlUtils.NO_TEXTURE ) {
            GLES20.glDeleteTextures(1,new int[]{mTextureId},0);
            mTextureId = OpenGlUtils.NO_TEXTURE ;
        }

        mTextureId = OpenGlUtils.loadTexture(this.mBitmap,OpenGlUtils.NO_TEXTURE);
        OpenGlUtils.checkGlError("loadTexture");
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        super.onDrawFrame(gl10);
        if( mTextureId == OpenGlUtils.NO_TEXTURE ) {
            return  ;
        }
        if( mFilter == null ) {
            mMagicImageInputFilter.onDrawFrame(mTextureId, mVertextBufferObject[0], mVertextBufferObject[1]);
        } else {
            int texureId = mMagicImageInputFilter.onDrawToFrameBuffer(mTextureId);
            mFilter.onDrawFrame(texureId,mVertextBufferObject[0],mVertextBufferObject[1]);
        }
    }

    @Override
    protected void onFilterChanged() {
        super.onFilterChanged();
        mMagicImageInputFilter.onDisplaySizeChanged(mSurfaceWidth,mSurfaceHeight);
        mMagicImageInputFilter.onInputSizeChanged(mSurfaceWidth,mSurfaceHeight);
        if( mFilter != null ) {
            this.mMagicImageInputFilter.initOffscreenFrameBuffer();
        } else {
            this.mMagicImageInputFilter.destroyOffscreenBuffer();
        }
        requestRender();
    }
}
