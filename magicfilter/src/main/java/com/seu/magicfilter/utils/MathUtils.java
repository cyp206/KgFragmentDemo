package com.seu.magicfilter.utils;

import com.seu.magicfilter.widget.MagicBaseView;

import java.nio.FloatBuffer;

/**
 * Created by hujinrong on 17/5/8.
 */

public class MathUtils {

    /**
     * 调整大小
     * @param surfaceWidth surface宽度
     * @param surfaceHeight surface高度
     * @param imageWidth image宽度
     * @param imageHeight image高度
     * @param scaleType 缩放类型
     * @param rotation 旋转角度
     * @param flipHorizontal 水平翻转
     * @param flipVertical 垂直翻转
     * @param gLCubeBuffer 顶点数据
     * @param gLTextureBuffer 纹理坐标数据
     */
    public static void adjustSize(int surfaceWidth,int surfaceHeight,
                              int imageWidth,int imageHeight,
                              MagicBaseView.ScaleType scaleType,
                              FloatBuffer gLCubeBuffer,
                              FloatBuffer gLTextureBuffer,
                              int rotation, boolean flipHorizontal, boolean flipVertical){
        float[] textureCords = TextureRotationUtil.getRotation(Rotation.fromInt(rotation),
                flipHorizontal, flipVertical);
        float[] cube = TextureRotationUtil.CUBE;
        float ratio1 = (float)surfaceWidth / imageWidth;
        float ratio2 = (float)surfaceHeight / imageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(imageWidth * ratioMax);
        int imageHeightNew = Math.round(imageHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float)surfaceWidth;
        float ratioHeight = imageHeightNew / (float)surfaceHeight;

        if(scaleType == MagicBaseView.ScaleType.CENTER_INSIDE){
            cube = new float[]{
                    TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                    TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                    TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                    TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
            };
        }else if(scaleType == MagicBaseView.ScaleType.FIT_XY){

        }else if(scaleType == MagicBaseView.ScaleType.CENTER_CROP){
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distVertical), addDistance(textureCords[1], distHorizontal),
                    addDistance(textureCords[2], distVertical), addDistance(textureCords[3], distHorizontal),
                    addDistance(textureCords[4], distVertical), addDistance(textureCords[5], distHorizontal),
                    addDistance(textureCords[6], distVertical), addDistance(textureCords[7], distHorizontal),
            };
        }
        gLCubeBuffer.clear();
        gLCubeBuffer.put(cube).position(0);
        gLTextureBuffer.clear();
        gLTextureBuffer.put(textureCords).position(0);
    }

    /**
     * 重置距离
     * @param coordinate 坐标
     * @param distance 距离
     * @return 重置距离
     */
    private static float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }
}
