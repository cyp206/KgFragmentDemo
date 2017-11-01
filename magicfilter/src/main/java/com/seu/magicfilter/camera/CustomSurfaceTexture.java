package com.seu.magicfilter.camera;

import android.graphics.SurfaceTexture;
import android.util.Log;

/**
 * Created by hujinrong on 17/4/28.
 */

public class CustomSurfaceTexture extends SurfaceTexture {
    public CustomSurfaceTexture(int texName) {
        super(texName);
    }

    @Override
    public void setDefaultBufferSize(int width, int height) {
        Log.d(getClass().getSimpleName(),String.format("[width=%d,height=%d]",width,height));
        super.setDefaultBufferSize(width, height);
    }

    @Override
    public void getTransformMatrix(float[] mtx) {
        super.getTransformMatrix(mtx);
//        printMatrix(mtx);
    }

    /**
     * 打印matrix.测试方法
     * @param matrix matrix对象
     */
    protected void printMatrix(float []matrix) {
        StringBuilder sb = new StringBuilder();
        for( int i = 0 ; i < matrix.length ;i++ ) {
            sb.append(matrix[i]+",");
        }
        Log.d(getClass().getSimpleName(),sb.toString());
    }
}
