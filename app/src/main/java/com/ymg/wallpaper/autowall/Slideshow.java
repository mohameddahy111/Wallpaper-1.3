package com.ymg.wallpaper.autowall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.ymg.wallpaper.R;

import java.io.File;
import java.io.FileFilter;

public class Slideshow extends WallpaperService {

    /* renamed from: d  reason: collision with root package name */
    public static final FileFilter f2742d = new a();

    /* renamed from: c  reason: collision with root package name */
    private final Handler f2743c = new Handler();

    static class a implements FileFilter {
        a() {
        }

        public boolean accept(File file) {
            String b;
            if (!file.isDirectory() && (b = cccc.b(file.getName())) != null) {
                return b.equals("jpg") || b.equals("jpeg") || b.equals("png") || b.equals("gif");
            }
            return false;
        }
    }

    class b extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
        private final Paint a;
        private final Matrix b = new Matrix();

        /* renamed from: c  reason: collision with root package name */
        private final GestureDetector f2744c;

        /* renamed from: d  reason: collision with root package name */
        private final Runnable f2745d = new a();

        /* renamed from: e  reason: collision with root package name */
        private int f2746e = 0;

        /* renamed from: f  reason: collision with root package name */
        private int f2747f = 0;

        /* renamed from: g  reason: collision with root package name */
        private int f2748g = 0;

        /* renamed from: h  reason: collision with root package name */
        private int f2749h = 0;

        /* renamed from: i  reason: collision with root package name */
        private float f2750i = 0.0f;

        /* renamed from: j  reason: collision with root package name */
        private float f2751j = 0.0f;

        /* renamed from: k  reason: collision with root package name */
        private boolean f2752k = false;

        /* renamed from: l  reason: collision with root package name */
        private String f2753l = null;

        /* renamed from: m  reason: collision with root package name */
        private int f2754m = -1;

        /* renamed from: n  reason: collision with root package name */
        private long f2755n = 0;

        /* renamed from: o  reason: collision with root package name */
        private boolean f2756o = true;
        private BroadcastReceiver p;
        private SharedPreferences q = null;
        private int r = 0;
        private boolean s = false;
        private boolean t = false;
        private boolean u = false;
        private boolean v = false;
        private boolean w = false;
        private boolean x = false;
        private boolean y = false;

        class a implements Runnable {
            a() {
            }

            public void run() {
                if (b.this.r > 0) {
                    b.this.h();
                }
            }
        }

        /* renamed from: com.digidevs.litwallz.autowall.Slideshow$b$b  reason: collision with other inner class name */
        class C0055b extends GestureDetector.SimpleOnGestureListener {
            C0055b(Slideshow slideshow) {
            }

            public boolean onDoubleTap(MotionEvent motionEvent) {
                try {
                    if (b.this.x) {
                        b.this.f2755n = 0;
                        b.this.j(false, true);
                        return true;
                    }
                } catch (Exception e2) {
                    Log.e("WAR", String.valueOf(e2));
                }
                return false;
            }
        }

