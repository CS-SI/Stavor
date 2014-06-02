package fragments;

import mission.Mission;
import mission.MissionAndId;
import simulator.Simulator;
import simulator.SimulatorStatus;
import cs.si.satatt.AboutActivity;
import cs.si.satatt.MainActivity;
import cs.si.satatt.MissionActivity;
import cs.si.satatt.R;
import cs.si.satatt.SatAttApplication;
import database.MissionReaderDbHelper;
import database.MissionReaderContract.MissionEntry;
import database.SerializationUtil;
import dialogs.DeleteMissionDialogFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

/**
 * A simulator fragment containing a web view.
 */
public final class SimulatorFragment extends Fragment implements LoaderCallbacks<Cursor> {
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
		
		sharedPref = this.getActivity().getSharedPreferences("cs.si.satatt", Context.MODE_PRIVATE);
		
		simulator = ((MainActivity)getActivity()).getSimulator();
		
		//Load missions in list
		missionsList = (ListView) rootView.findViewById(R.id.listView1);
		missionsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				restoreMissionsBackground();
				if(arg1!=null){
					arg1.setBackgroundResource(R.drawable.mission_item_sel);
					activeMissionId = Integer.parseInt((String) ((TextView)arg1.findViewById(R.id.textViewMissionId)).getText());
					activeMissionName=(String) ((TextView)arg1.findViewById(R.id.textViewMission)).getText();
					/*Toast.makeText(getActivity().getApplicationContext(), "Active mission: "+activeMissionId,
			                Toast.LENGTH_LONG).show();*/
				}else{
					activeMissionId=-1;
					activeMissionName="";
				}
			}
		});
		adapter = new SimpleCursorAdapter(
	    		this.getActivity().getApplicationContext(),
	            R.layout.mission_list_item, null,
	            new String[] {"_id", "name", "description"},
	            new int[] {R.id.textViewMissionId, R.id.textViewMission, R.id.textViewMissionDescription}, 0 );

		missionsList.setAdapter(adapter);
	    registerForContextMenu(missionsList);
		getLoaderManager().initLoader(R.id.listView1, null, this);
    	
    	//Switch local/remote
    	switch_remote = (Switch) rootView.findViewById(R.id.switch1);
    	sim_container = (ViewSwitcher) rootView.findViewById(R.id.sim_content);
    	loadCorrectSimulatorScreen(rootView);
    	switch_remote.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
            	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
            		simulator.disconnect();
            		selectFirstMissionInList();
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
	            		simulator.connect();
            		}else{
            			//Set mission
            			MissionAndId mis = getSelectedMission();
            			if(mis!=null){
                			simulator.setSelectedMission(mis.mission, mis.id);
                			simulator.connect();
            			}else{
            				Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_cannot_deserialize_selected_mission), Toast.LENGTH_LONG).show();
            			}
            		}
            	}
            }
        });
    	
    	Button button_delete = (Button)rootView.findViewById(R.id.buttonMissionDelete);
    	button_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(activeMissionId==-1){
					Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_select_first_a_mission), Toast.LENGTH_LONG).show();
				}else if (activeMissionId==0 ||activeMissionId==1 ||activeMissionId==2 ){
					Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_mission_not_removable), Toast.LENGTH_LONG).show();
				}else{
					showDeleteMissionDialog(activeMissionId, activeMissionName);
				}
			}
    		
    	});
    	
    	Button button_new = (Button)rootView.findViewById(R.id.buttonMissionNew);
    	button_new.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				((MainActivity)getActivity()).showMissionCreator();
			}
    		
    	});
    	
    	Button button_edit = (Button)rootView.findViewById(R.id.buttonMissionEdit);
    	button_edit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {

    			MissionAndId mis = getSelectedMission();
    			if(mis!=null){
    				((MainActivity)getActivity()).showMissionEditor(mis);
    			}else{
    				Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_cannot_deserialize_selected_mission), Toast.LENGTH_LONG).show();
    			}
			}
    		
    	});
    	
		return rootView;
	}
	
	private MissionAndId getSelectedMission(){
		String[] projection = {
				MissionEntry._ID,
			    MissionEntry.COLUMN_NAME_CLASS
			    };

		Cursor c = ((SatAttApplication)((MainActivity)getActivity()).getApplication()).db
			.query(
					MissionEntry.TABLE_NAME,  // The table to query
				    projection,                               // The columns to return
				    MissionEntry._ID+" = ?",                                // The columns for the WHERE clause
				    new String[]{Integer.toString(activeMissionId)},                            // The values for the WHERE clause
				    "",                                     // don't group the rows
				    "",                                     // don't filter by row groups
				    null                                 // The sort order
				    );
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			int idIndex = c.getColumnIndex(MissionEntry._ID);
			int nameIndex = c.getColumnIndex(MissionEntry.COLUMN_NAME_CLASS);
			//this.itemId = cursor.getLong(idIndex);
			byte[] mission_serie = c.getBlob(nameIndex);
			int mission_id = c.getInt(idIndex);
			Mission mis = SerializationUtil.deserialize(mission_serie);
			return new MissionAndId(mis, mission_id);
		}else{
			Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_cannot_find_selected_mission_in_db), Toast.LENGTH_LONG).show();
			return null;
		}
	}
    
	public void showDeleteMissionDialog(int id, String name) {
    	DialogFragment newFragment = DeleteMissionDialogFragment.newInstance(id, name);
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "delete");
    }

	private void restoreMissionsBackground() {
		if(missionsList!=null){
			for(int i = 0; i < missionsList.getChildCount(); i++){
				LinearLayout lay = (LinearLayout)missionsList.getChildAt(i);
				lay.setBackgroundResource(R.drawable.mission_item);
				TextView text_id = (TextView)lay.findViewById(R.id.textViewMissionId);
				TextView text_name = (TextView)lay.findViewById(R.id.textViewMission);
				if(simulator.getSelectedMissionid()==Integer.parseInt(text_id.getText().toString())){
					text_name.setTextColor(getResources().getColor(R.color.red));
				}else{
					text_name.setTextColor(getResources().getColor(R.color.white));
				}
			}
		}
	}

	private void loadCorrectSimulatorScreen(View view) {
		boolean remote = sharedPref.getBoolean(view.getContext().getString(R.string.pref_key_sim_global_remote), false);
    	switch_remote.setChecked(remote);
    	if(remote){
    		// Remote
    		sim_container.setDisplayedChild(1); 
    	}else{
    		// Local
    		sim_container.setDisplayedChild(0);
    		missionsList.post(new Runnable() {
		        @Override
		        public void run() {
		        	if(isAdded()){
			        	boolean remote = sharedPref.getBoolean(getString(R.string.pref_key_sim_global_remote), false);
			        	if(!remote){
			        		selectFirstMissionInList();
			        	}
		        	}
		        }    
		    });
    	}
	}
	
	private void selectFirstMissionInList(){
		int mActivePosition = 0;
    	missionsList.setSelection(mActivePosition);
		missionsList.performItemClick(missionsList.getChildAt(mActivePosition), mActivePosition, missionsList.getAdapter().getItemId(mActivePosition));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		MissionReaderDbHelper db_help = ((SatAttApplication)((MainActivity)getActivity()).getApplication()).db_help;
		
	    String sql="SELECT _ID, name, description FROM "+MissionEntry.TABLE_NAME+" ORDER BY name ASC;";
	    String[] params = null;
	    SQLiteCursorLoader loader = new SQLiteCursorLoader(
	    		getActivity().getApplicationContext(),
	    		db_help,
	    		sql,
	    		params);
		return loader;
	}
	
	SimpleCursorAdapter adapter;
	int activeMissionId = -1;
	String activeMissionName = "";
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		((SatAttApplication)((MainActivity)getActivity()).getApplication()).loader=(SQLiteCursorLoader)loader;
	    adapter.changeCursor(cursor);
		
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			missionsList.post(new Runnable() {
		        @Override
		        public void run() {
		        	boolean remote = sharedPref.getBoolean(getString(R.string.pref_key_sim_global_remote), false);
		        	if(!remote){
			        	int mActivePosition = 0;
				    	missionsList.setSelection(mActivePosition);
						missionsList.performItemClick(missionsList.getChildAt(mActivePosition), mActivePosition, missionsList.getAdapter().getItemId(mActivePosition));
		        	}
		        }    
		    });
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
	}
}
