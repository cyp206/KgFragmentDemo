package com.snow.yp.kgdemo.puzzViewThumbnail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y on 2017/5/17.
 * 简拼缩略图
 */

public class PuzzThumbnailView extends View {
    private static final float STEP_length = 0.005f;

    private List<Path> pathList;
    private Paint mPaint;
    private List<List<Rect>> listPoints;
    private float mRoundCorner;
    private int mPaintStrokeWidth;
    private boolean isinit;

    public PuzzThumbnailView(Context context) {
        super(context);
        init();
    }

    public PuzzThumbnailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public PuzzThumbnailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        List<Rect> pointList1 = new ArrayList<>();
        pointList1.add(new Rect(0, 0, 125, 125));
        pointList1.add(new Rect(1018, 0, 956, 125));
        pointList1.add(new Rect(1018, 207, 956, 195));
        pointList1.add(new Rect(815, 815, 765, 765));
        pointList1.add(new Rect(203, 1018, 194, 956));
        pointList1.add(new Rect(0, 1018, 125, 956));


        List<Rect> pointList2 = new ArrayList<>();
//        rect = getRealRect(scal, rect);

        pointList2.add(new Rect(1018, 207, 1018, 401));
        pointList2.add(new Rect(1222, 815, 1173, 864));
        pointList2.add(new Rect(1831, 1018, 1635, 1018));
        pointList2.add(new Rect(1222, 1222, 1173, 1173));
        pointList2.add(new Rect(1018, 1834, 1018, 1635));
        pointList2.add(new Rect(815, 1222, 864, 1173));
        pointList2.add(new Rect(203, 1018, 401, 1018));
        pointList2.add(new Rect(815, 815, 864, 864));

        List<Rect> pointList3 = new ArrayList<>();

        pointList3.add(new Rect(0, 1018, 125, 1080));
        pointList3.add(new Rect(203, 1018, 194, 1080));
        pointList3.add(new Rect(815, 1222, 765, 1271));
        pointList3.add(new Rect(1018, 1834, 956, 1841));
        pointList3.add(new Rect(1018, 2036, 956, 1911));
        pointList3.add(new Rect(0, 2036, 125, 1911));

        List<Rect> pointList4 = new ArrayList<>();

        pointList4.add(new Rect(1018, 0, 1080, 125));
        pointList4.add(new Rect(2036, 0, 1911, 125));
        pointList4.add(new Rect(2036, 1018, 1911, 956));
        pointList4.add(new Rect(1831, 1018, 1841, 956));
        pointList4.add(new Rect(1222, 815, 1271, 765));
        pointList4.add(new Rect(1018, 207, 1080, 195));


        List<Rect> pointList5 = new ArrayList<>();

        pointList5.add(new Rect(2036, 1018, 1911, 1080));
        pointList5.add(new Rect(2036, 2036, 1911, 1911));
        pointList5.add(new Rect(1018, 2036, 1080, 1911));
        pointList5.add(new Rect(1018, 1834, 1080, 1841));
        pointList5.add(new Rect(1222, 1222, 1271, 1271));
        pointList5.add(new Rect(1831, 1018, 1841, 1080));

        listPoints = new ArrayList<>();
        listPoints.add(pointList1);
        listPoints.add(pointList2);
        listPoints.add(pointList3);
        listPoints.add(pointList4);
        listPoints.add(pointList5);
//        int i = PuzzUtils.dp2px(getContext(), 250);
//        Log.i( "snow_666",getWidth()+"");
//        float scalX = getWidth() / 2036f;
//        float scalY = getHeight() / 2036f;

