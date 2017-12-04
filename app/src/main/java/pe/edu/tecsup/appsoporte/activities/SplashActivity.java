package pe.edu.tecsup.appsoporte.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.util.CurrentActivityListener;
import pe.edu.tecsup.appsoporte.util.PreferencesManager;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // CurrentActivityListener by ActivityLifecycleCallbacks
        this.getApplication().registerActivityLifecycleCallbacks(new CurrentActivityListener());

        new Handler().postDelayed(new Runnable() {
            public void run() {
                verifyLogged();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void verifyLogged(){
        if(PreferencesManager.getInstance(this).get(PreferencesManager.PREF_ISLOGGED) != null){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }else{
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }

}
