package com.cinebrah.cinebrah.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.cinebrah.cinebrah.net.GcmManager;
import com.cinebrah.cinebrah.net.models.Token;
import com.cinebrah.cinebrah.net.requests.LoginRequest;
import com.cinebrah.cinebrah.utils.AccountStore;
import com.octo.android.robospice.exception.NetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import timber.log.Timber;
import tr.xip.errorview.ErrorView;
import tr.xip.errorview.RetryListener;

public class LaunchActivity extends BaseActivity implements RetryListener, RequestListener<Token> {
    // TODO need to test the caching mechanisms of robospice for logins

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
        // Retrieves GCM registration ID is needed
        initGcm();
        if (!gettingGcm) {
            init();
        }
        // If retrieving gcm id, normal flow will continue after that has finished
        mErrorView.setErrorTitle(getString(R.string.dyel));
        mErrorView.setErrorSubtitle(getString(R.string.launch_error_default));
        mErrorView.setRetryButtonText(getString(R.string.launch_error_button));
        mErrorView.setOnRetryListener(this);
    }

    private void initGcm() {
        String regId = GcmManager.getRegistrationId();
        if ((regId == null) || regId.isEmpty()) {
            gettingGcm = true;
            new GcmManager().register();
        }
    }

    private void init() {
        String username = AccountStore.getUsername();
        String password = AccountStore.getPassword();
        if (username != null && password != null) {
            login(username, password);
        } else {
            // If no user credentials are set, no need to login. Start anonymously
            startRoomChooserActivity();
        }
    }

    private void showErrorView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mErrorView.setVisibility(View.VISIBLE);
                mWheel.setVisibility(View.INVISIBLE);
                cinebrahIcon.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getBus().unregister(this);
    }

    private void login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        getSpiceManager().execute(request, this);
    }

    @Override
    public void onRequestSuccess(Token token) {
        if (token != null) {
            AccountStore.storeToken(token);
            startRoomChooserActivity();
        } else {

            showErrorView();
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        if (spiceException instanceof NetworkException) {
            NetworkException exception = (NetworkException) spiceException;
            Timber.e(exception, exception.getMessage());
        }
        showErrorView();
    }

    @Subscribe
    public void onLoginIoErrorEvent(LoginRequest.IoError event) {
        RetrofitError error = event.getError();
        int code = error.getResponse().getStatus();
        switch (code) {
            case 400:
                /*Most likely incorrect login credentials,
                    ignore and login anonymously*/
                startRoomChooserActivity();
                return;
            default:
                mErrorView.setError(code);
                showErrorView();
        }
    }

    public void startRoomChooserActivity() {
        startActivity(new Intent(this, RoomChooserActivity.class));
        finish();
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

    /*@SuppressWarnings("unused")
    @Subscribe
    public void onIoErrorEvent(final ApiServiceOld.IOErrorEvent event) {
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
    }*/

    @Override
    public void onRetry() {
        mWheel.setVisibility(View.VISIBLE);
        init();
    }
}
