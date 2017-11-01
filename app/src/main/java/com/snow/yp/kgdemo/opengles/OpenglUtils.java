package com.snow.yp.kgdemo.opengles;

import android.opengl.GLES20;

/**
 * Created by y on 2017/7/12.
 */

public class OpenglUtils {

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;

    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {

            throw new RuntimeException(glOperation + ":glError" + error);
        }
    }

}
