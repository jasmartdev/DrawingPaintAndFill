package com.jasmartdev.drawingpaintandfill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends Activity implements OnClickListener {


    private DrawingView drawView;

    private ImageButton currPaint, draw_btn, fill_btn, erase_btn, new_btn, opacity_btn, open_bg_btn;

    private float smallBrush, mediumBrush, largeBrush;

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
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        draw_btn = (ImageButton) findViewById(R.id.draw_btn);
        draw_btn.setOnClickListener(this);
        drawView.setBrushSize(mediumBrush);
        erase_btn = (ImageButton) findViewById(R.id.erase_btn);
        erase_btn.setOnClickListener(this);
        new_btn = (ImageButton) findViewById(R.id.new_btn);
        new_btn.setOnClickListener(this);
        fill_btn = (ImageButton) findViewById(R.id.fill_btn);
        fill_btn.setOnClickListener(this);
        opacity_btn = (ImageButton) findViewById(R.id.opacity_btn);
        opacity_btn.setOnClickListener(this);
        open_bg_btn = (ImageButton) findViewById(R.id.open_bg_btn);
        open_bg_btn.setOnClickListener(this);
    }

    public void paintClicked(View view) {
        drawView.setErase(false);
        drawView.setPaintAlpha(100);
        drawView.setBrushSize(drawView.getLastBrushSize());
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
        if (view.getId() == R.id.draw_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton small_btn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            small_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setFill(false);
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton medium_btn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            medium_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setFill(false);
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton large_btn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            large_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(false);
                    drawView.setFill(false);
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        } else if (view.getId() == R.id.erase_btn) {
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);
            ImageButton small_btn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            small_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setFill(false);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton medium_btn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            medium_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setFill(false);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton large_btn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            large_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setFill(false);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });
            brushDialog.show();
        } else if (view.getId() == R.id.new_btn) {
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
        } else if (view.getId() == R.id.fill_btn) {
            drawView.setErase(false);
            drawView.setFill(true);
        } else if (view.getId() == R.id.opacity_btn) {
            final Dialog seekDialog = new Dialog(this);
            seekDialog.setTitle("Opacity level:");
            seekDialog.setContentView(R.layout.opacity_chooser);
            final TextView seekTxt = (TextView) seekDialog.findViewById(R.id.opq_txt);
            final SeekBar seekOpq = (SeekBar) seekDialog.findViewById(R.id.opacity_seek);
            seekOpq.setMax(100);
            int currLevel = drawView.getPaintAlpha();
            seekTxt.setText(currLevel + "%");
            seekOpq.setProgress(currLevel);
            seekOpq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekTxt.setText(Integer.toString(progress) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            Button opq_btn = (Button) seekDialog.findViewById(R.id.opq_ok);
            opq_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setPaintAlpha(seekOpq.getProgress());
                    seekDialog.dismiss();
                }
            });
            seekDialog.show();
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
                int width = ((display.getWidth()*30)/100);
                int height = ((display.getHeight()*30)/100);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
                imageView.setLayoutParams(parms);
                imageView.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), imgs.getResourceId(i, -1)));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                final int j = i;
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.drawImg(imgs.getResourceId(j, -1));
                        seekDialog.dismiss();
                    }
                });
                if (layout != null)
                    layout.addView(imageView);
            }
            seekDialog.show();
        }
    }
}
