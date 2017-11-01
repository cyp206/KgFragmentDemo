package com.snow.yp.kgdemo.popuwindow.inputPopuwindow;

import android.content.Context;
import android.widget.LinearLayout;

import com.snow.yp.kgdemo.R;

/**
 * Created by y on 2017/6/27.
 */

public class TextInputPopWindow extends BasePopuWindow {


    public TextInputPopWindow(Context context) {
        super(context);
        InputTextWidget inputView = new InputTextWidget(context);
        this.setContentView(inputView);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
//        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        double v = context.getResources().getDisplayMetrics().density * 50 + 0.5;
        this.setHeight((int) v);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.doodle_font_popbg));

    }


}
