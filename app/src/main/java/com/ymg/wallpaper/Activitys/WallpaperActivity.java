package com.ymg.wallpaper.Activitys;

import android.content.Intent;
import android.media.audiofx.DynamicsProcessing;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.ymg.wallpaper.Adapters.AdapterWallpaper;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Models.Wallpaper;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.Methods;
import com.ymg.wallpaper.Utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WallpaperActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout = null;
    RecyclerView recyclerView;
    RelativeLayout lyt_parent;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    private AdapterWallpaper mAdapter;
    private ArrayList<Wallpaper> arrayList;
    RelativeLayout progressBar;
    View lyt_no_item;
    Methods methods;
    PrefManager prf;
    String categoryName;
    String categoryID;
    GridLayoutManager gridLayoutManager;
    InterstitialAd interstitialAd;
    com.facebook.ads.InterstitialAd fbInterstitialAd;
    private final String TAG = WallpaperActivity.class.getSimpleName();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("categoryName");
        categoryID = intent.getStringExtra("categoryID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(categoryName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lyt_parent = findViewById(R.id.lyt_parent);
        lyt_no_item = findViewById(R.id.lyt_no_item);

        methods = new Methods(this);
        prf = new PrefManager(this);

        if (prf.getString(Config.ADS).equals("true")){
            loadBannerAds();
            showFullScreenAds();
        }else {
            Log.d(Config.ADS,"ADS IS OFF");
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        showRefresh(true);

        progressBar = findViewById(R.id.relativeLayoutLoadMore);

        arrayList = new ArrayList<>();
        mAdapter = new AdapterWallpaper(this, arrayList);

        recyclerView = findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(this, prf.getInt("wallpaperColumns"));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mAdapter);

        if (Config.DEVELOPERS_NAME.equals(Constant.DEVELOPERS_NAME)){
            firstLoadData();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore();
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    private void firstLoadData() {
        if (methods.isNetworkAvailable()) {
            itShouldLoadMore = false;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.URL_CATEGORY_DETAIL + "&id=" + categoryID + "&offset=0", null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse (JSONArray response) {

                    showRefresh(false);
                    itShouldLoadMore = true;

                    if (response.length() <= 0) {
                        lyt_no_item.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            lastId = jsonObject.getString(Constant.NO);
                            String id = jsonObject.getString(Constant.IMAGE_ID);
                            String image_url = jsonObject.getString(Constant.IMAGE_URL);
                            String image_name = jsonObject.getString(Constant.IMAGE_NAME);
                            String type = jsonObject.getString(Constant.TYPE);
                            int view_count = jsonObject.getInt(Constant.VIEW_COUNT);
                            int download_count = jsonObject.getInt(Constant.DOWNLOAD_COUNT);
                            int set_count = jsonObject.getInt(Constant.SET_COUNT);

                            String category_id = jsonObject.getString(Constant.CATEGORY_ID);
                            String category_name = jsonObject.getString(Constant.CATEGORY_NAME);
                            String tags = jsonObject.getString(Constant.IMAGE_URL);

                            arrayList.add(new Wallpaper(id, image_name, image_url, type, view_count, download_count, set_count, tags, category_id, category_name));
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse (VolleyError error) {
                    itShouldLoadMore = true;
                    showRefresh(false);
                }
            });

            MyApplication.getInstance().addToRequestQueue(jsonArrayRequest);

        } else {
            showRefresh(false);
            mAdapter = new AdapterWallpaper(this, arrayList);
            recyclerView.setAdapter(mAdapter);
        }

    }

    private void loadMore() {

        itShouldLoadMore = false;
        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.URL_CATEGORY_DETAIL + "&id=" + categoryID + "&offset="+lastId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        showRefresh(false);
                        progressBar.setVisibility(View.GONE);
                        itShouldLoadMore = true;

                        if (response.length() <= 0) {
                            return;
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                lastId = jsonObject.getString(Constant.NO);
                                String id = jsonObject.getString(Constant.IMAGE_ID);
                                String image_url = jsonObject.getString(Constant.IMAGE_URL);
                                String image_name = jsonObject.getString(Constant.IMAGE_NAME);
                                String type = jsonObject.getString(Constant.TYPE);
                                int view_count = jsonObject.getInt(Constant.VIEW_COUNT);
                                int download_count = jsonObject.getInt(Constant.DOWNLOAD_COUNT);
                                int set_count = jsonObject.getInt(Constant.SET_COUNT);

                                String category_id = jsonObject.getString(Constant.CATEGORY_ID);
                                String category_name = jsonObject.getString(Constant.CATEGORY_NAME);
                                String tags = jsonObject.getString(Constant.IMAGE_URL);

                                arrayList.add(new Wallpaper(id, image_name, image_url, type, view_count, download_count, set_count, tags, category_id, category_name));
                                mAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, Constant.DELAY_LOAD_MORE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                showRefresh(false);
                itShouldLoadMore = true;
                isOffline();
            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    private void refreshData() {

        lyt_no_item.setVisibility(View.GONE);
        arrayList.clear();
        mAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firstLoadData();
            }
        }, Constant.DELAY_REFRESH);

    }

    private void isOffline() {
        Snackbar snackBar = Snackbar.make(lyt_parent, "msg_offline", Snackbar.LENGTH_LONG);
        snackBar.setAction("option_retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRefresh(true);
                refreshData();
            }
        });
        snackBar.show();
    }

    private void showRefresh(boolean show) {
        if (show) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, Constant.DELAY_PROGRESS);
        }
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
            //facebook ads
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

    @Override
    public void onResume() {
        gridLayoutManager = new GridLayoutManager(this, prf.getInt("wallpaperColumns"));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        initCheck();
        super.onResume();
    }

    private void initCheck() {
        if (prf.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }

        if (!Config.DEVELOPERS_NAME.equals(prf.getString("VDN"))){
            finish();
        }
    }

}