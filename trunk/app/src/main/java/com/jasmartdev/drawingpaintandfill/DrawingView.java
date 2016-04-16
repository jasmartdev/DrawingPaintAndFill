package com.jasmartdev.drawingpaintandfill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFFFFFFFF;
    private final int bgColor = Define.BGCOLOR;
    private int downColor = Define.BGCOLOR, upColor = Define.BGCOLOR;
    private boolean isDrag = false;
    private float down_x;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int imgPos = -1;
    public static int s_CountNew = 0;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downColor = canvasBitmap.getPixel((int) touchX, (int) touchY);
                down_x = touchX;
                break;
            case MotionEvent.ACTION_UP:
                upColor = canvasBitmap.getPixel((int) touchX, (int) touchY);
                if (upColor == bgColor)
                    break;
                FastFloodFill fff = new FastFloodFill(canvasBitmap, upColor, paintColor);
                fff.floodFill((int) touchX, (int) touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                float dis = getResources().getDimension(R.dimen.drag_distance_x);
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
        s_CountNew++;
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
        Log.d("Hoang", "s_CountNew "+s_CountNew);
    }

    public void drawImg(int drawable) {
        Rect dest = new Rect(0, 0, getWidth(), getHeight());
        drawCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), drawable), null, dest, drawPaint);
    }

    public void setImgPos(int pos) {
        imgPos = pos;
    }

    public int getImgPos() {
        return imgPos;
    }

    public int getCountNew()
    {
        return s_CountNew;
    }
}