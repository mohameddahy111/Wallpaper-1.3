package com.ymg.wallpaper.Utils;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;

public class VideoLiveWallpaper extends WallpaperService {
    public static final String KEY_ACTION = "music";
    public static final String VIDEO_PARAMS_CONTROL_ACTION = "";

    class VideoEngine extends Engine {
        private BroadcastReceiver broadcastReceiver;
        private MediaPlayer mediaPlayer;

        VideoEngine() {
            super();
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            IntentFilter intentFilter = new IntentFilter(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
            VideoLiveWallpaper videoLiveWallpaper = VideoLiveWallpaper.this;
            BroadcastReceiver anonymousClass1 = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (intent.getBooleanExtra(VideoLiveWallpaper.KEY_ACTION, false)) {
                        VideoEngine.this.mediaPlayer.setVolume(0.0f, 0.0f);
                    } else {
                        VideoEngine.this.mediaPlayer.setVolume(1.0f, 1.0f);
                    }
                }
            };
            this.broadcastReceiver = anonymousClass1;
            videoLiveWallpaper.registerReceiver(anonymousClass1, intentFilter);
        }

        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.mediaPlayer = mediaPlayer;
            mediaPlayer.setSurface(surfaceHolder.getSurface());
            try {
                this.mediaPlayer.reset();
                MediaPlayer mediaPlayer2 = this.mediaPlayer;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(VideoLiveWallpaper.this.getFilesDir());
                stringBuilder.append("/file.mp4");
                mediaPlayer2.setDataSource(stringBuilder.toString());
                this.mediaPlayer.setLooping(true);
                this.mediaPlayer.setVideoScalingMode(2);
                this.mediaPlayer.prepare();
                this.mediaPlayer.start();
                stringBuilder = new StringBuilder();
                stringBuilder.append(VideoLiveWallpaper.this.getFilesDir());
                stringBuilder.append("/unmute");
                if (new File(stringBuilder.toString()).exists()) {
                    this.mediaPlayer.setVolume(1.0f, 1.0f);
                } else {
                    this.mediaPlayer.setVolume(0.0f, 0.0f);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onVisibilityChanged(boolean z) {
            if (z) {
                this.mediaPlayer.start();
            } else {
                this.mediaPlayer.pause();
            }
        }

        public void onSurfaceDestroyed(SurfaceHolder surfaceHolder) {
            super.onSurfaceDestroyed(surfaceHolder);
            if (this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.stop();
            }
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }

        public void onDestroy() {
            super.onDestroy();
            MediaPlayer mediaPlayer = this.mediaPlayer;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            VideoLiveWallpaper.this.unregisterReceiver(this.broadcastReceiver);
        }
    }

    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    public static void setToWallPaper(Context context) {
        Intent intent = new Intent("android.service.wallpaper.CHANGE_LIVE_WALLPAPER");
        intent.putExtra("android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT", new ComponentName(context, VideoLiveWallpaper.class));
        context.startActivity(intent);
        try {
            WallpaperManager.getInstance(context).clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
