package com.ymg.wallpaper.Utils;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.ymg.wallpaper.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import android.os.StrictMode.VmPolicy.Builder;



public class Methods {

    private Context context;

    // constructor
    public Methods (Context context) {
        this.context = context;
    }

    public static String createName(String str) {
        return str.substring(str.lastIndexOf(47) + 1);
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length <= 2147483647L) {
            int i = (int) length;
            byte[] bArr = new byte[i];
            int i2 = 0;
            FileInputStream fileInputStream = new FileInputStream(file);
            while (i2 < i) {
                try {
                    int read = fileInputStream.read(bArr, i2, i - i2);
                    if (read < 0) {
                        break;
                    }
                    i2 += read;
                } catch (Throwable th) {
                    fileInputStream.close();
                    throw th;
                }
            }
            fileInputStream.close();
            if (i2 >= i) {
                return bArr;
            }
            throw new IOException("Could not completely read file " + file.getName());
        }
        throw new IOException("File is too large!");
    }


    public static void setGifWallpaper(Context context2, byte[] bArr, String str, String str2) {
        PrefManager sharedPref = new PrefManager(context2);
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context2.getString(R.string.app_name));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (file.exists()) {
                File file2 = new File(file, str);
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                fileOutputStream.write(bArr);
                fileOutputStream.flush();
                fileOutputStream.close();
                ContentValues contentValues = new ContentValues();
                //contentValues.put(OneSignalDbContract.NotificationTable.COLUMN_NAME_TITLE, file2.getName());
                contentValues.put("_display_name", file2.getName());
                contentValues.put("description", "");
                contentValues.put("mime_type", str2);
                contentValues.put("date_added", Long.valueOf(System.currentTimeMillis()));
                contentValues.put("datetaken", Long.valueOf(System.currentTimeMillis()));
                contentValues.put("_data", file2.getAbsolutePath());
                context2.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                Constant.gifPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context2.getString(R.string.app_name);
                Constant.gifName = file2.getName();
                try {
                    WallpaperManager.getInstance(context2).clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
                intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context2, SetGIFAsWallpaperService.class));
                context2.startActivity(intent);
                sharedPref.saveGif(Constant.gifPath, Constant.gifName);
                Log.d("GIF_PATH", Constant.gifPath);
                Log.d("GIF_NAME", Constant.gifName);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void saveVideo(Context context, byte[] bArr, String str, String str2) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), context.getString(R.string.app_name));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (file.exists()) {
                File file2 = new File(file, str);
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                fileOutputStream.write(bArr);
                fileOutputStream.flush();
                fileOutputStream.close();
                ContentValues contentValues = new ContentValues(3);
                contentValues.put("_display_name", file2.getName());
                contentValues.put("mime_type", str2);
                contentValues.put("_data", file2.getAbsolutePath());
                context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                StrictMode.setVmPolicy(new Builder().build());
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
                stringBuilder.append("/");
                stringBuilder.append(context.getString(R.string.app_name));
                Constant.gifPath = stringBuilder.toString();
                Constant.gifName = file2.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static String getJSONString(String url) {
        String jsonString = null;
        HttpURLConnection linkConnection = null;
        try {
            URL linkurl = new URL(url);
            linkConnection = (HttpURLConnection) linkurl.openConnection();
            int responseCode = linkConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream linkinStream = linkConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int j = 0;
                while ((j = linkinStream.read()) != -1) {
                    baos.write(j);
                }
                byte[] data = baos.toByteArray();
                jsonString = new String(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (linkConnection != null) {
                linkConnection.disconnect();
            }
        }
        return jsonString;
    }

    public static void setAsGIFWallPaper(Context context, String str) {
        try {
            WallpaperManager.getInstance(context).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (DeviceDetectUtil.isMiUi()) {
//            Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
//            intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, str));
//            intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
//            context.startActivity(intent);
//            return;
//        }
        Intent intent2 = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
        intent2.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, str));
        intent2.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
        context.startActivity(intent2);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public int getScreenHeight() {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        point.y = display.getHeight();

        return point.y;
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static String withSuffix(long j) {
        if (j < 1000) {
            return "" + j;
        }
        double d = (double) j;
        int log = (int) (Math.log(d) / Math.log(1000.0d));
        double pow = Math.pow(1000.0d, (double) log);
        Double.isNaN(d);
        return String.format("%.1f%c", Double.valueOf(d / pow), Character.valueOf("KMGTPE".charAt(log - 1)));
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static void saveImage(Context context, byte[] bytes, String imgName, String extension) {
        FileOutputStream fos;
        try {
            //File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name));

            //File customDownloadDirectory = new File(externalStoragePublicDirectory, context.getString(R.string.app_name));

            if (!file.exists()) {
                file.mkdirs();
            }
            if (file.exists()) {
                File file2 = new File(file, imgName);
                fos = new FileOutputStream(file2);
                fos.write(bytes);
                fos.flush();
                fos.close();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, file2.getName());
                values.put(MediaStore.Images.Media.DISPLAY_NAME, file2.getName());
                values.put(MediaStore.Images.Media.DESCRIPTION, "");
                values.put(MediaStore.Images.Media.MIME_TYPE, extension);
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DATA, file2.getAbsolutePath());

                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareImage(Context context, byte[] bytes, String imgName, String extension) {
        FileOutputStream fos;
        try {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File customDownloadDirectory = new File(externalStoragePublicDirectory, context.getString(R.string.app_name));

            if (!customDownloadDirectory.exists()) {
                customDownloadDirectory.mkdirs();
            }
            if (customDownloadDirectory.exists()) {
                File file = new File(customDownloadDirectory, imgName);
                fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.flush();
                fos.close();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, file.getName());
                values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
                values.put(MediaStore.Images.Media.DESCRIPTION, "");
                values.put(MediaStore.Images.Media.MIME_TYPE, extension);
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                context.startActivity(Intent.createChooser(share, "Share Image"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
