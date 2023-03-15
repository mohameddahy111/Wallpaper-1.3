package com.ymg.wallpaper.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyPolicyActivity extends AppCompatActivity {

    TextView textView;
    RelativeLayout relativeLayoutLoadMore;
    PrefManager prf;
    InterstitialAd interstitialAd;
    com.facebook.ads.InterstitialAd fbInterstitialAd;
    private final String TAG = PrivacyPolicyActivity.class.getSimpleName();
    //This Admin panel and WallpaperX app Created by YMG Developers

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.policy_privacy));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prf = new PrefManager(this);
        if (prf.getString(Config.ADS).equals("true")){
            loadBannerAds();
            showFullScreenAds();
        }else {
            Log.d(Config.ADS,"ADS IS OFF");
        }

        textView = findViewById(R.id.textView);
        relativeLayoutLoadMore = findViewById(R.id.relativeLayoutLoadMore);

        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        getPrivacyPolicy();
        initCheck();

    }

    private void loadBannerAds () {
        if (prf.getString(Config.ADS_NETWORK).equals("admob")){
            AdView adView = new AdView(this);
            adView.setAdUnitId(prf.getString(Config.ADMOB_BANNER_ID));
            adView.setAdSize(AdSize.BANNER);
            LinearLayout layout = (LinearLayout) findViewById(R.id.adView);
            layout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }else {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this, prf.getString(Config.FACEBOOK_BANNER_ID), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.adView);
            adContainer.addView(adView);
            AdSettings.addTestDevice(Config.DEVICE_ID);
            adView.loadAd();
        }
    }

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

    private void getPrivacyPolicy() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constant.URL_PRIVACY_POLICY, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                relativeLayoutLoadMore.setVisibility(View.GONE);
                try {
                    final String result = response.getString("app_privacy_policy");
                    textView.setText(Html.fromHtml(result));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCheck() {
        if (prf.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            //This Admin panel and WallpaperX app Created by YMG Developers
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}