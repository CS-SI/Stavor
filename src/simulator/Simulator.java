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
	
	public void setSelectedMission(Mission mis, int id){
		mission = mis;
		mission_id = id;
	}

	public void resetSelectedMissionId() {
		mission_id=-1;
	}
	public int getSelectedMissionid(){
		return mission_id;
	}
	
	public void setButtonConnect(Button bt){
		buttonConnect=bt;
		updateConnectButtonText();
	}
	public void setSwitchSelector(Switch st){
		switchSelector=st;
		updateSwitchEnabled();
	}
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
	
	public ModelSimulation getSimulationResults(){
		return simulation;
	}
	
	public SimulatorStatus getSimulatorStatus(){
		return simulatorStatus;
	}
	public SimulationStatus getSimulationStatus(){
		return simulationStatus;
	}
	
	public SimulatorStatus connect(){
		if(simulatorStatus.equals(SimulatorStatus.Disconnected)){
    		//Log.d("Sim",System.currentTimeMillis()+": "+"simulator connecting thread");
			simulationStatus = SimulationStatus.Pause;
			playCondition = new ConditionVariable(false);
			connectThread();
			//Log.d("Sim",System.currentTimeMillis()+": "+"simulator has connected thread");
		}
		return simulatorStatus;
	}
	
	public SimulatorStatus disconnect(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			disconnectThread();
		}
		return simulatorStatus;
	}
	
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
	
	public SimulationStatus stop(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			play();
			reset=true;
		}
		return simulationStatus;
	}
	protected boolean reset = false;
	protected ConditionVariable playCondition;
	protected boolean cancel = false;
	
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

	private void connectThread() {
		//Log.d("Sim",System.currentTimeMillis()+": "+"simulator connecting thread interior");
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
				//Log.d("Sim",System.currentTimeMillis()+": "+"simulator pre preinitialize");
				simulation.preInitialize();
				//Log.d("Sim",System.currentTimeMillis()+": "+"simulator post preinitialize");
				thread = (SocketsThread) new SocketsThread(this,host,port).execute(simulation);
				//Log.d("Sim",System.currentTimeMillis()+": "+"end executing thread ");
			}catch(NumberFormatException nfe){
				setSimulatorStatus(SimulatorStatus.Disconnected);
			}
		}else{
			// Local
			setProgress(30 * 100);
			simulation = new ModelSimulation((MainActivity)activity);
			setProgress(40 * 100);
			simulation.preInitialize();
			sthread = null;
			activity.runOnUiThread( new Runnable() {
				public void run() {    
					sthread = (SimulatorThread) new SimulatorThread(((MainActivity)activity).getSimulator(), mission).execute(simulation);
		        }
			});
			
			//TODO new mission implement selector of mission
		}
		//Log.d("Sim",System.currentTimeMillis()+": "+"simulator interior thread connected");
	}
	
	private void disconnectThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			//thread.setDisconnected();
			//thread.cancel(true);
			
			thread.closeSocket();
			//cancel=true;
			
		}else{
			// Local
			//sthread.setDisconnected();
			cancel=true;
			playCondition.open();
			//sthread.cancel(false);
		}
	}
	private void resumeThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
		}else{
			playCondition.open();
		}
	}
	private void pauseThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
		}else{
			playCondition.close();
		}
	}

	public void setSimulatorStatus(SimulatorStatus new_status) {
		simulatorStatus = new_status;
		updateConnectButtonText();
		updateSwitchEnabled();
		setCorrectSimulatorControls();
		setProgress(100 * 100);
	}
	
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
		// TODO Auto-generated method stub
		activity.runOnUiThread( new Runnable() {
			public void run() {
				Toast.makeText(context, message,
		                Toast.LENGTH_LONG).show();
	        }
		});
	}
	public void goToHud() {
		// TODO Auto-generated method stub
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
