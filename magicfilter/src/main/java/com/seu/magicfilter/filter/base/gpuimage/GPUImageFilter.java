/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seu.magicfilter.filter.base.gpuimage;

import android.annotation.TargetApi;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Build;
import android.util.Log;

import com.seu.magicfilter.jni.GLESNative;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;

public class GPUImageFilter {
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    private final LinkedList<Runnable> mRunOnDraw;
    private final String mVertexShader;
    private final String mFragmentShader;
    protected int mGLProgId;
    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;

    protected int mInputWidth;
    protected int mInputHeight;
    protected boolean mIsInitialized;
    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;
    /**
     * 输出到屏幕，or输出到texture宽高
     */
    protected int mOutputWidth;
    protected int mOutputHeight;

    private int mOffscreenFrameBuffer = -1 ;
    private int mOffscreenFrameBufferTextureId= -1 ;

    private int[] mVertextBufferObject = new int[2] ;


    public GPUImageFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public GPUImageFilter(final String vertexShader, final String fragmentShader) {
        mRunOnDraw = new LinkedList<>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
        
        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
    }

    /**
     * 初始化onSurfaceCreated调用
     */
    public void init() {
        onInit();
        mIsInitialized = true;
        onInitialized();
    }


    /**
     * 留给子Filter继承，做初始化工作
     */
    protected void onInit() {
        mGLProgId = OpenGlUtils.loadProgram(mVertexShader, mFragmentShader);
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId, "inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId,
                "inputTextureCoordinate");
        //准备VBO
        prepareVBO(mGLCubeBuffer,TextureRotationUtil.CUBE.length,mGLTextureBuffer,TextureRotationUtil.TEXTURE_NO_ROTATION.length,mVertextBufferObject);

        mIsInitialized = true;
    }
    /**
     * 留给子Filter继承，做初始化完成工作
     */
    protected void onInitialized() {

    }

    /**
     * 初始化离屏FrameBuffer
     */
    public void initOffscreenFrameBuffer() {
        if( mInputWidth == 0 || mInputHeight == 0 ) {
            throw new RuntimeException("Input size cannot be zero.");
        }

        if( mOffscreenFrameBuffer !=0 && mOffscreenFrameBuffer !=-1 ) {
            return ;
        }

        int []frameBuffer = new int[1];
        int []frameBufferTexture = new int[1];

        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glGenTextures(1, frameBufferTexture, 0);


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTexture[0]);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mInputWidth, mInputHeight, 0,
//                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mOutputWidth, mOutputHeight, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, frameBufferTexture[0], 0);

