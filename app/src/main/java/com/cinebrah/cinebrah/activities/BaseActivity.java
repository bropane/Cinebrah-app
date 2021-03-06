package com.cinebrah.cinebrah.activities;

import android.support.v7.app.ActionBarActivity;

import com.cinebrah.cinebrah.net.CinebrahApiService;
import com.octo.android.robospice.SpiceManager;

/**
 * Created by Taylor on 10/30/2014.
 */
public class BaseActivity extends ActionBarActivity {

    private SpiceManager spiceManager = new SpiceManager(CinebrahApiService.class);

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        spiceManager.shouldStop();
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }*/

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                AppConstants.startAboutActivity(this);
                return true;
            *//*case R.id.action_signout:
                Intent logoutIntent = new Intent(this, LoginActivity.class);
                logoutIntent.setAction(LoginActivity.LOGOUT_ACTION);
                this.startActivity(logoutIntent);
                finish();
                return true;*//*
        }
        return super.onOptionsItemSelected(item);
    }*/
}
