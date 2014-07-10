package cs.si.stavor.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.orekit.propagation.SpacecraftState;

import cs.si.satcor.R;
import cs.si.stavor.app.Parameters;
import android.os.Handler;

public class ThreadRemote extends Thread{
	private final Handler mHandler;
	
	private String dstAddress;
	private int dstPort;
	private Socket socket = null;
	private Simulator simulator;
	private ObjectInputStream inputOStream;
    private InputStream inputStream;
	
	ThreadRemote(Handler handler, Simulator simu, String addr, int port) {
        mHandler = handler;

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
	

	@Override public void run() {
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
    	setDisconnected();
    }
	
	private long time_tmp_data = 0;
	private void publishProgress(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            	if(time_tmp_data==0 || (System.nanoTime()-time_tmp_data)>Parameters.Simulator.min_hud_model_refreshing_interval_ns){
            		time_tmp_data = System.nanoTime();
                	simulator.getSimulationResults().pushSimulationModel();
        		}
            }
        });
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
