package com.cinebrah.cinebrah.net.requests;

import android.support.annotation.Nullable;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.AdvanceVideo;
import com.cinebrah.cinebrah.net.models.RoomActionResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class AdvanceVideoRequest extends RetrofitSpiceRequest<RoomActionResponse, CinebrahEndpoints> {

    String roomId;
    AdvanceVideo advanceVideo;
    String formattedToken;

    public AdvanceVideoRequest(String roomId, String registrationId, String videoId,
                               @Nullable String formattedToken) {
        super(RoomActionResponse.class, CinebrahEndpoints.class);
        this.roomId = roomId;
        this.formattedToken = formattedToken;
        this.advanceVideo = new AdvanceVideo(registrationId, videoId);
    }

    @Override
    public RoomActionResponse loadDataFromNetwork() throws Exception {
        if (formattedToken == null) {
            return getService().requestAdvanceCurrentVideo(roomId, advanceVideo);
        } else {
            return getService().requestAdvanceCurrentVideo(roomId, advanceVideo, formattedToken);
        }
    }
}
