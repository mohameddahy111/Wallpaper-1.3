package com.ymg.wallpaper.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.ymg.wallpaper.Adapters.AdapterWallpaper;
import com.ymg.wallpaper.Adapters.FeaturedAdapter;
import com.ymg.wallpaper.Models.Slider;
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


public class LatestFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout = null;
    RecyclerView recyclerView;
    RelativeLayout lyt_parent;
    private String lastId = "0";
    private boolean itShouldLoadMore = true;
    private AdapterWallpaper mAdapter;
    private ArrayList<Wallpaper> arrayList;
    private ArrayList<Wallpaper> arrayListTemp;
    private ArrayList<Slider> mFeaturedList;
    RelativeLayout progressBar;
    View lyt_no_item;
    Methods methods;
    PrefManager prf;
    GridLayoutManager gridLayoutManager;
    String id;
    String image_url;
    String image_name;
    String type;
    int view_count;
    int download_count;
    int set_count;
    String category_id;
    String category_name;
    String tags;
    private ViewPager mFeaturedPager;
    private FeaturedAdapter mFeaturedPagerAdapter = null;
    RelativeLayout rlFeatured;

    private Handler handler;
    private int delay = 5000; //milliseconds
    private int page = 0;
    Runnable runnable = new Runnable() {
        public void run() {
            if (mFeaturedPagerAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            mFeaturedPager.setCurrentItem(page, true);
            handler.postDelayed(this, delay);
        }
    };

    public LatestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest, container, false);

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
        mFeaturedPager = view.findViewById(R.id.pager_featured_posts);
        rlFeatured = view.findViewById(R.id.rlFeatured);

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();
        mAdapter = new AdapterWallpaper(getActivity(), arrayList);

        //featured recipes
        handler = new Handler();
        mFeaturedList = new ArrayList<>();
        mFeaturedPagerAdapter = new FeaturedAdapter(getActivity(), mFeaturedList);
        mFeaturedPager.setAdapter(mFeaturedPagerAdapter);

        recyclerView = view.findViewById(R.id.recyclerView);
        gridLayoutManager = new GridLayoutManager(getActivity(),prf.getInt("wallpaperColumns"));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        loadSlider();
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

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mFeaturedPager, true);
        mFeaturedPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        return view;
    }

    private void loadSlider() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.URL_SLIDER, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response.length() <= 0) {
                    rlFeatured.setVisibility(View.GONE);
                    return;
                }

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        String slider_id = jsonObject.getString(Constant.SLIDER_ID);
                        String slider_name = jsonObject.getString(Constant.SLIDER_NAME);
                        String slider_image = jsonObject.getString(Constant.SLIDER_IMAGE);
                        String slider_link = jsonObject.getString(Constant.SLIDER_LINK);
                        String slider_type = jsonObject.getString(Constant.SLIDER_TYPE);
                        String slider_category = jsonObject.getString(Constant.SLIDER_CATEGORY);

                        mFeaturedList.add(new Slider(slider_id, slider_name, slider_image, slider_link, slider_type, slider_category));
                        mFeaturedPagerAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    private void firstLoadData() {
        if (methods.isNetworkAvailable()) {
            itShouldLoadMore = false;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constant.URL_RECENT_WALLPAPER + 0, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse (JSONArray response) {

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
                             id = jsonObject.getString(Constant.IMAGE_ID);
                             image_url = jsonObject.getString(Constant.IMAGE_URL);
                             image_name = jsonObject.getString(Constant.IMAGE_NAME);
                             type = jsonObject.getString(Constant.TYPE);
                             view_count = jsonObject.getInt(Constant.VIEW_COUNT);
                             download_count = jsonObject.getInt(Constant.DOWNLOAD_COUNT);
                             set_count = jsonObject.getInt(Constant.SET_COUNT);

                             category_id = jsonObject.getString(Constant.CATEGORY_ID);
                             category_name = jsonObject.getString(Constant.CATEGORY_NAME);
                             tags = jsonObject.getString(Constant.IMAGE_URL);

                            arrayList.add(new Wallpaper(id, image_name, image_url, type, view_count, download_count, set_count, tags, category_id, category_name));
                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse (VolleyError error) {
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
        rlFeatured.setVisibility(View.GONE);
        arrayList.clear();
        mFeaturedList.clear();
        mAdapter.notifyDataSetChanged();
        mFeaturedPagerAdapter.notifyDataSetChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rlFeatured.setVisibility(View.VISIBLE);
                firstLoadData();
                loadSlider();
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
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


}