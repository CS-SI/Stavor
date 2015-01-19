package cs.si.stavor.fragments;

import cs.si.stavor.MainActivity;
import cs.si.stavor.R;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.StavorApplication.TrackerName;
import cs.si.stavor.database.MissionReaderContract;
import cs.si.stavor.database.ReaderDbHelper;
import cs.si.stavor.database.StationsCursorAdapter;
import cs.si.stavor.database.StationsReaderContract;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import cs.si.stavor.dialogs.DeleteStationDialogFragment;
import cs.si.stavor.station.GroundStation;
import cs.si.stavor.station.StationAndId;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Loader;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Fragment to show all the simulator configurations
 * @author Xavier Gibert
 *
 */
public final class StationsFragment extends Fragment implements LoaderCallbacks<Cursor> {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private static String screenName = "Stations";
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 * @param sim_config 
	 */
	public static StationsFragment newInstance(int sectionNumber) {	
		StationsFragment fragment = new StationsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public StationsFragment() {
	}
	
	ListView stationsList;
	int last_station_selection = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//********** Google Analytics ***********
        // Get tracker.
        Tracker t = ((StavorApplication) getActivity().getApplication()).getTracker(
            TrackerName.APP_TRACKER);
        t.setScreenName(screenName);
        t.send(new HitBuilders.AppViewBuilder().build());
        //***************************************
		
		View rootView = inflater.inflate(R.layout.stations, container,
				false);
		
		//((MainActivity)getActivity()).showTutorialStations();
		
		//Load missions in list
		stationsList = (ListView) rootView.findViewById(R.id.listView1);
		
		stationsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg1!=null){
					LinearLayout lay = (LinearLayout) arg1;
					activeStationId = Integer.parseInt((String) ((TextView)lay.findViewById(R.id.textViewStationId)).getText());
					activeStationName=((TextView)lay.findViewById(R.id.textViewStationName)).getText().toString();
					selectStationByCursorPos(arg2, (LinearLayout)arg1);
				}else{
					activeStationId=-1;
					activeStationName="";
				}
			}
		});
		
		stationsList.setOnScrollListener(new OnScrollListener(){
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		    	updateListSelection();
		    }
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//if(scrollState == 0){
					updateListSelection();
				//}
		    }
		});
		
		adapter = new StationsCursorAdapter(
	    		this.getActivity().getApplicationContext(), null, ((StavorApplication)getActivity().getApplication()).loader);

		
		stationsList.setAdapter(adapter);
	    registerForContextMenu(stationsList);
		getLoaderManager().initLoader(R.id.listView1, null, this);
    	
    	//Delete
    	Button button_delete = (Button)rootView.findViewById(R.id.buttonMissionDelete);
    	button_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
					//Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_stop_simulator_first), Toast.LENGTH_LONG).show();
				//}else{
					if(activeStationId==-1){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.select_first_a_station), Toast.LENGTH_LONG).show();
					}else{
						showDeleteStationDialog(activeStationId, activeStationName);
					}
				//}
			}
    		
    	});
    	
    	//New
    	Button button_new = (Button)rootView.findViewById(R.id.buttonMissionNew);
    	button_new.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
					//Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_stop_simulator_first), Toast.LENGTH_LONG).show();
				//}else{
					((MainActivity)getActivity()).showStationCreator();
				//}
			}
    		
    	});
    	
    	//Edit
    	Button button_edit = (Button)rootView.findViewById(R.id.buttonMissionEdit);
    	button_edit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
					//Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_stop_simulator_first), Toast.LENGTH_LONG).show();
				//}else{
					if(activeStationId==-1){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.select_first_a_station), Toast.LENGTH_LONG).show();
					}else{
						StationAndId gs = getSelectedStation();
		    			if(gs!=null){
		    				((MainActivity)getActivity()).showStationEditor(gs);
		    			}else{
		    				System.err.println("Error loading station");
		    			}
					}
				//}
			}
    		
    	});
    	
		return rootView;
	}
	
	/**
	 * Returns the selected Mission from the database
	 * @return
	 */
	private StationAndId getSelectedStation(){
		String[] projection = {
				StationEntry._ID,
				StationEntry.COLUMN_NAME_NAME,
				StationEntry.COLUMN_NAME_LATITUDE,
				StationEntry.COLUMN_NAME_LONGITUDE,
				StationEntry.COLUMN_NAME_ALTITUDE,
				StationEntry.COLUMN_NAME_ELEVATION,
				StationEntry.COLUMN_NAME_ENABLED
			    };

		Cursor c = ((StavorApplication)((MainActivity)getActivity()).getApplication()).db
			.query(
					StationEntry.TABLE_NAME,  // The table to query
				    projection,                               // The columns to return
				    StationEntry._ID+" = ?",                                // The columns for the WHERE clause
				    new String[]{Integer.toString(activeStationId)},                            // The values for the WHERE clause
				    "",                                     // don't group the rows
				    "",                                     // don't filter by row groups
				    null                                 // The sort order
				    );
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			int idIndex = c.getColumnIndex(StationEntry._ID);
			int nameIndex = c.getColumnIndex(StationEntry.COLUMN_NAME_NAME);
			int latIndex = c.getColumnIndex(StationEntry.COLUMN_NAME_LATITUDE);
			int lonIndex = c.getColumnIndex(StationEntry.COLUMN_NAME_LONGITUDE);
			int altIndex = c.getColumnIndex(StationEntry.COLUMN_NAME_ALTITUDE);
			int elevIndex = c.getColumnIndex(StationEntry.COLUMN_NAME_ELEVATION);
			int enabledIndex = c.getColumnIndex(StationEntry.COLUMN_NAME_ENABLED);
			
			//this.itemId = cursor.getLong(idIndex);
			int station_id = c.getInt(idIndex);
			String station_name = c.getString(nameIndex);
			double station_lat = c.getDouble(latIndex);
			double station_lon = c.getDouble(lonIndex);
			double station_alt = c.getDouble(altIndex);
			double station_elev = c.getDouble(elevIndex);
			boolean station_enabled = c.getString(enabledIndex).equals("1");
			
			GroundStation gs = new GroundStation(
					station_enabled,
					station_name,
					station_lat,
					station_lon,
					station_alt,
					station_elev);
			
			return new StationAndId(gs, station_id);
		}else{
			Toast.makeText(getActivity().getApplicationContext(), getString(R.string.cannot_find_selected_station_in_db), Toast.LENGTH_LONG).show();
			return null;
		}
	}
    
	/**
	 * Shows the delete mission confirmation dialog
	 * @param id Mission id
	 * @param name Mission name
	 */
	public void showDeleteStationDialog(int id, String name) {
    	DialogFragment newFragment = DeleteStationDialogFragment.newInstance(id, name);
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "delete");
    }

	/**
	 * Change the text color of the missions in list that is selected
	 */
	private void restoreStationsBackground() {
		if(stationsList!=null){
			for(int i = 0; i < stationsList.getChildCount(); i++){
				LinearLayout lay = (LinearLayout)stationsList.getChildAt(i);
				lay.setBackgroundResource(R.drawable.mission_item);
			}
		}
	}
	
	private LinearLayout getStationView(int key){
		if(stationsList!=null){
			for(int i = 0; i < stationsList.getChildCount(); i++){
				LinearLayout lay = (LinearLayout)stationsList.getChildAt(i);
				TextView text_id = (TextView)lay.findViewById(R.id.textViewStationId);
				if(key==Integer.parseInt(text_id.getText().toString())){
					return lay;
				}
			}
		}
		return null;
	}
	
	private int getCursorPosFromStationKey(int key){
		for(int j = 0; j<adapter.getCount(); j++){
			adapter.getCursor().moveToPosition(j);
			int mis_key = adapter.getCursor().getInt(
					adapter.getCursor().getColumnIndex(
							MissionReaderContract.MissionEntry._ID));
			if(mis_key == key){
				return j;
			}
		}
		return -1;
	}
	
	private void selectFirstStationInList(){
		Cursor curs = (Cursor)stationsList.getItemAtPosition(0);
		if(curs!=null){
			activeStationId = curs.getInt(curs.getColumnIndex(StationsReaderContract.StationEntry._ID));
			activeStationName = curs.getString(curs.getColumnIndex(StationsReaderContract.StationEntry.COLUMN_NAME_NAME));
			selectStationByCursorPos(0,getStationView(activeStationId));
		}
	}
	
	private void selectStationByCursorPos(int curs_pos, LinearLayout lay){
		restoreStationsBackground();
		if(lay!=null&&curs_pos!=-1){
			try{
		    	lay.setBackgroundResource(R.drawable.mission_item_sel);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Select station in list with the database position
	 * @param position
	 */
	private void selectStationByKey(int key){
		if(key!=-1){
			int pos = getCursorPosFromStationKey(key);
			if(pos!=-1){//Found!
				selectStationByCursorPos(pos,getStationView(key));
			}else{//Not found, probably due to deletion
				selectFirstStationInList();
			}
		}else{
			selectFirstStationInList();
		}
	}
	private void updateListSelection(){
		selectStationByKey(activeStationId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		ReaderDbHelper db_help = ((StavorApplication)((MainActivity)getActivity()).getApplication()).db_help;
		
	    String sql="SELECT _ID, name, enabled FROM "+StationEntry.TABLE_NAME+" ORDER BY name COLLATE NOCASE ASC;";
	    String[] params = null;
	    SQLiteCursorLoader loader = new SQLiteCursorLoader(
	    		getActivity().getApplicationContext(),
	    		db_help,
	    		sql,
	    		params);
		return loader;
	}
	
	StationsCursorAdapter adapter;
	int activeStationId = -1;
	String activeStationName = "";
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		((StavorApplication)((MainActivity)getActivity()).getApplication()).loader=(SQLiteCursorLoader)loader;
	    adapter.changeCursor(cursor);
		
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			stationsList.post(new Runnable() {
		        @Override
		        public void run() {
        			selectStationByKey(activeStationId);
		        }    
		    });
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
	}
	
}
