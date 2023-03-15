package com.ymg.wallpaper.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.ymg.wallpaper.BuildConfig;
import com.ymg.wallpaper.Utils.ClickableViewPager;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Models.Wallpaper;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.DBHelper;
import com.ymg.wallpaper.Utils.Methods;
import com.ymg.wallpaper.Utils.PrefManager;
import com.ymg.wallpaper.Utils.VideoLiveWallpaper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class WallpaperDetailsActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout lyt_action;
    private int currentPosition = 0;
    private static int TOTAL_TEXT;
    ClickableViewPager viewPager;
    CustomPagerAdapter customPagerAdapter;
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
    RelativeLayout downloadLayout;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


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


        tvWallpaperViews = findViewById(R.id.tvWallpaperViews);
        tvWallpaperDownloads = findViewById(R.id.tvWallpaperDownloads);
        tvWallpaperSets = findViewById(R.id.tvWallpaperSets);
        tvWallpaperSize = findViewById(R.id.tvWallpaperSize);
        tvWallpaperResolution = findViewById(R.id.tvWallpaperResolution);
        tvWallpaperShare = findViewById(R.id.tvWallpaperShare);
        ivImageView = findViewById(R.id.ivImageView);
        playBtn = findViewById(R.id.playBtn);
        shareLayout = findViewById(R.id.shareLayout);
        downloadLayout = findViewById(R.id.downloadLayout);
        videoView = findViewById(R.id.videoView);

        customPagerAdapter = new CustomPagerAdapter(WallpaperDetailsActivity.this);

        Intent i = getIntent();
        currentPosition = i.getIntExtra("POSITION", 0);
        arrayList = (ArrayList<Wallpaper>) i.getSerializableExtra("array");

        TOTAL_TEXT = (arrayList.size() - 1);
        viewPager.setAdapter(customPagerAdapter);
        viewPager.setCurrentItem(currentPosition, true);
        viewPager.getAdapter().notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(0);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPosition = position;
                changePreviewText(position);
                favToggle(arrayList.get(currentPosition));
                loadBottomSheetData();

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("scroll", "PageScrollStateChanged");
            }
        });

        //apply wallpaper
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (verifyPermissions().booleanValue()) {
                    if (arrayList.get(currentPosition).type.equals("image")) {
                        CreateAlertDialog();
                    } else {
                        //setGif(Config.ADMIN_PANEL_URL + "/images/" + arrayList.get(currentPosition).getImage_url());

                    }
                    if (methods.isNetworkAvailable()) {
                        new MyTask().execute(Constant.URL_SET_COUNT + arrayList.get(currentPosition).getImage_id());
                    }
                }
            }
        });

        //favorite wallpaper
        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbHelper.isFavoritesExist(arrayList.get(currentPosition).image_id)) {
                    dbHelper.deleteFavorites(arrayList.get(currentPosition));
                    Toast.makeText(WallpaperDetailsActivity.this, getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addOneFavorite(arrayList.get(currentPosition));
                    Toast.makeText(WallpaperDetailsActivity.this, getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                }
                favToggle(arrayList.get(currentPosition));
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
                    if (arrayList.get(currentPosition).type.equals("image")){
                        downloadBitmap();
                    }else {
                        downloadGIfs(Config.ADMIN_PANEL_URL + "/images/" + arrayList.get(currentPosition).getImage_url());
                    }
                    if (methods.isNetworkAvailable()) {
                        new MyTask().execute(Constant.URL_DOWNLOAD_COUNT + arrayList.get(currentPosition).getImage_id());
                    }
                }
            }
        });

        loadBottomSheetData();
        initBottomSheet();
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
                            Methods.saveImage(WallpaperDetailsActivity.this, Methods.getBytesFromFile(resource), Methods.createName(imageURL), "image/gif");

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                relativeLayoutLoadMore.setVisibility(View.INVISIBLE);
                                Toast.makeText(WallpaperDetailsActivity.this, "Download Wallpaper Successfully", Toast.LENGTH_SHORT).show();
                                showFullScreenAds();
                            }, 2000);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();

    }

    public Boolean verifyPermissions() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        return false;
    }



    //load bottom sheet data
    private void loadBottomSheetData () {
        if (methods.isNetworkAvailable()){
            new MyTask().execute(Constant.URL_VIEW_COUNT + arrayList.get(currentPosition).getImage_id());
        }
        tvWallpaperViews.setText(Methods.withSuffix((long) arrayList.get(currentPosition).view_count));
        tvWallpaperDownloads.setText(Methods.withSuffix((long) arrayList.get(currentPosition).download_count));
        tvWallpaperSets.setText(Methods.withSuffix((long) arrayList.get(currentPosition).set_count));
        if (arrayList.get(currentPosition).type.equals("image")) {
            tvWallpaperShare.setText(arrayList.get(currentPosition).getCategory_name());
        } else {
            tvWallpaperShare.setText("GIF/IMAGE");
        }

        Glide.with(this)
                .asBitmap()
                .load(Config.ADMIN_PANEL_URL+"/images/"+arrayList.get(currentPosition).getImage_url())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //Bitmap resource = ((BitmapDrawable) ivImageView.getDrawable()).getBitmap();
                        int width = resource.getWidth();
                        int height = resource.getHeight();

                        tvWallpaperResolution.setText(width + " x " + height);

                        double kbBitmapSize = resource.getByteCount() /1024;
                        tvWallpaperSize.setText(kbBitmapSize+"KB");
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                 }
         });
    }

    //favorite state show on swipe
    private void favToggle(Wallpaper wallpaper2) {
        if (this.dbHelper.isFavoritesExist(wallpaper2.image_id)) {
            btn_favorite.setImageResource(R.drawable.ic_baseline_favorite);
        } else {
            btn_favorite.setImageResource(R.drawable.ic_baseline_favorite_border);
        }
    }

    //our custom adapter
    //This Admin panel and WallpaperX app Created by YMG Developers
    class CustomPagerAdapter extends PagerAdapter{

        Context mContext;
        LayoutInflater mLayoutInflater;

        CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.item_viewpager, container, false);

            LinearLayout ll = itemView.findViewById(R.id.ll_viewpager);
            imageView = itemView.findViewById(R.id.iv_full);
            videoView = itemView.findViewById(R.id.videoView);

            Glide.with(mContext)
                    .load(Config.ADMIN_PANEL_URL + "/images/" + arrayList.get(position).getImage_url())
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        getSupportActionBar().hide();
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        getSupportActionBar().show();
                    }
                }
            });

            container.addView(ll, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            return ll;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //view pager on swipe
    public void changePreviewText(int position) {
        currentPosition = position;
        Log.d("Main", "Current position: " + position);
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
        if (arrayList.get(currentPosition).type.equals("image")){
            Glide.with(this)
                    .asBitmap()
                    .load(Config.ADMIN_PANEL_URL+"/images/"+arrayList.get(currentPosition).getImage_url())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
                            intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                            intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
                            startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
                        }
                    });
        }else {
            shareGIfs(Config.ADMIN_PANEL_URL + "/images/" + arrayList.get(currentPosition).getImage_url());
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
                            Methods.shareImage(WallpaperDetailsActivity.this, Methods.getBytesFromFile(resource), Methods.createName(imageURL), "image/gif");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();
    }

    //help to share as image
    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            if (arrayList.get(currentPosition).type.equals("images")){
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "wallpaper" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            }else {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "wallpaper" + System.currentTimeMillis() + ".gif");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                bmpUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    //download image
    private void downloadBitmap() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(Config.ADMIN_PANEL_URL+"/images/"+arrayList.get(currentPosition).getImage_url())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                 }
        });
    }

    //save image to phone storage
    private void saveBitmap(Bitmap bitmap) {
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {


            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ "/" +getString(R.string.app_name));
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            Toast.makeText(WallpaperDetailsActivity.this, "Download Wallpaper Successfully", Toast.LENGTH_SHORT).show();
            try {
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String fileName = UUID.randomUUID() + ".jpg";
            String path = Environment.getExternalStorageDirectory().toString();
            File folder = new File(path + "/" + Environment.DIRECTORY_PICTURES +"/"+ getString(R.string.app_name));
            folder.mkdir();

            file = new File(folder, fileName);
            if (file.exists())
                file.delete();

            try {

                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                //send pictures to gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));


                Toast.makeText(WallpaperDetailsActivity.this, "Download Wallpaper Successfully", Toast.LENGTH_SHORT).show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        relativeLayoutLoadMore.setVisibility(View.GONE);
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

                        Methods.setGifWallpaper(WallpaperDetailsActivity.this, Methods.getBytesFromFile(file), Methods.createName(str), "image/gif");
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return true;
                    }
                }
            }).submit();
        showFullScreenAds();
    }

    public void CreateAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WallpaperDetailsActivity.this);
        builder.setTitle("Choose Option");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        showSetUsOption();
                        break;
                    case 1:
                        Intent intent = new Intent(getApplicationContext(), CropWallpaperActivity.class);
                        intent.putExtra("WALLPAPER_IMAGE_URL", Config.ADMIN_PANEL_URL+"/images/"+arrayList.get(currentPosition).getImage_url());
                        startActivity(intent);
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    //show set us option
    private void showSetUsDialog() {
        final Dialog dialog = new Dialog(WallpaperDetailsActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_set_dialog);

        LinearLayout llSetWallpaper= dialog.findViewById(R.id.llSetWallpaper);
        LinearLayout llCropWallpaper = dialog.findViewById(R.id.llCropWallpaper);
        llSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreenOption();
                dialog.dismiss();
            }
        });
        llCropWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showSetUsOption(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WallpaperDetailsActivity.this);
        builder.setTitle("Set us :");
        builder.setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
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

    //select screen dialog
    private void showScreenOption() {
        final Dialog dialog = new Dialog(WallpaperDetailsActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_option);

        LinearLayout llHome = dialog.findViewById(R.id.llHome);
        LinearLayout llLock = dialog.findViewById(R.id.llLock);
        LinearLayout llBoth = dialog.findViewById(R.id.llBoth);
        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToHomeScreen();
                dialog.dismiss();
            }
        });
        llLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToLockScreen();
                dialog.dismiss();
            }
        });
        llBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWallpaperToBothScreen();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //set wallpaper on Home Screen
    private void setWallpaperToHomeScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(Config.ADMIN_PANEL_URL+"/images/"+arrayList.get(currentPosition).getImage_url())
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperDetailsActivity.this);
                        try {

                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int height = metrics.heightPixels;
                            int width = metrics.widthPixels;
                            Bitmap bitmap = Bitmap.createScaledBitmap(resource,width,height, true);
                            wallpaperManager.getDesiredMinimumWidth();//returned 1280 on s3
                            wallpaperManager.getDesiredMinimumHeight();//also returned 1280 on s3
                            wallpaperManager.suggestDesiredDimensions(width, height);
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
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
        });
    }

    //set wallpaper on Lock Screen
    private void setWallpaperToLockScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(Config.ADMIN_PANEL_URL+"/images/"+arrayList.get(currentPosition).getImage_url())
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperDetailsActivity.this);
                        try {
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int height = metrics.heightPixels;
                            int width = metrics.widthPixels;
                            Bitmap bitmap = Bitmap.createScaledBitmap(resource,width,height, true);
                            wallpaperManager.getDesiredMinimumWidth();//returned 1280 on s3
                            wallpaperManager.getDesiredMinimumHeight();//also returned 1280 on s3
                            wallpaperManager.suggestDesiredDimensions(width, height);
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
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    //set wallpaper on Both Screen
    private void setWallpaperToBothScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(Config.ADMIN_PANEL_URL+"/images/"+arrayList.get(currentPosition).getImage_url())
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperDetailsActivity.this);
                        try {
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int height = metrics.heightPixels;
                            int width = metrics.widthPixels;
                            Bitmap bitmap = Bitmap.createScaledBitmap(resource,width,height, true);
                            wallpaperManager.getDesiredMinimumWidth();//returned 1280 on s3
                            wallpaperManager.getDesiredMinimumHeight();//also returned 1280 on s3
                            wallpaperManager.suggestDesiredDimensions(width, height);
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
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
        });
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

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)this,Manifest.permission.READ_EXTERNAL_STORAGE)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)WallpaperDetailsActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }else {
            ActivityCompat.requestPermissions((Activity)this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
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
        if (!Config.DEVELOPERS_NAME.equals(prf.getString("VDN"))){
            finish();
        }
        super.onResume();
    }
}