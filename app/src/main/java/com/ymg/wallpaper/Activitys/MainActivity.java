package com.ymg.wallpaper.Activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
//This Admin panel and Wallpaperx app Created by YMG Developers
//https://codecanyon.net/user/ymg-developer
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.onesignal.OneSignal;
import com.ymg.wallpaper.BuildConfig;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Fragments.CategoryFragment;
import com.ymg.wallpaper.Fragments.LatestFragment;
import com.ymg.wallpaper.Fragments.LiveWallpaperFragment;
import com.ymg.wallpaper.Fragments.PopularFragment;
import com.ymg.wallpaper.Fragments.RandomFragment;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.PrefManager;
import com.ymg.wallpaper.autowall.AutoActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    PrefManager prf;
    ViewPagerAdapter adapter;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    ChipNavigationBar chipNavigationBar;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialSearchView searchView;
    private int STORAGE_PERMISSION_CODE = 1;
    ViewPager viewPager;
    private static final String ONESIGNAL_APP_ID = "f8bf3c7f-fb54-4b9e-9be9-db9c149b57f2";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ymg.wallpaper.R.layout.activity_main);
        Toolbar toolbar = findViewById(com.ymg.wallpaper.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        MobileAds.initialize(this);
        AudienceNetworkAds.initialize(this);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        prf = new PrefManager(this);

        //Permission Dialog
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("IS_FIRST_TIME", true)) {

            givePermission();

            sharedPreferences.edit().putBoolean("IS_FIRST_TIME", false).apply();
        }

        if (prf.getString(Config.ADS).equals("true")){
            loadBannerAds();
        }else {
            //Toast.makeText(this, "ads is off", Toast.LENGTH_SHORT).show();
        }

        drawerLayout = findViewById(com.ymg.wallpaper.R.id.drawer_layout);
        navigationView = findViewById(com.ymg.wallpaper.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, com.ymg.wallpaper.R.string.navigation_drawer_open, com.ymg.wallpaper.R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(com.ymg.wallpaper.R.drawable.ic_action_action);


        ((SwitchCompat) this.navigationView.getMenu().findItem(com.ymg.wallpaper.R.id.nav_night).getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    prf.setNightModeState(true);
                    onResume();
                }else {
                    prf.setNightModeState(false);
                    onResume();
                }
            }
        });
        if (prf.loadNightModeState()){
            ((SwitchCompat) this.navigationView.getMenu().findItem(com.ymg.wallpaper.R.id.nav_night).getActionView()).setChecked(true);
        }

        searchView = (MaterialSearchView) findViewById(com.ymg.wallpaper.R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("wallpaper", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });


        viewPager = (ViewPager) findViewById(com.ymg.wallpaper.R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LatestFragment());
        adapter.addFragment(new LiveWallpaperFragment());
        adapter.addFragment(new RandomFragment());
        adapter.addFragment(new PopularFragment());
        adapter.addFragment(new CategoryFragment());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        chipNavigationBar = findViewById(com.ymg.wallpaper.R.id.chipNavigation);

        if (savedInstanceState==null){
            chipNavigationBar.setItemSelected(com.ymg.wallpaper.R.id.home,true);
        }

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                int i3;
                if (i == 0) {

                    i3 = com.ymg.wallpaper.R.id.home;
                } else if (i == 1) {

                    i3 = com.ymg.wallpaper.R.id.activity;
                } else if (i == 2) {

                    i3 = com.ymg.wallpaper.R.id.favorites;
                } else if (i == 3) {

                    i3 = com.ymg.wallpaper.R.id.settings;
                } else if (i == 4) {

                    i3 = com.ymg.wallpaper.R.id.category;
                } else {
                    return;
                }

                chipNavigationBar.setItemSelected(i3,true);
                chipNavigationBar.setItemEnabled(i3,true);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected (int i) {
                switch (i) {
                    case com.ymg.wallpaper.R.id.home:
                        viewPager.setCurrentItem(0);
                        break;
                    case com.ymg.wallpaper.R.id.activity:
                        viewPager.setCurrentItem(1);
                        break;
                    case com.ymg.wallpaper.R.id.favorites:
                        viewPager.setCurrentItem(2);
                        break;
                    case com.ymg.wallpaper.R.id.settings:
                        viewPager.setCurrentItem(3);
                        break;
                    case com.ymg.wallpaper.R.id.category:
                        viewPager.setCurrentItem(4);
                        break;
                    default:
                        return;
                }
            }
        });

        createFolder();
    }

    private void createFolder() {
        File myDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/"+Environment.DIRECTORY_PICTURES+"/"+getResources().getString(R.string.app_name));
        if(!myDirectory.exists()) {
            myDirectory.mkdirs();
        }

    }

    private void loadBannerAds () {
        if (prf.getString(Config.ADS_NETWORK).equals("admob")){
            AdView adView = new AdView(this);
            adView.setAdUnitId(prf.getString(Config.ADMOB_BANNER_ID));
            adView.setAdSize(AdSize.BANNER);
            LinearLayout layout = (LinearLayout) findViewById(com.ymg.wallpaper.R.id.adView);
            layout.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            //This Admin panel and WallpaperX app Created by YMG Developers
        }else {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this, prf.getString(Config.FACEBOOK_BANNER_ID), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(com.ymg.wallpaper.R.id.adView);
            adContainer.addView(adView);
            AdSettings.addTestDevice(Config.DEVICE_ID);
            adView.loadAd();
        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    public void onResume() {
        super.onResume();
        initCheck();
        createFolder();
    }

    private void initCheck() {
        if (prf.loadNightModeState()==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.ymg.wallpaper.R.menu.main, menu);

        MenuItem item = menu.findItem(com.ymg.wallpaper.R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (menuItem.getItemId() == R.id.nav_auto){
            startActivity(new Intent(MainActivity.this, AutoActivity.class));
        }
        if (menuItem.getItemId() == com.ymg.wallpaper.R.id.nav_favotite){
            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
        }
        if (menuItem.getItemId() == com.ymg.wallpaper.R.id.nav_rate){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
            }catch (ActivityNotFoundException ex){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
            }
        }
        if (menuItem.getItemId() == com.ymg.wallpaper.R.id.nav_share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(com.ymg.wallpaper.R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
            startActivity(Intent.createChooser(intent,"share via"));
        }
        if (menuItem.getItemId() == com.ymg.wallpaper.R.id.nav_contact){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getResources().getString(com.ymg.wallpaper.R.string.app_name)});
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(com.ymg.wallpaper.R.string.app_name));
            i.putExtra(Intent.EXTRA_TEXT   , getResources().getString(com.ymg.wallpaper.R.string.email_body_text));
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        if (menuItem.getItemId() == com.ymg.wallpaper.R.id.nav_about){
            showAbout();
        }
        if (menuItem.getItemId() == com.ymg.wallpaper.R.id.nav_settings){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            finish();
        }
        if (menuItem.getItemId() == com.ymg.wallpaper.R.id.nav_night){
            return false;
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void showAbout() {
        final Dialog customDialog;
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(com.ymg.wallpaper.R.layout.dialog_about, null);
        customDialog = new Dialog(this, com.ymg.wallpaper.R.style.DialogCustomTheme);
        customDialog.setContentView(customView);
        TextView tvClose = customDialog.findViewById(com.ymg.wallpaper.R.id.tvClose);
        TextView tvVersion = customDialog.findViewById(com.ymg.wallpaper.R.id.tvVersion);

        tvVersion.setText("Version "+ BuildConfig.VERSION_NAME);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }


    private void givePermission() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_permission);

        TextView tv_policy_decline =dialog.findViewById(R.id.tv_policy_decline);
        TextView tv_give_per_dialog =dialog.findViewById(R.id.tv_give_per_dialog);

        tv_policy_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_give_per_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyPermissions().booleanValue()){
                    Log.d("TAG","DONE");
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)this, Manifest.permission.READ_EXTERNAL_STORAGE)){

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ActivityCompat.requestPermissions((Activity)MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                            verifyPermissions().booleanValue();
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

    public Boolean verifyPermissions() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isOpen()){
            drawerLayout.close();
        }else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.logo)
                    .setTitle(getString(R.string.app_name))
                    .setMessage("Are you sure you want to close this App?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }



}