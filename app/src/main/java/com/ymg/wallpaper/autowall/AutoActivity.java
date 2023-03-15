package com.ymg.wallpaper.autowall;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.PrefManager;

import java.io.File;

public class AutoActivity extends AppCompatActivity {

    LinearLayout u;
    Toolbar toolbar;
    TextView w;
    TextView x;
    TextView y;

    AlertDialog alertDialog1;
    CharSequence[] values = {" 5 Minutes "," 15 Minutes ", " 30 Minutes "," 60 Minutes ", " 6 Hours "," 12 Hours "};
    PrefManager prf;
    private SharedPreferences q = null;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        prf = new PrefManager(this);

        this.w = findViewById(R.id.textView6);

        this.toolbar = findViewById(R.id.toolbar);
        setTitle("Auto Wallpaper");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.lla);

        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = pref.edit();


        this.u = (LinearLayout) findViewById(R.id.mygallery);

        File[] listFiles = new File(Environment.getExternalStorageDirectory().toString() + "/"+"Pictures"+"/"+getResources().getString(R.string.app_name)).listFiles();

        assert listFiles != null;

        if (listFiles.length != 0) {
            this.w.setVisibility(View.GONE);
            for (int i3 = 0; i3 < listFiles.length; i3++) {
                if (listFiles[i3].getName().endsWith(".jpeg") || listFiles[i3].getName().endsWith(".png") || listFiles[i3].getName().endsWith(".gif") || listFiles[i3].getName().endsWith(".jpg")) {
                    this.u.addView(U(listFiles[i3].getAbsolutePath()));
                }
            }
        } else {
            this.w.setVisibility(View.VISIBLE);
        }

        TextView button = (TextView) findViewById(R.id.btnSetWallpaper);
        this.y = button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {


                try {
                    WallpaperManager.getInstance(AutoActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(AutoActivity.this, Slideshow.class));
                startActivity(intent);

            }
        });
        TextView button2 = (TextView) findViewById(R.id.btnConfigure);
        this.x = button2;
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                CreateAlertDialog();
            }
        });
        S();


    }

    public void W(View r4) {
        Intent intent = new Intent();
        String r0  = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER";
        String r00 = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT";
        ComponentName r1 = new ComponentName(AutoActivity.this, Slideshow.class);
        Class<Slideshow> r2 = Slideshow.class;
        intent.putExtra(r0, r1);
        this.startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void CreateAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Auto Wallpaper Time");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("duration", "300000");
                        editor.commit();
                        break;
                    case 1:
                        SharedPreferences settings1 = getSharedPreferences("preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor1= settings1.edit();
                        editor1.putString("duration", "900000");
                        editor1.commit();
                        break;
                    case 2:
                        SharedPreferences settings2 = getSharedPreferences("preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor2= settings2.edit();
                        editor2.putString("duration", "1800000");
                        editor2.commit();
                        prf.setInt("wallTime",1800000);
                        break;
                    case 3:
                        SharedPreferences settings3 = getSharedPreferences("preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor3= settings3.edit();
                        editor3.putString("duration", "3600000");
                        editor3.commit();
                        prf.setInt("wallTime",3600000);
                        break;
                    case 4:
                        SharedPreferences settings4 = getSharedPreferences("preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor4= settings4.edit();
                        editor4.putString("duration", "21600000");
                        editor4.commit();
                        prf.setInt("wallTime",21600000);
                        break;
                    case 5:
                        SharedPreferences settings5 = getSharedPreferences("preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor5= settings5.edit();
                        editor5.putString("duration", "43200000");
                        editor5.commit();
                        prf.setInt("wallTime",43200000);
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }


    public int R (BitmapFactory.Options options, int i2, int i3) {
        float f2;
        float f3;
        int i4 = options.outHeight;
        int i5 = options.outWidth;
        if (i4 <= i3 && i5 <= i2) {
            return 1;
        }
        if (i5 > i4) {
            f3 = (float) i4;
            f2 = (float) i3;
        } else {
            f3 = (float) i5;
            f2 = (float) i2;
        }
        return Math.round(f3 / f2);
    }

    public void S () {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                return;
            }
            if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 5);
            }
        } catch (Exception e2) {
            Log.e("MainActivity", "Got exception ", e2);
        }
    }

    public Bitmap T (String str, int i2, int i3) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inSampleSize = R(options, i2, i3);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(str, options);
    }

    public View U (String str) {
        Bitmap T = T(str, 300, 500);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(300, 500));
        linearLayout.setGravity(17);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 500));
        imageView.setImageBitmap(T);
        
        linearLayout.addView(imageView);
        return linearLayout;
    }

}