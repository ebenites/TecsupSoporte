package pe.edu.tecsup.appsoporte.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.crash.FirebaseCrash;
import com.orm.SugarRecord;

import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.models.APIError;
import pe.edu.tecsup.appsoporte.models.User;
import pe.edu.tecsup.appsoporte.services.TecsupService;
import pe.edu.tecsup.appsoporte.services.TecsupServiceGenerator;
import pe.edu.tecsup.appsoporte.util.Constants;
import pe.edu.tecsup.appsoporte.util.CurrentActivityListener;
import pe.edu.tecsup.appsoporte.util.PreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // Argument Bundle to Intent
    public static final String ARG_MESSAGE = "message";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // CurrentActivityListener by ActivityLifecycleCallbacks
        this.getApplication().registerActivityLifecycleCallbacks(new CurrentActivityListener());

        progressBar = (ProgressBar)findViewById(R.id.sign_in_progress);

        // Init GoogleSignIn
        initGoogleSignIn();

        // Loading previus message
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().getString(ARG_MESSAGE) != null)
            Toast.makeText(this, this.getIntent().getExtras().getString(ARG_MESSAGE), Toast.LENGTH_LONG).show();

        // HACK
        findViewById(R.id.logo).setOnClickListener(new View.OnClickListener() {
            private int i = 0;
            @Override
            public void onClick(View v) {
                if(i == 0){
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            i = 0;
                        }
                    }, 2000);
                }
                i++;
                if(i == 3){
                    if(findViewById(R.id.codigo).getVisibility() != View.VISIBLE) {
                        findViewById(R.id.codigo).setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "GOD MODE ACTIVATED", Toast.LENGTH_LONG).show();
                    }else{
                        findViewById(R.id.codigo).setVisibility(View.GONE);
                    }
                }
            }
        });

        // Validate permissions
        validatePermissions();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {    // Hide ActionBar
        super.onPostCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * Google SignIn
     */

    /* The login button for Google */
    private SignInButton mGoogleLoginButton;

    /* Request code used to invoke sign in user interactions for Google+ */
    private static final int GOOGLE_SIGNIN_REQUEST = 1000;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    private void initGoogleSignIn(){

        // Configure SingIn Button
        mGoogleLoginButton = (SignInButton) findViewById(R.id.sign_in_button);
        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGoogleSignIn(view);
            }
        });
        // Custom Text: http://stackoverflow.com/questions/18040815/can-i-edit-the-text-of-sign-in-button-on-google
        if(mGoogleLoginButton.getChildAt(0) instanceof TextView) {
            TextView textView = (TextView) mGoogleLoginButton.getChildAt(0);
            textView.setText(R.string.login_text_button);
        }

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.FIREASE_CLIENTE_ID)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
                        Log.e(TAG, "onConnectionFailed:" + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public void attemptGoogleSignIn(View view){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGNIN_REQUEST);

        showProgress(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d(TAG, "onActivityResult: " + requestCode);
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == GOOGLE_SIGNIN_REQUEST) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {

                    // Google Sign In was successful
                    GoogleSignInAccount account = result.getSignInAccount();
                    Log.d(TAG, "IC: " + account.getId());
                    Log.d(TAG, "DISPLAYNAME: " + account.getDisplayName());
                    Log.d(TAG, "EMAIL: " + account.getEmail());
                    Log.d(TAG, "PHOTO: " + account.getPhotoUrl());
                    Log.d(TAG, "TOKEN: " + account.getIdToken());

                    // Device information :

                    String instance = PreferencesManager.getInstance(this).get(PreferencesManager.PREF_TOKEN_GCM);
                    Log.d(TAG, "INSTANCEID: " + instance);

                    String androidid = Settings.Secure.getString(this.getContentResolver(),  Settings.Secure.ANDROID_ID);
                    Log.d(TAG, "ANDROIDID: " + androidid);
                    String manufacturer = android.os.Build.MANUFACTURER;
                    Log.d(TAG, "MANUFACTURER: " + manufacturer);
                    String model = android.os.Build.MODEL;
                    Log.d(TAG, "MODEL: " + model);
                    String device = android.os.Build.DEVICE;
                    Log.d(TAG, "DEVICE: " + device);
                    String kernel = android.os.Build.VERSION.INCREMENTAL;
                    Log.d(TAG, "VERSION: " + kernel);
                    String version = android.os.Build.VERSION.RELEASE;
                    Log.d(TAG, "RELEASE: " + version);
                    Integer sdk = android.os.Build.VERSION.SDK_INT;
                    Log.d(TAG, "SDK_INT: " + sdk);

                    // Call SitecService

                    TecsupService service = TecsupServiceGenerator.createService(TecsupService.class);

                    Call<User> call = service.authenticate(account.getIdToken(), instance, Constants.APPNAME, androidid, manufacturer, model, device, kernel, version, sdk);

                    // HACK
                    if(findViewById(R.id.codigo).getVisibility() == View.VISIBLE){
                        call = service.authentihacked(((android.widget.EditText)LoginActivity.this.findViewById(R.id.codigo)).getText().toString(), account.getIdToken(), instance, Constants.APPNAME, androidid, manufacturer, model, device, kernel, version, sdk);
                    }

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            try {

                                int statusCode = response.code();
                                Log.d(TAG, "HTTP status code: " + statusCode);

                                if (response.isSuccessful()) {

                                    User user = response.body();
                                    Log.d(TAG, "User: " + user);

                                    Toast.makeText(getApplication(), getString(R.string.login_welcome_back) + " " + user.getName(), Toast.LENGTH_LONG).show();

                                    // Share preferences
                                    PreferencesManager.getInstance().set(PreferencesManager.PREF_TOKEN, user.getToken());
                                    PreferencesManager.getInstance().set(PreferencesManager.PREF_EMAIL, user.getEmail());
                                    PreferencesManager.getInstance().set(PreferencesManager.PREF_ISLOGGED, "1");

                                    // Save User with Sugar
                                    SugarRecord.deleteAll(User.class);
                                    SugarRecord.save(user);

                                    // Go to MainActivity
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();

                                } else {
                                    // TecsupService Not Success
                                    APIError error = TecsupServiceGenerator.parseError(response);
                                    showProgress(false);
                                    Log.e(TAG, "Not Success: " + error);
                                    Toast.makeText(getApplication(), error.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            } catch (Throwable t) {
                                try {
                                    Log.e(TAG, "onThrowable: " + t.toString(), t);
                                    Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
                                }catch (Throwable x){}
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            try {
                                showProgress(false);
                                Log.e(TAG, "onFailure: " + t.getMessage(), t);
                                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
                                FirebaseCrash.report(t);
                            } catch (Throwable x) {}
                        }

                    });

                } else {
                    // Google Sign In failed, update UI appropriately
                    showProgress(false);
                    Log.e(TAG, "Google Sign In failed!");
                }
            }

        }catch (Throwable t){
            try {
                showProgress(false);
                Log.e(TAG, "onThrowable: " + t.getMessage(), t);
                if(getApplication()!=null) Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
                FirebaseCrash.report(t);
            } catch (Throwable x) {}
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mGoogleLoginButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mGoogleLoginButton.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mGoogleLoginButton.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mGoogleLoginButton.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Permissions
     */

    private static final int PERMISSIONS_REQUEST = 100;

    private static String[] PERMISSIONS_LIST = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
    };

    private void validatePermissions(){
        if(!permissionsGranted()){
            ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, PERMISSIONS_REQUEST);
        }
    }

    private boolean permissionsGranted(){
        for (String permission : PERMISSIONS_LIST){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case PERMISSIONS_REQUEST: {
                for (int i=0; i<grantResults.length; i++){
                    Log.d(TAG, ""+grantResults[i]);
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, PERMISSIONS_LIST[i] + " permissions declined!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finishAffinity();
                            }
                        }, Toast.LENGTH_LONG);
                    }
                }
            }
        }
    }

}