//        GLES20.glViewport(0,0,mOutputHeight,mOutputHeight);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        this.mOffscreenFrameBuffer = frameBuffer[0];
        this.mOffscreenFrameBufferTextureId = frameBufferTexture[0];
    }

    /**
     * 销毁离屏缓存
     */
    public void destroyOffscreenBuffer() {
        if( mOffscreenFrameBuffer != -1 ) {
            GLES20.glDeleteFramebuffers(1, new int[]{ mOffscreenFrameBuffer }, 0 );
            mOffscreenFrameBuffer = -1 ;
        }

        if( mOffscreenFrameBufferTextureId != -1 ) {
            GLES20.glDeleteTextures(1, new int[]{mOffscreenFrameBufferTextureId}, 0);
            mOffscreenFrameBufferTextureId = -1 ;
        }

    }

    /**
     * 销毁Surface时候调用
     */
    public final void destroy() {
        mIsInitialized = false;
        onDestroy();
        GLES20.glDeleteProgram(mGLProgId);
    }


    /**
     * 留个子Filter继承使用，做销毁工作.
     */
    protected void onDestroy() {
        destroyOffscreenBuffer();
        delteVBO(mVertextBufferObject[0],mVertextBufferObject[1]);
    }


    /**
     * onSurfaceChanged调用
     * @param width 宽度
     * @param height 高度
     */
    public void onInputSizeChanged(final int width, final int height) {
        mInputWidth = width;
        mInputHeight = height;
    }

    /**
     * 显示大小改变
     * @param width 宽度
     * @param height 高度
     */
    public void onDisplaySizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
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

    public void delteVBO(int vertextVBO,int textVBO) {
        if( vertextVBO != -1 ) {
            GLES20.glDeleteBuffers(1,new int[]{vertextVBO},0);
        }

        if( textVBO !=-1 ) {
            GLES20.glDeleteBuffers(1,new int[]{textVBO},0);
        }
    }


    /**
     * 绘制在FrameBuffer上
     * @param sourceTextureId 待绘制的纹理
     * @return FrameBufferTextureId
     */
    public int onDrawToFrameBuffer(final int sourceTextureId) {
//        return this.onDrawToFrameBuffer(sourceTextureId,mGLCubeBuffer,mGLTextureBuffer);
        return this.onDrawToFrameBuffer(sourceTextureId,mVertextBufferObject[0],mVertextBufferObject[1]);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public ByteBuffer onDrawToFrameBufferPixelData(final int sourceTextureId, FloatBuffer glCubeBuffer, FloatBuffer glTextureBuffer) {
        int []pob = new int[1];
        GLES30.glGenBuffers(1,pob,0);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, pob[0]);
        GLES30.glBufferData(GLES30.GL_PIXEL_PACK_BUFFER, 4*mInputHeight*mInputWidth, null, GLES30.GL_DYNAMIC_READ);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER,0);

        Log.d(getClass().getSimpleName(),String.format("[width=%d,height=%d]",mInputWidth,mInputHeight));
        GLES20.glViewport(0,0,mInputWidth,mInputHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mOffscreenFrameBuffer);
        OpenGlUtils.checkGlError("after bindFrameBuffer");

        OpenGlUtils.checkGlError("after glBufferData");
        //绑定之后需要重新设置.
        onDrawFrame(sourceTextureId,glCubeBuffer,glTextureBuffer);
        OpenGlUtils.checkGlError("after onDrawFrame.");
        long start = System.currentTimeMillis();

        GLES20.glFinish();
        GLES30.glPixelStorei(GLES30.GL_PACK_ALIGNMENT,1);
        OpenGlUtils.checkGlError("before glReadPixels1");
        GLES30.glReadBuffer(GLES30.GL_COLOR_ATTACHMENT0);
        OpenGlUtils.checkGlError("before glReadPixels2");
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, pob[0]);
        OpenGlUtils.checkGlError("before glReadPixels3");
        Log.d(getClass().getSimpleName(),String.format("size is [%d,%d]",mInputWidth,mInputHeight));
        GLESNative.glReadPixels(mInputWidth,mInputHeight,GLES30.GL_RGBA,GLES30.GL_UNSIGNED_BYTE,0);
        OpenGlUtils.checkGlError("after glReadPixels");
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, pob[0]);
        Buffer buf = GLES30.glMapBufferRange(GLES30.GL_PIXEL_PACK_BUFFER, 0, 4*mInputWidth*mInputHeight, GLES30.GL_MAP_READ_BIT);
        ByteBuffer byteBuffer = ((ByteBuffer) buf).order(ByteOrder.nativeOrder());
        OpenGlUtils.checkGlError("after glMapBufferRange.");
        Log.d(getClass().getSimpleName(),"cost time is %d"+(System.currentTimeMillis()-start));
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        GLES30.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER,0);

        return byteBuffer ;
    }

    /**
     * 绘制在FrameBuffer上
     * @param sourceTextureId 纹理Id
     * @return 像素数据
     */
    public ByteBuffer onDrawToFrameBufferPixelData(final int sourceTextureId) {
        return onDrawToFrameBufferPixelData(sourceTextureId,mGLCubeBuffer,mGLTextureBuffer);
    }


    /**
     * 会在到FrameBuffer上
     * @param sourceTextureId 待绘制的纹理ID
     * @param cubeBuffer 顶点
     * @param textureBuffer 纹理坐标
     * @return 像素数据
     */
    public int onDrawToFrameBuffer(final int sourceTextureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
//        GLES20.glViewport(0,0,mInputWidth,mInputHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mOffscreenFrameBuffer);
        //绑定之后需要重新设置.
        onDrawFrame(sourceTextureId,cubeBuffer,textureBuffer);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return mOffscreenFrameBufferTextureId ;
    }

    /**
     * 会在到FrameBuffer上
     * @param sourceTextureId 待绘制的纹理ID
     * @param vertexVBO 顶点VBO
     * @param textureVBO 纹理坐标VBO
     * @return 像素数据
     */
    public int onDrawToFrameBuffer(final int sourceTextureId, int vertexVBO, int textureVBO) {
//        GLES20.glViewport(0,0,mInputWidth,mInputHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mOffscreenFrameBuffer);
        //绑定之后需要重新设置.
        onDrawFrame(sourceTextureId,vertexVBO,textureVBO);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return mOffscreenFrameBufferTextureId ;
    }

    /**
     * 利用传入的纹理，顶点数据，写入到屏幕缓存
     * @param textureId     纹理Id
     * @param cubeBuffer    顶点数据
     * @param textureBuffer 纹理坐标
     * @return result of draw.
     */
    public int onDrawFrame(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return OpenGlUtils.NOT_INIT;
        }

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        onDrawArraysAfter();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return OpenGlUtils.ON_DRAWN;
    }

    /**
     * 利用传入的纹理，顶点数据，写入到屏幕缓存
     * @param textureId     纹理Id
     * @return result of draw.
     */
    public int onDrawFrame(final int textureId, int vertexVBO,int textureVBO) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!mIsInitialized) {
            return OpenGlUtils.NOT_INIT;
        }

        GLES20.glBindBuffer(GL_ARRAY_BUFFER,vertexVBO);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        GLES20.glBindBuffer(GL_ARRAY_BUFFER,textureVBO);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                0);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        onDrawArraysAfter();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GL_ARRAY_BUFFER,0);
        return OpenGlUtils.ON_DRAWN;
    }


    /**
     * 绘制到缓存
     * @param textureId 用到的纹理Id.
     * @return result of draw.
     */
    public int onDrawFrame(final int textureId) {
//        return onDrawFrame(textureId,mGLCubeBuffer,mGLTextureBuffer);
        return onDrawFrame(textureId,mVertextBufferObject[0],mVertextBufferObject[1]);
    }


    /**
     * 子Filter实现,在drawarray之前
     */
    protected void onDrawArraysPre() {}

    /**
     * 子Filter实现，在drawArray之后
     */
    protected void onDrawArraysAfter() {}

    /**
     * onDrawFrame执行操作.
     */
    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    public int getIntputWidth() {
        return mInputWidth;
    }

    public int getIntputHeight() {
        return mInputHeight;
    }

    public int getProgram() {
        return mGLProgId;
    }

    public int getAttribPosition() {
        return mGLAttribPosition;
    }

    public int getAttribTextureCoordinate() {
        return mGLAttribTextureCoordinate;
    }

    public int getUniformTexture() {
        return mGLUniformTexture;
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param intValue intValue
     */
    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param floatValue floatValue
     */
    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param arrayValue arrayValue
     */
    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param arrayValue arrayValue
     */
    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param arrayValue arrayValue
     */
    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param arrayValue arrayValue
     */
    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param point point
     */
    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param matrix matrix
     */
    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    /**
     * 绘制时设置参数
     * @param location OpenGL shader uniform location.
     * @param matrix matrix
     */
    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    /**
     * onDrawFrame时候调用
     * @param runnable 执行逻辑
     */
    public void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    public GPUImageFilter clone() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null ;
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
        Log.d("matrix",sb.toString());
    }

}
