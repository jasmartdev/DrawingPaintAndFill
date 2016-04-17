package com.jasmartdev.drawingpaintandfill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

public class DrawingView extends View {

    public static Paint drawPaint, canvasPaint;
    public static int paintColor = 0xFFFFFFFF;
    private final int bgColor = Define.BGCOLOR;
    private int downColor = Define.BGCOLOR, upColor = Define.BGCOLOR;
    private boolean isDrag = false;
    private float down_x;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int img = -1;
    public static int s_CountChange = 0;
    private boolean ismovefinished, istouchup;
    private float touchX;
    private float touchY;

    public static Bitmap bm;
    private int bm_offsetX, bm_offsetY;
    private Path touchPath;
    private Path animPath;
    private PathMeasure pathMeasure;
    private float pathLength;
    private float step;
    private float distance;
    private float curX, curY;
    private float curAngle;
    private float targetAngle;
    private float stepAngle;
    private float[] pos;
    private float[] tan;
    private Matrix matrix;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static void setupDrawing() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void initBrush() {
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.brush);
        bm_offsetX = bm.getWidth() / 2;
        bm_offsetY = bm.getHeight() / 2;
        animPath = new Path();
        pos = new float[2];
        tan = new float[2];
        matrix = new Matrix();
        touchPath = new Path();
        step = 10;
        curX = -bm.getWidth();
        curY = -bm.getHeight();
        stepAngle = 10;
    }

    public void setBrush(int id) {
        bm = BitmapFactory.decodeResource(getResources(), id);
        bm_offsetX = bm.getWidth() / 2;
        bm_offsetY = bm.getHeight() / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        Rect dest = new Rect(0, 0, getWidth(), getHeight());
        drawCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), img), null, dest, drawPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        matrix.reset();
        if ((targetAngle - curAngle) > stepAngle) {
            curAngle += stepAngle;
            matrix.postRotate(curAngle, bm_offsetX, bm_offsetY);
            matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm, matrix, null);
            invalidate();
        } else if ((curAngle - targetAngle) > stepAngle) {
            curAngle -= stepAngle;
            matrix.postRotate(curAngle, bm_offsetX, bm_offsetY);
            matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm, matrix, null);
            invalidate();
        } else {
            curAngle = targetAngle;
            if (distance < pathLength) {
                pathMeasure.getPosTan(distance, pos, tan);
                targetAngle = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
                matrix.postRotate(curAngle, bm_offsetX, bm_offsetY);
                curX = pos[0] - bm_offsetX;
                curY = pos[1] - bm_offsetY;
                matrix.postTranslate(curX, curY);
                canvas.drawBitmap(bm, matrix, null);
                distance += step;
                invalidate();
            } else {
                matrix.postRotate(curAngle, bm_offsetX, bm_offsetY);
                matrix.postTranslate(curX, curY);
                canvas.drawBitmap(bm, matrix, null);
                if (istouchup)
                    ismovefinished = true;
            }
        }
        if (ismovefinished) {
            ismovefinished = false;
            istouchup = false;
            FastFloodFill fff = new FastFloodFill(canvasBitmap, upColor, paintColor);
            fff.floodFill((int) touchX, (int) touchY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchPath.lineTo(event.getX(), event.getY());
                istouchup = false;
                break;
            case MotionEvent.ACTION_UP:
                touchPath.lineTo(event.getX(), event.getY());
                animPath = new Path(touchPath);
                pathMeasure = new PathMeasure(animPath, false);
                pathLength = pathMeasure.getLength();
                distance = 0;
                curAngle = 0;
                targetAngle = 0;
                touchPath.reset();
                touchPath.moveTo(event.getX(), event.getY());
                upColor = canvasBitmap.getPixel((int) touchX, (int) touchY);
                if (upColor == bgColor)
                    break;
                istouchup = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touchPath.lineTo(event.getX(), event.getY());
                istouchup = false;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        drawPaint.setShader(null);
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void drawImg(int drawable) {
        Rect dest = new Rect(0, 0, getWidth(), getHeight());
        drawCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), drawable), null, dest, drawPaint);
    }

    public void setImg(int pos) {
        img = pos;
    }

    public static int getCountChange() {
        return s_CountChange;
    }

    public static void increaseCountChange() {
        s_CountChange++;
        Log.d("Hoang", "increaseCountChange " + s_CountChange);
    }

    public static void resetCountChange() {
        s_CountChange = 0;
        Log.d("Hoang", "resetCountChange " + s_CountChange);
    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }
}