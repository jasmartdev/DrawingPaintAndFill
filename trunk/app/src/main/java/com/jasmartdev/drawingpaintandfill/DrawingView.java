package com.jasmartdev.drawingpaintandfill;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000, paintAlpha = 255;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    private boolean fill = false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
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
        if (erase) return;
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (fill) {
                    FastFloodFill fff = new FastFloodFill(canvasBitmap, canvasBitmap.getPixel((int) touchX, (int) touchY), paintColor);
                    fff.floodFill((int) touchX, (int) touchY);
                    break;
                } else
                    drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (fill)
                    break;
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                if (fill)
                    break;
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        if (newColor.startsWith("#")) {
            paintColor = Color.parseColor(newColor);
            drawPaint.setColor(paintColor);
            drawPaint.setShader(null);
        } else {
            int patternID = getResources().getIdentifier(
                    newColor, "drawable", "com.jasmartdev.drawingpaintandfill");
            Bitmap patternBMP = BitmapFactory.decodeResource(getResources(), patternID);
            BitmapShader patternBMPshader = new BitmapShader(patternBMP,
                    Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            drawPaint.setColor(0xFFFFFFFF);
            drawPaint.setShader(patternBMPshader);
        }
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return lastBrushSize;
    }

    public void setErase(boolean isErase) {
        erase = isErase;
        if (erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void setFill(boolean isFill) {
        fill = isFill;
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public int getPaintAlpha() {
        return Math.round((float) paintAlpha / 255 * 100);
    }

    public void setPaintAlpha(int newAlpha) {
        paintAlpha = Math.round((float) newAlpha / 100 * 255);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }

    public void drawImg(int drawable) {
        Rect dest = new Rect(0, 0, getWidth(), getHeight());
        drawCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(), drawable), null, dest, drawPaint);
    }
}