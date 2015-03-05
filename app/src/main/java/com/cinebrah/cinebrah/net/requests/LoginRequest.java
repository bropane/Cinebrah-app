package com.cinebrah.cinebrah.net.requests;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.net.CinebrahEndpoints;
import com.cinebrah.cinebrah.net.models.AccountCredential;
import com.cinebrah.cinebrah.net.models.Token;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * Created by Taylor on 3/4/2015.
 */
public class LoginRequest extends RetrofitSpiceRequest<Token, CinebrahEndpoints> {

    AccountCredential credential;

    public LoginRequest(String username, String password) {
        super(Token.class, CinebrahEndpoints.class);
        this.credential = new AccountCredential(username, password);
    }

    @Override
    public Token loadDataFromNetwork() throws Exception {
        Token token = null;
        try {
            token = getService().login(credential);
        } catch (RetrofitError e) {
            Timber.e(e, "Could not login");
            BaseApplication.getBus().post(new IoError(e));
        }
        return token;
    }

    public static class IoError {
        RetrofitError e;

        protected IoError(RetrofitError e) {
            this.e = e;
        }

        public RetrofitError getError() {
            return e;
        }
    }
}
