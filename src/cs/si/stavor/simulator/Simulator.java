package cs.si.stavor.simulator;

import org.xwalk.core.XWalkView;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.mission.Mission;
import cs.si.stavor.model.Browsers;
import cs.si.stavor.model.ModelSimulation;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.ConditionVariable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Simulator object
 * @author Xavier Gibert
 *
 */
public class Simulator {
	//General
	private SharedPreferences sharedPref;
	private Context context;
	private Activity activity;
	
	//Simulation core
	private SimulatorStatus simulatorStatus = SimulatorStatus.Disconnected;
	private SimulationStatus simulationStatus = SimulationStatus.Pause;
	private ThreadRemote thread_remote;
	private ThreadLocal thread_local;
	private Handler handler_remote, handler_local;
	
	//Results
	private ModelSimulation simulation;
	
	//GUI views
	private Button buttonConnect;
	private Switch switchSelector;
	
	//Mission
	private Mission mission;
	private int mission_id=-1;
	
	//Flags
	private boolean wasPlaying = false;//Flag to restore playing if fragment has paused-resumed while playing
	
	/**
	 * Pauses the simulator but keeping the previous status (playing/paused)
	 */
	public void temporaryPause(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			if(simulationStatus.equals(SimulationStatus.Play)){
				wasPlaying = true;
				pause();
			}else{
				wasPlaying = false;
			}
		}
	}
	/**
	 * restores the status of the simulator to the previous one before calling temporaryPause()
	 */
	public void resumeTemporaryPause(){
		if(wasPlaying){
			play();
		}
	}
	/**
	 * Resets the flag of the temporary pause, for when the simulator is reconnected.
	 */
	public void resetTemporaryPause(){
		wasPlaying = false;
	}
	
	/**
	 * Sets the mission before connecting the simulator
	 * @param mis
	 * @param id
	 */
	public void setSelectedMission(Mission mis, int id){
		mission = mis;
		mission_id = id;
	}

	/**
	 * Set no mission selected
	 */
	public void resetSelectedMissionId() {
		mission_id=-1;
	}
	
	/**
	 * Returns the selected mission id
	 * @return
	 */
	public int getSelectedMissionid(){
		return mission_id;
	}
	
	/**
	 * Set connect button View
	 * @param bt
	 */
	public void setButtonConnect(Button bt){
		buttonConnect=bt;
		updateConnectButtonText();
	}
	
	/**
	 * Set local/remote switch View
	 * @param st
	 */
	public void setSwitchView(Switch st){
		switchSelector=st;
		enableCorrectSimulatorViews();
	}
	
	/**
	 * Sets the Hud and browser view
	 * @param v
	 * @param x
	 */
	public void setHudView(Browsers type, View v, XWalkView x){
		simulation.setHud(type, v,x);
	}
	
	public void clearHud(){
		simulation.clearHud();
	}
	
	public void setBrowserLoaded(boolean is){
		simulation.setBrowserloaded(is);
	}
	

	private ProgressDialog progress;
	/**
	 * Sets the simulator connecting progress
	 * @param prog
	 */
	public void setProgress(final int prog){
		if(prog==10000 && progress==null){//Case of disconnecting simulator
		}else{
			if(progress==null){
				progress = new ProgressDialog(activity);
				progress.setProgressNumberFormat("");
				progress.setTitle(context.getString(R.string.dialog_simulator_title));
				progress.setMax(10000);
				progress.setMessage(context.getString(R.string.dialog_simulator_message));
				progress.setCancelable(false);
				progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progress.setCanceledOnTouchOutside(false);
				progress.setIndeterminate(false);
				if(!activity.isFinishing())
					progress.show();
			} 
			progress.setProgress(prog);
			if(prog==10000){
				progress.dismiss();
				progress = null;
			}
			/*try{
				activity.runOnUiThread( new Runnable() {
					public void run() {
						activity.setProgress(prog);
			        }
				});
			}catch(NullPointerException nulle){
				
			}*/
		}
	}

	
	public Context getContext(){
		return context;
	}
	
	public Simulator(MainActivity act){
		reconstruct(act);
		context = activity.getApplicationContext();
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		simulation = new ModelSimulation(act);
		handler_remote = new Handler();
		handler_local = new Handler();
	}
	public void reconstruct(MainActivity act){
		activity = act;
	}
	
	/**
	 * Returns the simulation step results
	 * @return
	 */
	public ModelSimulation getSimulationResults(){
		return simulation;
	}
	
	/**
	 * Returns the simulator status (Connected/Disconnected)
	 * @return
	 */
	public SimulatorStatus getSimulatorStatus(){
		return simulatorStatus;
	}
	
	/**
	 * Returns the simulation status (Play/Pause)
	 * @return
	 */
	public SimulationStatus getSimulationStatus(){
		return simulationStatus;
	}
	
	/**
	 * Connects the simulator
	 * @return
	 */
	public SimulatorStatus connect(){
		resetTemporaryPause();
		if(simulatorStatus.equals(SimulatorStatus.Disconnected)){
			simulationStatus = SimulationStatus.Pause;
			playCondition = new ConditionVariable(false);
			connectThread();
		}
		return simulatorStatus;
	}
	
	/**
	 * Disconnects the simulator
	 * @return
	 */
	public SimulatorStatus disconnect(){
		resetTemporaryPause();
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			resetSelectedMissionId();
			disconnectThread();
		}
		return simulatorStatus;
	}
	
	/**
	 * Play teh simulator
	 * @return
	 */
	public SimulationStatus play(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			if(simulationStatus.equals(SimulationStatus.Pause)){
				resumeThread();
				simulationStatus = SimulationStatus.Play;
			}
		}
    	setCorrectSimulatorControls();
		return simulationStatus;
	}
	
	/**
	 * Pause the simulator
	 * @return
	 */
	public SimulationStatus pause(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			if(simulationStatus.equals(SimulationStatus.Play)){
				pauseThread();
				simulationStatus = SimulationStatus.Pause;
			}
		}
    	setCorrectSimulatorControls();
		return simulationStatus;
	}
	
	/**
	 * Stop the simulator
	 * @return
	 */
	public SimulationStatus stop(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			play();
			reset=true;
		}
		return simulationStatus;
	}
	protected boolean reset = false;
	protected ConditionVariable playCondition;//Play/Pause condition
	protected boolean cancel = false;
	
	/**
	 * Connects the thread corresponding to user selection (Local/Remote)
	 */
	private void connectThread() {
		setProgress(10 * 100);
		if(buttonConnect!=null){
			activity.runOnUiThread( new Runnable() {
				public void run() {
					buttonConnect.setEnabled(false);
		        }
			});
		}
		if(switchSelector!=null){
			activity.runOnUiThread( new Runnable() {
				public void run() {
					switchSelector.setEnabled(false);
		        }
			});
		}
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			try{
				setProgress(20 * 100);
				String host = sharedPref.getString(context.getString(R.string.pref_key_sim_remote_host), Parameters.Simulator.Remote.default_host);
				int port = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_key_sim_remote_port), Parameters.Simulator.Remote.default_port));
				setProgress(30 * 100);
				simulation = new ModelSimulation((MainActivity)activity);
				setProgress(40 * 100);
				simulation.preInitialize();
				setProgress(50 * 100);
				Boolean ssl = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_remote_ssl), Parameters.Simulator.Remote.default_ssl);
				thread_remote = new ThreadRemote(handler_remote,this,host,port,ssl);
				thread_remote.start();
			}catch(NumberFormatException nfe){
				setSimulatorStatus(SimulatorStatus.Disconnected);
			}
		}else{
			// Local
			setProgress(30 * 100);
			simulation = new ModelSimulation((MainActivity)activity);
			setProgress(40 * 100);
			simulation.preInitialize(); 
			setProgress(50 * 100);
			thread_local = new ThreadLocal(handler_local, ((MainActivity)activity).getSimulator(), mission);
			thread_local.start();
		}
	}
	
	/**
	 * Disconnects simulator thread
	 */
	private void disconnectThread() {
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			thread_remote.closeSocket();
			
		}else{
			// Local
			cancel=true;
			playCondition.open();
		}
	}
	private void resumeThread() {
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
		}else{
			playCondition.open();
		}
	}
	private void pauseThread() {
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
		}else{
			playCondition.close();
		}
	}

	/**
	 * Sets the simulator status (Connected/Disconnected)
	 * @param new_status
	 */
	public void setSimulatorStatus(SimulatorStatus new_status) {
		simulatorStatus = new_status;
		updateConnectButtonText();
		enableCorrectSimulatorViews();
		setCorrectSimulatorControls();
		setProgress(100 * 100);
	}
	
	/**
	 * Sets the correct text in simulator button according to its status (Connected/Disconnected)
	 */
	public void updateConnectButtonText(){
		if(buttonConnect!=null){
			activity.runOnUiThread( new Runnable() {
				public void run() {
					buttonConnect.setEnabled(true);
					boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
					if(remote){
						if(simulatorStatus.equals(SimulatorStatus.Connected))
				    		buttonConnect.setText(context.getString(R.string.sim_disconnect));
				    	else
				    		buttonConnect.setText(context.getString(R.string.sim_connect));
					}else{
						if(simulatorStatus.equals(SimulatorStatus.Connected))
				    		buttonConnect.setText(context.getString(R.string.sim_stop));
				    	else
				    		buttonConnect.setText(context.getString(R.string.sim_start));
					}
		        }
			});
		}
	}
	
	/**
	 * Enable or disable the simulator mode switch and controls according to its status (Connected/Disconnected)
	 */
	private void enableCorrectSimulatorViews(){
			activity.runOnUiThread( new Runnable() {
				public void run() {
					if(switchSelector!=null){
						if(simulatorStatus.equals(SimulatorStatus.Connected))
				    		switchSelector.setEnabled(false);
				    	else
				    		switchSelector.setEnabled(true);
					}
		        }
			});
	}
	/*
	private static void enableView(ViewGroup layout, boolean enabled) {
	    layout.setEnabled(false);
	    for (int i = 0; i < layout.getChildCount(); i++) {
	        View child = layout.getChildAt(i);
	        if (child instanceof ViewGroup && !(child instanceof ListView) ) {
	            enableView((ViewGroup) child, enabled);
	        } else {
	            child.setEnabled(enabled);
	        }
	    }
	}*/

	private String message = "";
	public void showMessage(String string) {
		message = string;
		activity.runOnUiThread( new Runnable() {
			public void run() {
				Toast.makeText(context, message,
		                Toast.LENGTH_LONG).show();
	        }
		});
	}
	public void goToHud() {
		((MainActivity)activity).showSection(1);
	}
	
	ImageButton but_play;
	ImageButton but_stop;
	public void setControlButtons(ImageButton b_play, ImageButton b_stop) {
		but_play=b_play;
		but_stop=b_stop;
		
		//Listeners
		but_play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	if(simulationStatus.equals(SimulationStatus.Play)){
            		pause();
            	}else{
            		play();
            	}
            }
        });
		but_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	stop();
            }
        });

	}
	
	/**
	 * Change the play/pause icon according to simulation status
	 * and enable or disable the controls depending on simulator status
	 */
	public void setCorrectSimulatorControls() {
		if(but_play!=null && but_stop!=null){
			activity.runOnUiThread( new Runnable() {
				public void run() {
					boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
					if(remote){
						but_play.setVisibility(View.GONE);
						but_stop.setVisibility(View.GONE);
					}else{
						but_play.setVisibility(View.VISIBLE);
						but_stop.setVisibility(View.VISIBLE);
						if(getSimulatorStatus().equals(SimulatorStatus.Disconnected)){
							but_play.setEnabled(false);
							but_stop.setEnabled(false);
						}else{
							but_play.setEnabled(true);
							but_stop.setEnabled(true);
							if(getSimulationStatus().equals(SimulationStatus.Play)){
								//set pause drawable
								but_play.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
							}else{
								but_play.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
							}
						}
					}
		        }
			});
		}
	}
	
}
