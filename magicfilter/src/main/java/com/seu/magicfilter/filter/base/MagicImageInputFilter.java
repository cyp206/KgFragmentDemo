package com.seu.magicfilter.filter.base;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

/**
 * Created by hujinrong on 17/4/20.
 */

public class MagicImageInputFilter extends GPUImageFilter {

    public MagicImageInputFilter() {
        super(OpenGlUtils.readShaderFromRawResource(R.raw.image_vertex)
                ,OpenGlUtils.readShaderFromRawResource(R.raw.image_fragment));
    }

}
