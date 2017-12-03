package pe.edu.tecsup.appsoporte.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by ebenites on 11/01/2017.
 * http://motzcod.es/post/133609925342/access-the-current-android-activity-from-anywhere
 * http://baroqueworksdev.blogspot.pe/2012/12/how-to-use-activitylifecyclecallbacks.html
 */

public final class CurrentActivityListener implements Application.ActivityLifecycleCallbacks {

    private static Activity currentActivity;

    public CurrentActivityListener(){}

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
