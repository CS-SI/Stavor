package simulator;

import java.io.Serializable;

import model.ModelSimulation;
import cs.si.satatt.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Simulator {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7144983071691596540L;
	private SimulatorStatus simulatorStatus = SimulatorStatus.Disconnected;
	private SimulationStatus simulationStatus = SimulationStatus.Stop;
	private SharedPreferences sharedPref;
	private Context context;
	private Activity activity;
	private SocketsThread thread;
	private SimulatorThread sthread;
	private ModelSimulation simulation;
	
	public Simulator(Activity act){
		activity = act;
		sharedPref = activity.getSharedPreferences("cs.si.satatt", Context.MODE_PRIVATE);
		context = activity.getApplicationContext();
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
			connectThread();
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

	private void connectThread() {
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
			try{
				String host = sharedPref.getString(context.getString(R.string.pref_key_sim_remote_host), "127.0.0.1");
				int port = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_key_sim_remote_port), "1520"));
				simulation = new ModelSimulation(activity);
				thread = (SocketsThread) new SocketsThread(this,host,port).execute(simulation);
			}catch(NumberFormatException nfe){
			}
		}else{
			// Local
		}
	}
	
	private void disconnectThread() {
		// TODO Auto-generated method stub
		boolean remote = sharedPref.getBoolean(context.getString(R.string.pref_key_sim_global_remote), false);
		if(remote){
			// Remote
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
	}
	
}
