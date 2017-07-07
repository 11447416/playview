package com.example.anjou.exotest;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.Formatter;

public class MainActivity extends AppCompatActivity {


    private Button btn1;
    private TextView tv1, dubug1;
    private SeekBar seek1;
    private DefaultTimeBar stb1;
    private RadioGroup rg;
    private TextView start, end;

    private String url="http://dl.dingliqc.com/chengdu.m4a";
    private SimpleExoPlayer mp3Player;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        btn1 = (Button) findViewById(R.id.mp3_btn);
        tv1 = (TextView) findViewById(R.id.mp3_time_self);
        start = (TextView) findViewById(R.id.start);
        end = (TextView) findViewById(R.id.end);
        dubug1 = (TextView) findViewById(R.id.mp3_debug);
        seek1 = (SeekBar) findViewById(R.id.mp3_pro);
        stb1 = (DefaultTimeBar) findViewById(R.id.mp3_de_time);
        rg = (RadioGroup) findViewById(R.id.type);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.mp3:
                        url="http://dl.dingliqc.com/zhidao.mp3";
                        break;
                    case R.id.wav:
                        url="http://dl.dingliqc.com/suiyueshentou.wav";
                        break;
                    case R.id.m4a:
                        url = "http://dl.dingliqc.com/chengdu.m4a";
                        break;
                    case R.id.mp32:
                        url="http://220.181.190.187/tellingVnum/callmanage/records/1000004_63595a171c268014_18200389572_17072881641_15928564313.mp3";
                        break;
                    case R.id.wav2:
                        url="http://uimg-dev.ukuaiqi.com/13eff4a9-c786-4489-90da-4ec6a7be2986/20170705_163545.wav";
                        break;
                }
                if (mp3Player != null) {
                    mp3Player.setPlayWhenReady(false);
                    mp3Player.release();
                    tv1.setText("");
                    seek1.setProgress(0);
                    seek1.setSecondaryProgress(0);
                    dubug1.setText("");
                    stb1.setPosition(0);
                    stb1.setBufferedPosition(0);
                    mp3Player = null;
                    btn1.setText("播放");
                }
            }
        });
    }

    public void mp3(View view) {
        if (mp3Player == null) {

            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory selectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(selectionFactory);
            mp3Player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "ua", bandwidthMeter);
            ExtractorMediaSource mediaSource = new ExtractorMediaSource(Uri.parse(url),
                    dataSourceFactory, new DefaultExtractorsFactory(), null, null);
//        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(mediaSource);  //循环播放
            mp3Player.prepare(mediaSource);
            mp3Player.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {
                    Log.i(TAG, "onTimelineChanged: " + timeline.getPeriodCount());
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.i(TAG, "onTracksChanged: ");
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.d(TAG, "onLoadingChanged() called with: isLoading = [" + isLoading + "]");
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.d(TAG, "onPlayerStateChanged() called with: playWhenReady = [" + playWhenReady + "], playbackState = [" + playbackState + "]");
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    error.printStackTrace();
                    Log.d(TAG, "onPlayerError() called with: error = [" + error + "]");
                    Toast.makeText(MainActivity.this,"发生错误，请检查网络",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPositionDiscontinuity() {
                    Log.i(TAG, "onPositionDiscontinuity: ");

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                    Log.i(TAG, "onPlaybackParametersChanged: ");

                }
            });
            seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if(null!=mp3Player&&b)mp3Player.seekTo(i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mp3Player.setPlayWhenReady(true);
            DebugTextViewHelper debugViewHelper = new DebugTextViewHelper(mp3Player, dubug1);
            debugViewHelper.start();
            btn1.setText("暂停");
            updateMp3();//开始更新UI
            Log.i(TAG, "mp3: 第一次播放");
        } else if (mp3Player.getPlayWhenReady()) {
            btn1.setText("播放");
            mp3Player.setPlayWhenReady(false);
            Log.i(TAG, "mp3: 暂时");
        } else if (!mp3Player.getPlayWhenReady()) {
            btn1.setText("暂停");
            mp3Player.setPlayWhenReady(true);
            Log.i(TAG, "mp3: 播放");
            updateMp3();//开始更新UI
        }
    }

    private Runnable upMp3 = new Runnable() {
        @Override
        public void run() {
            if (mp3Player == null) return;
            tv1.setText("当前播放位置:" + mp3Player.getCurrentPosition() + "/:" + mp3Player.getVolume()+"\n" +
                    "buffer："+(int)mp3Player.getBufferedPosition()+"／"+mp3Player.getDuration()+"="+(mp3Player.getBufferedPosition()/(double)mp3Player.getDuration())+"\n"+
            "play:"+(int)mp3Player.getCurrentPosition()+"／"+mp3Player.getDuration()+"="+(mp3Player.getCurrentPosition()/(double)mp3Player.getDuration()));

            seek1.setProgress((int)mp3Player.getCurrentPosition());
            seek1.setSecondaryProgress((int) mp3Player.getBufferedPosition());
            seek1.setMax((int) mp3Player.getDuration());

            //更新自带的timebar
            stb1.setEnabled(true);
            stb1.setPosition(mp3Player.getCurrentPosition());
            stb1.setBufferedPosition(mp3Player.getBufferedPosition());
            stb1.setDuration(mp3Player.getDuration());

            start.setText(int2time((int) (mp3Player.getCurrentPosition())));
            end.setText(int2time((int) (mp3Player.getDuration())));

            if (mp3Player.getPlayWhenReady()) {
                tv1.postDelayed(upMp3, 1000);
            }
        }
    };

    private void updateMp3() {
        long delayMs;
        if (mp3Player.getPlayWhenReady() && mp3Player.getPlaybackState() == ExoPlayer.STATE_READY) {
            long position = mp3Player.getCurrentPosition();
            delayMs = 1000 - (position % 1000);
            if (delayMs < 200) {
                delayMs += 1000;
            }
        } else {
            delayMs = 1000;
        }
        tv1.postDelayed(upMp3, delayMs);
    }


    /**
     * 把毫秒转换成：1:20:30这里形式,一般用于声音视频的播放
     *
     * @param timeMs 毫秒
     * @return
     */
    public static String int2time(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter();
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }
}
