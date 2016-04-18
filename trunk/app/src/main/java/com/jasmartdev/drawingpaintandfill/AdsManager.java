package com.jasmartdev.drawingpaintandfill;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdsManager {

    private InterstitialAd adViewInters;
    private Context context;
    private long closeTime;
    private final int ADS_TIME_WAIT = 60000;

    public AdsManager(Context ct, String id) {
        context = ct;
        adViewInters = new InterstitialAd(ct);
        adViewInters.setAdUnitId(id);
        adViewInters.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AdRequest adRequest = new AdRequest.Builder().build();
                adViewInters.loadAd(adRequest);
                closeTime = System.currentTimeMillis();
            }
        });
        closeTime = -1;
    }

    public void showAds() {
        if (System.currentTimeMillis() - closeTime > ADS_TIME_WAIT) {
            if (!adViewInters.isLoaded()) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adViewInters.loadAd(adRequest);
            } else {
                adViewInters.show();
                DrawingView.resetCountChange();
            }
        }
    }
}
