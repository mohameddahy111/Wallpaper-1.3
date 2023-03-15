package com.ymg.wallpaper.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ymg.wallpaper.Adapters.AdapterWallpaper;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Models.Wallpaper;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.DBHelper;
import com.ymg.wallpaper.Utils.PrefManager;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {


    private AdapterWallpaper adapterWallpaper;
    DBHelper dbHelper;
    ArrayList<Wallpaper> items;
    //View lyt_no_favorite;
    private RecyclerView recyclerView;
    PrefManager prf;
    LinearLayout noFavoriteLayout;
    InterstitialAd interstitialAd;
    com.facebook.ads.InterstitialAd fbInterstitialAd;
    private final String TAG = FavoriteActivity.class.getSimpleName();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.menu_favorite);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //this.lyt_no_favorite = findViewById(R.id.lyt_not_found);
        this.dbHelper = new DBHelper(this);

//        prf = new PrefManager(this);
//        if (prf.getString(Config.ADS).equals("true")){
//            loadBannerAds();
//            showFullScreenAds();
//        }else {
//            Log.d(Config.ADS,"ADS IS OFF");
//        }

        items = new ArrayList<>();

        noFavoriteLayout=  findViewById(R.id.noFavoriteLayout);
        this.recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, prf.getInt("wallpaperColumns")));
        this.recyclerView.setHasFixedSize(true);
        adapterWallpaper = new AdapterWallpaper(this, items);
        this.adapterWallpaper = adapterWallpaper;
        this.recyclerView.setAdapter(adapterWallpaper);
        displayData();

    }

    private void displayData() {
        ArrayList<Wallpaper> allFavorite = this.dbHelper.getAllFavorite(DBHelper.TABLE_FAVORITE);
        adapterWallpaper.setItems(allFavorite);

        if (allFavorite.size() == 0) {
            noFavoriteLayout.setVisibility(View.VISIBLE);
        } else {
            noFavoriteLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

//    private void loadBannerAds () {
//        if (prf.getString(Config.ADS_NETWORK).equals("admob")){
//            AdView adView = new AdView(this);
//            adView.setAdUnitId(prf.getString(Config.ADMOB_BANNER_ID));
//            adView.setAdSize(AdSize.BANNER);
//            LinearLayout layout = (LinearLayout) findViewById(R.id.adView);
//            layout.addView(adView);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            adView.loadAd(adRequest);
//        }else {
//            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this, prf.getString(Config.FACEBOOK_BANNER_ID), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//            LinearLayout adContainer = (LinearLayout) findViewById(R.id.adView);
//            adContainer.addView(adView);
//            AdSettings.addTestDevice(Config.DEVICE_ID);
//            adView.loadAd();
//        }
//    }

//    private void showFullScreenAds () {
//        if (prf.getString(Config.ADS_NETWORK).equals("admob")) {
//            interstitialAd = new InterstitialAd(this);
//            interstitialAd.setAdUnitId(prf.getString(Config.ADMOB_INTER_ID));
//            AdRequest request = new AdRequest.Builder().build();
//            interstitialAd.loadAd(request);
//            interstitialAd.setAdListener(new AdListener() {
//                public void onAdLoaded () {
//                    if (interstitialAd.isLoaded()) {
//                        interstitialAd.show();
//                    }
//                }
//            });
//        }else {
//            fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, prf.getString(Config.FACEBOOK_INTER_ID));
//            // Create listeners for the Interstitial Ad
//            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
//                @Override
//                public void onInterstitialDisplayed(Ad ad) {
//                    // Interstitial ad displayed callback
//                    Log.e(TAG, "Interstitial ad displayed.");
//                }
//
//                @Override
//                public void onInterstitialDismissed(Ad ad) {
//                    // Interstitial dismissed callback
//                    Log.e(TAG, "Interstitial ad dismissed.");
//                }
//
//                @Override
//                public void onError(Ad ad, AdError adError) {
//                    // Ad error callback
//                    Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
//                }
//
//                @Override
//                public void onAdLoaded(Ad ad) {
//                    // Interstitial ad is loaded and ready to be displayed
//                    Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
//                    // Show the ad
//                    fbInterstitialAd.show();
//                }
//
//                @Override
//                public void onAdClicked(Ad ad) {
//                    // Ad clicked callback
//                    Log.d(TAG, "Interstitial ad clicked!");
//                }
//
//                @Override
//                public void onLoggingImpression(Ad ad) {
//                    // Ad impression logged callback
//                    Log.d(TAG, "Interstitial ad impression logged!");
//                }
//            };
//
//            // For auto play video ads, it's recommended to load the ad
//            // at least 30 seconds before it is shown
//            fbInterstitialAd.loadAd(
//                    fbInterstitialAd.buildLoadAdConfig()
//                            .withAdListener(interstitialAdListener)
//                            .build());
//        }
//    }

    public void onResume() {
        super.onResume();
        displayData();
        recyclerView.setLayoutManager(new GridLayoutManager(this, prf.getInt("wallpaperColumns")));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterWallpaper);
        initCheck();
    }

    private void initCheck() {
        if (prf.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}