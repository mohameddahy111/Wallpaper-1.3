package com.ymg.wallpaper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ymg.wallpaper.Activitys.VideoWallpaperActivity;
import com.ymg.wallpaper.Config;
import com.ymg.wallpaper.Models.Wallpaper;
import com.ymg.wallpaper.R;
import com.ymg.wallpaper.Utils.PrefManager;
import com.ymg.wallpaper.Activitys.WallpaperDetailsActivity;
import com.ymg.wallpaper.Utils.VideoLiveWallpaper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class AdapterWallpaper extends RecyclerView.Adapter<AdapterWallpaper.MyViewHolder> {

    private ArrayList<Wallpaper> wallpapers;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    PrefManager prf;

    public interface OnItemClickListener {
        void onItemClick(View view, Wallpaper obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemOverflowClickListener) {
        this.mOnItemClickListener = mItemOverflowClickListener;
    }

    public AdapterWallpaper(Context context, ArrayList<Wallpaper> wallpapers) {
        this.context = context;
        this.wallpapers = wallpapers;
        prf = new PrefManager(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivWallpaper,ivGifWallpaper;
        TextView tvWallpaper;
        RelativeLayout relativeLayoutColor;

        MyViewHolder(View view) {
            super(view);

            ivWallpaper = view.findViewById(R.id.ivWallpaper);
            tvWallpaper = view.findViewById(R.id.tvWallpaper);
            ivGifWallpaper = view.findViewById(R.id.ivGifWallpaper);
            relativeLayoutColor = view.findViewById(R.id.relativeLayoutColor);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (prf.getString("wallpaperColumnsString").equals("three")){
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_small, parent, false));
        }else {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Glide.with(context)
                .load(Config.ADMIN_PANEL_URL + "/images/" + wallpapers.get(position).getImage_url())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivWallpaper);

        holder.tvWallpaper.setText(wallpapers.get(position).getImage_upload());

        if (wallpapers.get(position).type.equals("image")){
            holder.ivGifWallpaper.setVisibility(View.GONE);
        }else {
            if (wallpapers.get(position).getImage_url().endsWith(".gif")){
                holder.ivGifWallpaper.setImageResource(R.drawable.gif);
                holder.ivGifWallpaper.setVisibility(View.VISIBLE);
            }else {
                holder.ivGifWallpaper.setImageResource(R.drawable.ic_live);
                holder.ivGifWallpaper.setVisibility(View.VISIBLE);
            }
        }

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        //holder.relativeLayoutColor.setBackgroundColor(color);

        holder.ivWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wallpapers.get(position).type.equals("image")){
                    final Intent intshow = new Intent(context, WallpaperDetailsActivity.class);
                    intshow.putExtra("POSITION", position);
                    intshow.putExtra("array", (Serializable) wallpapers);
                    context.startActivity(intshow);
                }else {
                    final Intent intshow = new Intent(context, VideoWallpaperActivity.class);
                    intshow.putExtra("IMAGE_ID", wallpapers.get(position).getImage_id());
                    intshow.putExtra("IMAGE", wallpapers.get(position).getImage_url());
                    intshow.putExtra("NAME", wallpapers.get(position).getImage_upload());
                    intshow.putExtra("VIEW", wallpapers.get(position).getView_count());
                    intshow.putExtra("DOWNLOAD", wallpapers.get(position).getDownload_count());
                    intshow.putExtra("SETS", wallpapers.get(position).getSet_count());
                    intshow.putExtra("TYPE", wallpapers.get(position).getType());
                    context.startActivity(intshow);
                }

            }
        });
    }

    public void setItems(ArrayList<Wallpaper> list) {
        wallpapers = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return wallpapers.size();
    }

}