package fragments;

import java.util.ArrayList;
import java.util.List;

import simulator.Simulator;
import simulator.SimulatorStatus;
import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import database.MissionReaderContract.MissionEntry;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ViewSwitcher;

/**
 * A simulator fragment containing a web view.
 */
public final class SimulatorFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 * @param sim_config 
	 */
	public static SimulatorFragment newInstance(int sectionNumber) {	
		SimulatorFragment fragment = new SimulatorFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SimulatorFragment() {
	}
	
	public Simulator simulator;
	
	Switch switch_remote;
	ViewSwitcher sim_container;
	SharedPreferences sharedPref;
	Button button_connect;
	AutoCompleteTextView host_view;
	EditText port_view;
	ListView missionsList;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.sim, container,
				false);
		
		//sharedPref = PreferenceManager.getDefaultSharedPreferences(container.getContext());
		sharedPref = this.getActivity().getSharedPreferences("cs.si.satatt", Context.MODE_PRIVATE);
		
		//simulator = (Simulator) getArguments().getSerializable(ARG_SIM_OBJ);
		simulator = ((MainActivity)getActivity()).getSimulator();
    	//simulator.setHudView(null);
		
		
		//Load missions in list
		missionsList = (ListView) rootView.findViewById(R.id.listView1);
		loadMissionList();
    	
    	switch_remote = (Switch) rootView.findViewById(R.id.switch1);
    	sim_container = (ViewSwitcher) rootView.findViewById(R.id.sim_content);
    	loadCorrectSimulatorScreen(rootView);
    	switch_remote.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        // Do Something
    	    	sharedPref.edit().putBoolean(buttonView.getContext().getString(R.string.pref_key_sim_global_remote), isChecked).commit();
    	    	loadCorrectSimulatorScreen(buttonView);
    	    }
    	});
    	
    	host_view = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextViewHost);
    	port_view = (EditText) rootView.findViewById(R.id.editTextPort);
    	
    	button_connect = (Button) rootView.findViewById(R.id.buttonConnect);
    	simulator.setButtonConnect(button_connect);
    	simulator.setSwitchSelector(switch_remote);
    	button_connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
            		simulator.disconnect();
            	}else{
            		boolean remote = sharedPref.getBoolean(v.getContext().getString(R.string.pref_key_sim_global_remote), false);
            		if(remote){
	            		sharedPref.edit().putString(v.getContext().getString(
	            				R.string.pref_key_sim_remote_host),
	            				host_view.getText().toString()
	            				).commit();
	            		sharedPref.edit().putString(v.getContext().getString(
	            				R.string.pref_key_sim_remote_port), 
	            				port_view.getText().toString()
	            				).commit();
            		}else{
            			//Set mission
            		}
            		//Log.d("Sim",System.currentTimeMillis()+": "+"button connect pressed");
            		simulator.connect();
            		//Log.d("Sim",System.currentTimeMillis()+": "+"button connect onlick ends");
            	}
            }
        });
    	
		return rootView;
	}
    

	private void loadMissionList() {
		SQLiteDatabase db = ((MainActivity)getActivity()).db;

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
		    MissionEntry._ID,
		    MissionEntry.COLUMN_NAME_NAME,
		    MissionEntry.COLUMN_NAME_DESCRIPTION,
		    };

		// How you want the results sorted in the resulting Cursor
		String sortOrder =
		    MissionEntry.COLUMN_NAME_NAME + " ASC";

		Cursor c = db.query(
		    MissionEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    "",                                // The columns for the WHERE clause
		    null,                            // The values for the WHERE clause
		    "",                                     // don't group the rows
		    "",                                     // don't filter by row groups
		    sortOrder                                 // The sort order
		    );

	    c.moveToFirst(); 

	    
	    ListAdapter adapter=new SimpleCursorAdapter(this.getActivity().getApplicationContext(),
	                     R.layout.mission_list_item, c,
	                     new String[] {"_id", "name", "description"},
	                     new int[] {R.id.textViewMissionId, R.id.textViewMission, R.id.textViewMissionDescription}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
	    missionsList.setAdapter(adapter); 
		
	}

	private void loadCorrectSimulatorScreen(View view) {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(view.getContext().getString(R.string.pref_key_sim_global_remote), false);
    	switch_remote.setChecked(remote);
    	if(remote){
    		// Remote
    		sim_container.setDisplayedChild(1); 
    	}else{
    		// Local
    		sim_container.setDisplayedChild(0);
    	}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
