package com.seu.magicfilter.filter.adavanced;

import android.opengl.GLES20;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.utils.OpenGlUtils;

public class MagicBrannanFilter extends GPUImageFilter {
    private int[] mInputTextureHandles = {-1,-1,-1,-1,-1};
    private int[] mInputTextureUniformLocations = {-1,-1,-1,-1,-1};
    private int mGLStrengthLocation;

    public MagicBrannanFilter(){
        super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.brannan));
    }

    @Override
    protected void onDestroy() {
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
    protected void onInit(){
        super.onInit();
        for(int i = 0; i < mInputTextureUniformLocations.length; i++) {
            mInputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture" + (2 + i));
        }
        mGLStrengthLocation = GLES20.glGetUniformLocation(mGLProgId,
				"strength");
    }

    @Override
    protected void onInitialized(){
        super.onInitialized();
        setFloat(mGLStrengthLocation, 1.0f);
        runOnDraw(new Runnable(){
            public void run(){
                mInputTextureHandles[0] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/brannan_process.png");
                mInputTextureHandles[1] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/brannan_blowout.png");
                mInputTextureHandles[2] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/brannan_contrast.png");
                mInputTextureHandles[3] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/brannan_luma.png");
                mInputTextureHandles[4] = OpenGlUtils.loadTexture(MagicParams.getContext(), "filter/brannan_screen.png");
            }
        });
    }
}
