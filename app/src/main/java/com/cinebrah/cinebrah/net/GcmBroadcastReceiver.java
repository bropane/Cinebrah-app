package com.cinebrah.cinebrah.net;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.cinebrah.cinebrah.BaseApplication;

/**
 * Created by Taylor on 9/5/2014.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        String regId = intent.getExtras().getString("registration_id");

        if (regId != null && !regId.equals("")) {
        /* Now we can do what ever we want with the regId:
        * 1. send it to our server
        * 2. store it once successfuly registered on the server side */
            GcmManager.storeRegistrationId(regId);
            BaseApplication.getBus().post(new GcmManager.GcmRegisterEvent(regId));
        }
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

    }
}
