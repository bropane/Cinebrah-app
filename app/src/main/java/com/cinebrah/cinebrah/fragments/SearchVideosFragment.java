package com.cinebrah.cinebrah.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.net.YoutubeSearcher;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchVideosFragment extends ListFragment {

    private static final String LOG_TAG = "SearchVideosFragment";

    String nextPageToken, currentQuery;

    SearchResultsAdapter mAdapter;

    SearchView searchView;
    MenuItem mSearch;

    public SearchVideosFragment() {
    }

    public static SearchVideosFragment newInstance() {
        SearchVideosFragment fragment = new SearchVideosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_search_videos, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(mAdapter);
        getListView().setOnScrollListener(new YoutubeSearchScrollListener() {
            @Override
            public void onLoadMore(String page, int totalItemsCount) {
//                BaseApplication.getApiService().getYoutubeSearcher().searchVideos(currentQuery, nextPageToken);
            }
        });
        getListView().setVerticalScrollBarEnabled(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new SearchResultsAdapter(getActivity(), new ArrayList<YoutubeSearcher.YoutubeSearchResult>());
        BaseApplication.getBus().register(this);
        Log.d(LOG_TAG, "OnAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BaseApplication.getBus().unregister(this);
        Log.d(LOG_TAG, "OnDetach");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_fragment, menu);
        configureSearchView(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void configureSearchView(Menu menu) {
        /*MenuItem search = menu.findItem(R.id.menu_search);
        mSearch = search;
        searchView = (SearchView) search.getActionView();
        searchView.setQueryHint(getString(R.string.search_youtube));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                BaseApplication.getApiService().getYoutubeSearcher().searchVideos(s, null);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });
        searchView.setSubmitButtonEnabled(false);*/
    }

    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        YoutubeSearcher.YoutubeSearchResult searchResult = (YoutubeSearcher.YoutubeSearchResult) getListAdapter().getItem(position);
        /*BaseApplication.getApiService().queueVideo(new QueueVideo(searchResult.getResult().getId().getVideoId(),
                searchResult.getResult().getSnippet().getTitle(),
                searchResult.getResult().getSnippet().getChannelTitle(),
                searchResult.getResult().getSnippet().getThumbnails().getMedium().getUrl(),
                searchResult.getDuration(),
                null));*/
        BaseApplication.getBus().post(new SearchItemSelected(searchResult));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onReceivedYoutubeSearchResults(YoutubeSearcher.YoutubeSearchResultsEvent event) {
        if (event != null) {
            currentQuery = event.getSearchQuery();
            nextPageToken = event.getNextPageToken();
            if (event.isNewSearch()) {
                mAdapter.clear();
            }
            mAdapter.addAll(event.getSearchResults());

            //Resets the list to the top if a new search was executed
            if (event.isNewSearch()) {
                getListView().setSelection(0);
            }
        }
    }

    public static class SearchItemSelected {
        YoutubeSearcher.YoutubeSearchResult selectedResult;

        public SearchItemSelected(YoutubeSearcher.YoutubeSearchResult selectedResult) {
            this.selectedResult = selectedResult;
        }

        public YoutubeSearcher.YoutubeSearchResult getSelectedResult() {
            return selectedResult;
        }
    }

    public static class SearchResultsAdapter extends BaseAdapter {

        private List<YoutubeSearcher.YoutubeSearchResult> searchResults;
        private Context context;

        public SearchResultsAdapter(Context context, List<YoutubeSearcher.YoutubeSearchResult> searchResults) {
            super();
            this.context = context;
            this.searchResults = searchResults;
        }

        public void add(YoutubeSearcher.YoutubeSearchResult item) {
            searchResults.add(item);
            notifyDataSetChanged();
        }

        public void addAll(List<YoutubeSearcher.YoutubeSearchResult> items) {
            for (YoutubeSearcher.YoutubeSearchResult item : items) {
                add(item);
            }
            notifyDataSetChanged();
        }

        public void clear() {
            searchResults.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return searchResults.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public YoutubeSearcher.YoutubeSearchResult getItem(int i) {
            return searchResults.get(i);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder viewHolder;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.list_item_search_result, null);
                viewHolder = new ViewHolder(row);
                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) row.getTag();
            }

            ImageView thumbnail = viewHolder.getThumbnail();
            TextView videoTitle = viewHolder.getVideoTitle();
            TextView videoChannelTitle = viewHolder.getChannelTitle();
            TextView duration = viewHolder.getDuration();

            //Loads youtube thumbnail using Ion library into image view
//            Ion.with(thumbnail).load(getItem(position).getResult().getSnippet().getThumbnails().getMedium().getUrl());
            videoTitle.setText(this.getItem(position).getResult().getSnippet().getTitle());
            videoChannelTitle.setText(this.getItem(position).getResult().getSnippet().getChannelTitle());
            duration.setText(this.getItem(position).getDurationFormatted());
            return row;
        }

        class ViewHolder {

            @InjectView(R.id.image_youtube_thumbnail)
            ImageView thumbnail;
            @InjectView(R.id.text_video_title)
            TextView videoTitle;
            @InjectView(R.id.text_video_channel_title)
            TextView channelTitle;
            @InjectView(R.id.text_video_duration)
            TextView duration;

            ViewHolder(View base) {
                ButterKnife.inject(this, base);
            }

            public ImageView getThumbnail() {
                return thumbnail;
            }

            public TextView getVideoTitle() {
                return videoTitle;
            }

            public TextView getChannelTitle() {
                return channelTitle;
            }

            public TextView getDuration() {
                return duration;
            }
        }

    }
}
