package com.cinebrah.cinebrah.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.activities.CinemaActivity;
import com.cinebrah.cinebrah.net.GcmIntentService;
import com.cinebrah.cinebrah.net.models.QueueVideoDepreciated;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QueuedVideosFragment extends ListFragment {

    ArrayList<QueueVideoDepreciated> mQueuedVideos;
    QueuedVideosAdapter mAdapter;
    Button headerButton;
    View header;

    public QueuedVideosFragment() {
        mQueuedVideos = new ArrayList<QueueVideoDepreciated>();
    }

    public static QueuedVideosFragment newInstance() {
        QueuedVideosFragment fragment = new QueuedVideosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_queued_videos, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getListView().setEmptyView(emptyTV);
        initListHeader();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BaseApplication.getBus().register(this);
        mAdapter = new QueuedVideosAdapter(getActivity(), mQueuedVideos);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BaseApplication.getBus().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(mAdapter);
    }

    /*@Subscribe
    public void onReceivedRoomInfo(ApiServiceOld.RoomInfoEvent event) {
        mAdapter.removeAll();
        List<ApiCinebrahApiMessagesQueuedVideoMessage> queuedVideos = event.getQueuedVideos();
        for (ApiCinebrahApiMessagesQueuedVideoMessage message : queuedVideos) {
            QueueVideo video = new QueueVideo(null, message.getVideoTitle(),
                    message.getChannelTitle(), message.getThumbnailUrl(),
                    message.getVideoDuration(), message.getQueuedBy());
            mAdapter.addItem(video);
        }
        updateList();
    }*/

    @Subscribe
    public void onNextVideoReceived(GcmIntentService.NextVideoEvent event) {
        if (mAdapter.getCount() > 0) {
            mAdapter.removeItem(0);
            updateList();
        }
    }

    @Subscribe
    public void onReceivedNewQueuedVideo(GcmIntentService.NewVideoQueuedEvent event) {
        mAdapter.addItem(event.getVideo());
        updateList();
    }

    private void initListHeader() {
        header = getActivity().getLayoutInflater().inflate(R.layout.queued_videos_header, getListView(), false);
        headerButton = (Button) header.findViewById(R.id.button_queue_video);
        headerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("QueueFragment", "onClick");
                ((CinemaActivity) getActivity()).openSearchFragment();
            }
        });
        getListView().addHeaderView(header);
    }

    private void updateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    class QueuedVideosAdapter extends BaseAdapter {

        Context context;
        ArrayList<QueueVideoDepreciated> queueVideoDepreciateds;

        public QueuedVideosAdapter(Context context, ArrayList<QueueVideoDepreciated> queueVideoDepreciateds) {
            super();
            this.context = context;
            this.queueVideoDepreciateds = queueVideoDepreciateds;
        }

        @Override
        public Object getItem(int i) {
            return queueVideoDepreciateds.get(i);
        }

        @Override
        public int getCount() {
            return queueVideoDepreciateds.size();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View row = convertView;
            ViewHolder viewHolder;
            if (row == null) {
                LayoutInflater mInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = mInflater.inflate(R.layout.list_item_queued_video, null);
                viewHolder = new ViewHolder(row);
                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) row.getTag();
            }

            TextView videoTitleTV = viewHolder.getVideoTitleTV();
            TextView videoChannelTitleTV = viewHolder.getChannelTitleTV();
            TextView durationTV = viewHolder.getDurationTV();
            TextView queuedByTV = viewHolder.getQueuedByTV();
            ImageView thumbnailView = viewHolder.getThumbnailView();

            QueueVideoDepreciated video = queueVideoDepreciateds.get(position);
            videoTitleTV.setText(video.getVideoTitle());
            videoChannelTitleTV.setText(video.getChannelTitle());
            durationTV.setText(video.getDurationText());
            queuedByTV.setText("Queued by: " + video.getQueuedBy());

            //Loads youtube thumbnail using Ion library into image view
//            Ion.with(thumbnailView).load(video.getThumbnailUrl());
            return row;
        }

        public void addItem(QueueVideoDepreciated video) {
            queueVideoDepreciateds.add(video);
        }

        public void addAll(List<QueueVideoDepreciated> videos) {
            queueVideoDepreciateds.addAll(videos);
        }

        public void removeItem(int i) {
            queueVideoDepreciateds.remove(i);
        }

        public void removeAll() {
            queueVideoDepreciateds.clear();
        }

        class ViewHolder {

            @InjectView(R.id.text_video_title)
            TextView videoTitleTV;
            @InjectView(R.id.text_video_channel_title)
            TextView channelTitleTV;
            @InjectView(R.id.text_video_duration)
            TextView durationTV;
            @InjectView(R.id.text_queued_by)
            TextView queuedByTV;
            @InjectView(R.id.image_youtube_thumbnail)
            ImageView thumbnailView;

            ViewHolder(View base) {
                ButterKnife.inject(this, base);
            }

            public TextView getVideoTitleTV() {
                return videoTitleTV;
            }

            public TextView getChannelTitleTV() {
                return channelTitleTV;
            }

            public TextView getDurationTV() {
                return durationTV;
            }

            public TextView getQueuedByTV() {
                return queuedByTV;
            }

            public ImageView getThumbnailView() {
                return thumbnailView;
            }
        }
    }

}
