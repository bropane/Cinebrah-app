package com.cinebrah.cinebrah.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appspot.cinebrahs.cinebrahApi.model.ApiCinebrahApiMessagesRoomMessage;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.utils.AppConstants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by Taylor on 2/11/2015.
 */
public class RoomAdapter extends BaseAdapter {

    private ArrayList<ApiCinebrahApiMessagesRoomMessage> rooms;
    private ThumbnailListener thumbnailListener;
    private Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private LayoutInflater inflater;
    private HashMap<String, ApiCinebrahApiMessagesRoomMessage> roomMap;

    public RoomAdapter() {
        super();
        this.rooms = new ArrayList<>();
        this.roomMap = new HashMap<>();
        thumbnailViewToLoaderMap = new HashMap<>();
        thumbnailListener = new ThumbnailListener();
        Timber.d("Creating RoomAdapter");
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
    }

    public void add(ApiCinebrahApiMessagesRoomMessage item) {
        rooms.add(item);
        roomMap.put(item.getRoomId(), item);
        notifyDataSetChanged();
    }

    public void addAll(List<ApiCinebrahApiMessagesRoomMessage> items) {
        for (ApiCinebrahApiMessagesRoomMessage item : items) {
            rooms.add(item);
            roomMap.put(item.getRoomId(), item);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        Timber.d("Room Adapter Clear called");
        rooms.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public ApiCinebrahApiMessagesRoomMessage getItem(int i) {
        return rooms.get(i);
    }

    public ApiCinebrahApiMessagesRoomMessage getItem(String roomId) {
        return roomMap.get(roomId);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder viewHolder;
        ApiCinebrahApiMessagesRoomMessage room = getItem(position);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_room, parent, false);
            viewHolder = new ViewHolder(row);
            row.setTag(viewHolder);
            YouTubeThumbnailView thumbnail = viewHolder.getThumbnail();
            thumbnail.setTag(room.getVideoId());
            thumbnail.initialize(AppConstants.DEVELOPER_KEY, thumbnailListener);
        } else {
            viewHolder = (ViewHolder) row.getTag();
            YouTubeThumbnailView thumbnail = viewHolder.getThumbnail();
            YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
            if (loader == null) {
                // 2) The view is already created, and is currently being initialized. We store the
                //    current videoId in the tag.
                thumbnail.setTag(room.getVideoId());
            } else {
                // 3) The view is already created and already initialized. Simply set the right videoId
                //    on the loader.
                if (room.getVideoId() != null) {
                    try {
                        loader.setVideo(room.getVideoId());
                    } catch (IllegalStateException e) {
                        thumbnail.initialize(AppConstants.DEVELOPER_KEY, thumbnailListener);
                    }
                } else {
                    thumbnail.setImageResource(R.drawable.ic_room_placeholder_thumbnail);
                }
            }
        }

        TextView roomName = viewHolder.getRoomName();
        TextView currentVideo = viewHolder.getCurrentVideo();
        TextView watcherCount = viewHolder.getWatcherCount();

        roomName.setText(this.getItem(position).getRoomName());
        watcherCount.setText(this.getItem(position).getUserCount().toString());
        currentVideo.setText(this.getItem(position).getVideoName());
        return row;
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.ic_room_placeholder_thumbnail);
            String videoId = (String) view.getTag();
            if (videoId != null)
                loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
//                view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
//                view.setImageResource(R.drawable.no_thumbnail);
        }
    }


    class ViewHolder {

        @InjectView(R.id.image_room_thumbnail)
        YouTubeThumbnailView thumbnail;
        @InjectView(R.id.text_room_name)
        TextView roomName;
        @InjectView(R.id.text_watcher_count)
        TextView watcherCount;
        @InjectView(R.id.text_current_video)
        TextView currentVideo;

        ViewHolder(View base) {
            ButterKnife.inject(this, base);
        }

        public YouTubeThumbnailView getThumbnail() {
            return thumbnail;
        }

        public TextView getRoomName() {
            return roomName;
        }

        public TextView getWatcherCount() {
            return watcherCount;
        }

        public TextView getCurrentVideo() {
            return currentVideo;
        }
    }

}