package com.example.anjou.exotest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.anjou.exotest.exoplayview.PlayView;
import com.example.anjou.exotest.exoplayview.listener.OnNextEventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;

public class VideoActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();
    private SimpleExoPlayer player;
    private PlayView playView;

    public static final String videoUrl = "http://192.168.31.128:3000/api/home/test/?vid=52167b014cbc49313656d6e5fdf24aee&tvid=826305300";

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
        playView.play(videoUrl);
        playView.setOnNextEventListener(new OnNextEventListener() {
            @Override
            public String onNextVideoUrl() {
                return videoUrl;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playView.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playView.start();
    }
}
