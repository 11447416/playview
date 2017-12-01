package com.example.anjou.exotest.exoplayview.listener;


import android.util.Log;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.upstream.DataSpec;

import java.io.IOException;

/**
 * 加载视频的回调监听器
 * Created by zjie on 2017/11/28.
 */

public class LoadEventListener implements AdaptiveMediaSourceEventListener {
    private String TAG = "playEvent";

    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
        Log.d(TAG, "onLoadStarted() called with: dataSpec = [" + dataSpec + "], dataType = [" + dataType + "], trackType = [" + trackType + "], trackFormat = [" + trackFormat + "], trackSelectionReason = [" + trackSelectionReason + "], trackSelectionData = [" + trackSelectionData + "], mediaStartTimeMs = [" + mediaStartTimeMs + "], mediaEndTimeMs = [" + mediaEndTimeMs + "], elapsedRealtimeMs = [" + elapsedRealtimeMs + "]");
    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        Log.d(TAG, "onLoadCompleted() called with: dataSpec = [" + dataSpec + "], dataType = [" + dataType + "], trackType = [" + trackType + "], trackFormat = [" + trackFormat + "], trackSelectionReason = [" + trackSelectionReason + "], trackSelectionData = [" + trackSelectionData + "], mediaStartTimeMs = [" + mediaStartTimeMs + "], mediaEndTimeMs = [" + mediaEndTimeMs + "], elapsedRealtimeMs = [" + elapsedRealtimeMs + "], loadDurationMs = [" + loadDurationMs + "], bytesLoaded = [" + bytesLoaded + "]");
    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        Log.d(TAG, "onLoadCanceled() called with: dataSpec = [" + dataSpec + "], dataType = [" + dataType + "], trackType = [" + trackType + "], trackFormat = [" + trackFormat + "], trackSelectionReason = [" + trackSelectionReason + "], trackSelectionData = [" + trackSelectionData + "], mediaStartTimeMs = [" + mediaStartTimeMs + "], mediaEndTimeMs = [" + mediaEndTimeMs + "], elapsedRealtimeMs = [" + elapsedRealtimeMs + "], loadDurationMs = [" + loadDurationMs + "], bytesLoaded = [" + bytesLoaded + "]");
    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
        Log.d(TAG, "onLoadError() called with: dataSpec = [" + dataSpec + "], dataType = [" + dataType + "], trackType = [" + trackType + "], trackFormat = [" + trackFormat + "], trackSelectionReason = [" + trackSelectionReason + "], trackSelectionData = [" + trackSelectionData + "], mediaStartTimeMs = [" + mediaStartTimeMs + "], mediaEndTimeMs = [" + mediaEndTimeMs + "], elapsedRealtimeMs = [" + elapsedRealtimeMs + "], loadDurationMs = [" + loadDurationMs + "], bytesLoaded = [" + bytesLoaded + "], error = [" + error + "], wasCanceled = [" + wasCanceled + "]");
    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
        Log.d(TAG, "onUpstreamDiscarded() called with: trackType = [" + trackType + "], mediaStartTimeMs = [" + mediaStartTimeMs + "], mediaEndTimeMs = [" + mediaEndTimeMs + "]");
    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
        Log.d(TAG, "onDownstreamFormatChanged() called with: trackType = [" + trackType + "], trackFormat = [" + trackFormat + "], trackSelectionReason = [" + trackSelectionReason + "], trackSelectionData = [" + trackSelectionData + "], mediaTimeMs = [" + mediaTimeMs + "]");
    }
}
