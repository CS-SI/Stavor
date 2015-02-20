package cs.si.stavor.simulator;

import org.xwalk.core.XWalkView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.StavorApplication.TrackerName;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.mission.Mission;
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
    private SimulationSense simulationSense = SimulationSense.Forward;
	private ThreadRemote thread_remote;
	private ThreadLocal thread_local;
	private Handler handler_remote, handler_local;

    public void setSimulationSense(SimulationSense simulationSense){
        this.simulationSense = simulationSense;
    }
    public SimulationSense getSimulationSense() {
        return simulationSense;
    }
	
	//Results
	private ModelSimulation simulation;
	
	//GUI views
	private Button buttonConnect;
	private Switch switchSelector;
	
	//Mission
	private Mission mission;
	
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
	 */
	public void setSelectedMission(Mission mis){
        disconnect();
		mission = mis;
        connect();
	}

	private ProgressDialog progress;
	/**
	 * Sets the simulator connecting progress
	 * @param prog
	 */
	public void setProgress(final int prog){
		/*if(prog==10000 && progress==null){//Case of disconnecting simulator
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
		}*/
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
			
			//********** Google Analytics ***********
			boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
			String remote_str = "Local";
			if(remote) remote_str = "Remote";
            // Get tracker.
            Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
                TrackerName.APP_TRACKER);
            t.setScreenName("Simulator");
            t.send(new HitBuilders.EventBuilder()
            	.setCategory("Simulator")
            	.setAction("Connect")
            	.setLabel(remote_str)
            	.setValue(1)
            	.build());
            //***************************************
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
			disconnectThread();
			
			//********** Google Analytics ***********
			boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
			String remote_str = "Local";
			if(remote) remote_str = "Remote";
            // Get tracker.
            Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
                TrackerName.APP_TRACKER);
            t.setScreenName("Simulator");
            t.send(new HitBuilders.EventBuilder()
            	.setCategory("Simulator")
            	.setAction("Disconnect")
            	.setLabel(remote_str)
            	.setValue(1)
            	.build());
            //***************************************
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
				
				//********** Google Analytics ***********
				boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
				String remote_str = "Local";
				if(remote) remote_str = "Remote";
	            // Get tracker.
	            Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
	                TrackerName.APP_TRACKER);
	            t.setScreenName("Simulator");
	            t.send(new HitBuilders.EventBuilder()
	            	.setCategory("Simulator")
	            	.setAction("Play")
	            	.setLabel(remote_str)
	            	.setValue(1)
	            	.build());
	            //***************************************
			}
		}
    	//setCorrectSimulatorControls();
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
				
				//********** Google Analytics ***********
				boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
				String remote_str = "Local";
				if(remote) remote_str = "Remote";
	            // Get tracker.
	            Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
	                TrackerName.APP_TRACKER);
	            t.setScreenName("Simulator");
	            t.send(new HitBuilders.EventBuilder()
	            	.setCategory("Simulator")
	            	.setAction("Pause")
	            	.setLabel(remote_str)
	            	.setValue(1)
	            	.build());
	            //***************************************
			}
		}
    	//setCorrectSimulatorControls();
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
			
			//********** Google Analytics ***********
			boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
			String remote_str = "Local";
			if(remote) remote_str = "Remote";
            // Get tracker.
            Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
                TrackerName.APP_TRACKER);
            t.setScreenName("Simulator");
            t.send(new HitBuilders.EventBuilder()
            	.setCategory("Simulator")
            	.setAction("Stop")
            	.setLabel(remote_str)
            	.setValue(1)
            	.build());
            //***************************************
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
			thread_remote.setDisconnected();
			
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
		//setCorrectSimulatorControls();
		//setProgress(100 * 100);
	}


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


	/**
	 * Change the play/pause icon according to simulation status
	 * and enable or disable the controls depending on simulator status
	 */
	/*public void setCorrectSimulatorControls() {
		//TODO XGGDEBUG: interface
	}*/

    public SimulatorControlsStatus getControlsStatus() {
        return new SimulatorControlsStatus(this);
    }

    public void doSlowSimulation() {
        mission.sim_step = mission.sim_step * 0.5;
    }
    public void doAccelerateSimulation() {
        mission.sim_step = mission.sim_step * 2;
    }

    public void setCurrentSimulationProgress(int percentage) {
        boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
        if(!remote){
            try {
                if(thread_local != null)
                    thread_local.setCurrentSimulationProgress(percentage);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public int getSimulationProgress() {
        boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
        if(!remote){
            try {
                if(thread_local != null)
                    return thread_local.getSimulationProgress();
                else
                    return 0;
            }catch(Exception e){
                e.printStackTrace();
                return 0;
            }
        }else{
            return 0;
        }
    }

    public void updateGuiControls() {
        ((MainActivity)activity).updateGuiControls();
    }
}
