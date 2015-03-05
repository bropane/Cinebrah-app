package com.cinebrah.cinebrah.net.requests;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class LogoutRequest extends RetrofitSpiceRequest<Void, CinebrahEndpoints> {

    String formattedToken;

    public LogoutRequest(String formattedToken) {
        super(Void.class, CinebrahEndpoints.class);
        this.formattedToken = formattedToken;
    }

    @Override
    public Void loadDataFromNetwork() throws Exception {
        getService().logout(formattedToken);
        return null;
    }
}
