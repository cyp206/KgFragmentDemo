package com.snow.yp.kgdemo.puzzViewThumbnail;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Created by y on 2017/5/11.
 */

public class PuzzUtils {

    /**
     * return the  rects   min < left top >   max < right bottom>
     *
     * @param dst
     * @param rect2
     * @return dst
     */
    public static Rect compileRectForPoint(Rect dst, Rect rect2) {
        dst.left = Math.min(dst.left, rect2.left);
        dst.right = Math.max(dst.right, rect2.left);
        dst.top = Math.min(dst.top, rect2.top);
        dst.bottom = Math.max(dst.bottom, rect2.top);
        return dst;
    }


    public static Rect compileRect(Rect dst, Rect rect2) {
        dst.left = Math.min(dst.left, rect2.left);
        dst.right = Math.max(dst.right, rect2.right);
        dst.top = Math.min(dst.top, rect2.top);
        dst.bottom = Math.max(dst.bottom, rect2.bottom);
        return dst;
    }


    public static Rect scalRect(Rect rect, float scalX, float ScalY) {
        rect.left = (int) (rect.left * scalX);
        rect.right = (int) (rect.right * scalX);
        rect.top = (int) (rect.top * ScalY);
        rect.bottom = (int) (rect.bottom * ScalY);

        return rect;
    }


    public static int dp2px(Context context, int dp) {
        if (context == null) return dp;
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5);
    }

    public static int px2dp(Context context, int px) {
        if (context == null) return px;
        return (int) ((px - 0.5) / context.getResources().getDisplayMetrics().density);
    }


    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    public static float calculateDistance(MotionEvent motionEvent) {
        if (motionEvent == null || motionEvent.getPointerCount() <= 1) return 0;

        double x = motionEvent.getX(0) - motionEvent.getX(1);
        double y = motionEvent.getY(0) - motionEvent.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    public static Point getCenterPoint(MotionEvent motionEvent) {
        Point point = new Point(0, 0);
        if (motionEvent == null || motionEvent.getPointerCount() <= 1) return point;
        double x = (motionEvent.getX(1) + motionEvent.getX(0)) / 2;
        double y = (motionEvent.getY(1) + motionEvent.getY(0)) / 2;
        point.x = (int) x;
        point.y = (int) y;


        return point;
    }
}
