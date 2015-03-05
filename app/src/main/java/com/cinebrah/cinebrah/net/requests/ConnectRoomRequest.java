package com.cinebrah.cinebrah.net.requests;

import android.support.annotation.Nullable;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.RegistrationId;
import com.cinebrah.cinebrah.net.models.RoomConnectionResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class ConnectRoomRequest extends RetrofitSpiceRequest<RoomConnectionResponse, CinebrahEndpoints> {

    String roomId;
    RegistrationId registrationId;
    String formattedToken;

    public ConnectRoomRequest(String roomId, String registrationId, @Nullable String formattedToken) {
        super(RoomConnectionResponse.class, CinebrahEndpoints.class);
        this.roomId = roomId;
        this.registrationId = new RegistrationId(registrationId);
        this.formattedToken = formattedToken;
    }

    @Override
    public RoomConnectionResponse loadDataFromNetwork() throws Exception {
        if (formattedToken == null) {
            return getService().connectToRoom(roomId, registrationId);
        } else {
            return getService().connectToRoom(roomId, registrationId, formattedToken);
        }
    }
}
