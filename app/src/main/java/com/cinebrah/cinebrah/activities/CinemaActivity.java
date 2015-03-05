package com.cinebrah.cinebrah.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.fragments.ChatFragment;
import com.cinebrah.cinebrah.fragments.CinebrahPlayerFragment;
import com.cinebrah.cinebrah.fragments.SearchVideosFragment;
import com.cinebrah.cinebrah.net.YoutubeSearcher;
import com.squareup.otto.Subscribe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class CinemaActivity extends ActionBarActivity {

    public static final String KEY_ROOM_ID = "room_id";
    private final static String LOG_TAG = "MainActivity";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    @InjectView(R.id.cinema_other_views)
    LinearLayout otherViews;
    @InjectView(R.id.layout_expandable_queued_videos)
    FrameLayout mExpandableQueueListLayout;
    @InjectView(R.id.cinema_fragment_container)
    FrameLayout mFragmentContainer;
    @InjectView(R.id.back_fragment_container)
    FrameLayout backFragmentContainer;

//    ApiServiceOld.RoomInfoEvent currentRoomInfo = new ApiServiceOld.RoomInfoEvent();

    boolean isQueueListExpanded = false;
    boolean isTutorialShowing;
    boolean isConnectedToRoom = false;
//    boolean isPlayerReady = false;

    CinebrahPlayerFragment cinebrahPlayerFragment;
    ChatFragment chatFragment;
    SearchVideosFragment searchVideosFragment;

    Handler handler;
//    PlayVideoTask playVideoTask;

    String roomId;

    public static String getYouTubeVideoId(String video_url) {
        if (video_url != null && video_url.length() > 0) {
            Uri video_uri = Uri.parse(video_url);
            String video_id = video_uri.getQueryParameter("v");
            if (video_id == null)
                video_id = parseYoutubeVideoId(video_url);
            return video_id;
        }
        return null;
    }

    public static String parseYoutubeVideoId(String youtubeUrl) {
        String video_id = null;
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 &&
                youtubeUrl.startsWith("http")) {
            // ^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/
            String expression = "^.*((youtu.be" + "\\/)"
                    + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                // Regular expression some how doesn't work with id with "v" at
                // prefix
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
                else if (groupIndex1 != null && groupIndex1.length() == 10)
                    video_id = "v" + groupIndex1;
            }
        }
        return video_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema);
        ButterKnife.inject(this);
        Intent startIntent = getIntent();
        roomId = startIntent.getStringExtra(KEY_ROOM_ID);
//        BaseApplication.getApiService().getRoomInfo(roomId);
        getSupportActionBar().hide();
        cinebrahPlayerFragment = new CinebrahPlayerFragment();
        chatFragment = ChatFragment.newInstance();
        searchVideosFragment = SearchVideosFragment.newInstance();
        getFragmentManager().beginTransaction()
                .add(R.id.player_container, cinebrahPlayerFragment)
                .add(R.id.cinema_fragment_container, chatFragment)
                .add(R.id.back_fragment_container, searchVideosFragment)
                .commit();

        handler = new Handler();
//        playVideoTask = new PlayVideoTask();

        BaseApplication.getBus().register(this);
