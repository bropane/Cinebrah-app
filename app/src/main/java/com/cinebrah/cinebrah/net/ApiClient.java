package com.cinebrah.cinebrah.net;

import com.cinebrah.cinebrah.utils.AppConstants;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by Taylor on 3/2/2015.
 */
public class ApiClient {

    public static CinebrahEndpoints getCinebrahService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Content-Type", "application/json");
                request.addHeader("User-Agent", "Cinebrah-Android-App");
            }
        };
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(AppConstants.APP_SERVER)
                .setRequestInterceptor(requestInterceptor)
                .build();
        return restAdapter.create(CinebrahEndpoints.class);
    }

}
