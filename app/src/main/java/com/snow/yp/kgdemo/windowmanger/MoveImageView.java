package com.snow.yp.kgdemo.windowmanger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class MoveImageView extends ImageView {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private WindowManager mWindowManager = (WindowManager) getContext().getApplicationContext().getSystemService("window");
    private float mRawX;
    private float mRawY;
    private float mStartX;
    private float mStartY;
    private FloatViewParamsListener mListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MoveImageView(Context context) {
        super(context);
    }

    public MoveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * ���ü�������������ǰImageView���ݲ���
     *
     * @param listener
     */
    public void setFloatViewParamsListener(FloatViewParamsListener listener) {
        mListener = listener;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int titleHeight = 0;
        if (mListener != null) {
            titleHeight = mListener.getTitleHeight();
        }

        mRawX = event.getRawX();
        mRawY = event.getRawY() - titleHeight;

        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();

                break;

            case MotionEvent.ACTION_MOVE:
                updateWindowPosition();
                break;

            case MotionEvent.ACTION_UP:
                updateWindowPosition();
                break;
        }

        // ���Ĵ����¼�
        return true;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * ���´��ڲ��������Ƹ��������ƶ�
     */
    private void updateWindowPosition() {
        if (mListener != null) {
            // ��������
            LayoutParams layoutParams = mListener.getLayoutParams();
            layoutParams.x = (int) (mRawX - mStartX);
            layoutParams.y = (int) (mRawY - mStartY);

            // ʹ������Ч
            mWindowManager.updateViewLayout(this, layoutParams);
        }
    }


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================


    /**
     * ��ǰ��ͼ���ڻ�ȡ����
     */
    public interface FloatViewParamsListener {

        /**
         * ��ȡ�������߶�
         * ��Ϊ��Ҫͨ��Window�����ȡ������ʹ�ô˰취
         *
         * @return
         */
        public int getTitleHeight();


        /**
         * ��ȡ��ǰWindowManager.LayoutParams ����
         *
         * @return
         */
        public LayoutParams getLayoutParams();
    }


}
