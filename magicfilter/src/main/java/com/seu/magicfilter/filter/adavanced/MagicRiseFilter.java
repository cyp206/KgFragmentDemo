package com.seu.magicfilter.filter.adavanced;

import android.opengl.GLES20;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.utils.OpenGlUtils;

public class MagicRiseFilter extends GPUImageFilter {
    private int[] mInputTextureHandles = {-1,-1,-1};
    private int[] mInputTextureUniformLocations = {-1,-1,-1};

    public MagicRiseFilter(){
        super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.rise));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(mInputTextureHandles.length, mInputTextureHandles, 0);
        for(int i = 0; i < mInputTextureHandles.length; i++) {
            mInputTextureHandles[i] = -1;
        }
    }

    @Override
    protected void onDrawArraysAfter(){
        for(int i = 0; i < mInputTextureHandles.length
                && mInputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        }
    }

    @Override
    protected void onDrawArraysPre(){
        for(int i = 0; i < mInputTextureHandles.length
                && mInputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mInputTextureHandles[i]);
            GLES20.glUniform1i(mInputTextureUniformLocations[i], (i+3));
        }
    }

    @Override
    public void onInit(){
        super.onInit();
        for(int i = 0; i < mInputTextureUniformLocations.length; i++){
            mInputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
        }
    }

    @Override
    public void onInitialized(){
        super.onInitialized();
        runOnDraw(new Runnable(){
            public void run(){
                mInputTextureHandles[0] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/blackboard1024.png");
                mInputTextureHandles[1] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/overlaymap.png");
                mInputTextureHandles[2] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/risemap.png");
            }
        });
    }
}