package com.ymg.wallpaper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ymg.wallpaper.Activitys.WallpaperActivity;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Models.Slider;
import com.ymg.wallpaper.R;
import java.util.ArrayList;


public class FeaturedAdapter extends PagerAdapter {

    private Context mContext;

    private ArrayList<Slider> mItemList;
    ArrayList<String> colors = new ArrayList<>();
    private LayoutInflater inflater;

    public FeaturedAdapter(Context mContext, ArrayList<Slider> mItemList) {
        this.mContext = mContext;
        this.mItemList = mItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }


    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {

        View rootView = inflater.inflate(R.layout.item_featured, view, false);

        final TextView tvSlider = (TextView) rootView.findViewById(R.id.tvSlider);
        final ImageView ivSlider = rootView.findViewById(R.id.ivSlider);
        final RelativeLayout llMain = rootView.findViewById(R.id.llMain);

        final Slider slider = mItemList.get(position);

        String imgUrl = slider.getSlider_image();
        Glide.with(mContext)
                .load(Config.ADMIN_PANEL_URL+"/images/"+imgUrl)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivSlider);

        tvSlider.setText(Html.fromHtml(slider.getSlider_name()));


        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (slider.getSlider_type().equals("link")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(slider.getSlider_link().toString()));
                    mContext.startActivity(browserIntent);
                }else {
                    Intent intent = new Intent(mContext, WallpaperActivity.class);
                    intent.putExtra("categoryID", slider.getSlider_category());
                    intent.putExtra("categoryName", slider.getSlider_name());
                    mContext.startActivity(intent);
                }
            }
        });


        view.addView(rootView);


        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
