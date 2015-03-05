package com.cinebrah.cinebrah.net.requests;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.RoomDetailed;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/3/2015.
 */
public class GetRoomDetailedRequest extends RetrofitSpiceRequest<RoomDetailed, CinebrahEndpoints> {

    private String roomId;

    public GetRoomDetailedRequest(String roomId) {
        super(RoomDetailed.class, CinebrahEndpoints.class);
        this.roomId = roomId;
    }

    @Override
    public RoomDetailed loadDataFromNetwork() throws Exception {
        return getService().getRoomDetailed(roomId);
    }
}
