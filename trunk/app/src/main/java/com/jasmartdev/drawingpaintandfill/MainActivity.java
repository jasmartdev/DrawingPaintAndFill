package com.jasmartdev.drawingpaintandfill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements OnClickListener {


    private DrawingView drawView;

    private ImageButton currPaint, new_btn, open_bg_btn, save_btn;

    private TypedArray imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        drawView = (DrawingView) findViewById(R.id.drawing);
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        new_btn = (ImageButton) findViewById(R.id.new_btn);
        new_btn.setOnClickListener(this);
        open_bg_btn = (ImageButton) findViewById(R.id.open_bg_btn);
        open_bg_btn.setOnClickListener(this);
        save_btn = (ImageButton) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);
    }

    public void paintClicked(View view) {
        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) view;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.new_btn) {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if (view.getId() == R.id.open_bg_btn) {
            final Dialog seekDialog = new Dialog(this);
            seekDialog.setTitle("Choose background image:");
            seekDialog.setContentView(R.layout.bgimg_chooser);
            final LinearLayout layout = (LinearLayout) seekDialog.findViewById(R.id.bgimg_layout);
            imgs = getResources().obtainTypedArray(R.array.img_array);
            for (int i = 0; i < imgs.length(); i++) {
                final ImageView imageView = new ImageView(this);
                imageView.setId(i);
                imageView.setPadding(2, 2, 2, 2);
                Display display = getWindowManager().getDefaultDisplay();
                int width = ((display.getWidth() * 30) / 100);
                int height = ((display.getHeight() * 30) / 100);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
                imageView.setLayoutParams(parms);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                option.inDither = false;
                option.inPurgeable = true;
                imageView.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), imgs.getResourceId(i, -1), option));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                final int j = i;
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.startNew();
                        drawView.drawImg(imgs.getResourceId(j, -1));
                        seekDialog.dismiss();
                    }
                });
                if (layout != null)
                    layout.addView(imageView);
            }
            seekDialog.show();
        } else if (view.getId() == R.id.save_btn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    drawView.setDrawingCacheEnabled(true);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/drawing");
                    myDir.mkdirs();
                    String fname = timeStamp + ".png";
                    File file = new File(myDir, fname);
                    if (file.exists()) file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        drawView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        drawView.destroyDrawingCache();
                        Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved to storage!", Toast.LENGTH_SHORT);
                        savedToast.show();
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + myDir.getAbsolutePath())));
                    } catch (Exception e) {
                        Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Drawing could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                        e.printStackTrace();
                    }
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }
}
