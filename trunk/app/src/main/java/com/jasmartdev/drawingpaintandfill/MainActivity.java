package com.jasmartdev.drawingpaintandfill;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends FragmentActivity implements OnClickListener {


    private DrawingView drawView;
    private DrawPagerAdapter padapter;
    private ViewPager mViewPager;
    private ImageButton currPaint, btn_new, btn_open_bg, btn_save, btn_effect;
    private InterstitialAd adViewInters;
    private int cur_page;
    public static int s_cur_group = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        DrawingView.setupDrawing();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        padapter = new DrawPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(padapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {

            @Override
            public void onPageSelected(int position) {
                initDraw();
                if ((DrawingView.getCountNew() % 3 == 0) && adViewInters.isLoaded()) {
                    adViewInters.show();
                }
            }
        });
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        btn_new = (ImageButton) findViewById(R.id.btn_new);
        btn_new.setOnClickListener(this);
        btn_open_bg = (ImageButton) findViewById(R.id.btn_open_bg);
        btn_open_bg.setOnClickListener(this);
        btn_save = (ImageButton) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_effect = (ImageButton) findViewById(R.id.btn_effect);
        btn_effect.setOnClickListener(this);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        adViewInters = new InterstitialAd(this);
        adViewInters.setAdUnitId("ca-app-pub-5633074162966218/5227681085");
        adViewInters.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });
        cur_page = mViewPager.getCurrentItem();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Hoang", "Main onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Hoang", "Main onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Hoang", "Main onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Hoang", "Main onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Hoang", "Main onstop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Hoang", "Main onDestroy");
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
                    if (!adViewInters.isLoaded()) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        adViewInters.loadAd(adRequest);
                    }
                    if ((drawView.getCountNew() % 3 == 0) && adViewInters.isLoaded()) {
                        adViewInters.show();
                    }
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
            seekDialog.setTitle("Choose image");
            seekDialog.setContentView(R.layout.bgimg_chooser);
            BgImgChooseExpandListAdapter ExpAdapter;
            ArrayList<Group> ExpListItems;
            ExpandableListView ExpandList;
            ExpandList = (ExpandableListView) seekDialog.findViewById(R.id.exp_list);
            ExpListItems = SetStandardGroups();
            ExpAdapter = new BgImgChooseExpandListAdapter(seekDialog.getContext(), ExpListItems, seekDialog, mViewPager, padapter);
            ExpandList.setAdapter(ExpAdapter);
            seekDialog.show();
            if (!adViewInters.isLoaded()) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adViewInters.loadAd(adRequest);
            }
            if ((drawView.getCountNew() % 3 == 0) && adViewInters.isLoaded()) {
                adViewInters.show();
            }
        } else if (view.getId() == R.id.btn_save) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/drawing");
                    myDir.mkdirs();
                    String fname = timeStamp + ".png";
                    File file = new File(myDir, fname);
                    if (file.exists()) file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        drawView.getCanvasBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
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
        } else if (view.getId() == R.id.btn_effect) {
            final Dialog effectDialog = new Dialog(this);
            effectDialog.setTitle("Choose effect");
            effectDialog.setContentView(R.layout.effect_chooser);
            ImageButton eff1 = (ImageButton) effectDialog.findViewById(R.id.effect_brush1);
            eff1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrush(R.drawable.brush);
                    drawView.invalidate();
                    effectDialog.dismiss();
                }
            });
            ImageButton eff2 = (ImageButton) effectDialog.findViewById(R.id.effect_brush2);
            eff2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawView.setBrush(R.drawable.fill);
                    drawView.invalidate();
                    effectDialog.dismiss();
                }
            });
            effectDialog.show();
        }
    }

    public TypedArray getChildImages(int pos) {
        switch (pos) {
            case 0:
                return getResources().obtainTypedArray(R.array.num_array);
            case 1:
                return getResources().obtainTypedArray(R.array.char_array);
            default:
                return null;
        }
    }

    public ArrayList<Group> SetStandardGroups() {

        String group_names[] = getResources().getStringArray(R.array.img_array);
        ArrayList<Group> list = new ArrayList<Group>();
        ArrayList<Child> ch_list;
        Group gru;
        for (int i = 0; i < group_names.length; i++) {
            gru = new Group();
            gru.setName(group_names[i]);
            TypedArray imgs;
            imgs = getChildImages(i);
            ch_list = new ArrayList<Child>();
            for (int j = 0; j < imgs.length(); j++) {
                Child ch = new Child();
                ch.setImage(imgs.getResourceId(j, -1));
                ch_list.add(ch);
            }
            gru.setItems(ch_list);
            list.add(gru);
        }
        return list;
    }

    private void initDraw() {
        Log.d("Hoang", "initDraw drawView:" + drawView + " cur_page " + cur_page + " mViewPager.getCurrentItem() " + mViewPager.getCurrentItem());
        if (drawView == null || cur_page != mViewPager.getCurrentItem()) {
            int index = mViewPager.getCurrentItem();
            SlideFragment sfm = (SlideFragment) padapter.getRegisteredFragment(index);
            drawView = sfm.getDrawView();
            cur_page = mViewPager.getCurrentItem();
            Log.d("Hoang", "initDraw 222 drawView:" + drawView + " sfm " + sfm + " cur_page " + cur_page);
        }
    }
}
