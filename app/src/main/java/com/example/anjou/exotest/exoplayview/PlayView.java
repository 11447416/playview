
package com.example.anjou.exotest.exoplayview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.anjou.exotest.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.Util;

import java.util.Formatter;
import java.util.Locale;

public final class PlayView extends FrameLayout implements View.OnClickListener {

    private final AspectRatioFrameLayout contentFrame;
    private final ProgressBar smallProgress;
    private final SeekBar seekBar;
    private final LinearLayout controllerView;
    private final ImageButton ibBack;
    private final ImageButton ibForward;
    private final ImageButton ibPlay;
    private final ImageButton ibNext;
    private final ImageButton ibFull;
    private final TextView tvCurrent;
    private final TextView tvAll;


    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    private final View surfaceView;
    private final ComponentListener componentListener;
    private SimpleExoPlayer player;
    private int surfaceType = SURFACE_TYPE_SURFACE_VIEW;

    private boolean statusLoop = false;

    public PlayView(Context context) {
        this(context, null);
    }

    public PlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.exo_play_view, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        contentFrame = findViewById(R.id.play_container);
        contentFrame.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        smallProgress = (ProgressBar) findViewById(R.id.play_progress);
        seekBar = (SeekBar) findViewById(R.id.play_seek);
        controllerView = (LinearLayout) findViewById(R.id.play_controller);
        ibBack = (ImageButton) findViewById(R.id.play_back);
        ibForward = (ImageButton) findViewById(R.id.play_forward);
        ibPlay = (ImageButton) findViewById(R.id.play_play);
        ibNext = (ImageButton) findViewById(R.id.play_next);
        ibFull = (ImageButton) findViewById(R.id.play_full);
        tvCurrent = (TextView) findViewById(R.id.play_current);
        tvAll = (TextView) findViewById(R.id.play_all);

        ibBack.setOnClickListener(this);
        ibForward.setOnClickListener(this);
        ibPlay.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibFull.setOnClickListener(this);
        this.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b && player != null) {
                    player.seekTo((long) (player.getDuration() * (i / 100f)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!(statusLoop)) {
                    statusLoop = true;
                    post(getStatusLoop);
                }
            }
        });

        //添加视频容器
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView = surfaceType == SURFACE_TYPE_TEXTURE_VIEW ? new TextureView(context) : new SurfaceView(context);
        surfaceView.setLayoutParams(params);
        contentFrame.addView(surfaceView, 0);


        componentListener = new ComponentListener();

    }

    public void setPlayer(SimpleExoPlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeVideoListener(componentListener);
            if (surfaceView instanceof TextureView) {
                this.player.clearVideoTextureView((TextureView) surfaceView);
            } else if (surfaceView instanceof SurfaceView) {
                this.player.clearVideoSurfaceView((SurfaceView) surfaceView);
            }
        }
        this.player = player;

        if (player != null) {
            if (surfaceView instanceof TextureView) {
                player.setVideoTextureView((TextureView) surfaceView);
            } else if (surfaceView instanceof SurfaceView) {
                player.setVideoSurfaceView((SurfaceView) surfaceView);
            }
            player.addVideoListener(componentListener);
            statusLoop = true;
            post(getStatusLoop);
        }
    }

    private Runnable getStatusLoop = new Runnable() {
        @Override
        public void run() {
            if (player == null || !statusLoop) {
                return;
            }
            long position = player.getCurrentPosition();
            long duration = player.getDuration();
            tvCurrent.setText(int2time(position));
            tvAll.setText(int2time(duration));
            int progress = (int) ((double) position / (double) duration * 100f);
            seekBar.setProgress(progress);
            smallProgress.setProgress(progress);

            int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
            if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
                long delayMs;
                if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                    delayMs = 1000 - (position % 1000);
                    if (delayMs < 200) {
                        delayMs += 1000;
                    }
                } else {
                    delayMs = 1000;
                }
                postDelayed(getStatusLoop, delayMs);
            } else {
                statusLoop = false;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_back:
                break;
            case R.id.play_forward:
                break;
            case R.id.play_play:
                if (player != null) {
                    boolean onPlay=!player.getPlayWhenReady();
                    player.setPlayWhenReady(onPlay);
                    ibPlay.setImageResource(onPlay?R.drawable.pause:R.drawable.play);
                    statusLoop =onPlay;
                    post(getStatusLoop);
                }
                break;
            case R.id.play_next:
                break;
            case R.id.play_full:
                break;
            default:
                //隐藏或者显示控制view
                if (controllerView.getVisibility() == VISIBLE) {
                    controllerView.setVisibility(GONE);
                    smallProgress.setVisibility(VISIBLE);
                } else {
                    controllerView.setVisibility(VISIBLE);
                    smallProgress.setVisibility(GONE);
                }
                break;

        }
    }


    private final class ComponentListener implements SimpleExoPlayer.VideoListener {
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            if (contentFrame != null) {
                float aspectRatio = height == 0 ? 1 : (width * pixelWidthHeightRatio) / height;
                contentFrame.setAspectRatio(aspectRatio);
            }
        }

        @Override
        public void onRenderedFirstFrame() {

        }
    }

    /**
     * 把毫秒转换成：1:20:30这里形式,一般用于声音视频的播放
     *
     * @param timeMs 毫秒
     * @return
     */
    public static String int2time(long timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter();
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

}
