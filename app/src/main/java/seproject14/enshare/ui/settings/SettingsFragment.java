package seproject14.enshare.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import seproject14.enshare.R;
import seproject14.enshare.ui.MainActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public String sharingOption;
    public static boolean anonymousState;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferances);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSummaryEditPreferance(getPreferenceManager().getSharedPreferences());
        setSummaryListPreferance(getPreferenceManager().getSharedPreferences());
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //sharingOption = sharedPreferences.getString("sharing_options", "");
        anonymousState = sharedPreferences.getBoolean(getString(R.string.anonymous), true);
        if(key.equals(getString(R.string.username))){
            Log.d("*********yay*****", "Working!"+ "" + key + " " +sharedPreferences.getString(key, "") +
                    getString(R.string.username));
            Log.d("*********yay*****", "Working! called" + getSharingOption() + getAnonymousState());
            ((MainActivity)getActivity()).setAccountName(sharedPreferences.getString(key, ""));
            setSummaryEditPreferance(sharedPreferences);
        }
        if(key.equals(getString(R.string.sharing_options))){
            Log.d("********yay2*****", sharedPreferences.getString(key, "") +"*****");
            setSharingOptions(sharedPreferences.getString(key, ""));
            setSummaryListPreferance(sharedPreferences);
        }
        if(key.equals(getString(R.string.anonymous))){
            Log.d("********yay3*****", sharedPreferences.getBoolean(key, true) +"*****");
            setAnonynousState(sharedPreferences.getBoolean(key, true));
        }

    }

    private void setSharingOptions(String option){
            sharingOption = option;
    }

    public String getSharingOption(){
        return sharingOption;
    }

    private void setAnonynousState(boolean state){
        anonymousState = state;
    }

    public boolean getAnonymousState(){
        return anonymousState;
    }

    private void setSummaryEditPreferance(SharedPreferences sharedPreferences){
        EditTextPreference editTextPref = (EditTextPreference) findPreference("name");
        String uName = ((MainActivity)getActivity()).getAccountName();
        if (uName != null) {
            uName = uName.split("@")[0];
        }
        Log.d("**********uname***********", "uname "+ uName);
      //  editTextPref.setSummary(sharedPreferences.getString("name", sharedPreferences.getString("name","")));
        editTextPref.setSummary(uName);

    }

    private void setSummaryListPreferance(SharedPreferences sharedPreferences){
        String options = getString(R.string.sharing_options);
        Log.d("*****yay4******","**********option******" + options);
        ListPreference listPreference = (ListPreference) findPreference(options);
        listPreference.setSummary(sharedPreferences.getString(options, sharedPreferences.getString(options,"")));
    }
}
