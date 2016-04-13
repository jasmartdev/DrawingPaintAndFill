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
    void FloodFill2(int x, int y)
    {
        Point pt = new Point(x, y);
        Queue<Point> q = new LinkedList<Point>();
        q.add(pt);
        while (q.size() > 0) {
            Point n = q.poll();
            if (image.getPixel(n.x, n.y) != startColor)
                continue;

            Point w = n, e = new Point(n.x + 1, n.y);

            while ((w.x > 0) && (image.getPixel(w.x, w.y) == startColor)) {
                image.setPixel(w.x, w.y, fillColor);
                if ((w.y > 0) && (image.getPixel(w.x, w.y - 1) == startColor))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < image.getHeight() - 1) && (image.getPixel(w.x, w.y + 1) == startColor))
                    q.add(new Point(w.x, w.y + 1));

                w.x--;
            }

            while ((e.x < image.getWidth() - 1) && (image.getPixel(e.x, e.y) == startColor)) {
                image.setPixel(e.x, e.y, fillColor);
                if ((e.y > 0) && (image.getPixel(e.x, e.y - 1) == startColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < image.getHeight() - 1) && (image.getPixel(e.x, e.y + 1) == startColor))
                    q.add(new Point(e.x, e.y + 1));

                e.x++;
            }
        }
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
        return (startColor == pixels[px]);
    }
    protected boolean CheckPixel2(int px) {
        return (0xFF000000 != pixels[px]);
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
