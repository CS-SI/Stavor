package settings;

import cs.si.satatt.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsBasicFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.basic_indicators);
    }
}
