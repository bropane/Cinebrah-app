package com.cinebrah.cinebrah.net.requests;

import android.support.annotation.Nullable;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.RegistrationId;
import com.cinebrah.cinebrah.net.models.RoomConnectionResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class ConnectRandomRoomRequest extends RetrofitSpiceRequest<RoomConnectionResponse, CinebrahEndpoints> {

    RegistrationId registrationId;
    String formattedToken;

    public ConnectRandomRoomRequest(String registrationId, @Nullable String formattedToken) {
        super(RoomConnectionResponse.class, CinebrahEndpoints.class);
        this.registrationId = new RegistrationId(registrationId);
        this.formattedToken = formattedToken;
    }

    @Override
    public RoomConnectionResponse loadDataFromNetwork() throws Exception {
        if (formattedToken == null) {
            return getService().connectToRandomRoom(registrationId);
        } else {
            return getService().connectToRandomRoom(registrationId, formattedToken);
        }
    }
}
