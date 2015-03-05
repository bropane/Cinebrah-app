package com.cinebrah.cinebrah.net.requests;

import android.support.annotation.Nullable;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.AdvanceVideo;
import com.cinebrah.cinebrah.net.models.RoomActionResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class SkipVideoRequest extends RetrofitSpiceRequest<RoomActionResponse, CinebrahEndpoints> {

    String roomId;
    AdvanceVideo advanceVideo;
    String formattedToken;

    public SkipVideoRequest(String roomId, String registrationId, String videoId,
                            @Nullable String formattedToken) {
        super(RoomActionResponse.class, CinebrahEndpoints.class);
        this.roomId = roomId;
        this.formattedToken = formattedToken;
        this.advanceVideo = new AdvanceVideo(registrationId, videoId);
    }

    @Override
    public RoomActionResponse loadDataFromNetwork() throws Exception {
        if (formattedToken == null) {
            return getService().requestSkipCurrentVideo(roomId, advanceVideo);
        } else {
            return getService().requestSkipCurrentVideo(roomId, advanceVideo, formattedToken);
        }
    }
}
