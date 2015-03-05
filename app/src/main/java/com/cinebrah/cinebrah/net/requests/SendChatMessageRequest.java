package com.cinebrah.cinebrah.net.requests;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.SentMessage;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class SendChatMessageRequest extends RetrofitSpiceRequest<Void, CinebrahEndpoints> {

    String roomId;
    SentMessage message;
    String formattedToken;

    public SendChatMessageRequest(String roomId, String registrationId,
                                  String message, String formattedToken) {
        super(Void.class, CinebrahEndpoints.class);
        this.roomId = roomId;
        this.message = new SentMessage(registrationId, message);
        this.formattedToken = formattedToken;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        getService().sendChatMessage(roomId, message, formattedToken);
        return null;
    }
}
