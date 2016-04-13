package com.jasmartdev.drawingpaintandfill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements OnClickListener {


    private DrawingView drawView;

    private ImageButton currPaint, btn_new, btn_open_bg, btn_save;

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
        btn_new = (ImageButton) findViewById(R.id.btn_new);
        btn_new.setOnClickListener(this);
        btn_open_bg = (ImageButton) findViewById(R.id.btn_open_bg);
        btn_open_bg.setOnClickListener(this);
        btn_save = (ImageButton) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
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
        if (view.getId() == R.id.btn_new) {
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
        } else if (view.getId() == R.id.btn_open_bg) {
            final Dialog seekDialog = new Dialog(this);
            seekDialog.setTitle("Choose background image:");
            seekDialog.setContentView(R.layout.bgimg_chooser);
            BgImgChooseExpandListAdapter ExpAdapter;
            ArrayList<Group> ExpListItems;
            ExpandableListView ExpandList;
            ExpandList = (ExpandableListView) seekDialog.findViewById(R.id.exp_list);
            ExpListItems = SetStandardGroups();
            ExpAdapter = new BgImgChooseExpandListAdapter(seekDialog.getContext(), ExpListItems, seekDialog, drawView);
            ExpandList.setAdapter(ExpAdapter);
            seekDialog.show();
        } else if (view.getId() == R.id.btn_save) {
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

    public ArrayList<Group> SetStandardGroups() {

        String group_names[] = getResources().getStringArray(R.array.img_array);
        ArrayList<Group> list = new ArrayList<Group>();
        ArrayList<Child> ch_list;
        Group gru = new Group();
        gru.setName(group_names[0]);
        TypedArray imgs;
        imgs = getResources().obtainTypedArray(R.array.num_array);
        ch_list = new ArrayList<Child>();
        for (int i = 0; i < imgs.length(); i++) {
            Child ch = new Child();
            ch.setImage(imgs.getResourceId(i, -1));
            ch_list.add(ch);
        }
        gru.setItems(ch_list);
        list.add(gru);
        gru = new Group();
        gru.setName(group_names[1]);
        imgs = getResources().obtainTypedArray(R.array.char_array);
        ch_list = new ArrayList<Child>();
        for (int i = 0; i < imgs.length(); i++) {
            Child ch = new Child();
            ch.setImage(imgs.getResourceId(i, -1));
            ch_list.add(ch);
        }
        gru.setItems(ch_list);
        list.add(gru);
        return list;
    }
}