        class c extends BroadcastReceiver {
            c() {
            }

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.MEDIA_MOUNTED") || action.equals("android.intent.action.MEDIA_CHECKING")) {
                    b.this.f2756o = true;
                    b.this.setTouchEventsEnabled(true);
                    b.this.h();
                    return;
                }
                b.this.f2756o = false;
                b.this.setTouchEventsEnabled(false);
                Slideshow.this.f2743c.removeCallbacks(b.this.f2745d);
            }
        }

        class d extends BroadcastReceiver {
            d() {
            }

            public void onReceive(Context context, Intent intent) {
                System.out.println("android.intent.action.SCREEN_ON");
                if (b.this.y) {
                    b.this.f2755n = 0;
                    b.this.h();
                }
            }
        }

        b() {
            super();
            Paint paint = new Paint();
            this.a = paint;
            try {
                paint.setColor(-1);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setAntiAlias(true);
                paint.setTextSize(18.0f);
                SharedPreferences sharedPreferences = Slideshow.this.getSharedPreferences("preferences", MODE_PRIVATE);
                this.q = sharedPreferences;
                sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                onSharedPreferenceChanged(this.q, null);
                setOffsetNotificationsEnabled(true);
                setTouchEventsEnabled(true);
            } catch (Exception e2) {
                Log.e("WAR", String.valueOf(e2));
            }
            this.f2744c = new GestureDetector(Slideshow.this, new C0055b(Slideshow.this));
        }

        private Bitmap k() {
            int i2;
            Bitmap bitmap;
            Slideshow.this.getResources().getDisplayMetrics();
            String string = Slideshow.this.getString(R.string.no_photos_found_1);
            String string2 = Slideshow.this.getString(R.string.no_photos_found_2);
            String string3 = Slideshow.this.getString(R.string.no_photos_found_3);
            if (o() == 2) {
                bitmap = l(R.drawable.placeholder);
                i2 = (int) (((double) (bitmap.getHeight() / 10)) * 8.6d);
                string2 = string2 + " " + string3;
                string3 = "";
            } else {
                bitmap = l(R.drawable.placeholder);
                i2 = (bitmap.getHeight() / 10) * 7;
            }
            int height = bitmap.getHeight() / 40;
            Bitmap.Config config = bitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            Bitmap copy = bitmap.copy(config, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(Color.rgb(255, 255, 255));
            paint.setTextSize((float) height);
            paint.setShadowLayer(1.0f, 0.0f, 1.0f, -1);
            Rect rect = new Rect();
            Rect rect2 = new Rect();
            Rect rect3 = new Rect();
            paint.getTextBounds(string, 0, string.length(), rect);
            paint.getTextBounds(string2, 0, string2.length(), rect2);
            paint.getTextBounds(string3, 0, string3.length(), rect3);
            canvas.drawText(string, (float) ((copy.getWidth() - rect.width()) / 2), (float) i2, paint);
            int i3 = i2 + height;
            canvas.drawText(string2, (float) ((copy.getWidth() - rect2.width()) / 2), (float) (i3 + 10), paint);
            canvas.drawText(string3, (float) ((copy.getWidth() - rect3.width()) / 2), (float) (i3 + height + 20), paint);
            return copy;
        }

        private Bitmap l(int i2) {
            return m(cccc.c(Slideshow.this.getResources(), i2, this.f2748g, this.f2749h, cccc.a.FIT));
        }

        private Bitmap m(Bitmap bitmap) {
            int i2 = 0;
            boolean z2 = this.v;
            int i3 = z2 ? this.f2748g : this.f2746e;
            int i4 = z2 ? this.f2749h : this.f2747f;
            if (bitmap == null) {
                return Bitmap.createBitmap(i3, i4, Bitmap.Config.ARGB_8888);
            }
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (this.t) {
                int i5 = Slideshow.this.getResources().getConfiguration().orientation;
                if (width > height && i5 == 1) {
                    i2 = 90;
                } else if (height > width && i5 == 2) {
                    i2 = -90;
                }
                bitmap = cccc.g(bitmap, i2, this.b);
            }
            return (width == i3 && height == i4) ? bitmap : cccc.i(this.b, bitmap, i3, i4, true, this.u);
        }

        private Bitmap n(String str) {
            return m(cccc.d(str, this.f2748g, this.f2749h, cccc.a.FIT));
        }

        /* access modifiers changed from: protected */
        public void g() {
            try {
                if (Build.VERSION.SDK_INT < 23) {
                    return;
                }
                if (Slideshow.this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED || Slideshow.this.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Slideshow.this.getBaseContext(), SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Slideshow.this.getApplication().startActivity(intent);
                }
            } catch (Exception e2) {
                Log.e("Slideshow", "Got exception ", e2);
            }
        }

        /* access modifiers changed from: package-private */
        public void h() {
            j(false, false);
        }

        /* access modifiers changed from: package-private */
        public void i(boolean z2) {
            j(z2, false);
        }

        /* access modifiers changed from: package-private */
        public void j(boolean z2, boolean z3) {
            Bitmap bitmap;
            int i2;
            int random;
            try {
                String externalStorageState = Environment.getExternalStorageState();
                if (externalStorageState.equals("mounted") || externalStorageState.equals("mounted_ro")) {
                    try {
                        Canvas canvas = null;
                        int i3 = 0;
                        if (this.f2753l == null || (this.r > 0 && this.f2755n < System.currentTimeMillis() - ((long) this.r))) {
                            File[] f2 = cccc.f(new File(Environment.getExternalStorageDirectory().toString() + "/"+Environment.DIRECTORY_PICTURES+"/"+getResources().getString(R.string.app_name)), this.w, Slideshow.f2742d);
                            if (f2 != null && f2.length >= 1) {
                                int length = f2.length;
                                if (this.s) {
                                    int i4 = this.f2754m;
                                    do {
                                        random = (int) (Math.random() * ((double) length));
                                        this.f2754m = random;
                                        if (length <= 1) {
                                            break;
                                        }
                                    } while (random == i4);
                                } else {
                                    int i5 = this.f2754m + 1;
                                    this.f2754m = i5;
                                    if (i5 >= length) {
                                        this.f2754m = 0;
                                    }
                                }
                                String absolutePath = f2[this.f2754m].getAbsolutePath();
                                this.f2753l = absolutePath;
                                bitmap = n(absolutePath);
                            } else if (z3) {
                                Intent intent = new Intent(Slideshow.this.getBaseContext(), SettingsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Slideshow.this.getApplication().startActivity(intent);
                                return;
                            } else {
                                bitmap = k();
                                this.f2753l = "default";
                            }
                            this.f2755n = System.currentTimeMillis();
                        } else {
                            bitmap = z2 ? this.f2753l.equals("default") ? k() : n(this.f2753l) : null;
                        }
                        SurfaceHolder surfaceHolder = getSurfaceHolder();
                        if (bitmap != null) {
                            try {
                                if (this.v) {
                                    i3 = 0 - ((int) (((float) this.f2746e) * this.f2750i));
                                    i2 = 0 - ((int) (((float) this.f2747f) * this.f2751j));
                                } else {
                                    i2 = 0;
                                }
                                try {
                                    canvas = surfaceHolder.lockCanvas();
                                    canvas.drawColor(-16777216);
                                    canvas.drawBitmap(bitmap, (float) i3, (float) i2, this.a);
                                    bitmap.recycle();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            } catch (Throwable th) {
                                if (0 != 0) {
                                    surfaceHolder.unlockCanvasAndPost(null);
                                }
                                throw th;
                            }
                        }
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                        Slideshow.this.f2743c.removeCallbacks(this.f2745d);
                        if (this.f2752k) {
                            Slideshow.this.f2743c.postDelayed(this.f2745d, 15000);
                        }
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (Exception e4) {
                Log.e("drawFrame", "Got exception ", e4);
            }
        }

        public int o() {
            Point point = new Point();
            ((WindowManager) Slideshow.this.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
            return point.y > point.x ? 1 : 2;
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            try {
                super.onCreate(surfaceHolder);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
                intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
                intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
                intentFilter.addAction("android.intent.action.MEDIA_EJECT");
                intentFilter.addAction("android.intent.action.MEDIA_NOFS");
                intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
                intentFilter.addAction("android.intent.action.MEDIA_SHARED");
                intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
                intentFilter.addDataScheme("file");
                c cVar = new c();
                this.p = cVar;
                Slideshow.this.registerReceiver(cVar, intentFilter);
                Slideshow.this.registerReceiver(new d(), new IntentFilter("android.intent.action.SCREEN_ON"));
                setTouchEventsEnabled(this.f2756o);
                g();
            } catch (Exception e2) {
                Log.e("WAR", String.valueOf(e2));
            }
        }

        public void onDestroy() {
            super.onDestroy();
            Slideshow.this.f2743c.removeCallbacks(this.f2745d);
            this.q.unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onOffsetsChanged(float f2, float f3, float f4, float f5, int i2, int i3) {
            try {
                this.f2750i = f2;
                this.f2751j = f3;
                i(true);
            } catch (Exception e2) {
                Log.e("WAR", String.valueOf(e2));
            }
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            try {
                Resources resources = Slideshow.this.getResources();
                if (str == null) {
                    sharedPreferences.getString(resources.getString(R.string.preferences_folder_key), resources.getString(R.string.preferences_folder_default));
                    this.r = Integer.valueOf(sharedPreferences.getString(resources.getString(R.string.preferences_duration_key), resources.getString(R.string.preferences_duration_default))).intValue();
                    this.s = sharedPreferences.getBoolean(resources.getString(R.string.preferences_random_key), Boolean.valueOf(resources.getString(R.string.preferences_random_default)).booleanValue());
                    this.t = sharedPreferences.getBoolean(resources.getString(R.string.preferences_rotate_key), Boolean.valueOf(resources.getString(R.string.preferences_rotate_default)).booleanValue());
                    this.u = sharedPreferences.getBoolean(resources.getString(R.string.preferences_fit_in_screen_key), Boolean.valueOf(resources.getString(R.string.preferences_fit_in_screen_default)).booleanValue());
                    this.v = sharedPreferences.getBoolean(resources.getString(R.string.preferences_scroll_key), Boolean.valueOf(resources.getString(R.string.preferences_scroll_default)).booleanValue());
                    this.w = sharedPreferences.getBoolean(resources.getString(R.string.preferences_recurse_key), Boolean.valueOf(resources.getString(R.string.preferences_recurse_default)).booleanValue());
                    this.x = sharedPreferences.getBoolean(resources.getString(R.string.preferences_doubletap_key), Boolean.valueOf(resources.getString(R.string.preferences_doubletap_default)).booleanValue());
                    this.y = sharedPreferences.getBoolean(resources.getString(R.string.preferences_screen_awake_key), Boolean.valueOf(resources.getString(R.string.preferences_screen_awake_default)).booleanValue());
                } else if (str.equals(resources.getString(R.string.preferences_folder_key))) {
                    sharedPreferences.getString(str, resources.getString(R.string.preferences_folder_default));
                    this.f2754m = -1;
                } else if (str.equals(resources.getString(R.string.preferences_duration_key))) {
                    this.r = Integer.parseInt(sharedPreferences.getString(str, resources.getString(R.string.preferences_duration_default)));
                    return;
                } else if (str.equals(resources.getString(R.string.preferences_random_key))) {
                    this.s = sharedPreferences.getBoolean(str, Boolean.valueOf(resources.getString(R.string.preferences_random_default)).booleanValue());
                    return;
                } else if (str.equals(resources.getString(R.string.preferences_rotate_key))) {
                    this.t = sharedPreferences.getBoolean(str, Boolean.valueOf(resources.getString(R.string.preferences_rotate_default)).booleanValue());
                    return;
                } else if (str.equals(resources.getString(R.string.preferences_fit_in_screen_key))) {
                    this.u = sharedPreferences.getBoolean(str, Boolean.valueOf(resources.getString(R.string.preferences_fit_in_screen_default)).booleanValue());
                    return;
                } else if (str.equals(resources.getString(R.string.preferences_scroll_key))) {
                    boolean z2 = sharedPreferences.getBoolean(str, Boolean.valueOf(resources.getString(R.string.preferences_scroll_default)).booleanValue());
                    this.v = z2;
                    if (!z2) {
                        return;
                    }
                } else if (str.equals(resources.getString(R.string.preferences_recurse_key))) {
                    this.w = sharedPreferences.getBoolean(str, Boolean.valueOf(resources.getString(R.string.preferences_recurse_default)).booleanValue());
                    return;
                } else if (str.equals(resources.getString(R.string.preferences_doubletap_key))) {
                    this.x = sharedPreferences.getBoolean(str, Boolean.valueOf(resources.getString(R.string.preferences_doubletap_default)).booleanValue());
                    return;
                } else if (str.equals(resources.getString(R.string.preferences_screen_awake_key))) {
                    this.y = sharedPreferences.getBoolean(str, Boolean.valueOf(resources.getString(R.string.preferences_screen_awake_default)).booleanValue());
                    return;
                } else {
                    return;
                }
                this.f2755n = 0;
            } catch (Exception e2) {
                Log.e("WAR", String.valueOf(e2));
            }
        }

        public void onSurfaceChanged(SurfaceHolder surfaceHolder, int i2, int i3, int i4) {
            try {
                super.onSurfaceChanged(surfaceHolder, i2, i3, i4);
                this.f2746e = i3;
                this.f2747f = i4;
                this.f2748g = i3 * 2;
                this.f2749h = i4;
                i(true);
            } catch (Exception e2) {
                Log.e("WAR", String.valueOf(e2));
            }
        }

        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
            this.f2755n = 0;
        }

        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder) {
            super.onSurfaceDestroyed(surfaceHolder);
            this.f2752k = false;
            Slideshow.this.f2743c.removeCallbacks(this.f2745d);
        }

        public void onTouchEvent(MotionEvent motionEvent) {
            super.onTouchEvent(motionEvent);
            this.f2744c.onTouchEvent(motionEvent);
        }

        public void onVisibilityChanged(boolean z2) {
            try {
                this.f2752k = z2;
                if (z2) {
                    h();
                } else {
                    Slideshow.this.f2743c.removeCallbacks(this.f2745d);
                }
            } catch (Exception e2) {
                Log.e("WAR", String.valueOf(e2));
            }
        }
    }

    public void onCreate() {
        super.onCreate();
    }

    public Engine onCreateEngine() {
        return new b();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}