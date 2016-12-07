package com.cinebrah.cinebrah.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.ui.LibsActivity;

/**
 * Created by Taylor on 9/3/2014.
 */
public class AppConstants {

    public static final String DEVELOPER_KEY = "key";
    public static final String YOUTUBE_DATA_API_KEY = "key";
    public static final String WEB_CLIENT_ID = "googleusercontent.com";
    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
    public static final String CINEBRAH_PREFS_NAME = "cinebrahprefs";
    public static final String KEY_DEFAULT_ACCOUNT = "default_account";
    public static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
    public static final String APP_SERVER = "https://cinebrah.herokuapp.com";
    public static final String DEV_APP_SERVER = "http://127.0.0.1:5000";
    public static final String CINEBRAH_API_KEY = "key";

    public static int countGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            return 0;
        } else {
            return accounts.length;
        }
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    public static SharedPreferences getPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return BaseApplication.getContext().getSharedPreferences(AppConstants.CINEBRAH_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    public static boolean isFirstLaunch() {
        boolean isFirstLaunch;
        SharedPreferences preferences = AppConstants.getPreferences();
        isFirstLaunch = preferences.getBoolean(KEY_IS_FIRST_LAUNCH, true);
        return isFirstLaunch;
    }

    public static void setFirstLaunch(boolean launch) {
        AppConstants.getPreferences().edit().putBoolean(KEY_IS_FIRST_LAUNCH, launch).commit();
    }

    public static void startAboutActivity(Context context) {
        Intent i = new Intent(context, LibsActivity.class);
        i.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));

        i.putExtra(Libs.BUNDLE_LIBS, new String[]{"crouton", "Otto"});

        //Display the library license (OPTIONAL
        i.putExtra(Libs.BUNDLE_LICENSE, true);

        //Set a title (OPTIONAL)
        i.putExtra(Libs.BUNDLE_TITLE, "Libraries");

        //start the activity
        context.startActivity(i);
    }


}
