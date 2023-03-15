package com.ymg.wallpaper.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.snackbar.Snackbar;
import com.ymg.wallpaper.Adapters.AdapterCategory;
import com.ymg.wallpaper.Models.Category;
import com.ymg.wallpaper.Activitys.MyApplication;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.Methods;
import com.ymg.wallpaper.Activitys.WallpaperActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout = null;
    RecyclerView recyclerView;
    RelativeLayout lyt_parent;
    //DBHelper dbHelper;
    private AdapterCategory mAdapter;
    private ArrayList<Category> arrayList;
    RelativeLayout progressBar;
    private SearchView searchView;
    //private InterstitialAd interstitialAd;
    int counter = 1;
    View lyt_no_item, view;
    Methods tools;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_home, container, false);

        lyt_parent = view.findViewById(R.id.lyt_parent);
        lyt_no_item = view.findViewById(R.id.lyt_no_item);

//       if (Config.ENABLE_RTL_MODE) {
//          lyt_parent.setRotationY(180);
//        }

        tools = new Methods(getActivity());

        setHasOptionsMenu(true);

        //loadInterstitialAd();


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        showRefresh(true);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.relativeLayoutLoadMore);

        arrayList = new ArrayList<>();
        mAdapter = new AdapterCategory(getActivity(), arrayList);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);
        //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.grid_space_wallpaper);
        //recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setAdapter(mAdapter);

        firstLoadData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mAdapter.setOnItemClickListener(new AdapterCategory.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final Category obj, int position) {
                Intent intent = new Intent(getActivity(), WallpaperActivity.class);
                intent.putExtra("categoryID", obj.getCategory_id());
                intent.putExtra("categoryName", obj.getCategory_name());
                startActivity(intent);

                //showInterstitialAd();
            }
        });

        return view;
    }

    private void firstLoadData() {

        if (tools.isNetworkAvailable()) {

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.URL_CATEGORY, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    showRefresh(false);

                    if (response.length() <= 0) {
                        lyt_no_item.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            String category_id = jsonObject.getString(Constant.CATEGORY_ID);
                            String category_name = jsonObject.getString(Constant.CATEGORY_NAME);
                            String category_image = jsonObject.getString(Constant.CATEGORY_IMAGE);
                            String total_wallpaper = jsonObject.getString(Constant.TOTAL_WALLPAPER);

                            arrayList.add(new Category(category_id, category_name, category_image, total_wallpaper));
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showRefresh(false);
                    //Toast.makeText(getActivity(), getResources().getString(R.string.whoops), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), error+"", Toast.LENGTH_SHORT).show();

                }
            });

            MyApplication.getInstance().addToRequestQueue(jsonArrayRequest);

        } else {
            showRefresh(false);
            mAdapter = new AdapterCategory(getActivity(), arrayList);
            recyclerView.setAdapter(mAdapter);
        }

    }

    private void refreshData() {
        if (tools.isNetworkAvailable()) {
            lyt_no_item.setVisibility(View.GONE);
            arrayList.clear();
            mAdapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    firstLoadData();
                }
            }, Constant.DELAY_REFRESH);
        } else {
            showRefresh(false);
            isOffline();
        }
    }

    private void isOffline() {
        Snackbar snackBar = Snackbar.make(lyt_parent, getResources().getString(R.string.no_wallpaper_found), Snackbar.LENGTH_LONG);
        snackBar.setAction(getResources().getString(R.string.option_retry), new View.OnClickListener() {
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
}