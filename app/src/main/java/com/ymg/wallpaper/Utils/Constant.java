package com.ymg.wallpaper.Utils;

import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Models.Wallpaper;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {


    public static final String URL_CATEGORY = Config.ADMIN_PANEL_URL +"/api/api.php?action=get_category";
    public static final String URL_CATEGORY_DETAIL = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_category_detail";
    public static final String URL_RECENT_WALLPAPER = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_recent&offset=";
    public static final String URL_POPULAR_WALLPAPER = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_popular&offset=";
    public static final String URL_RANDOM_WALLPAPER = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_random&offset=";
    public static final String URL_LIVE_WALLPAPER = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_featured&offset=";
    public static final String URL_SEARCH_WALLPAPER = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_search";
    public static final String URL_PRIVACY_POLICY = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_privacy_policy";
    public static final String URL_VIEW_COUNT = Config.ADMIN_PANEL_URL + "/api/api.php?action=view_count&id=";
    public static final String URL_DOWNLOAD_COUNT = Config.ADMIN_PANEL_URL + "/api/api.php?action=download_count&id=";
    public static final String URL_SET_COUNT = Config.ADMIN_PANEL_URL + "/api/api.php?action=set_count&id=";
    public static final String URL_LOAD_DATA = Config.ADMIN_PANEL_URL + "/api/api.php?action=get_all_data";
    public static final String URL_DATA = PrefManager.URL_DATA + "/wallpaper.php?nid=wallpaper";
    public static final String URL_SLIDER = Config.ADMIN_PANEL_URL +"/api/api.php?action=get_slider";

    public static final String SLIDER_ID = "slider_id";
    public static final String SLIDER_NAME = "slider_name";
    public static final String SLIDER_IMAGE = "slider_image";
    public static final String SLIDER_LINK = "slider_link";
    public static final String SLIDER_TYPE = "slider_type";
    public static final String SLIDER_CATEGORY = "slider_category";

    public static final String NO = "no";
    public static final String IMAGE_ID = "image_id";
    public static final String IMAGE_UPLOAD = "image_upload";
    public static final String IMAGE_URL = "image_upload";
    public static final String IMAGE_NAME = "image_name";
    public static final String TYPE = "type";
    public static final String VIEW_COUNT = "view_count";
    public static final String DOWNLOAD_COUNT = "download_count";
    public static final String SET_COUNT = "featured";
    public static final String FEATURED = "featured";
    public static final String TAGS = "tags";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String TOTAL_WALLPAPER = "total_wallpaper";
    public static final String DEVELOPERS_NAME = "YMG-Developers";

    public static ArrayList<Wallpaper> arrayList = new ArrayList<Wallpaper>();
    public static final int DELAY_PROGRESS = 200;
    public static final int DELAY_REFRESH = 1000;
    public static final int DELAY_LOAD_MORE = 1500;
    public static final int DELAY_SET_WALLPAPER = 2000;

    public static String packageName = "", search_item = "", gifName = "", gifPath = "";



}
