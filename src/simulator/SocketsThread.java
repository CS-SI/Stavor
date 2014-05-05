package simulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.orekit.propagation.SpacecraftState;

import cs.si.satatt.R;
import cs.si.satatt.SerializationUtil;
import model.ModelSimulation;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SocketsThread extends AsyncTask<ModelSimulation, SimResults, Boolean>{
 
	private String dstAddress;
	private int dstPort;
	private Socket socket = null;
	private Simulator simulator;
	private ObjectInputStream inputOStream;
	private int bytesRead;
    private InputStream inputStream;
    private byte[] buffer;
    private ByteArrayInputStream bis;
    private ByteArrayOutputStream byteArrayOutputStream;
	
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
    		try {
        		socket = new Socket(dstAddress, dstPort);
        		socket.setTcpNoDelay(true);
        		byteArrayOutputStream = new ByteArrayOutputStream(1024);
        		buffer = new byte[524288];
        	    inputStream = socket.getInputStream();
				inputOStream = new ObjectInputStream( inputStream );
				setConnected();
				simulator.goToHud();
        	    simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_simulator_connected));
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
				while (true){
					long procTime = System.currentTimeMillis();
					SpacecraftState sstate = (SpacecraftState) inputOStream.readObject();
					if(sstate!=null){
						SimResults results = new SimResults(sstate, 0);
			            publishProgress(results);
					}
			    	Log.d("Simulator", "Receive time: "+(System.currentTimeMillis()-procTime));
		            if(isCancelled())
		                break;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_disconnected));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			}
    	}
        return true;
    }
 
    @Override
    protected void onProgressUpdate(SimResults... values) {

		long procTime = System.currentTimeMillis();
        simulator.getSimulationResults().updateSimulation(values[0].spacecraftState, values[0].sim_progress);

    	Log.d("Simulator", "Update time: "+(System.currentTimeMillis()-procTime));
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
    	simulator.setSimulatorStatus(SimulatorStatus.Connected);
    }
    
    public void setDisconnected(){
    	closeSocket();
    	simulator.setSimulatorStatus(SimulatorStatus.Disconnected);
    }

}