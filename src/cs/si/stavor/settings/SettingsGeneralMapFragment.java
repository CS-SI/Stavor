package cs.si.stavor.settings;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import cs.si.stavor.MainActivity;
import cs.si.stavor.R;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.StavorApplication.TrackerName;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * General settings of the application
 * @author Xavier Gibert
 *
 */
public class SettingsGeneralMapFragment extends PreferenceFragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	
	private static String screenName = "Settings - GeneralMap";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SettingsGeneralMapFragment newInstance(int sectionNumber) {	
		SettingsGeneralMapFragment fragment = new SettingsGeneralMapFragment();
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
        addPreferencesFromResource(R.xml.preferences_map);
        
        //((MainActivity)getActivity()).showTutorialConfigMap();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_dark));

        return view;
    }
}
