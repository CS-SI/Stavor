package simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.orekit.propagation.SpacecraftState;

import cs.si.satatt.R;
import model.ModelSimulation;
import android.os.AsyncTask;
import android.util.Log;
import app.Parameters;

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
	
	private void closeSocket(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    @Override
    protected Boolean doInBackground(ModelSimulation... params) {
    	
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Disconnected)){
    		Log.d("Sim",System.currentTimeMillis()+": "+"simulator connecting socket");
    		try {
        		socket = new Socket(dstAddress, dstPort);
        		//socket.setTcpNoDelay(true);
        	    inputStream = socket.getInputStream();
				inputOStream = new ObjectInputStream( inputStream );
				setConnected();
				simulator.goToHud();
        	    simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_simulator_connected));
        	    Log.d("Sim",System.currentTimeMillis()+": "+"socket openend");
        	} catch (UnknownHostException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        		simulator.showMessage(simulator.getContext().getString(R.string.sim_unknown_host));
        		setDisconnected();
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        		simulator.showMessage(simulator.getContext().getString(R.string.sim_io_error)+": "+e.getMessage());
        		setDisconnected();
        	}
    	}
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
		    try {
		    	//Log.d("Sim",System.currentTimeMillis()+": "+"enter infinite loop");
				while (true){
					//Log.d("Sim",System.currentTimeMillis()+": "+"before readObject");
					SpacecraftState sstate = (SpacecraftState) inputOStream.readObject();
					//Log.d("Sim",System.currentTimeMillis()+": "+"after readObject");
					if(sstate!=null){
						SimResults results = new SimResults(sstate, 0);
						simulator.getSimulationResults().updateSimulation(results.spacecraftState, results.sim_progress);
						//Log.d("Sim",System.currentTimeMillis()+": "+"end update data");
						
			            publishProgress();
					}
		            if(isCancelled())
		                break;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_disconnected));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
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
    		//Log.d("Sim",System.currentTimeMillis()+": "+"update gui");
    		time_tmp_gui = System.nanoTime();
    		simulator.getSimulationResults().updateHUD();
    		//Log.d("Sim",System.currentTimeMillis()+": "+"end update gui");
    	}
    	if(time_tmp_data==0 || (System.nanoTime()-time_tmp_data)>Parameters.Simulator.min_hud_model_refreshing_interval_ns){
			//Log.d("Sim",System.currentTimeMillis()+": "+"update data");
    		time_tmp_data = System.nanoTime();
        	simulator.getSimulationResults().pushSimulationModel();
			//Log.d("Sim",System.currentTimeMillis()+": "+"end update data");
		}else{
			//Log.d("Sim",System.currentTimeMillis()+": "+"avoid update data");
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
        /*Toast.makeText(MainHilos.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();*/
    	//setDisconnected();
    }
    
    private void setConnected(){
    	Log.d("Sim",System.currentTimeMillis()+": "+"Simulator connected");
    	simulator.setSimulatorStatus(SimulatorStatus.Connected);
    }
    
    public void setDisconnected(){
		Log.d("Sim",System.currentTimeMillis()+": "+"Simulator disconnected");
    	closeSocket();
    	simulator.setSimulatorStatus(SimulatorStatus.Disconnected);
    }

}