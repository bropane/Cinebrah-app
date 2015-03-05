package com.cinebrah.cinebrah.net.requests;

import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.AccountCredential;
import com.cinebrah.cinebrah.net.models.Registered;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by Taylor on 3/4/2015.
 */
public class RegisterRequest extends RetrofitSpiceRequest<Registered, CinebrahEndpoints> {

    AccountCredential credential;

    public RegisterRequest(String username, String password, String email) {
        super(Registered.class, CinebrahEndpoints.class);
        this.credential = new AccountCredential(username, password, email);
    }

    @Override
    public Registered loadDataFromNetwork() throws Exception {
        return getService().register(credential);
    }
}
