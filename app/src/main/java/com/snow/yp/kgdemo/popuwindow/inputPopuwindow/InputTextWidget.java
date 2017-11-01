package com.snow.yp.kgdemo.popuwindow.inputPopuwindow;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.snow.yp.kgdemo.R;

/**
 * Created by y on 2017/6/27.
 */

public class InputTextWidget extends FrameLayout {
    public InputTextWidget(@NonNull Context context) {
        this(context, null);
    }

    public InputTextWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputTextWidget(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.pop_text_input_view, this);

    }


}
