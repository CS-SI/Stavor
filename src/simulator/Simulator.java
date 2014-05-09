package simulator;

import org.xwalk.core.XWalkView;

import model.ModelSimulation;
import cs.si.satatt.R;
import cs.si.satatt.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class Simulator {
	private SimulatorStatus simulatorStatus = SimulatorStatus.Disconnected;
	private SimulationStatus simulationStatus = SimulationStatus.Stop;
	private SharedPreferences sharedPref;
	private Context context;
	private Activity activity;
	private SocketsThread thread;
	private SimulatorThread sthread;
	private ModelSimulation simulation;
	private Button buttonConnect;
	private Switch switchSelector;
	
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
			connectThread();
			//Log.d("Sim",System.currentTimeMillis()+": "+"simulator has connected thread");
		}
		return simulatorStatus;
	}
	
	public SimulatorStatus disconnect(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			stop();
			disconnectThread();
		}
		return simulatorStatus;
	}
	
	public SimulationStatus play(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			if(simulationStatus.equals(SimulationStatus.Pause)){
				resumeThread();
				simulationStatus = SimulationStatus.Play;
			}else if(simulationStatus.equals(SimulationStatus.Stop)){
				startThread();
				simulationStatus = SimulationStatus.Play;
			}
		}
		return simulationStatus;
	}
	
	public SimulationStatus pause(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			if(simulationStatus.equals(SimulationStatus.Play)){
				pauseThread();
				simulationStatus = SimulationStatus.Pause;
			}
		}
		return simulationStatus;
	}
	
	public SimulationStatus stop(){
		if(simulatorStatus.equals(SimulatorStatus.Connected)){
			if(!simulationStatus.equals(SimulationStatus.Stop)){
				stopThread();
				simulationStatus = SimulationStatus.Stop;
			}
		}
		return simulationStatus;
	}
	
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
			}
		}else{
			// Local
		}
		setProgress(100 * 100);
		//Log.d("Sim",System.currentTimeMillis()+": "+"simulator interior thread connected");
	}
	
	private void disconnectThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			thread.setDisconnected();
			thread.cancel(false);
			
		}else{
			// Local
		}
	}
	
	private void startThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
		}else{
			// Local
		}
	}
	private void resumeThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			thread.notify();
		}else{
			// Local
		}
	}
	private void pauseThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			try {
				thread.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			// Local
		}
	}
	private void stopThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			try {
				thread.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			// Local
		}
	}

	public void setSimulatorStatus(SimulatorStatus new_status) {
		simulatorStatus = new_status;
		updateConnectButtonText();
		updateSwitchEnabled();
	}
	
	private void updateConnectButtonText(){
		if(buttonConnect!=null){
			activity.runOnUiThread( new Runnable() {
				public void run() {
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
	
}
