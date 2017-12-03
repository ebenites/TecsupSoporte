package pe.edu.tecsup.appsoporte.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.activities.MainActivity;
import pe.edu.tecsup.appsoporte.util.Constants;
import pe.edu.tecsup.appsoporte.util.PreferencesManager;

/**
 * Created by ebenites on 2/03/2017.
 * https://stackoverflow.com/questions/37711082/how-to-handle-notification-when-app-in-background-in-firebase
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    public static int NOTIFICATION_ID = 0;

    /**
     * Notification Only Foreground
     * Data Foreground/Background
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        try {
            if(PreferencesManager.getInstance(this).get(PreferencesManager.PREF_ISLOGGED) == null) {
                Log.d(TAG, "Closed session!");
                return;
            }

            if(PreferencesManager.getInstance(this).get(PreferencesManager.PREF_SETTINGS_NOTIFICATION_OFF) != null){
                Log.d(TAG, "PREF_SETTINGS_NOTIFICATION_OFF!");
                return;
            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {

                Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                String sound = remoteMessage.getNotification().getSound();
                String icon = remoteMessage.getNotification().getIcon();
                String color = remoteMessage.getNotification().getColor();

                // Notification Builder
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                // Icon
                int iconId = (icon == null)? R.drawable.in_logo:this.getResources().getIdentifier(icon, "drawable", this.getPackageName());

                // Color
                int colorARGB = (color == null)? ContextCompat.getColor(this, R.color.primary): Color.argb(
                        255,
                        Integer.valueOf( color.substring( 1, 3 ), 16 ),
                        Integer.valueOf( color.substring( 3, 5 ), 16 ),
                        Integer.valueOf( color.substring( 5, 7 ), 16 )
                );

                // Sound
                Uri soundUri = (sound == null || "default".equals(sound))? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION): Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/raw/" + sound);

                // Intent
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                    Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());

                    JSONObject data = new JSONObject(remoteMessage.getData().toString());
                    intent.putExtra(Constants.FIREBASE_PAYLOAD_GO, data.getString(Constants.FIREBASE_PAYLOAD_GO));
                    // intent.putExtra("other", json.getString("other"));
                }

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                // Notification
                Notification notification = builder
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(iconId)
                        .setColor(colorARGB)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

                // Notification manager
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID++, notification);

                // Play sound
                RingtoneManager.getRingtone(this, soundUri).play();

            }else if (remoteMessage.getData().size() > 0) {
                // Check if message contains ONLY data payload (Foreground or Background).
                Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());

//                RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).play();
//
//                JSONObject data = new JSONObject(remoteMessage.getData().toString());
//                String type = data.getString(Constants.FIREBASE_PAYLOAD_GO);
//                // JSONObject product = data.getJSONObject("product");
//
//                if (!isAppIsInBackground(getApplicationContext())) {
//                    // app is in foreground, broadcast the push message
//                    Log.d(TAG, "Foreground App");
//                }else{
//                    // If the app is in background, firebase system tray itself handles the notification
//                    Log.d(TAG, "Background App");
//                }

                // TODO: ...

            }

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

//    private static boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }
//
//        return isInBackground;
//    }

}
