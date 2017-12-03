package pe.edu.tecsup.appsoporte.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import pe.edu.tecsup.appsoporte.util.Constants;
import pe.edu.tecsup.appsoporte.util.PreferencesManager;

/**
 * Created by ebenites on 2/03/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // Saving gcm token to shared preferences
        PreferencesManager.getInstance(this).set(PreferencesManager.PREF_TOKEN_GCM, refreshedToken);

        // Suscribe to topics
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_TOPIC_SUPPORTS);

        // sending gcm token to your server or sending when login to match username with token
        //sendRegistrationToServer(refreshedToken);
    }

}
