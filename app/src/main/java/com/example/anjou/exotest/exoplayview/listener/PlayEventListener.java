package com.example.anjou.exotest.exoplayview.listener;

import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

/**
 * 播放过程中状态的监听
 * Created by zjie on 2017/11/28.
 */

public class PlayEventListener implements Player.EventListener {
    public final String TAG = this.getClass().getName();

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d(TAG, "onTimelineChanged() called with: timeline = [" + timeline + "], manifest = [" + manifest + "]");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

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
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
