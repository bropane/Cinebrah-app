package com.cinebrah.cinebrah.net;

/**
 * Created by Taylor on 3/3/2015.
 */

import com.cinebrah.cinebrah.utils.AppConstants;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class CinebrahApiService extends RetrofitGsonSpiceService {

    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(CinebrahEndpoints.class);
    }

    @Override
    protected String getServerUrl() {
        return AppConstants.APP_SERVER;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("User-Agent", "Cinebrah-Android-App");
                request.addHeader("Api-Key", AppConstants.CINEBRAH_API_KEY);
            }
        };
        return new RestAdapter.Builder()
                .setEndpoint(AppConstants.APP_SERVER)
                .setRequestInterceptor(requestInterceptor);
    }
}
