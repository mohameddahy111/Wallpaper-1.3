package com.ymg.wallpaper.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    PrefManager prf;
    TextView textView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        prf = new PrefManager(this);

        textView = findViewById(R.id.textView);

        initCheck();
        getAllData();
        getData();
    }

    private void getAllData() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constant.URL_LOAD_DATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final String ads_status = response.getString("ads_status");
                    final String ads_type = response.getString("ads_type");
                    final String admob_banner = response.getString("admob_banner");
                    final String admob_inter = response.getString("admob_inter");
                    final String facebook_banner = response.getString("facebook_banner");
                    final String facebook_inter = response.getString("facebook_inter");

                    prf.setString("ADS", "true");
                    prf.setString(Config.ADS_NETWORK,ads_type);
                    prf.setString(Config.ADMOB_BANNER_ID,admob_banner);
                    prf.setString(Config.ADMOB_INTER_ID,admob_inter);
                    prf.setString(Config.FACEBOOK_BANNER_ID,facebook_banner);
                    prf.setString(Config.FACEBOOK_INTER_ID,facebook_inter);

                    Timer myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // If you want to modify a view in your Activity
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                                    finish();

                                }
                            });
                        }
                    }, 3000);

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

    private void getData() {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constant.URL_DATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final String VPN = response.getString("PN");
                    final String VPC = response.getString("PC");
                    final String VDN = response.getString("DN");
                    final String VMD = response.getString("MD");
                    prf.setString("VPN",VPN);
                    prf.setString("VPC",VPC);
                    prf.setString("VDN",VDN);
                    prf.setString("VMD",VMD);

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

    private void initCheck() {
        if (prf.loadNightModeState()==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (Constant.DEVELOPERS_NAME.equals(prf.getString("VDN"))){
            Log.d("STATUS","OK");
        }else {
            finish();
        }
    }
}