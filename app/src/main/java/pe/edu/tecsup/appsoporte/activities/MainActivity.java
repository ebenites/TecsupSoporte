package pe.edu.tecsup.appsoporte.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.orm.SugarRecord;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pe.edu.tecsup.appsoporte.BuildConfig;
import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.fragments.IncidentFragment;
import pe.edu.tecsup.appsoporte.fragments.SettingsFragment;
import pe.edu.tecsup.appsoporte.models.User;
import pe.edu.tecsup.appsoporte.services.TecsupService;
import pe.edu.tecsup.appsoporte.services.TecsupServiceGenerator;
import pe.edu.tecsup.appsoporte.util.Constants;
import pe.edu.tecsup.appsoporte.util.CurrentActivityListener;
import pe.edu.tecsup.appsoporte.util.NetworkUtils;
import pe.edu.tecsup.appsoporte.util.PreferencesManager;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    static {
        // https://github.com/square/picasso/issues/1109#issuecomment-228886243
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        // CurrentActivityListener by ActivityLifecycleCallbacks
        this.getApplication().registerActivityLifecycleCallbacks(new CurrentActivityListener());

        // Initialized preference manager
        PreferencesManager.getInstance(this);

        // Setear Toolbar como action bar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        User user = SugarRecord.last(User.class);
        Log.d(TAG, "User: " + user);

        String url = TecsupServiceGenerator.PHOTO_URL + user.getId() + "/photo.jpg";

        Picasso.with(this).load(url).into(new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "onBitmapLoaded");
                //Drawable d = new BitmapDrawable(getResources(), bitmap);

                int minSize = (bitmap.getWidth() > bitmap.getHeight())?bitmap.getHeight():bitmap.getWidth();
                int x = (bitmap.getWidth() - minSize)/2;
                int y = (bitmap.getHeight() - minSize)/2;

                bitmap = Bitmap.createBitmap(bitmap, x, y, minSize, minSize);

                bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

                //BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                drawable.setCircular(true);
                drawable.setAntiAlias(true);

                toolbar.setNavigationIcon(drawable);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(TAG, "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG, "onPrepareLoad");
            }
        });

        // BottomNavigation
        BottomNavigationView bottomNavigationView  = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_pending:
                        showPendingFragment();
                        break;
                    case R.id.menu_attending:
                        showAttendingFragment();
                        break;
                    case R.id.menu_closed:
                        showClosedFragment();
                        break;
                }
                return true;
            }
        });


        // Styling progress bar
        ((ProgressBar)findViewById(R.id.main_progress)).getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_ATOP);

        // Set UserIdentifier to Crashlytics
        Crashlytics.setUserIdentifier(PreferencesManager.getInstance(this).get(PreferencesManager.PREF_EMAIL));

        // Update instance id
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Log.d(TAG, "INSTANCEID: " + instanceIdResult.getToken());
                PreferencesManager.getInstance(MainActivity.this).set(PreferencesManager.PREF_TOKEN_GCM, instanceIdResult.getToken());
            }
        });

        // FirebaseAnalytics
        FirebaseAnalytics.getInstance(this).setUserId(PreferencesManager.getInstance().get(PreferencesManager.PREF_EMAIL));
        FirebaseAnalytics.getInstance(this).setUserProperty(PreferencesManager.PREF_EMAIL, PreferencesManager.getInstance().get(PreferencesManager.PREF_EMAIL));
        FirebaseAnalytics.getInstance(this).setUserProperty(PreferencesManager.PREF_SEDE, user.getSede());

        // Fragment inicial (Home)
        if (savedInstanceState == null) {
            showPendingFragment();
        }

        // Go From Notification
        onNewIntent(this.getIntent());

        // Check AutoUpdate
        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(TecsupServiceGenerator.API_BASE_URL + "/api/version/" + BuildConfig.APPLICATION_ID)
                .setButtonDoNotShowAgain(null)
//                .setCancelable(false)
                .start();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + intent);

        if(intent.getExtras() != null && intent.getExtras().containsKey(Constants.FIREBASE_PAYLOAD_GO)) {
            String go = intent.getExtras().getString(Constants.FIREBASE_PAYLOAD_GO);
            Log.d(TAG, Constants.FIREBASE_PAYLOAD_GO + ": " + go);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPendingFragment() {
        showFragment(IncidentFragment.newInstance(Constants.INCIDENT_STATUS_PENDIENT));
    }

    public void showAttendingFragment() {
        showFragment(IncidentFragment.newInstance(Constants.INCIDENT_STATUS_ATENTION));
    }

    public void showClosedFragment() {
        showFragment(IncidentFragment.newInstance(Constants.INCIDENT_STATUS_CLOSED));
    }

    public void showSettingsFragment(View view) {
        showFragment(new SettingsFragment());
    }

    private void showFragment(Fragment fragment){

        if (!NetworkUtils.isConnected(this)) {
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_empty).setTitle(R.string.connection_error).setMessage(R.string.disconnection_error).create().show();
            return;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
    }

    public void logout(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_logout)
                .setMessage("Si cierras la sesión NO recibirás notificaciones.")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeSession();
                        finish();
                    }
                }).create().show();
    }

    public static void removeSession(){
        try {
            String token = PreferencesManager.getInstance().get(PreferencesManager.PREF_TOKEN);
            Retrofit retrofit =  new Retrofit.Builder()
                    .baseUrl(TecsupServiceGenerator.API_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                    .build();
            Response<String> response = retrofit.create(TecsupService.class).logout(token).execute();
            Log.d(TAG, "Response: " + response.body());
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
        PreferencesManager.getInstance().remove(PreferencesManager.PREF_TOKEN);
//        PreferencesManager.getInstance().remove(PreferencesManager.PREF_EMAIL);
        PreferencesManager.getInstance().remove(PreferencesManager.PREF_ISLOGGED);
        SugarRecord.deleteAll(User.class);
    }

}
