package com.cinebrah.cinebrah;

import android.app.Application;
import android.content.Context;

import com.cinebrah.cinebrah.net.ApiService;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import timber.log.Timber;

/**
 * Created by Taylor on 8/13/2014.
 */
public class BaseApplication extends Application {

    private static Context mContext;

    private static Bus mBus;

    private static ApiService mApi;

    public static ApiService getApiService() {
        return mApi;
    }

    public static Context getContext() {
        return mContext;
    }

    public static Bus getBus() {
        return mBus;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mBus = new Bus(ThreadEnforcer.ANY);
        mApi = new ApiService();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
