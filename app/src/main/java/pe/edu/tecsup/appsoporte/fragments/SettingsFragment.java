package pe.edu.tecsup.appsoporte.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.messaging.FirebaseMessaging;

import pe.edu.tecsup.appsoporte.R;
import pe.edu.tecsup.appsoporte.util.Constants;
import pe.edu.tecsup.appsoporte.util.PreferencesManager;

public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.title_settings);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // News Switch
        Switch newsSwitch = (Switch) view.findViewById(R.id.notification_switch);
        newsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Log.d(TAG, "subscribeToTopic FIREBASE_TOPIC_SUPPORTS");
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.FIREBASE_TOPIC_SUPPORTS);
                    PreferencesManager.getInstance().set(PreferencesManager.PREF_SETTINGS_NOTIFICATION_OFF, "1");
                }else{
                    Log.d(TAG, "unsubscribeFromTopic FIREBASE_TOPIC_SUPPORTS");
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FIREBASE_TOPIC_SUPPORTS);
                    PreferencesManager.getInstance().remove(PreferencesManager.PREF_SETTINGS_NOTIFICATION_OFF);
                }
            }
        });

        if(PreferencesManager.getInstance().get(PreferencesManager.PREF_SETTINGS_NOTIFICATION_OFF) != null)
            newsSwitch.setChecked(true);

        // ProgressBar Gone
        getActivity().findViewById(R.id.main_progress).setVisibility(View.GONE);

        return view;
    }

}
