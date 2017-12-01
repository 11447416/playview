package com.example.anjou.exotest;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.anjou.exotest.exoplayview.PlayView;
import com.example.anjou.exotest.exoplayview.listener.LoadEventListener;
import com.example.anjou.exotest.exoplayview.listener.PlayEventListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();
    private SimpleExoPlayer player;
    private PlayView playView;

    public static final String videoUrl = "http://192.168.0.29:3000/api/home/test/?vid=52167b014cbc49313656d6e5fdf24aee&tvid=826305300";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        init();
        initPlay();
    }
    private void init() {
        playView = (PlayView) findViewById(R.id.play);
    }

    private void initPlay() {
        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(bandwidthMeter);
        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player.addListener(new PlayEventListener());
        playView.setPlayer(player);
        player.setPlayWhenReady(true);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"));
        HlsMediaSource source = new HlsMediaSource(Uri.parse(videoUrl), dataSourceFactory, new Handler(), new LoadEventListener());
        player.prepare(source);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player!=null){
            player.release();
        }
    }
}
