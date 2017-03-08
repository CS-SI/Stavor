package cs.si.stavor.fragments;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.database.MissionReaderContract;
import cs.si.stavor.database.ReaderDbHelper;
import cs.si.stavor.database.SerializationUtil;
import cs.si.stavor.database.MissionReaderContract.MissionEntry;
import cs.si.stavor.dialogs.CopyMissionDialogFragment;
import cs.si.stavor.dialogs.DeleteMissionDialogFragment;
import cs.si.stavor.mission.Mission;
import cs.si.stavor.mission.MissionAndId;
import cs.si.stavor.simulator.Simulator;
import cs.si.stavor.simulator.SimulatorStatus;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;


/**
 * Fragment to show all the simulator configurations
 * @author Xavier Gibert
 *
 */
public final class SimulatorFragment extends Fragment implements LoaderCallbacks<Cursor> {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private static String screenName = "Simulator";
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param sectionNumber
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
	CheckBox checkSSL;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		
		View rootView = inflater.inflate(R.layout.sim, container,
				false);
		
		((MainActivity)getActivity()).refreshActionBarIcons();
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		
		simulator = ((MainActivity)getActivity()).getSimulator();
		
		//Load missions in list
		missionsList = (ListView) rootView.findViewById(R.id.listView1);
		missionsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(arg1!=null){
					activeMissionId = Integer.parseInt(((TextView)arg1.findViewById(R.id.textViewMissionId)).getText().toString());
					activeMissionName=((TextView)arg1.findViewById(R.id.textViewMission)).getText().toString();
					TextView text_description = ((TextView)arg1.findViewById(R.id.textViewMissionDescription));
					if(text_description.getVisibility() == View.GONE){
						Toast.makeText(getActivity().getApplicationContext(),
								text_description.getText().toString(), 
								Toast.LENGTH_LONG).show();
					}
					markActiveMission();
				}else{
					activeMissionId=-1;
					activeMissionName="";
				}
			}
		});
		
		missionsList.setOnScrollListener(new OnScrollListener(){
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				updateListSelection();
		    }
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//if(scrollState == 0){//Stop scroll
					updateListSelection();
				//}
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
    	checkSSL = (CheckBox) rootView.findViewById(R.id.checkBoxSSL);
    	String host = sharedPref.getString(getString(R.string.pref_key_sim_remote_host), Parameters.Simulator.Remote.default_host);
		String port = sharedPref.getString(getString(R.string.pref_key_sim_remote_port), Parameters.Simulator.Remote.default_port);
		Boolean ssl = sharedPref.getBoolean(getString(R.string.pref_key_sim_remote_ssl), Parameters.Simulator.Remote.default_ssl);
		host_view.setText(host);
		port_view.setText(port);
		checkSSL.setChecked(ssl);
		checkSSL.setEnabled(Parameters.App.pro_version);
		if(!Parameters.App.pro_version){
			checkSSL.setText(checkSSL.getText()+" "+getString(R.string.pro_only));
			checkSSL.setChecked(false);
		}
		
    	button_connect = (Button) rootView.findViewById(R.id.buttonConnect);
    	simulator.setButtonConnect(button_connect);
    	simulator.setSwitchView(switch_remote);
    	button_connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
            		//int tmp_sel = last_mission_selection;
            		simulator.disconnect();
            		//selectMissionInList(tmp_sel);
            		updateListSelection();
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
	            		sharedPref.edit().putBoolean(v.getContext().getString(
	            				R.string.pref_key_sim_remote_ssl), 
	            				checkSSL.isChecked()
	            				).commit();
	            		simulator.connect();
            		}else{
            			//Set mission
            			MissionAndId mis = getMission(activeMissionId);
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
    	
    	//Delete
    	Button button_delete = (Button)rootView.findViewById(R.id.buttonMissionDelete);
    	button_delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
					//Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_stop_simulator_first), Toast.LENGTH_LONG).show();
				//}else{
					if(activeMissionId==-1){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_select_first_a_mission), Toast.LENGTH_LONG).show();
					}else if (activeMissionId<=Parameters.Simulator.amount_mission_examples ){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_mission_not_removable), Toast.LENGTH_LONG).show();
					}else{
						showDeleteMissionDialog(activeMissionId, activeMissionName);
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
					((MainActivity)getActivity()).showMissionCreator();
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
					if(activeMissionId==-1){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_select_first_a_mission), Toast.LENGTH_LONG).show();
					}else if (activeMissionId<=Parameters.Simulator.amount_mission_examples){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_mission_not_editable), Toast.LENGTH_LONG).show();
					}else{
						MissionAndId mis = getMission(activeMissionId);
		    			if(mis!=null){
		    				((MainActivity)getActivity()).showMissionEditor(mis);
		    			}else{
		    				Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_cannot_deserialize_selected_mission), Toast.LENGTH_LONG).show();
		    			}
					}
				//}
			}
    		
    	});
    	
    	ImageView img_orekit = (ImageView)rootView.findViewById(R.id.nav_item_icon);
    	img_orekit.setOnClickListener(new View.OnClickListener(){
    	    public void onClick(View v){


    	        Intent intent = new Intent();
    	        intent.setAction(Intent.ACTION_VIEW);
    	        intent.addCategory(Intent.CATEGORY_BROWSABLE);
    	        intent.setData(Uri.parse(Parameters.About.orekit_site));
    	        startActivity(intent);
    	    }
    	});
    	
    	showGuideSimulator(rootView);
    	
		return rootView;
	}
	
	/**
	 * Returns the selected Mission from the database
	 * @return
	 */
	private MissionAndId getMission(int id){
		String[] projection = {
				MissionEntry._ID,
			    MissionEntry.COLUMN_NAME_CLASS
			    };
		Cursor c = ((StavorApplication)((MainActivity)getActivity()).getApplication()).db
			.query(
					MissionEntry.TABLE_NAME,  // The table to query
				    projection,                               // The columns to return
				    MissionEntry._ID+" = ?",                                // The columns for the WHERE clause
				    new String[]{Integer.toString(id)},                            // The values for the WHERE clause
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
    
	/**
	 * Shows the delete mission confirmation dialog
	 * @param id Mission id
	 * @param name Mission name
	 */
	public void showDeleteMissionDialog(int id, String name) {
    	DialogFragment newFragment = DeleteMissionDialogFragment.newInstance(id, name);
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "delete");
    }
	
	/**
	 * Shows the copy mission confirmation dialog
	 * @param id Mission id
	 * @param name Mission name
	 */
	public void showCopyMissionDialog(int id, String name, Mission mis) {
    	DialogFragment newFragment = CopyMissionDialogFragment.newInstance(id, name, mis);
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "copy");
    }



	/**
	 * load local or remote simulator view
	 * @param view
	 */
	private void loadCorrectSimulatorScreen(View view) {
		simulator.updateConnectButtonText();
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
	
	/**
	 * Change the text color of the missions in list that is selected
	 */
	private void markActiveMission() {
		if(missionsList!=null){
			for(int i = 0; i < missionsList.getChildCount(); i++){
				LinearLayout lay = (LinearLayout)missionsList.getChildAt(i);
				TextView text_id = (TextView)lay.findViewById(R.id.textViewMissionId);
				//TextView text_name = (TextView)lay.findViewById(R.id.textViewMission);
				//TextView text_description = (TextView)lay.findViewById(R.id.textViewMissionDescription);
				ImageView image_selected = (ImageView)lay.findViewById(R.id.imageViewSelection);
				if(simulator.getSelectedMissionid()==Integer.parseInt(text_id.getText().toString())){
					//text_name.setTextColor(getResources().getColor(R.color.selected_mission));
					//text_name.setPaintFlags(text_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
					image_selected.setVisibility(View.VISIBLE);
					//text_description.setVisibility(View.VISIBLE);
				}else{
					//text_name.setTextColor(getResources().getColor(R.color.white));
					//text_name.setPaintFlags(text_name.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
					image_selected.setVisibility(View.GONE);
					//text_description.setVisibility(View.GONE);
				}
			}
		}
	}
	
	private void selectFirstMissionInList(){
		try{
			missionsList.setItemChecked(0, true);
			missionsList.setSelection(0);
			Cursor curs = (Cursor)missionsList.getItemAtPosition(0);
			if(curs!=null){
				activeMissionId = curs.getInt(curs.getColumnIndex(MissionReaderContract.MissionEntry._ID));
				activeMissionName = curs.getString(curs.getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_NAME));
				markActiveMission();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void selectMissionByKey(int key){
		if(key!=-1){
			boolean found = false;
			for(int j = 0; j<adapter.getCount(); j++){
				adapter.getCursor().moveToPosition(j);
				int mis_key = adapter.getCursor().getInt(
						adapter.getCursor().getColumnIndex(
								MissionReaderContract.MissionEntry._ID));
				if(mis_key == key){
					found=true;
					missionsList.setItemChecked(j, true);
					missionsList.setSelection(j);
					Cursor curs = (Cursor)missionsList.getItemAtPosition(j);
					activeMissionId = curs.getInt(curs.getColumnIndex(MissionReaderContract.MissionEntry._ID));
					activeMissionName = curs.getString(curs.getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_NAME));
					markActiveMission();
				}
			}
			if(!found){
				selectFirstMissionInList();
			}
		}else{
			selectFirstMissionInList();
		}
	}
	private void updateListSelection(){
		markActiveMission();
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
		
	    String sql="SELECT _ID, name, description FROM "+MissionEntry.TABLE_NAME+" ORDER BY name COLLATE NOCASE ASC;";
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
		((StavorApplication)((MainActivity)getActivity()).getApplication()).loader=(SQLiteCursorLoader)loader;
	    adapter.changeCursor(cursor);
		
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			missionsList.post(new Runnable() {
		        @Override
		        public void run() {
		        	boolean remote = sharedPref.getBoolean(getString(R.string.pref_key_sim_global_remote), false);
		        	if(!remote){
		        		selectMissionByKey(activeMissionId);
		        	}
		        }    
		    });
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.changeCursor(null);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.listView1) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    
	    missionsList.setItemChecked(info.position, true);
	    missionsList.setSelection(info.position);
		Cursor curs = (Cursor)missionsList.getItemAtPosition(info.position);
		activeMissionId = curs.getInt(curs.getColumnIndex(MissionReaderContract.MissionEntry._ID));
		activeMissionName = curs.getString(curs.getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_NAME));
		markActiveMission();
		
	    if(adapter!=null && adapter.getCursor()!=null){
		    adapter.getCursor().moveToPosition(info.position);
		    String header = adapter.getCursor().getString(
		    		adapter.getCursor().getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_NAME));
		    menu.setHeaderTitle(header);
	    }
	    String[] menuItems = getResources().getStringArray(R.array.missions_menu);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	  }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  //String[] menuItems = getResources().getStringArray(R.array.missions_menu);
	  //String menuItemName = menuItems[menuItemIndex];
	  
	  int listItemKey = -1;
	  try{
		if(adapter!=null && adapter.getCursor()!=null){
		    adapter.getCursor().moveToPosition(info.position);
		    listItemKey = adapter.getCursor().getInt(
		    		adapter.getCursor().getColumnIndex(MissionReaderContract.MissionEntry._ID));
		    String listItemName = adapter.getCursor().getString(
		    		adapter.getCursor().getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_NAME));
			  if(menuItemIndex==0){
					if(listItemKey==-1){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_select_first_a_mission), Toast.LENGTH_LONG).show();
					}else if (listItemKey==0 ||listItemKey==1 ||listItemKey==2 || listItemKey==3 ){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_mission_not_removable), Toast.LENGTH_LONG).show();
					}else{
						  showDeleteMissionDialog(listItemKey, listItemName);
					}
			  }else if(menuItemIndex==1){
				  showCopyMissionDialog(listItemKey, listItemName, getMission(listItemKey).mission);
			  }else if(menuItemIndex==2){
					if(listItemKey==-1){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_select_first_a_mission), Toast.LENGTH_LONG).show();
					}else if (listItemKey==0 ||listItemKey==1 ||listItemKey==2  ||listItemKey==3){
						Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_mission_not_editable), Toast.LENGTH_LONG).show();
					}else{
						MissionAndId mis = getMission(listItemKey);
						if(mis!=null){
							((MainActivity)getActivity()).showMissionEditor(mis);
						}else{
							Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sim_local_cannot_deserialize_selected_mission), Toast.LENGTH_LONG).show();
						}
					}
			  }
		}
	  }catch(Exception e){
		  
	  }
	  
	  return true;
	}
	

	FrameLayout guideFrame;
    public void showGuideSimulator(View rootView){
    	
    	guideFrame = (FrameLayout) rootView.findViewById(R.id.guideLayout);
    	
    	String key = getString(R.string.pref_key_guide_simulator);
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    	if(sharedPref.getBoolean(key, Parameters.App.show_guide)){
    		guideFrame.setVisibility(View.VISIBLE);
    		guideFrame.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					//Check selection of don't show again
					CheckBox cb = (CheckBox)arg0.findViewById(R.id.checkBoxGuide);
					if(cb.isChecked()){
						String key = getString(R.string.pref_key_guide_simulator);
				    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				    	sharedPref.edit().putBoolean(key, false).commit();
					}

					//arg0.setVisibility(View.GONE);
					
					Animation fadeOut = new AlphaAnimation(1.00f, 0.00f);
	                fadeOut.setDuration(1000);
	                fadeOut.setAnimationListener(new AnimationListener() {
	                    public void onAnimationStart(Animation animation) {}
	                    public void onAnimationRepeat(Animation animation) {}
	                    public void onAnimationEnd(Animation animation) {
	                    	guideFrame.setVisibility(View.GONE);
	                    }
	                });

	                arg0.startAnimation(fadeOut);
	                arg0.setOnClickListener(null);
				}
    		});
	    	/*String title = getString(R.string.tutorial_title_simulator);
	    	String message = getString(R.string.tutorial_message_simulator);
	    	showTutorialDialog(key, title, message);*/
    	}else{
    		guideFrame.setVisibility(View.GONE);
    	}
    }
	
}