        mRoundCorner = 0;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(PuzzUtils.dp2px(getContext(), 20));
//        recaculate(listPoints, scalX, scalY);

//        initPaths(listPoints);

    }


    private void recaculate(List<List<Rect>> listPoints, float scalX, float scalY) {
        for (List<Rect> pointList : listPoints) {
            for (Rect rect : pointList) {
                PuzzUtils.scalRect(rect, scalX, scalY);
            }
        }
    }

    private void initPaths(List<List<Rect>> listPoints) {
        pathList = new ArrayList<>();
        for (List<Rect> pointList : listPoints) {
            Path path = new Path();
            for (int x = 0; x < pointList.size(); x++) {
                Rect rect = pointList.get(x);

                if (x == 0) {
                    path.moveTo(rect.left, rect.top);
                } else {
                    path.lineTo(rect.left, rect.top);
                }
            }
            path.close();
            pathList.add(path);

        }
    }

    private void initRoundCornerPaths(List<List<Rect>> listPoints) {
        pathList = new ArrayList<>();
        for (List<Rect> pointList : listPoints) {
//            Path path = new Path();
            List<Point> listPoint = new ArrayList<>();
            for (int x = 0; x < pointList.size(); x++) {
                Rect rect = pointList.get(x);
                Point point = new Point(rect.left, rect.top);
                listPoint.add(point);
            }
            pathList.add(makePath(listPoint));

        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getWidth() != 0 && getHeight() != 0 && isinit == false) {
            float scalX = (getWidth() - 5) / 2036f;
            float scalY = (getHeight() - 5) / 2036f;
            recaculate(listPoints, scalX, scalY);
            initRoundCornerPaths(listPoints);
            isinit = true;

        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        Matrix matrix = new Matrix();
        matrix.postTranslate(2, 2);
        canvas.concat(matrix);
        if (pathList == null || pathList.size() <= 0) return;
        for (Path p : pathList) {
            canvas.drawPath(p, mPaint);

        }

    }

    private void doDraw(Canvas canvas, Path path) {
        Bitmap bitmapPath = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_4444);
        Bitmap bitmap2 = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_4444);
        Bitmap bitmap22 = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_4444);

        Canvas canvas1 = new Canvas(bitmap2);
        canvas1.drawColor(Color.BLUE);
        canvas1.save();


        Canvas canvasPath = new Canvas(bitmapPath);
        canvasPath.drawColor(Color.TRANSPARENT);
        Paint pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pathPaint.setColor(Color.WHITE);
        pathPaint.setStrokeWidth(mPaintStrokeWidth);
        canvasPath.drawPath(path, pathPaint);


        Canvas canvasRet = new Canvas(bitmap22);


        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvasRet.drawBitmap(bitmapPath, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvasRet.save();

        canvasRet.drawBitmap(bitmap2, 0, 0, paint);

        canvas.drawBitmap(bitmap22, 0, 0, mPaint);
    }


    private Path makePath(List<Point> pointList) {
        //将第0个点再次添加到最后.
        pointList.add(pointList.get(0));
        List<Point> newPoints = new ArrayList<>();
        //生成路径中的点.p1,p2 p1p2a,p1p2b.
        for (int i = 0, size = pointList.size(); i < size - 1; i++) {
            Point p1 = pointList.get(i);
            Point p2 = pointList.get(i + 1);

            Point p1p2a = interpolate(p1, p2, mRoundCorner * STEP_length);
            Point p1p2b = interpolate(p1, p2, 1 - mRoundCorner * STEP_length);

            newPoints.add(p1p2a);
            newPoints.add(p1p2b);
            newPoints.add(p2);
        }

        Log.d(getClass().getSimpleName(), String.format("point is %s", printPoints(newPoints)));
        Log.d(getClass().getSimpleName(), String.format("Count is %d", newPoints.size()));

        int count = 0;
        List<CustomeLine> lines = new ArrayList<>();
        //绘制线段
        Path path = new Path();
        for (int i = 0; i < newPoints.size() - 1; i += 3) {
            Point point1 = newPoints.get(i);
            Point point2 = newPoints.get(i + 1);
            lines.add(new CustomeLine(point1, point2));
            count++;
        }

        Log.d(getClass().getSimpleName(), String.format("CustomeLine Count is %d", count));
        count = 0;
        //
        List<Cubic> cubics = new ArrayList<>();
        List<Point> cubicPoints = new ArrayList<>(newPoints);
        cubicPoints.add(newPoints.get(0));
        Log.d(getClass().getSimpleName(), String.format("cubicPoints is %s", printPoints(cubicPoints)));
        for (int i = 1; i < cubicPoints.size() - 2; i += 3) {
            Point point1 = cubicPoints.get(i);
            Point point2 = cubicPoints.get(i + 1);
            Point point3 = cubicPoints.get(i + 2);
            Log.d(getClass().getSimpleName(), String.format("cubic %s,%s,%s", point1.toString(), point2.toString(), point3.toString()));
            cubics.add(new Cubic(point1, point2, point3));
            count++;
        }
        Log.d(getClass().getSimpleName(), String.format("Cubic Count is %d", count));

        Cubic cubic1 = cubics.get(0);
        path.moveTo(cubic1.start.x, cubic1.start.y);
        for (int i = 0; i < lines.size(); i++) {
            CustomeLine line = lines.get(i);
            if (i != 0) {
                line(path, line.end);
            }
            Cubic cubic = cubics.get(i);
            cubicTo(path, cubic.start, cubic.center, cubic.end);
        }
        path.close();
        return path;
    }


    public Point interpolate(Point p1, Point p2, double t) {
        return new Point((int) Math.round(p1.x * (1 - t) + p2.x * t),
                (int) Math.round(p1.y * (1 - t) + p2.y * t));
    }


    private String printPoints(List<Point> points) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            stringBuilder.append(String.format(",%s", point));
        }
        return stringBuilder.toString();
    }

    private void cubicTo(Path path, Point start, Point center, Point end) {
        path.cubicTo(start.x, start.y, center.x, center.y, end.x, end.y);
    }

    private void line(Path path, Point point) {
        path.lineTo(point.x, point.y);
    }


    public void setPadding(int x) {
        mPaintStrokeWidth = PuzzUtils.dp2px(getContext(), x) / 8;
        if (mPaintStrokeWidth < 0) mPaintStrokeWidth = 1;
        mPaint.setStrokeWidth(mPaintStrokeWidth);
        initRoundCornerPaths(listPoints);
        invalidate();
    }

    public void setCorner(int x) {
        mRoundCorner = x;
        initRoundCornerPaths(listPoints);
        invalidate();
    }

    public void setPointList(List<List<Rect>> listPoints) {
        if (listPoints == null || listPoints.size() <= 0) return;
        this.listPoints = listPoints;
    }
}
