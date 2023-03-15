package com.ymg.wallpaper.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.snackbar.Snackbar;
import com.ymg.wallpaper.Adapters.AdapterWallpaper;
import com.ymg.wallpaper.Models.Wallpaper;
import com.ymg.wallpaper.Activitys.MyApplication;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.Constant;
import com.ymg.wallpaper.Utils.Methods;
import com.ymg.wallpaper.Utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RandomFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout = null;
    RecyclerView recyclerView;
    RelativeLayout lyt_parent;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    private AdapterWallpaper mAdapter;
    private ArrayList<Wallpaper> arrayList;
    int counter = 1;
    RelativeLayout progressBar;
    View lyt_no_item;
    Methods methods;
    PrefManager prf;

    public RandomFragment () {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lyt_parent = view.findViewById(R.id.lyt_parent);
        lyt_no_item = view.findViewById(R.id.lyt_no_item);

        //if (Config.ENABLE_RTL_MODE) {
            //lyt_parent.setRotationY(180);
        //}

        methods = new Methods(getActivity());
        prf = new PrefManager(getActivity());

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        showRefresh(true);

        progressBar = view.findViewById(R.id.relativeLayoutLoadMore);

        arrayList = new ArrayList<>();
        mAdapter = new AdapterWallpaper(getActivity(), arrayList);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), prf.getInt("wallpaperColumns")));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mAdapter);

        firstLoadData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        if (itShouldLoadMore) {
                            loadMore();
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        return view;
    }

    private void firstLoadData() {
      if (methods.isNetworkAvailable()) {
         itShouldLoadMore = false;


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.URL_RANDOM_WALLPAPER + 0, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                showRefresh(false);
                itShouldLoadMore = true;

                if (response.length() <= 0) {
                    lyt_no_item.setVisibility(View.VISIBLE);
                    return;
                }

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        lastId = jsonObject.getString(Constant.NO);
                        String id = jsonObject.getString(Constant.IMAGE_ID);
                        String image_url = jsonObject.getString(Constant.IMAGE_URL);
                        String image_name = jsonObject.getString(Constant.IMAGE_NAME);
                        String type = jsonObject.getString(Constant.TYPE);
                        int view_count = jsonObject.getInt(Constant.VIEW_COUNT);
                        int download_count = jsonObject.getInt(Constant.DOWNLOAD_COUNT);
                        int set_count = jsonObject.getInt(Constant.SET_COUNT);

                        String category_id = jsonObject.getString(Constant.CATEGORY_ID);
                        String category_name = jsonObject.getString(Constant.CATEGORY_NAME);
                        String tags = jsonObject.getString(Constant.IMAGE_URL);

                        arrayList.add(new Wallpaper(id, image_name, image_url, type, view_count, download_count, set_count, tags, category_id, category_name));
                        mAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                itShouldLoadMore = true;
                showRefresh(false);
            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonArrayRequest);

        } else {
            showRefresh(false);
            mAdapter = new AdapterWallpaper(getContext(), arrayList);
            recyclerView.setAdapter(mAdapter);
        }

    }

    private void loadMore() {

        itShouldLoadMore = false;
        progressBar.setVisibility(View.VISIBLE);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.URL_RECENT_WALLPAPER + lastId, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        showRefresh(false);
                        progressBar.setVisibility(View.GONE);
                        itShouldLoadMore = true;

                        if (response.length() <= 0) {
                            return;
                        }

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);

                                lastId = jsonObject.getString(Constant.NO);
                                String id = jsonObject.getString(Constant.IMAGE_ID);
                                String image_url = jsonObject.getString(Constant.IMAGE_URL);
                                String image_name = jsonObject.getString(Constant.IMAGE_NAME);
                                String type = jsonObject.getString(Constant.TYPE);
                                int view_count = jsonObject.getInt(Constant.VIEW_COUNT);
                                int download_count = jsonObject.getInt(Constant.DOWNLOAD_COUNT);
                                int set_count = jsonObject.getInt(Constant.SET_COUNT);

                                String category_id = jsonObject.getString(Constant.CATEGORY_ID);
                                String category_name = jsonObject.getString(Constant.CATEGORY_NAME);
                                String tags = jsonObject.getString(Constant.IMAGE_URL);

                                arrayList.add(new Wallpaper(id, image_name, image_url, type, view_count, download_count, set_count, tags, category_id, category_name));
                                mAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, Constant.DELAY_LOAD_MORE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                showRefresh(false);
                itShouldLoadMore = true;
                isOffline();
            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    private void refreshData() {

        lyt_no_item.setVisibility(View.GONE);
        arrayList.clear();
        mAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firstLoadData();
            }
        }, Constant.DELAY_REFRESH);

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

    @Override
    public void onResume() {

        super.onResume();
    }
}