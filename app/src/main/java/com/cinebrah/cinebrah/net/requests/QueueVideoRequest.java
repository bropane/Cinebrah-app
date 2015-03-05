package com.cinebrah.cinebrah.net.requests;

import android.support.annotation.Nullable;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.QueuedVideoResponse;
import com.cinebrah.cinebrah.net.models.QueuingVideo;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class QueueVideoRequest extends RetrofitSpiceRequest<QueuedVideoResponse, CinebrahEndpoints> {

    String roomId;
    QueuingVideo queuingVideo;
    String formattedToken;

    public QueueVideoRequest(String roomId, String registrationId, String videoId, String videoTitle,
                             String channelTitle, int duration, @Nullable String formattedToken) {
        super(QueuedVideoResponse.class, CinebrahEndpoints.class);
        this.roomId = roomId;
        queuingVideo = new QueuingVideo(registrationId, videoId, videoTitle, channelTitle, duration);
        this.formattedToken = formattedToken;
    }

    @Override
    public QueuedVideoResponse loadDataFromNetwork() throws Exception {
        if (formattedToken == null) {
            return getService().queueVideo(roomId, queuingVideo);
        } else {
            return getService().queueVideo(roomId, queuingVideo, formattedToken);
        }
    }
}

