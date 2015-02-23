package com.cinebrah.cinebrah.net;

import android.os.AsyncTask;
import android.util.Log;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.net.models.QueueVideo;
import com.cinebrah.cinebrah.utils.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Taylor on 9/9/2014.
 */
public class YoutubeSearcher {

    private static final String LOG_TAG = "YoutubeSearcher";

    private static final long NUMBER_OF_VIDEOS_RETURNED_SEARCH = 15;

    private static YouTube youtube;

    public YoutubeSearcher() {
        youtube = new YouTube.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {

            }
        }).setApplicationName(BaseApplication.getContext().getString(R.string.app_name)).build();
    }

    public static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
        System.out.println("\n=============================================================");
        System.out.println(
                " First " + NUMBER_OF_VIDEOS_RETURNED_SEARCH + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");
        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }
        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();
            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getMedium();
                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println("Channel Title: " + singleVideo.getSnippet().getChannelTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }

    public void searchVideos(final String query, final String nextPageToken) {
        new AsyncTask<Void, Void, YoutubeSearchResultsEvent>() {
            @Override
            protected YoutubeSearchResultsEvent doInBackground(Void... voids) {
                try {
                    boolean isNewSearch = true;
                    YouTube.Search.List search = youtube.search().list("id,snippet");
                    search.setKey(AppConstants.YOUTUBE_DATA_API_KEY);
                    search.setQ(query);
                    search.setType("video");
                    search.setVideoSyndicated("true");
                    search.setSafeSearch("moderate");
                    if (nextPageToken != null) {
                        search.setPageToken(nextPageToken);
                        isNewSearch = false;
                    }
                    search.setFields("nextPageToken,items(id/kind,id/videoId,snippet/title,snippet/thumbnails/medium/url,snippet/channelTitle)");
                    search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED_SEARCH);
                    SearchListResponse searchResponse = search.execute();
                    List<SearchResult> searchResults = searchResponse.getItems();

//                    YoutubeSearcher.prettyPrint(searchResults.iterator(), query);
                    if (searchResults != null) {
                        List<Video> durations = getVideoDuration(searchResults);
                        ArrayList<YoutubeSearchResult> newSearchResults = new ArrayList<YoutubeSearchResult>();
                        for (int i = 0; i < searchResults.size(); i++) {
                            YoutubeSearchResult result = new YoutubeSearchResult(searchResults.get(i), durations.get(i).getContentDetails().getDuration());
                            newSearchResults.add(result);
                        }
                        return new YoutubeSearchResultsEvent(query, isNewSearch, searchResponse.getNextPageToken(), newSearchResults);
                    } else {
                        Log.e(LOG_TAG, "Could not retrieve search results");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(YoutubeSearchResultsEvent searchResults) {
                super.onPostExecute(searchResults);
                if (searchResults != null) {
                    BaseApplication.getBus().post(searchResults);
                }
                //TODO need to do some error handling here
            }
        }.execute();
    }

    private List<Video> getVideoDuration(List<SearchResult> searchResults) {
        try {
            StringBuilder sb = new StringBuilder();
            for (SearchResult result : searchResults) {
                sb.append(result.getId().getVideoId()).append(",");
            }
            String videoIds = sb.toString();
            YouTube.Videos.List list = youtube.videos().list("contentDetails");
            list.setKey(AppConstants.YOUTUBE_DATA_API_KEY);
            list.setFields("items/id,items/contentDetails/duration");
            list.setId(videoIds);
            VideoListResponse videoResponse = list.execute();
            return videoResponse.getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class YoutubeSearchResultsEvent {
        String searchQuery;
        boolean newSearch = true;
        String nextPageToken;
        List<YoutubeSearchResult> searchResults;

        public YoutubeSearchResultsEvent(String searchQuery, boolean newSearch, String nextPageToken, List<YoutubeSearchResult> searchResults) {
            this.searchQuery = searchQuery;
            this.newSearch = newSearch;
            this.nextPageToken = nextPageToken;
            this.searchResults = searchResults;
        }

        public String getSearchQuery() {
            return searchQuery;
        }

        public boolean isNewSearch() {
            return newSearch;
        }

        public String getNextPageToken() {
            return nextPageToken;
        }

        public List<YoutubeSearchResult> getSearchResults() {
            return searchResults;
        }
    }

    public static class YoutubeVideoEvent {

        QueueVideo queueVideo;

        public YoutubeVideoEvent(QueueVideo queueVideo) {
            this.queueVideo = queueVideo;
        }

        public QueueVideo getQueueVideo() {
            return queueVideo;
        }
    }

    public class YoutubeSearchResult {
        SearchResult result;
        String duration;

        public YoutubeSearchResult(SearchResult result, String duration) {
            this.result = result;
            this.duration = duration;
        }

        public SearchResult getResult() {
            return result;
        }

        public int getDuration() {
            //Uses joda time to parse ISO8601 time to seconds
            PeriodFormatter formatter = ISOPeriodFormat.standard();
            Period p = formatter.parsePeriod(duration);
            return p.toStandardSeconds().getSeconds();
        }

        public String getDurationFormatted() {
            //Formats duration from seconds into 00:00:00 or 0:00
            long duration = getDuration();
            int hours = (int) duration / 3600;
            int remainder = (int) duration - hours * 3600;
            int minutes = remainder / 60;
            remainder = remainder - minutes * 60;
            int seconds = remainder;
            if (hours == 0) {
                return String.format("%d:%02d", minutes, seconds);
            } else {
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            }
        }
    }
}
