package pe.edu.tecsup.appsoporte.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import pe.edu.tecsup.appsoporte.util.PreferencesManager;

/**
 * Created by ebenites on 26/06/2017.
 */

public class FCMBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = FCMBroadcastReceiver.class.getSimpleName();

    private static final String ACTION_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION";
    private static final String ACTION_RECEIVE = "com.google.android.c2dm.intent.RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());
        try{

            for (String key : intent.getExtras().keySet()) {
                Log.d(TAG, "Extras: key->"  + key + ", value->" + intent.getExtras().get(key));
            }

            switch (intent.getAction()) {
                case ACTION_REGISTRATION:
                    // TODO
                    break;

                case ACTION_RECEIVE:

                    if(PreferencesManager.getInstance(context).get(PreferencesManager.PREF_ISLOGGED) == null) {
                        Log.d(TAG, "Closed session!");
                        abortBroadcast();
                        return;
                    }

                    if(PreferencesManager.getInstance(context).get(PreferencesManager.PREF_SETTINGS_NOTIFICATION_OFF) != null){
                        Log.d(TAG, "PREF_SETTINGS_NOTIFICATION_OFF!");
                        abortBroadcast();
                        return;
                    }

                    break;

                default:
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}
