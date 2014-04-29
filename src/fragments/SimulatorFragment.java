package fragments;

import java.io.Serializable;

import model.ModelSimulation;
import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TabHost;
import android.widget.ToggleButton;

/**
 * A simulator fragment containing a web view.
 */
public final class SimulatorFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_SIM_OBJ = "simulation_object";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 * @param sim_config 
	 */
	public static SimulatorFragment newInstance(int sectionNumber, ModelSimulation simulation) {	
		SimulatorFragment fragment = new SimulatorFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putSerializable(ARG_SIM_OBJ, (Serializable) simulation);
		fragment.setArguments(args);
		return fragment;
	}

	public SimulatorFragment() {
	}
	
	public ModelSimulation sim;
	
	ToggleButton button_local;
	ToggleButton button_remote;
	SharedPreferences sharedPref;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.simulator, container,
				false);
		
		//sharedPref = PreferenceManager.getDefaultSharedPreferences(container.getContext());
		sharedPref = this.getActivity().getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
		
		sim = (ModelSimulation) getArguments().getSerializable(ARG_SIM_OBJ);
    	sim.setCurrentView(rootView);
    	
    	button_local = (ToggleButton) rootView.findViewById(R.id.toggleButton1);
    	button_local.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        if (isChecked) {
    	            // The toggle local is enabled
    	        	button_remote.setChecked(false);
    	        } else {
    	            // The toggle local is disabled
    	        	button_remote.setChecked(true);
    	        }
    	        sharedPref.edit().putBoolean(buttonView.getContext().getString(R.string.pref_key_sim_global_remote), !isChecked).commit();
    	    }
    	});
    	button_remote = (ToggleButton) rootView.findViewById(R.id.toggleButton2);
    	button_remote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        if (isChecked) {
    	            // The toggle remote is enabled
    	        	button_local.setChecked(false);
    	        } else {
    	            // The toggle remote is disabled
    	        	button_local.setChecked(true);
    	        }
    	        sharedPref.edit().putBoolean(buttonView.getContext().getString(R.string.pref_key_sim_global_remote), isChecked).commit();
    	    }
    	});
    	
    	loadCorrectSimulatorScreen(rootView);
    	
		 /*TabHost mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
		 mTabHost.getTabWidget().setStripEnabled(true);
		*/
		/*TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(
				ARG_SECTION_NUMBER)));*/
		return rootView;
	}
    

	private void loadCorrectSimulatorScreen(View view) {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(view.getContext().getString(R.string.pref_key_sim_global_remote), false);
		button_local.setChecked(!remote);
    	button_remote.setChecked(remote);
    	if(remote){
    		// Remote
    	}else{
    		// Local
    	}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
