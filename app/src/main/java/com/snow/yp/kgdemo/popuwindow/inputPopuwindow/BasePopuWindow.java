package com.snow.yp.kgdemo.popuwindow.inputPopuwindow;

import android.content.Context;
import android.view.Gravity;
import android.widget.PopupWindow;

/**
 * Created by y on 2017/6/23.
 */

public class BasePopuWindow extends PopupWindow {
    public BasePopuWindow(Context context) {
        super(context);
    }


    public void showPopWindow() {
        showAtLocation(getContentView(), Gravity.BOTTOM, 0, 0);
    }


    public void dismissPopWindow() {
        dismiss();
    }
}
