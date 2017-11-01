package com.snow.yp.kgdemo.windowmanger;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.snow.yp.kgdemo.R;

/**
 * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 * <p>
 * android.view.WindowManager$BadTokenException:
 * Unable to add window android.view.ViewRootImpl$W@40d35a40 --permission denied for this window type
 */
public class WindowMangerActivity extends Activity {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

//        mWindowManager.
        mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // ����������bitmap��ʽ
        mWindowParams.format = PixelFormat.RGBA_8888;

        // ����������Layout Params�г������������꣬���
        mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowParams.x = 100;
        mWindowParams.y = 100;

        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        MoveImageView imageView = new MoveImageView(this);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageView.setFloatViewParamsListener(new FloatViewListener());

        mWindowManager.addView(imageView, mWindowParams);


    }


    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class FloatViewListener implements MoveImageView.FloatViewParamsListener {
        @Override
        public int getTitleHeight() {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;

            int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
            int titleBarHeight = contentTop - statusBarHeight;

            return titleBarHeight;
        }

        @Override
        public WindowManager.LayoutParams getLayoutParams() {
            return mWindowParams;
        }
    }


}