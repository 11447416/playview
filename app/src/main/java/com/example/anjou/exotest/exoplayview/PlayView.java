
package com.example.anjou.exotest.exoplayview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.anjou.exotest.R;
import com.example.anjou.exotest.exoplayview.listener.LoadEventListener;
import com.example.anjou.exotest.exoplayview.listener.PlayEventListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public final class PlayView extends FrameLayout implements View.OnClickListener {
    public static final int FAST_SEEK_MS = 15000;//快进快退的时间长度
    private AspectRatioFrameLayout contentFrame;
    private ProgressBar smallProgress;
    private SeekBar seekBar;
    private LinearLayout controllerView;
    private ImageButton ibBack;
    private ImageButton ibForward;
    private ImageButton ibPlay;
    private ImageButton ibNext;
    private ImageButton ibFull;
    private ImageButton ibTitleBack;
    private ImageButton ibLock;
    private TextView tvTitleText;
    private TextView tvCurrent;
    private TextView tvAll;
    private LinearLayout llTitleContainer;
    private LinearLayout llTitleBackContainer;


    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    private View surfaceView;
    private ComponentListener componentListener;
    private SimpleExoPlayer player;
    private int surfaceType = SURFACE_TYPE_SURFACE_VIEW;

    private boolean statusLoop = false;
    private Activity activity;
    private Integer height;
    private Integer width;
    private boolean fullScreen = false;//是否是全屏模式
    private boolean lock = false;//屏幕是否锁定

    public PlayView(Context context) {
        this(context, null);
    }

    public PlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.activity = (Activity) context;
        //播放界面，不使用ActionBar
        if (activity instanceof AppCompatActivity) {
            ((AppCompatActivity) activity).getSupportActionBar().hide();
        }
        initNormal(context);
    }

    private void setFull(boolean isFull) {
        fullScreen = isFull;
        View decorView = activity.getWindow().getDecorView();
        //设置是否是完全的沉浸式
        if (isFull) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            decorView.setSystemUiVisibility(
                    ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            & ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            & ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            & ~View.SYSTEM_UI_FLAG_FULLSCREEN
                            & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
        //设置屏幕方向
        if (isFull) {
            //横屏设置
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            //竖屏设置
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //设置画面大小
        if (isFull) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            //保存之前的大小
            if (width == null && height == null) {
                height = layoutParams.height;
                width = layoutParams.width;
            }
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = height;
            layoutParams.width = width;
            setLayoutParams(layoutParams);
        }
        //设置全屏按钮
        ibFull.setImageResource(isFull ? R.drawable.paly_control_refull : R.drawable.paly_control_full);
    }

    private void initNormal(Context context) {
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
        ibTitleBack = (ImageButton) findViewById(R.id.title_back);
        ibLock = (ImageButton) findViewById(R.id.play_lock);
        tvTitleText = (TextView) findViewById(R.id.title_text);
        tvCurrent = (TextView) findViewById(R.id.play_current);
        tvAll = (TextView) findViewById(R.id.play_all);
        llTitleContainer = (LinearLayout) findViewById(R.id.title_container);
        llTitleBackContainer = (LinearLayout) findViewById(R.id.title_back_container);

        ibBack.setOnClickListener(this);
        ibForward.setOnClickListener(this);
        ibPlay.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibFull.setOnClickListener(this);
        ibLock.setOnClickListener(this);
        ibTitleBack.setOnClickListener(this);
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
                if (!statusLoop) {
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

    //设置标题
    private void setTitle(String title) {
        tvTitleText.setText(title);
    }

    //通过传入视频url直接播放
    public void play(String url) {
        if (player != null) {
            player.release();
        }
        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(bandwidthMeter);
        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector);
        player.addListener(new PlayEventListener());
        player.setPlayWhenReady(true);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(activity,
                Util.getUserAgent(activity, "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"));
        HlsMediaSource source = new HlsMediaSource(Uri.parse(url), dataSourceFactory, new Handler(), new LoadEventListener());
        player.prepare(source);
        setPlayer(player);
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
            statusLoop = false;
        }
    }

    /**
     * 通过传入 SimpleExoPlayer 执行播放
     *
     * @param player
     */
    public void setPlayer(SimpleExoPlayer player) {
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
            if (progress >= 0) {
                seekBar.setProgress(progress);
                smallProgress.setProgress(progress);
            }

            postDelayed(getStatusLoop, 1000);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        statusLoop = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_back:
                if (player != null) {
                    long seekTo = player.getCurrentPosition() - FAST_SEEK_MS;
                    if (seekTo < 0) {
                        seekTo = 0;
                    }
                    player.seekTo(seekTo);
                    //播放停止以后，返回开始播放，要重新启动时间循环
                    if (!statusLoop) {
                        statusLoop = true;
                        post(getStatusLoop);
                    }
                }
                break;
            case R.id.play_forward:
                if (player != null) {
                    long seekTo = player.getCurrentPosition() + FAST_SEEK_MS;
                    if (seekTo > player.getDuration()) {
                        seekTo = player.getDuration();
                    }
                    player.seekTo(seekTo);
                }
                break;
            case R.id.play_play:
                if (player != null) {
                    boolean onPlay = !player.getPlayWhenReady();
                    player.setPlayWhenReady(onPlay);
                    ibPlay.setImageResource(onPlay ? R.drawable.paly_control_pause : R.drawable.paly_control_play);
                    statusLoop = onPlay;
                    post(getStatusLoop);
                }
                break;
            case R.id.play_next:
                break;
            case R.id.play_full:
            case R.id.title_back:
                setFull(!fullScreen);
                ibLock.setVisibility(fullScreen ? VISIBLE : GONE);
                llTitleBackContainer.setVisibility(fullScreen ? VISIBLE : GONE);
                break;
            case R.id.play_lock:
                if (fullScreen) {
                    lock = !lock;
                    ibLock.setImageResource(lock ? R.drawable.play_lock : R.drawable.play_unlock);
                    controllerView.setVisibility(lock ? GONE : VISIBLE);
                    llTitleContainer.setVisibility(lock ? GONE : VISIBLE);
                }
                break;
            default:
                //隐藏或者显示控制view
                if (fullScreen) {
                    if (lock) {
                        //锁定的时候
                        ibLock.setVisibility(ibLock.getVisibility() == GONE ? VISIBLE : GONE);
                    } else {
                        //没有锁定，就是所有的提示显示或者隐藏
                        if (controllerView.getVisibility() == VISIBLE) {
                            controllerView.setVisibility(GONE);
                            smallProgress.setVisibility(VISIBLE);
                            llTitleContainer.setVisibility(GONE);
                            ibLock.setVisibility(GONE);
                        } else {
                            controllerView.setVisibility(VISIBLE);
                            smallProgress.setVisibility(GONE);
                            llTitleContainer.setVisibility(VISIBLE);
                            ibLock.setVisibility(VISIBLE);
                        }
                    }
                } else {
                    if (controllerView.getVisibility() == VISIBLE) {
                        controllerView.setVisibility(GONE);
                        smallProgress.setVisibility(VISIBLE);
                        llTitleContainer.setVisibility(GONE);
                    } else {
                        controllerView.setVisibility(VISIBLE);
                        smallProgress.setVisibility(GONE);
                        llTitleContainer.setVisibility(VISIBLE);
                    }
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
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        //避免加载的瞬间，时间不正常
        if (totalSeconds < 0 || minutes < 0 || hours < 0) {
            totalSeconds = 0;
            minutes = 0;
            hours = 0;
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

}