//        doLayout();

        String youtubeId = getBrowserYoutubeSelection();
        if (youtubeId != null) {
            cinebrahPlayerFragment.playVideo(youtubeId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        BaseApplication.getApiService().disconnectFromRoom(AppConstants.getUserId(), roomId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
        BaseApplication.getBus().unregister(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_queue_list)
    void openQueueList() {
        expandQueueList(!isQueueListExpanded);
    }

    /*@SuppressWarnings("unused")
    @OnClick(R.id.button_next)
    void skipVideo() {
        if (currentRoomInfo.getCurrentVideoId() != null) {
            BaseApplication.getApiService().voteSkip(currentRoomInfo.getCurrentVideoId());
            Crouton.makeText(this, R.string.vote_skip,
                    Style.INFO, R.id.cinema_fragment_container).show();
        } else {
            Crouton.makeText(this, R.string.no_videos_vote_skip,
                    Style.INFO, R.id.cinema_fragment_container).show();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        return true;
    }

/*    private void doLayout() {
        Log.d(LOG_TAG, "doLayout Fullscreen: " + fullscreen);
        RelativeLayout.LayoutParams playerParams =
                (RelativeLayout.LayoutParams) mYoutubePlayerFragment.getView().getLayoutParams();
        if (fullscreen) {
            // When in fullscreen, the visibility of all other views than the player should be set to
            // GONE and the player should be laid out across the whole screen.
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            otherViews.setVisibility(View.GONE);
        } else {
            otherViews.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams otherViewsParams = (RelativeLayout.LayoutParams) otherViews.getLayoutParams();
            playerParams.width = otherViewsParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            otherViewsParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = defaultPlayerHeight;
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            cinebrahPlayerFragment.initialize();
        }
    }

    public void openSearchFragment() {
        Log.d(LOG_TAG, "openSearch");
        cinebrahPlayerFragment.minimize();
    }

    public void expandQueueList(boolean expand) {
        //If expand is true replace search fragment with chat. If false the opposite.
        if (expand) {
            mExpandableQueueListLayout.setVisibility(View.VISIBLE);
            mFragmentContainer.setVisibility(View.GONE);
            mFragmentContainer.requestLayout();
            isQueueListExpanded = true;
        } else {
            mExpandableQueueListLayout.setVisibility(View.GONE);
            mFragmentContainer.setVisibility(View.VISIBLE);
            mFragmentContainer.requestLayout();
            isQueueListExpanded = false;
        }
    }

    /*private void playCurrentVideo() {
        if (currentRoomInfo.getCurrentVideoId() != null && !currentRoomInfo.getCurrentVideoId().equals("None")) {
            cinebrahPlayerFragment.playVideo(currentRoomInfo.getCurrentVideoId(), 1000 * (int) currentRoomInfo.getCurrentVideoTime());
        }
    }*/

    private String getYoutubeShareData() {
        /**
         * Retrieves Youtube video link if share button is selected in Youtube main app
         */
        Bundle extras = getIntent().getExtras();
        String data = null;
        if (extras.containsKey(Intent.EXTRA_TEXT)) {
            data = extras.getString(Intent.EXTRA_TEXT);
            Log.d(LOG_TAG, data);
        }
        return data;
    }

    private String getBrowserYoutubeSelection() {
        String videoId = null;
        if (getIntent().getData() != null) {//check if intent is not null
            Uri data = getIntent().getData();//set a variable for the Intent
            String scheme = data.getScheme();//get the scheme (http,https)
            String fullPath = data.getEncodedSchemeSpecificPart();//get the full path -scheme - fragments
            String combine = scheme + "://" + fullPath; //combine to get a full URI
            videoId = CinemaActivity.getYouTubeVideoId(combine);
        }
        return videoId;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onResizeEvent(CinebrahPlayerFragment.ResizeEvent event) {
        if (event.isMinimized()) {
            backFragmentContainer.setVisibility(View.VISIBLE);
//            getActionBar().show();
        } else {
            backFragmentContainer.setVisibility(View.GONE);
//            getActionBar().hide();
        }
    }

    /*@SuppressWarnings("unused")
    @Subscribe
    public void onVideoEndedEvent(CinebrahPlayerFragment.VideoEndedEvent event) {
        BaseApplication.getApiService().requestAdvanceQueue(currentRoomInfo.getCurrentVideoId());
    }*/

    /*@SuppressWarnings("unused")
    @Subscribe
    public void onReceivedRoomInfo(ApiServiceOld.RoomInfoEvent event) {
        this.currentRoomInfo = event;
        if (cinebrahPlayerFragment.isPlayerReady()) {
            playCurrentVideo();
        } else {
            handler.postDelayed(playVideoTask, 1000);
        }
    }*/

    /*@SuppressWarnings("unused")
    @Subscribe
    public void onNextVideoReceived(GcmIntentService.NextVideoEvent event) {
        currentRoomInfo.setCurrentVideoId(event.getYoutubeId());
        if (event.getYoutubeId() != null) {
            cinebrahPlayerFragment.playVideo(event.getYoutubeId());
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Crouton.makeText(CinemaActivity.this, R.string.next_video, Style.INFO, R.id.cinema_fragment_container).show();
                }
            });
        } else {
            cinebrahPlayerFragment.pause();
        }
    }*/
/*
    @SuppressWarnings("unused")
    @Subscribe
    public void onSearchItemSelected(SearchVideosFragment.SearchItemSelected event) {
        mSearch.collapseActionView();
        getFragmentManager().beginTransaction().replace(R.id.cinema_fragment_container, chatFragment).commit();
    }*/

    @SuppressWarnings("unused")
    @Subscribe
    public void onReceivedYoutubeVideoDuration(YoutubeSearcher.YoutubeVideoEvent event) {
        Log.i(LOG_TAG, "Queuing Video: \n" + "Video ID: " + event.getQueueVideoDepreciated().getVideoId() +
                "\nTitle: " + event.getQueueVideoDepreciated().getVideoTitle() +
                "\nChannel Title: " + event.getQueueVideoDepreciated().getChannelTitle() +
                "\nDuration in Seconds: " + event.getQueueVideoDepreciated().getDuration());
//        BaseApplication.getApiService().queueVideo(event.getQueueVideo());
    }

    /*@SuppressWarnings("unused")
    @Subscribe
    public void onQueuedVideoResponse(ApiServiceOld.QueuedVideoEvent event) {
        Crouton.makeText(this, R.string.queued_video_success, Style.CONFIRM, R.id.cinema_fragment_container).show();
    }*/

/*    @SuppressWarnings("unused")
    @Subscribe
    public void onUserCountChanged(final GcmIntentService.UserCountChangeEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWatchingCountTV.setText(event.getUserCount() + " " + getString(R.string.online));
            }
        });
    }*/

    /*class PlayVideoTask implements Runnable {
        @Override
        public void run() {
            playCurrentVideo();
        }
    }*/

}
