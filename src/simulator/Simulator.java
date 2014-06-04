package simulator;

import org.xwalk.core.XWalkView;

import mission.Mission;
import model.ModelSimulation;
import cs.si.satatt.R;
import cs.si.satatt.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.ConditionVariable;
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
	private SimulatorStatus simulatorStatus = SimulatorStatus.Disconnected;
	private SimulationStatus simulationStatus = SimulationStatus.Pause;
	private SharedPreferences sharedPref;
	private Context context;
	private Activity activity;
	private SocketsThread thread;
	private SimulatorThread sthread;
	private ModelSimulation simulation;
	private Button buttonConnect;
	private Switch switchSelector;
	private Mission mission;
	private int mission_id=-1;
	
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
	public void setSwitchSelector(Switch st){
		switchSelector=st;
		updateSwitchEnabled();
	}
	
	/**
	 * Sets the Hud and browser view
	 * @param v
	 * @param x
	 */
	public void setHudView(View v, XWalkView x){
		simulation.setHud(v,x);
	}
	
	public void setBrowserLoaded(boolean is){
		simulation.setBrowserloaded(is);
	}
	
	public Context getContext(){
		return context;
	}
	
	public Simulator(MainActivity act){
		activity = act;
		sharedPref = activity.getSharedPreferences("cs.si.satatt", Context.MODE_PRIVATE);
		context = activity.getApplicationContext();
		simulation = new ModelSimulation(act);
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
	 * Sets the simulator connecting progress
	 * @param prog
	 */
	private void setProgress(final int prog){
		try{
			activity.runOnUiThread( new Runnable() {
				public void run() {
					activity.setProgress(prog);
		        }
			});
		}catch(NullPointerException nulle){
			
		}
	}

	/**
	 * Connects the thread corresponding to user selection (Local/Remote)
	 */
	private void connectThread() {
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
		setProgress(10 * 100);
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			try{
				setProgress(20 * 100);
				String host = sharedPref.getString(context.getString(R.string.pref_key_sim_remote_host), "127.0.0.1");
				int port = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_key_sim_remote_port), "1520"));
				setProgress(30 * 100);
				simulation = new ModelSimulation((MainActivity)activity);
				setProgress(40 * 100);
				simulation.preInitialize();
				thread = (SocketsThread) new SocketsThread(this,host,port).execute(simulation);
			}catch(NumberFormatException nfe){
				setSimulatorStatus(SimulatorStatus.Disconnected);
			}
		}else{
			// Local
			setProgress(30 * 100);
			simulation = new ModelSimulation((MainActivity)activity);
			setProgress(40 * 100);
			simulation.preInitialize();    
			sthread = (SimulatorThread) new SimulatorThread(((MainActivity)activity).getSimulator(), mission).execute(simulation);
		    
		}
	}
	
	/**
	 * Disconnects simulator thread
	 */
	private void disconnectThread() {
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			thread.closeSocket();
			
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
		updateSwitchEnabled();
		setCorrectSimulatorControls();
		setProgress(100 * 100);
	}
	
	/**
	 * Sets the correct text in simulator button according to its status (Connected/Disconnected)
	 */
	private void updateConnectButtonText(){
		if(buttonConnect!=null){
			activity.runOnUiThread( new Runnable() {
				public void run() {
					buttonConnect.setEnabled(true);
					if(simulatorStatus.equals(SimulatorStatus.Connected))
			    		buttonConnect.setText(context.getString(R.string.sim_disconnect));
			    	else
			    		buttonConnect.setText(context.getString(R.string.sim_connect));
		        }
			});
		}
	}
	
	/**
	 * Enable or disable the simulator mode switch according to its status (Connected/Disconnected)
	 */
	private void updateSwitchEnabled(){
		if(switchSelector!=null){
			activity.runOnUiThread( new Runnable() {
				public void run() {
					if(simulatorStatus.equals(SimulatorStatus.Connected))
			    		switchSelector.setEnabled(false);
			    	else
			    		switchSelector.setEnabled(true);
		        }
			});
		}
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
								but_play.setImageDrawable(activity.getResources().getDrawable(R.drawable.pause));
							}else{
								but_play.setImageDrawable(activity.getResources().getDrawable(R.drawable.play));
							}
						}
					}
		        }
			});
		}
	}
	
}
