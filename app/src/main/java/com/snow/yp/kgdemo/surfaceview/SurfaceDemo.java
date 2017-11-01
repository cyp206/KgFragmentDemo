package com.snow.yp.kgdemo.surfaceview;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.snow.yp.kgdemo.R;

/**
 * Created by y on 2017/5/24.
 */

public class SurfaceDemo extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private boolean mIsRunning;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Bitmap tempBg;
    private Paint clearPaint;

    public SurfaceDemo(Context context) {
        super(context);
        initSurface();
        init();
    }


    public SurfaceDemo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSurface();
        init();

    }

    public SurfaceDemo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSurface();
        init();

    }


    private void initSurface() {
        setZOrderOnTop(true);
//        setZOrderMediaOverlay(true);

        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRunning = true;
        Log.i("snow_surface", "surfaceCreated");

        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRunning = false;

    }

    @Override
    public void run() {
        draw();
    }

    private void draw() {
        mCanvas = mHolder.lockCanvas();
        if (mCanvas != null) {

            try {
                doDraw(mCanvas);
            } catch (Exception e) {
            } finally {
                mHolder.unlockCanvasAndPost(mCanvas);
            }

        }

    }


    public static final int MODE_FUNCTION_FILTER = 0;
    public static final int MODE_FUNCTION_STICKER = 1;
    public static final int MODE_FUNCTION_GRAFFITI = 2;
    public static final int MODE_FUNCTION_TEXT = 3;
    public static final int MODE_FUNCTION_EDIT = 4;

    private int lastValue;
    private Bitmap bg_red;
    private Bitmap bgIconShape;
    private Bitmap bg_result;
    private int currentIconPosition;
    private Canvas canvas11;
    private Rect rect;
    private static final int DefaultHeight = 55;//dp
    private Bitmap bgIcon;
    private int targetPosition;
    private int tempIconPositon;
    private int transformDistance;
    private Bitmap bgCustomBg;
    private Canvas bgCanvas;
    private Paint colorPaint;
    private int currentMode = MODE_FUNCTION_FILTER;
    private int lastMode;
    private int bgColorStrat;
    private int bgColorEnd;


    private void init() {
        bg_red = BitmapFactory.decodeResource(getResources(), R.drawable.bg_fun_select);
        bgIconShape = BitmapFactory.decodeResource(getResources(), R.drawable.bg_fun_select_icon);
        lastValue = -1;
        currentIconPosition = 0;
        bgIcon = Bitmap.createBitmap(dp2px(getContext(), 58), dp2px(getContext(), 50), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bgIcon);
        canvas.drawBitmap(bgIconShape, null, new Rect(0, 0, bgIcon.getWidth(), bgIcon.getHeight()), new Paint());
        bgColorStrat = Mode1_1;
        bgColorEnd = Mode1_2;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (bg_result == null) {
            bg_result = Bitmap.createBitmap(width, dp2px(getContext(), DefaultHeight), Bitmap.Config.ARGB_4444);
            bgCustomBg = Bitmap.createBitmap(width, dp2px(getContext(), DefaultHeight), Bitmap.Config.ARGB_4444);
            tempBg = Bitmap.createBitmap(width, dp2px(getContext(), DefaultHeight), Bitmap.Config.ARGB_4444);
            canvas11 = new Canvas(bg_result);
            bgCanvas = new Canvas(bgCustomBg);
            colorPaint = new Paint();

        }
        rect = new Rect(0, 0, width, dp2px(getContext(), DefaultHeight));

    }

    public Paint getClearPaint() {
        if (clearPaint == null) {
            clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        return clearPaint;
    }

    protected void doDraw(Canvas canvas) {

        canvas.drawPaint(getClearPaint());
//        canvas.drawColor(Color.TRANSPARENT);
        Paint paint = new Paint();
        Paint paint1 = new Paint();


        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas11.drawPaint(paint1);
        canvas11.drawColor(Color.TRANSPARENT);
        paint1.setXfermode(null);
        canvas11.drawBitmap(bgIcon, currentIconPosition, 0, paint1);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
//
        chanBgColor();
        canvas11.drawBitmap(bgCustomBg, null, rect, paint1);
        paint1.setXfermode(null);
        canvas.drawBitmap(bg_result, 0, 0, paint);

    }


    private static final int Mode1_1 = Color.parseColor("#ff00de");
    private static final int Mode1_2 = Color.parseColor("#fb3353");
    private static final int Mode2_1 = Color.parseColor("#9aff56");
    private static final int Mode2_2 = Color.parseColor("#52e8e3");
    private static final int Mode3_1 = Color.parseColor("#ffd700");
    private static final int Mode3_2 = Color.parseColor("#ff6c00");
    private static final int Mode4_1 = Color.parseColor("#00eaff");
    private static final int Mode4_2 = Color.parseColor("#9d68ff");
    private static final int Mode5_1 = Color.parseColor("#c85fff");
    private static final int Mode5_2 = Color.parseColor("#9d68ff");


    private void chanBgColor() {
        LinearGradient linearGradient = new LinearGradient(0, 0,
                getMeasuredWidth(), (float) (getMeasuredHeight() * 0.7),
                bgColorStrat, bgColorEnd, Shader.TileMode.CLAMP);
        colorPaint.setShader(linearGradient);
        bgCanvas.drawRect(rect, colorPaint);

    }


    private void move() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(100);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (value != lastValue) {
                    currentIconPosition = tempIconPositon + (int) (transformDistance * (value * 1f / 100f));
                    lastValue = value;
                    run();
//                    invalidate();
                }
            }
        });
        valueAnimator.setDuration(200);

        int[] curentColors = getBgColors(currentMode);
        int[] lsatColors = getBgColors(lastMode);


        ValueAnimator colorValueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), lsatColors[0], curentColors[0]);
        colorValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bgColorStrat = (int) animation.getAnimatedValue();


            }
        });
        colorValueAnimator.setDuration(200);


        ValueAnimator colorEndValueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), lsatColors[1], curentColors[1]);
        colorEndValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bgColorEnd = (int) animation.getAnimatedValue();

            }
        });
        colorEndValueAnimator.setDuration(200);


        colorEndValueAnimator.start();
        colorValueAnimator.start();
        valueAnimator.start();
    }

    private int[] getBgColors(int currentMode) {
        int[] ints = new int[2];
        switch (currentMode) {
            case MODE_FUNCTION_FILTER:
                ints[0] = Mode1_1;
                ints[1] = Mode1_2;
                break;
            case MODE_FUNCTION_TEXT:
                ints[0] = Mode2_1;
                ints[1] = Mode2_2;
                break;
            case MODE_FUNCTION_STICKER:
                ints[0] = Mode3_1;
                ints[1] = Mode3_2;
                break;
            case MODE_FUNCTION_GRAFFITI:
                ints[0] = Mode4_1;
                ints[1] = Mode4_2;
                break;
            case MODE_FUNCTION_EDIT:
                ints[0] = Mode5_1;
                ints[1] = Mode5_2;
                break;

        }

        return ints;
    }


    public void changPosition(int mode, int viewPosotion) {
        lastMode = currentMode;
        currentMode = mode;
        targetPosition = viewPosotion - dp2px(getContext(), 18);
        transformDistance = targetPosition - currentIconPosition;
        tempIconPositon = currentIconPosition;
        move();

    }

    public void initPosition(int mode, int viewPosotion) {
        targetPosition = viewPosotion - dp2px(getContext(), 18);
        currentIconPosition = targetPosition;
        int[] bgColors = getBgColors(mode);
        currentMode = mode;
        bgColorStrat = bgColors[0];
        bgColorEnd = bgColors[1];
        mIsRunning = true;
        run();
//        invalidate();

    }

    public static int dp2px(Context context, int dp) {
        if (context == null) return dp;
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5);
    }

}
