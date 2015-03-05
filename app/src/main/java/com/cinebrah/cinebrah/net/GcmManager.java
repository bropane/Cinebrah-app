package com.cinebrah.cinebrah.net;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.cinebrah.cinebrah.BaseApplication;
import com.cinebrah.cinebrah.utils.AppConstants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by Taylor on 9/5/2014.
 */
public class GcmManager {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String LOG_TAG = "GcmManager";
    String SENDER_ID = "576346265196";
    GoogleCloudMessaging gcm;

    public GcmManager() {
        gcm = GoogleCloudMessaging.getInstance(BaseApplication.getContext());
    }

    public static String getRegistrationId() {
        final SharedPreferences prefs = AppConstants.getPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(LOG_TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;

    }

    public static void clearRegistrationId() {
        final SharedPreferences prefs = AppConstants.getPreferences();
        prefs.edit().remove(PROPERTY_REG_ID).remove(PROPERTY_APP_VERSION).commit();
    }

    private static int getAppVersion() {
        try {
            PackageInfo packageInfo = BaseApplication.getContext().getPackageManager()
                    .getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void storeRegistrationId(String regId) {
        final SharedPreferences prefs = AppConstants.getPreferences();
        int appVersion = getAppVersion();
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    public void register() {
        AsyncTask task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String regId = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(BaseApplication.getContext());
                    }
                    regId = gcm.register(SENDER_ID);
                    Timber.i("Device registered, registration ID=%s", regId);
                    // Persist the regID - no need to register again.
                    storeRegistrationId(regId);
                } catch (IOException ex) {
                    Timber.e(ex, "Could not get registration ID");
//                    BaseApplication.getBus().post(new ApiServiceOld.IOErrorEvent(ex));
                    this.cancel(true);
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return regId;
            }

            @Override
            protected void onPostExecute(String regId) {
//                BaseApplication.getBus().post(new GcmRegisterEvent(regId));
            }
        }.execute();
    }

    public static class GcmRegisterEvent {

        String regId;

        GcmRegisterEvent(String regId) {
            this.regId = regId;
        }

        public String getRegId() {
            return regId;
        }
    }


}
