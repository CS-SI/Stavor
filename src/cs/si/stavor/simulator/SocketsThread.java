package cs.si.stavor.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.orekit.propagation.SpacecraftState;

import cs.si.stavor.R;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.model.ModelSimulation;
import android.os.AsyncTask;

/**
 * Remote simulator thread
 * @author Xavier Gibert
 *
 */
public class SocketsThread extends AsyncTask<ModelSimulation, Void, Boolean>{
 
	private String dstAddress;
	private int dstPort;
	private Socket socket = null;
	private Simulator simulator;
	private ObjectInputStream inputOStream;
    private InputStream inputStream;
	
	public SocketsThread(Simulator simu, String addr, int port){
		simulator = simu;
		dstAddress = addr;
		dstPort = port;
	}
	
	public void closeSocket(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    @Override
    protected Boolean doInBackground(ModelSimulation... params) {
    	
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Disconnected)){
    		//Connect remote simulator
    		try {
    			simulator.setProgress(60 * 100);
        		socket = new Socket();
        		socket.connect(new InetSocketAddress(dstAddress, dstPort), Parameters.Simulator.Remote.remote_connection_timeout_ms);
        	    inputStream = socket.getInputStream();
				inputOStream = new ObjectInputStream( inputStream );
    			simulator.setProgress(80 * 100);
				setConnected();
				simulator.goToHud();
        	    simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_simulator_connected));
        	} catch (UnknownHostException e) {
        		e.printStackTrace();
        		simulator.showMessage(simulator.getContext().getString(R.string.sim_unknown_host));
        		setDisconnected();
        	} catch (IOException e) {
        		e.printStackTrace();
        		simulator.showMessage(simulator.getContext().getString(R.string.sim_io_error)+": "+e.getMessage());
        		setDisconnected();
        	}
    	}
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
		    try {
				while (true){//Infinite simulation loop
					SpacecraftState sstate = (SpacecraftState) inputOStream.readObject();
					if(sstate!=null){
						simulator.getSimulationResults().updateSimulation(sstate, 0);
						
			            publishProgress();
					}
		            /*if(simulator.cancel){
		            	simulator.cancel=false;
		                break;
		            }*/
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_disconnected)+": "+e.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			}
    	}
        return true;
    }

	private long time_tmp_data = 0;
    private long time_tmp_gui = 0;
    @Override
    protected void onProgressUpdate(Void... values) {
    	if(time_tmp_gui==0 || (System.nanoTime()-time_tmp_gui)>Parameters.Simulator.min_hud_panel_refreshing_interval_ns){
    		time_tmp_gui = System.nanoTime();
    		simulator.getSimulationResults().updateHUD();
    	}
    	if(time_tmp_data==0 || (System.nanoTime()-time_tmp_data)>Parameters.Simulator.min_hud_model_refreshing_interval_ns){
    		time_tmp_data = System.nanoTime();
        	simulator.getSimulationResults().pushSimulationModel();
		}
    }
 
    @Override
    protected void onPreExecute() {
    	
    }
 
    @Override
    protected void onPostExecute(Boolean result) {
    	setDisconnected();
    }
 
    @Override
    protected void onCancelled() {
    	super.onCancelled();
    }
    
    private void setConnected(){
    	//Log.d("Sim",System.currentTimeMillis()+": "+"Simulator connected");
    	simulator.setSimulatorStatus(SimulatorStatus.Connected);
    }
    
    public void setDisconnected(){
		//Log.d("Sim",System.currentTimeMillis()+": "+"Simulator disconnected");
    	closeSocket();
    	simulator.setSimulatorStatus(SimulatorStatus.Disconnected);
    	simulator.resetSelectedMissionId();
    }

}