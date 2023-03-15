package com.ymg.wallpaper.autowall;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class cccc {

    public enum a {
        CROP,
        FIT
    }

    public static int a(int i2, int i3, int i4, int i5, a aVar) {
        float f2 = ((float) i2) / ((float) i3);
        float f3 = ((float) i4) / ((float) i5);
        return aVar == a.FIT ? f2 > f3 ? i2 / i4 : i3 / i5 : f2 > f3 ? i3 / i5 : i2 / i4;
    }

    public static String b(String str) {
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf <= 0 || lastIndexOf >= str.length() - 1) {
            return null;
        }
        return str.substring(lastIndexOf + 1).toLowerCase();
    }

    public static Bitmap c(Resources resources, int i2, int i3, int i4, a aVar) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, i2, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = a(options.outWidth, options.outHeight, i3, i4, aVar);
        return BitmapFactory.decodeResource(resources, i2, options);
    }

    public static Bitmap d(String str, int i2, int i3, a aVar) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = a(options.outWidth, options.outHeight, i2, i3, aVar);
        return BitmapFactory.decodeFile(str, options);
    }

    public static void e(Collection<File> collection, File file, FileFilter fileFilter) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    e(collection, file2, fileFilter);
                } else {
                    Collections.addAll(collection, file.listFiles(fileFilter));
                }
            }
        }
    }

    public static File[] f(File file, boolean z, FileFilter fileFilter) {
        if (!z) {
            return file.listFiles(fileFilter);
        }
        LinkedList linkedList = new LinkedList();
        e(linkedList, file, fileFilter);
        return (File[]) linkedList.toArray(new File[linkedList.size()]);
    }

    public static Bitmap g(Bitmap bitmap, int i2, Matrix matrix) {
        if (i2 == 0 || bitmap == null) {
            return bitmap;
        }
        if (matrix == null) {
            matrix = new Matrix();
        }
        matrix.setRotate((float) i2, ((float) bitmap.getWidth()) / 2.0f, ((float) bitmap.getHeight()) / 2.0f);
        try {
            Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (bitmap == createBitmap) {
                return bitmap;
            }
            bitmap.recycle();
            return createBitmap;
        } catch (OutOfMemoryError e2) {
            Log.e("BitmapUtil", "Got oom exception ", e2);
            return bitmap;
        }
    }

    public static Bitmap h(Matrix matrix, Bitmap bitmap, int i2, int i3, boolean z) {
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        float f2 = width / height;
        float f3 = (float) i2;
        float f4 = (float) i3;
        float f5 = f3 / f4;
        float f6 = f4 / height;
        float f7 = f3 / width;
        if ((!z || f6 >= f7) && ((z && f6 >= f7) || f2 <= f5)) {
            f6 = f7;
        }
        if (f6 == 0.0f || (f6 >= 0.9f && f6 <= 1.0f)) {
            matrix = null;
        } else {
            matrix.setScale(f6, f6);
        }
        if (matrix == null) {
            return bitmap;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return createBitmap;
    }

    public static Bitmap i(Matrix matrix, Bitmap bitmap, int i2, int i3, boolean z, boolean z2) {
        if (z2) {
            bitmap = h(matrix, bitmap, i2, i3, true);
        }
        int width = bitmap.getWidth() - i2;
        int height = bitmap.getHeight() - i3;
        if ((!z || z2) && (width < 0 || height < 0)) {
            Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            int max = Math.max(0, width / 2);
            int max2 = Math.max(0, height / 2);
            Rect rect = new Rect(max, max2, Math.min(i2, bitmap.getWidth()) + max, Math.min(i3, bitmap.getHeight()) + max2);
            int width2 = (i2 - rect.width()) / 2;
            int height2 = (i3 - rect.height()) / 2;
            canvas.drawBitmap(bitmap, rect, new Rect(width2, height2, i2 - width2, i3 - height2), (Paint) null);
            bitmap.recycle();
            return createBitmap;
        }
        Bitmap h2 = h(matrix, bitmap, i2, i3, false);
        Bitmap createBitmap2 = Bitmap.createBitmap(h2, Math.max(0, h2.getWidth() - i2) / 2, Math.max(0, h2.getHeight() - i3) / 2, i2, i3);
        h2.recycle();
        return createBitmap2;
    }
}
