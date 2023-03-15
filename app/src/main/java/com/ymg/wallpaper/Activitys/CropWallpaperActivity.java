package com.ymg.wallpaper.Activitys;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.PrefManager;

import java.io.IOException;

public class CropWallpaperActivity extends AppCompatActivity {

    private CropImageView mCropImageView;
    String str_image;
    Toolbar toolbar;
    Bitmap bitmap = null;
    private InterstitialAd interstitialAd;
    com.facebook.ads.InterstitialAd fbInterstitialAd;
    CharSequence[] names = {" Home Screen "," Lock Screen ", " Both Screen "};
    AlertDialog alertDialog;
    RelativeLayout relativeLayoutLoadMore;
    RelativeLayout rootLayout;
    private final String TAG = WallpaperDetailsActivity.class.getSimpleName();
    PrefManager prf;
    FloatingActionButton buttonSetWallpaper;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        prf = new PrefManager(this);

        Intent i = getIntent();
        str_image = i.getStringExtra("WALLPAPER_IMAGE_URL");

        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        rootLayout = findViewById(R.id.rootLayout);
        relativeLayoutLoadMore = findViewById(R.id.relativeLayoutLoadMore);
        buttonSetWallpaper = findViewById(R.id.buttonSetWallpaper);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        ImageLoader.getInstance().loadImage(str_image, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                mCropImageView.setImageBitmap(arg2);
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {

            }
        });

        buttonSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                showSetUsOption();
            }
        });
    }

    public void showSetUsOption(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CropWallpaperActivity.this);
        builder.setTitle("Set us :");
        builder.setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        setWallpaperToHomeScreen();
                        break;
                    case 1:
                        setWallpaperToLockScreen();
                        break;
                    case 2:
                        setWallpaperToBothScreen();
                        break;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    //set wallpaper on Home Screen
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setWallpaperToHomeScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        bitmap = mCropImageView.getCroppedImage();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(CropWallpaperActivity.this);
        try {
            wallpaperManager.setBitmap(bitmap,null, true, WallpaperManager.FLAG_SYSTEM);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(rootLayout,"Wallpaper was set successfully",Snackbar.LENGTH_LONG).show();
                    relativeLayoutLoadMore.setVisibility(View.GONE);
                    showFullScreenAds();
                }
            }, Constant.DELAY_SET_WALLPAPER);
        } catch (IOException e) {
            e.printStackTrace();
            relativeLayoutLoadMore.setVisibility(View.GONE);
        }

    }

    //set wallpaper on Lock Screen
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setWallpaperToLockScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        bitmap = mCropImageView.getCroppedImage();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(CropWallpaperActivity.this);
        try {
            wallpaperManager.setBitmap(bitmap,null, true, WallpaperManager.FLAG_LOCK);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(rootLayout,"Wallpaper was set successfully",Snackbar.LENGTH_LONG).show();
                    relativeLayoutLoadMore.setVisibility(View.GONE);
                    showFullScreenAds();
                }
            }, Constant.DELAY_SET_WALLPAPER);
        } catch (IOException e) {
            e.printStackTrace();
            relativeLayoutLoadMore.setVisibility(View.GONE);
        }

    }

    //set wallpaper on Both Screen
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setWallpaperToBothScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        bitmap = mCropImageView.getCroppedImage();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(CropWallpaperActivity.this);
        try {
            wallpaperManager.setBitmap(bitmap);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(rootLayout,"Wallpaper was set successfully",Snackbar.LENGTH_LONG).show();
                    relativeLayoutLoadMore.setVisibility(View.GONE);
                    showFullScreenAds();
                }
            }, Constant.DELAY_SET_WALLPAPER);
        } catch (IOException e) {
            e.printStackTrace();
            relativeLayoutLoadMore.setVisibility(View.GONE);
        }
    }

    //ads network
    private void showFullScreenAds () {
        if (prf.getString(Config.ADS_NETWORK).equals("admob")) {
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(prf.getString(Config.ADMOB_INTER_ID));
            AdRequest request = new AdRequest.Builder().build();
            interstitialAd.loadAd(request);
            interstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded () {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            });
        }else {
            fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, prf.getString(Config.FACEBOOK_INTER_ID));
            // Create listeners for the Interstitial Ad
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                    Log.e(TAG, "Interstitial ad displayed.");
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    Log.e(TAG, "Interstitial ad dismissed.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                    // Show the ad
                    fbInterstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d(TAG, "Interstitial ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d(TAG, "Interstitial ad impression logged!");
                }
            };

            // For auto play video ads, it's recommended to load the ad
            // at least 30 seconds before it is shown
            fbInterstitialAd.loadAd(
                    fbInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
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
}