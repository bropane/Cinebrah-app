package com.cinebrah.cinebrah.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.utils.AppConstants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

/**
 * Created by Taylor on 10/2/2014.
 */
public class CinebrahPlayerFragment extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener,
        YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener, YouTubePlayer.OnFullscreenListener {

    private final static String LOG_TAG = "CinebrahPlayerFragment";

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    YouTubePlayer mPlayer;

    boolean fullscreen;
    boolean isPlayerReady = false;
    boolean isMinimized = false;

    int defaultPlayerHeight;
    float playerMinScale;

    View fragmentLayout;

    Video currentVideo;

    public CinebrahPlayerFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLayout = super.onCreateView(inflater, container, savedInstanceState);
        defaultPlayerHeight = getResources().getDimensionPixelSize(R.dimen.player_height);
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.player_scale_factor, outValue, true);
        playerMinScale = outValue.getFloat();
        Log.d(LOG_TAG, "Height: " + defaultPlayerHeight);
        doLayout();
        return fragmentLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        //super.onSaveInstanceState(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayer == null || isPlayerReleased()) {
            initialize();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    //TODO Write OnClick to maximize the minimized fragment

    public void initialize() {
        this.initialize(AppConstants.DEVELOPER_KEY, this);
    }

    public void playVideo(String videoId) {
        playVideo(videoId, 0);
    }

    public void playVideo(String videoId, int startTime) {
        currentVideo = new Video(videoId, startTime);
        if (isPlayerReady()) {
            mPlayer.loadVideo(currentVideo.getVideoId(), currentVideo.getStartTime());
        }
    }

    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    public boolean isPlayerReady() {
        return isPlayerReady;
    }

    public void minimize() {
        FrameLayout.LayoutParams playerParams =
                (FrameLayout.LayoutParams) fragmentLayout.getLayoutParams();
        playerParams.width = getResources().getDimensionPixelSize(R.dimen.player_minimized_width);
        playerParams.height = getResources().getDimensionPixelSize(R.dimen.player_minimized_height);
        FrameLayout container = (FrameLayout) fragmentLayout.getParent();
        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        containerParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.player_minimized_margin);
        containerParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.player_minimized_margin);
        fragmentLayout.requestLayout();
        container.requestLayout();
        isMinimized = true;
        BaseApplication.getBus().post(new ResizeEvent(isMinimized));
    }

    public void maximize() {
        RelativeLayout.LayoutParams playerParams =
                (RelativeLayout.LayoutParams) fragmentLayout.getLayoutParams();
        playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        playerParams.height = getResources().getDimensionPixelSize(R.dimen.player_height);
        FrameLayout container = (FrameLayout) fragmentLayout.getParent();
        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        containerParams.bottomMargin = 0;
        containerParams.rightMargin = 0;
        fragmentLayout.requestLayout();
        container.requestLayout();
        isMinimized = false;
        BaseApplication.getBus().post(new ResizeEvent(isMinimized));
    }

    private void doLayout() {
        /*Log.d(LOG_TAG, "doLayout Fullscreen: " + fullscreen);
        RelativeLayout.LayoutParams playerParams =
                (RelativeLayout.LayoutParams) fragmentLayout.getLayoutParams();
        if (fullscreen) {
            // When in fullscreen, the visibility of all other views than the player should be set to
            // GONE and the player should be laid out across the whole screen.
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        } else {
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = defaultPlayerHeight;
        }
        BaseApplication.getBus().post(new FullScreenEvent(fullscreen));*/
    }

    private boolean isPlayerReleased() {
        //Hack
        try {
            mPlayer.isPlaying();
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.mPlayer = youTubePlayer;
        mPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        //If the tutorial is going to be shown do not allow landscape, it will be allowed after tutorial is dismissed
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
        mPlayer.setOnFullscreenListener(this);
        mPlayer.setPlayerStateChangeListener(this);
        mPlayer.setPlaybackEventListener(this);
        isPlayerReady = true;
        if (currentVideo != null) {
            this.playVideo(currentVideo.videoId, currentVideo.startTime);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
        isPlayerReady = false;
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        if (!isMinimized) {
            fullscreen = isFullscreen;
            doLayout();
        }
    }

    @Override
    public void onPlaying() {
        Log.d(LOG_TAG, "onPlaying");
        /*if (bufferBar.getVisibility() == View.VISIBLE) {
            bufferBar.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onBuffering(boolean b) {
        Log.d(LOG_TAG, "isBuffering: " + b);
        /*if (b) {
            if (!mPlayer.isPlaying()) {
                bufferBar.setVisibility(View.VISIBLE);
            }
        } else {
            bufferBar.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onSeekTo(int i) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
        Log.i(LOG_TAG, "Playback has stopped");
        BaseApplication.getBus().post(new VideoEndedEvent());
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.e(LOG_TAG, "Player Error: " + errorReason.toString());
        if (errorReason == YouTubePlayer.ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
            mPlayer = null;
            isPlayerReady = false;
        }
    }

    public static class FullScreenEvent {
        boolean isFullscreen;

        public FullScreenEvent(boolean isFullscreen) {
            this.isFullscreen = isFullscreen;
        }

        public boolean isFullscreen() {
            return isFullscreen;
        }
    }

    public static class VideoEndedEvent {

    }

    public static class ResizeEvent {
        boolean isMinimized;

        public ResizeEvent(boolean isMinimized) {
            this.isMinimized = isMinimized;
        }

        public boolean isMinimized() {
            return isMinimized;
        }
    }

    class Video {
        String videoId;
        int startTime;

        Video(String videoId) {
            this.videoId = videoId;
        }

        Video(String videoId, int startTime) {
            this.videoId = videoId;
            this.startTime = startTime;
        }

        public String getVideoId() {
            return videoId;
        }

        public int getStartTime() {
            return startTime;
        }
    }
}
