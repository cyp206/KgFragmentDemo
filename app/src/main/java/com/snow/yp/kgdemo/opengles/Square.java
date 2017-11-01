package com.snow.yp.kgdemo.opengles;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.snow.yp.kgdemo.opengles.OpenglUtils.checkGlError;
import static com.snow.yp.kgdemo.opengles.OpenglUtils.loadShader;

/**
 * Created by y on 2017/7/12.
 */

public class Square {


    private final String vertexShaderCode =

            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    static final int COORDS_PER_VERTEX = 3;
    // 逆时针 从左上 开始
    static float[] squareCoords = {
            -0.5f, 0.5f, 0f,  //left top
            -0.5f, -0.5f, 0f, //left bottom
            0.5f, -0.5f, 0f, //right bottom
            0.5f, 0.5f, 0f,// right top
    };
    private final short drawOrder[] = {0, 1, 2}; // 绘制顺序   made up of 2 triangle
    float color[] = {0.2f, 0.709803922f, 0.898039216f, 1.0f};


    public Square() {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder()); //??
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);


        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex

    public void draw(float[] mMVPMatrix) {
        GLES20.glUseProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");


        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate date
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);


        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        checkGlError("glGetUniformLocation");


        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        checkGlError("glUniformMatrix4fv");


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);


        GLES20.glDisableVertexAttribArray(mPositionHandle);


    }
}
