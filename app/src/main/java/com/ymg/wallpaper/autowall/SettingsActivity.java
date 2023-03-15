package com.ymg.wallpaper.autowall;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ymg.wallpaper.R;

public class SettingsActivity extends PreferenceActivity {


    /* access modifiers changed from: protected */
    public void a() {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 5);
        }
    }

    /* access modifiers changed from: protected */
//    public final void onActivityResult(int i2, int i3, Intent intent) {
//        if (i3 == -1 && i2 == 1) {
//            SharedPreferences.Editor edit = getSharedPreferences("preferences", MODE_PRIVATE).edit();
//            edit.putString(getResources().getString(R.string.preferences_folder_key), intent.getStringExtra("folder"));
//            edit.commit();
//        }
//    }

    /* access modifiers changed from: protected */
//    @Override
//    public void onCreate(final Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
//    }

//    public void onRequestPermissionsResult(int i2, String[] strArr, int[] iArr) {
//    }
}
