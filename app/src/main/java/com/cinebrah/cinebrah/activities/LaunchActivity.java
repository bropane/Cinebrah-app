package com.cinebrah.cinebrah.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.net.ApiService;
import com.cinebrah.cinebrah.net.GcmManager;
import com.cinebrah.cinebrah.utils.AppConstants;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tr.xip.errorview.ErrorView;
import tr.xip.errorview.RetryListener;

public class LaunchActivity extends ActionBarActivity implements RetryListener {

    public static final String LOGOUT_FLAG_KEY = "logout";

    @InjectView(R.id.progress_bar_launch)
    ProgressWheel mWheel;

    @InjectView(R.id.error_view_launch)
    ErrorView mErrorView;

    @InjectView(R.id.iv_cinebrah_icon)
    ImageView cinebrahIcon;

    boolean gettingGcm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.inject(this);
        BaseApplication.getBus().register(this);
        initGcm();
        if (!gettingGcm) {
            init();
        }

//        mErrorView.setErrorTitle("DYEL?");
//        mErrorView.setErrorSubtitle("Could not connect");
        mErrorView.setRetryButtonText("RETRY");
        mErrorView.setOnRetryListener(this);
    }

    private void initGcm() {
        String regId = GcmManager.getRegistrationId();
        if (regId.isEmpty() || regId == null) {
            gettingGcm = true;
            new GcmManager().register();
        }
    }

    private void init() {
        String userId = AppConstants.getUserId();
        String regId = GcmManager.getRegistrationId();
        String email = AppConstants.getStoredEmail();
        if (!regId.isEmpty() || regId != null) {
            if (email != null) {
                login();
            } else if (userId != null) {
                login(userId);
            } else {
                register();
            }
        } else {
            showErrorView();
        }
    }

    private void showErrorView() {
        mErrorView.setVisibility(View.VISIBLE);
        mWheel.setVisibility(View.INVISIBLE);
        cinebrahIcon.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getBus().unregister(this);
    }

    private void register() {
        BaseApplication.getApiService().init();
        BaseApplication.getApiService().registerAnon();
    }

    private void login() {
        //Use this is logging in with Google Plus creds
        String email = AppConstants.getStoredEmail();
        BaseApplication.getApiService().init(email);
        BaseApplication.getApiService().login(null);
    }

    private void login(String userId) {
        BaseApplication.getApiService().init();
        BaseApplication.getApiService().login(userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onGcmRegisteredEvent(GcmManager.GcmRegisterEvent event) {
        init();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onRegisterEvent(ApiService.RegisterEvent event) {
        if (event.isSuccessful()) {
            startActivity(new Intent(this, RoomChooserActivity.class));
            finish();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onLoginEvent(ApiService.UserDataEvent event) {
        AppConstants.setUserId(event.getUserId());
        startActivity(new Intent(this, RoomChooserActivity.class));
        finish();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onIoErrorEvent(final ApiService.IOErrorEvent event) {
        LaunchActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    GoogleJsonResponseException e = (GoogleJsonResponseException) event.getException();
                    mErrorView.setError(e.getStatusCode());
                } catch (ClassCastException e) {
                    mErrorView.setErrorTitle(R.string.error_view_title);
                    mErrorView.setErrorSubtitle(R.string.error_view_subtitle);
                }
                showErrorView();
            }
        });
    }

    @Override
    public void onRetry() {
        mWheel.setVisibility(View.VISIBLE);
        init();
    }
}
