package com.ymg.wallpaper.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.ymg.wallpaper.BuildConfig;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Models.Wallpaper;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.ClickableViewPager;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.DBHelper;
import com.ymg.wallpaper.Utils.Methods;
import com.ymg.wallpaper.Utils.PrefManager;
import com.ymg.wallpaper.Utils.TextureVideoView;
import com.ymg.wallpaper.Utils.VideoLiveWallpaper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class VideoWallpaperActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout lyt_action;
    private int currentPosition = 0;
    private static int TOTAL_TEXT;
    ClickableViewPager viewPager;
    WallpaperDetailsActivity.CustomPagerAdapter customPagerAdapter;
    ArrayList<Wallpaper> arrayList;
    RelativeLayout relativeLayoutLoadMore;
    RelativeLayout rootLayout;
    File file;
    AlertDialog alertDialog1;
    AlertDialog alertDialog;
    Methods methods;
    DBHelper dbHelper;
    TextView tvApply;
    TextView tvWallpaperViews;
    TextView tvWallpaperDownloads;
    TextView tvWallpaperSets;
    TextView tvWallpaperSize;
    TextView tvWallpaperResolution;
    TextView tvWallpaperShare;
    ImageView ivImageView;
    ImageView imageView;
    ImageButton btn_favorite;
    ImageButton btnShare;
    ImageButton btnDownload;
    PrefManager prf;
    InterstitialAd interstitialAd;
    com.facebook.ads.InterstitialAd fbInterstitialAd;
    private final String TAG = WallpaperDetailsActivity.class.getSimpleName();
    CharSequence[] values = {" Set Wallpaper "," Crop Wallpaper "};
    CharSequence[] names = {" Home Screen "," Lock Screen ", " Both Screen "};
    private int STORAGE_PERMISSION_CODE = 1;
    String StringDownload = "";
    File FileDownload;
    File DeleteFile1;
    VideoView videoView;
    RelativeLayout playBtn;
    RelativeLayout shareLayout;
    RelativeLayout favoriteLayout;
    PhotoView photoView;

    String wallpaperName,wallpaperImage,wallpaperType, wallpaperId;
    int wallpaperViews,wallpaperDownloads,wallpaperSets;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        wallpaperName = intent.getStringExtra("NAME");
        wallpaperImage = intent.getStringExtra("IMAGE");
        wallpaperType = intent.getStringExtra("TYPE");
        wallpaperViews = intent.getIntExtra("VIEW",0);
        wallpaperDownloads = intent.getIntExtra("DOWNLOAD",0);
        wallpaperSets = intent.getIntExtra("SETS",0);
        wallpaperId = intent.getStringExtra("IMAGE_ID");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        dbHelper = new DBHelper(this);
        methods = new Methods(this);
        prf = new PrefManager(this);

        lyt_action = findViewById(R.id.lyt_action);
        viewPager = findViewById(R.id.viewPager);
        tvApply = findViewById(R.id.tvApply);
        btn_favorite = findViewById(R.id.btn_favorite);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        relativeLayoutLoadMore = findViewById(R.id.relativeLayoutLoadMore);
        rootLayout = findViewById(R.id.rootLayout);
        photoView = findViewById(R.id.photoView);

        tvWallpaperViews = findViewById(R.id.tvWallpaperViews);
        tvWallpaperDownloads = findViewById(R.id.tvWallpaperDownloads);
        tvWallpaperSets = findViewById(R.id.tvWallpaperSets);
        tvWallpaperSize = findViewById(R.id.tvWallpaperSize);
        tvWallpaperResolution = findViewById(R.id.tvWallpaperResolution);
        tvWallpaperShare = findViewById(R.id.tvWallpaperShare);
        ivImageView = findViewById(R.id.ivImageView);
        playBtn = findViewById(R.id.playBtn);
        shareLayout = findViewById(R.id.shareLayout);
        favoriteLayout = findViewById(R.id.favoriteLayout);
        videoView = findViewById(R.id.videoView);

        favoriteLayout.setVisibility(View.GONE);


        getVideoData();


        //apply wallpaper
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (verifyPermissions().booleanValue()) {

                    if (wallpaperImage.endsWith(".gif")){
                        setGif(Config.ADMIN_PANEL_URL + "/images/" + wallpaperImage);
                    }else {
                        setVideoWallpaper();
                    }

                    if (methods.isNetworkAvailable()) {
                        new VideoWallpaperActivity.MyTask().execute(Constant.URL_SET_COUNT + wallpaperId);
                    }
                }
            }
        });

        //share wallpaper
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                shareWallpaper();
            }
        });

        //download wallpaper
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (verifyPermissions().booleanValue()) {
                    if (wallpaperImage.endsWith(".gif")){
                        downloadGIfs(Config.ADMIN_PANEL_URL + "/images/" + wallpaperImage);

                    }else {
                        downloadVideos(Config.ADMIN_PANEL_URL + "/images/" + wallpaperImage);

                    }
                    if (methods.isNetworkAvailable()) {
                        new VideoWallpaperActivity.MyTask().execute(Constant.URL_DOWNLOAD_COUNT + wallpaperId);
                    }
                }
            }
        });

        loadBottomSheetData();
        initBottomSheet();
    }

    private void getVideoData() {

        if (wallpaperImage.endsWith(".gif")){
            Glide.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/images/" + wallpaperImage)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(photoView);
        }else {

            videoView.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.GONE);
            String path = Config.ADMIN_PANEL_URL+"/images/"+wallpaperImage;
            Uri uri = Uri.parse(path);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
            //videoView.setMediaController(null);
            //videoView.setScaleType(TextureVideoView.ScaleType.BOTTOM);
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    Log.d("video", "setOnErrorListener ");
                    return true;
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(0.0f, 0.0f);
                }
            });

        }

    }

    public Boolean verifyPermissions() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        return false;
    }

    private void downloadVideos(String imageURL) {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Methods.saveImage(VideoWallpaperActivity.this, Methods.getBytesFromFile(resource), Methods.createName(imageURL), "video/mp4");

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                relativeLayoutLoadMore.setVisibility(View.INVISIBLE);
                                Toast.makeText(VideoWallpaperActivity.this, "Download Wallpaper Successfully", Toast.LENGTH_SHORT).show();
                                showFullScreenAds();
                            }, 2000);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();

    }

    private void downloadGIfs(String imageURL) {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Methods.saveImage(VideoWallpaperActivity.this, Methods.getBytesFromFile(resource), Methods.createName(imageURL), "image/gif");

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                relativeLayoutLoadMore.setVisibility(View.INVISIBLE);
                                Toast.makeText(VideoWallpaperActivity.this, "Download Wallpaper Successfully", Toast.LENGTH_SHORT).show();
                                showFullScreenAds();
                            }, 2000);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();

    }


    //load bottom sheet data
    private void loadBottomSheetData () {
        if (methods.isNetworkAvailable()){
            new VideoWallpaperActivity.MyTask().execute(Constant.URL_VIEW_COUNT + wallpaperId);
        }
        tvWallpaperViews.setText(Methods.withSuffix((long) wallpaperViews));
        tvWallpaperDownloads.setText(Methods.withSuffix((long) wallpaperDownloads));
        tvWallpaperSets.setText(Methods.withSuffix((long) wallpaperSets));
        tvWallpaperShare.setText("GIF/IMAGE");

    }


    //show bottom layout
    private void initBottomSheet() {

        RelativeLayout relativeLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(relativeLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ((RelativeLayout) findViewById(R.id.lyt_expand)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    //share wallpaper code
    private void shareWallpaper() {
        if (wallpaperImage.endsWith(".gif")){
            shareGIfs(Config.ADMIN_PANEL_URL + "/images/" + wallpaperImage);
        }else {
            shareGIfs(Config.ADMIN_PANEL_URL + "/images/" + wallpaperImage);
        }
    }

    private void shareGIfs(String imageURL) {
        Glide.with(this)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Methods.shareImage(VideoWallpaperActivity.this, Methods.getBytesFromFile(resource), Methods.createName(imageURL), "image/gif");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();
    }


    //for video
    public void setVideoWallpaper() {
        this.prf.saveGif(Constant.gifPath, Constant.gifName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.gifPath);
        stringBuilder.append("/");
        stringBuilder.append(Constant.gifName);
        String stringBuilder2 = stringBuilder.toString();
        Objects.requireNonNull(stringBuilder2);
        File file = new File(stringBuilder2);
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(getFilesDir());
        stringBuilder3.append("/file.mp4");
        copyFile(file, new File(stringBuilder3.toString()));
        VideoLiveWallpaper.setToWallPaper(this);
    }

    public void copyFile(File fromFile, File toFile) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannelInput = null;
        FileChannel fileChannelOutput = null;
        try {
            fileInputStream = new FileInputStream(fromFile);
            fileOutputStream = new FileOutputStream(toFile);
            fileChannelInput = fileInputStream.getChannel();
            fileChannelOutput = fileOutputStream.getChannel();
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (fileChannelInput != null)
                    fileChannelInput.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
                if (fileChannelOutput != null)
                    fileChannelOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //set gif as wallpaper
    public void setGif(final String str) {

        Glide.with(this)
                .download(str.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(GlideException glideException, Object obj, Target<File> target, boolean z) {
                        return false;
                    }
                    public boolean onResourceReady(File file, Object obj, Target<File> target, DataSource dataSource, boolean z) {
                        try {

                            Methods.setGifWallpaper(VideoWallpaperActivity.this, Methods.getBytesFromFile(file), Methods.createName(str), "image/gif");
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return true;
                        }
                    }
                }).submit();
        showFullScreenAds();
    }



    //my task
    private static class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return Methods.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                Log.d("TAG", "no data found!");
            } else {

                try {

                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("result");
                    //This Admin panel and WallpaperX app Created by YMG Developers
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private MyAsyncTask() {
        }

        MyAsyncTask(VideoWallpaperActivity activityVideo, VideoWallpaperActivity wallpaperDetailsActivity) {
            this();
        }

        protected Void doInBackground(Void... voidArr) {
            try {
                Context context = VideoWallpaperActivity.this;
                Methods.saveVideo(context, Methods.getBytesFromFile(FileDownload), Methods.createName(VideoWallpaperActivity.this.StringDownload), "video/mp4");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public void loadvideo(final String str) {
        if (verifyPermissions().booleanValue()) {

            Glide.with((FragmentActivity) this).download(str).listener(new RequestListener<File>() {
                public boolean onLoadFailed(GlideException glideException, Object obj, Target<File> target, boolean z) {
                    return false;
                }

                public boolean onResourceReady(File file, Object obj, Target<File> target, DataSource dataSource, boolean z) {
                    VideoWallpaperActivity.this.FileDownload = file;
                    VideoWallpaperActivity.this.StringDownload = str;
                    new VideoWallpaperActivity.MyAsyncTask(VideoWallpaperActivity.this, null).execute(new Void[0]);
                    return true;
                }
            }).submit();
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

    @Override
    protected void onResume() {
        loadvideo(Config.ADMIN_PANEL_URL + "/images/" + wallpaperImage);
        getVideoData();
        super.onResume();
    }
}