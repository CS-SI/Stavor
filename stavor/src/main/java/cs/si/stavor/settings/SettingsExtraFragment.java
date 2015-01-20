package cs.si.stavor.settings;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.StavorApplication.TrackerName;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Settings of the extra indicators
 * @author Xavier Gibert
 *
 */
public class SettingsExtraFragment extends PreferenceFragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private static String screenName = "Settings - ExtraIndicators";
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SettingsExtraFragment newInstance(int sectionNumber) {	
		SettingsExtraFragment fragment = new SettingsExtraFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
		((MainActivity) activity).raiseLoadBrowserFlag();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //********** Google Analytics ***********
          // Get tracker.
          Tracker t = ((StavorApplication) getActivity().getApplication()).getTracker(
              TrackerName.APP_TRACKER);
          t.setScreenName(screenName);
          t.send(new HitBuilders.AppViewBuilder().build());
          //***************************************
          
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.extra_indicators);
        
        //((MainActivity)getActivity()).showTutorialConfig();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_dark));

        return view;
    }
}
