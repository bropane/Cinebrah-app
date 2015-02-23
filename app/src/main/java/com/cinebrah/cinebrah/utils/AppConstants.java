package com.cinebrah.cinebrah.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.appspot.cinebrahs.cinebrahApi.CinebrahApi;
import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.R;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.tundem.aboutlibraries.Libs;
import com.tundem.aboutlibraries.ui.LibsActivity;

/**
 * Created by Taylor on 9/3/2014.
 */
public class AppConstants {

    public static final String DEVELOPER_KEY = "AIzaSyAVqBNyJNaXBB3IZFbRQ4KQwucVlscOA8o";
    public static final String YOUTUBE_DATA_API_KEY = "AIzaSyB_X55scQwesxGYgmFJSy6TSfLGmVCH_Vc";
    public static final String WEB_CLIENT_ID = "576346265196-1pfmc8oeven5qv46r8ur4b8n4bgnbfve.apps.googleusercontent.com";
    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
    public static final String CINEBRAH_PREFS_NAME = "cinebrahprefs";
    public static final String KEY_DEFAULT_ACCOUNT = "default_account";
    public static final String KEY_IS_FIRST_LAUNCH = "is_first_launch";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_EMAIL = "email";

    /**
     * Class instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    /**
     * Class instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();


    /**
     * Retrieve a CinebrahApi api service handle to access the API.
     */
    public static CinebrahApi getApiServiceHandle(@Nullable GoogleAccountCredential credential) {
        // Use a builder to help formulate the API request.
        CinebrahApi.Builder service = new CinebrahApi.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY, credential);
        return service.build();
    }

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

    public static String getUserId() {
        String userId = AppConstants.getPreferences().getString(AppConstants.KEY_USER_ID, null);
        return userId;
    }

    public static void setUserId(String userId) {
        AppConstants.getPreferences().edit().putString(AppConstants.KEY_USER_ID, userId).commit();
    }

    public static String getStoredEmail() {
        String email = AppConstants.getPreferences().getString(AppConstants.KEY_EMAIL, null);
        return email;
    }

    public static void setStoredEmail(String email) {
        AppConstants.getPreferences().edit().putString(AppConstants.KEY_EMAIL, email).commit();
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
