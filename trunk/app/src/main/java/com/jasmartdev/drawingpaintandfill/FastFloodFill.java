package com.jasmartdev.drawingpaintandfill;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.LinkedList;
import java.util.Queue;

public class FastFloodFill {

    protected Bitmap image = null;
    protected int width = 0;
    protected int height = 0;
    protected int[] pixels = null;
    protected int fillColor = 0;
    protected int startColor;
    private final int bgColor = Define.BGCOLOR;
    protected boolean[] pixelsChecked;
    protected Queue<FloodFillRange> ranges;

    public FastFloodFill(Bitmap img) {
        copyImage(img);
    }

    public FastFloodFill(Bitmap img, int targetColor, int newColor) {
        useImage(img);
        setFillColor(newColor);
        setTargetColor(targetColor);
    }

    public void setTargetColor(int targetColor) {
        startColor = targetColor;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int value) {
        fillColor = value;
    }

    public Bitmap getImage() {
        return image;
    }

    public void copyImage(Bitmap img) {
        width = img.getWidth();
        height = img.getHeight();

        image = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(image);
        canvas.drawBitmap(img, 0, 0, null);

        pixels = new int[width * height];

        image.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }

    public void useImage(Bitmap img) {
        width = img.getWidth();
        height = img.getHeight();
        image = img;
        pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }

    protected void prepare() {
        pixelsChecked = new boolean[pixels.length];
        ranges = new LinkedList<FloodFillRange>();
    }

    public void floodFill(int x, int y) {
        prepare();
        int startPixel = pixels[(width * y) + x];
        startColor = startPixel;
        LinearFill(x, y);
        FloodFillRange range;
        while (ranges.size() > 0) {
            range = ranges.remove();
            int downPxIdx = (width * (range.Y + 1)) + range.startX;
            int upPxIdx = (width * (range.Y - 1)) + range.startX;
            int upY = range.Y - 1;
            int downY = range.Y + 1;
            for (int i = range.startX; i <= range.endX; i++) {
                if (range.Y > 0 && (!pixelsChecked[upPxIdx])
                        && CheckPixel(upPxIdx))
                    LinearFill(i, upY);
                if (range.Y < (height - 1) && (!pixelsChecked[downPxIdx])
                        && CheckPixel(downPxIdx))
                    LinearFill(i, downY);
                downPxIdx++;
                upPxIdx++;
            }
        }
        image.setPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }

    protected void LinearFill(int x, int y) {
        int lFillLoc = x;
        int pxIdx = (width * y) + x;
        while (true) {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;
            lFillLoc--;
            pxIdx--;
            if (lFillLoc < 0 || (pixelsChecked[pxIdx]) || !CheckPixel(pxIdx)) {
                break;
            }
        }
        lFillLoc++;
        int rFillLoc = x;
        pxIdx = (width * y) + x;
        while (true) {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;
            rFillLoc++;
            pxIdx++;
            if (rFillLoc >= width || pixelsChecked[pxIdx] || !CheckPixel(pxIdx)) {
                break;
            }
        }
        rFillLoc--;
        FloodFillRange r = new FloodFillRange(lFillLoc, rFillLoc, y);
        ranges.offer(r);
    }

    protected boolean CheckPixel(int px) {
        return (bgColor != pixels[px]);
    }

    protected class FloodFillRange {
        public int startX;
        public int endX;
        public int Y;

        public FloodFillRange(int startX, int endX, int y) {
            this.startX = startX;
            this.endX = endX;
            this.Y = y;
        }
    }
}
