package fragments;

import java.util.ArrayList;
import java.util.List;

import mission.Mission;
import simulator.Simulator;
import simulator.SimulatorStatus;
import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
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
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CursorAdapter;
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
import android.widget.ListAdapter;
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
		
		//sharedPref = PreferenceManager.getDefaultSharedPreferences(container.getContext());
		sharedPref = this.getActivity().getSharedPreferences("cs.si.satatt", Context.MODE_PRIVATE);
		
		//simulator = (Simulator) getArguments().getSerializable(ARG_SIM_OBJ);
		simulator = ((MainActivity)getActivity()).getSimulator();
    	//simulator.setHudView(null);
		
		
		//Load missions in list
		missionsList = (ListView) rootView.findViewById(R.id.listView1);
		missionsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				try{
					restoreMissionsBackground();
					arg1.setBackgroundResource(R.drawable.mission_item_sel);
					activeMissionId = Integer.parseInt((String) ((TextView)arg1.findViewById(R.id.textViewMissionId)).getText());
					activeMissionName=(String) ((TextView)arg1.findViewById(R.id.textViewMission)).getText();
					/*Toast.makeText(getActivity().getApplicationContext(), "Active mission: "+activeMissionId,
			                Toast.LENGTH_LONG).show();*/
				}catch(Exception e){
					e.printStackTrace();
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
	    //getLoaderManager().initLoader(0, null, this);
		getLoaderManager().initLoader(R.id.listView1, null, this);
		//
    	
    	
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
	            		simulator.connect();
            		}else{
            			//Set mission
            			String[] projection = {
            					MissionEntry._ID,
            				    MissionEntry.COLUMN_NAME_CLASS
            				    };

        				//XGGDEBUG: put db in activity to not load it always.
            			Cursor c = ((MainActivity)getActivity()).db_help.getWritableDatabase()
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
            				Mission mis = SerializationUtil.deserialize(mission_serie);
            				if(mis!=null){
		            			simulator.setSelectedMission(mis);
		            			simulator.connect();
            				}else{
            					Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_cannot_deserialize_selected_mission), Toast.LENGTH_LONG).show();
            				}
            			}else{
            				Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_cannot_find_selected_mission_in_db), Toast.LENGTH_LONG).show();
            			}
            		}
            	}
            }
        });
    	
    	Button button_delete = (Button)rootView.findViewById(R.id.buttonMissionDelete);
    	button_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(activeMissionId==-1){
					Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_select_first_a_mission), Toast.LENGTH_LONG).show();
				}else if (activeMissionId==0 ||activeMissionId==1 ||activeMissionId==2 ){
					Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_mission_not_removable), Toast.LENGTH_LONG).show();
				}else{
					showDeleteMissionDialog(activeMissionId, activeMissionName);
				}
			}
    		
    	});
    	
		return rootView;
	}
    
	public void showDeleteMissionDialog(int id, String name) {
    	DialogFragment newFragment = DeleteMissionDialogFragment.newInstance(id, name);
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "delete");
    }

	private void restoreMissionsBackground() {
		for(int i = 0; i < missionsList.getChildCount(); i++){
			LinearLayout lay = (LinearLayout)missionsList.getChildAt(i);
			lay.setBackgroundResource(R.drawable.mission_item);
		}
	}
	/*private void loadMissionList() {
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
	    
	    
	}*/

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
    		//((MainActivity)getActivity()).loader.reset();
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		MissionReaderDbHelper db_help = ((MainActivity)getActivity()).db_help;
		
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
		((MainActivity)getActivity()).loader=(SQLiteCursorLoader)loader;
	    adapter.changeCursor(cursor);
		
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			//int idIndex = cursor.getColumnIndex(MissionEntry._ID);
			//int nameIndex = cursor.getColumnIndex(MissionEntry.COLUMN_NAME_NAME);
			//this.itemId = cursor.getLong(idIndex);
			//String name = cursor.getString(nameIndex);
			//((EditText)findViewById(R.id.textViewMission)).setText(name);
			//((EditText)findViewById(R.id.person)).setText(borrower);
			
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
