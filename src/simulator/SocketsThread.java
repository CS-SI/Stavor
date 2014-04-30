package simulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.orekit.propagation.SpacecraftState;

import model.ModelSimulation;
import android.os.AsyncTask;
import android.widget.Toast;

public class SocketsThread extends AsyncTask<ModelSimulation, SimResults, Boolean>{
 
	private ModelSimulation sim;
	private String dstAddress;
	private int dstPort;
	private Socket socket = null;
	private Simulator simulator;
	private ObjectInputStream inputOStream;
	private int bytesRead;
    private InputStream inputStream;
    private byte[] buffer;
    private ByteArrayOutputStream byteArrayOutputStream;
	
	String response = "";
	
	public SocketsThread(Simulator simu, String addr, int port){
		simulator = simu;
		dstAddress = addr;
		dstPort = port;
	}
	
    @Override
    protected Boolean doInBackground(ModelSimulation... params) {
    	/*
	     * notice:
	     * inputStream.read() will block if no data return
	     */
	    try {
    		response = "";
			while ((bytesRead = inputStream.read(buffer)) != -1){
				//byteArrayOutputStream.write(buffer, 0, bytesRead);
				//response += byteArrayOutputStream.toString("UTF-8");
				
				inputOStream = new ObjectInputStream(inputStream);
				SpacecraftState sstate = (SpacecraftState) inputOStream.readObject();

				SimResults results = new SimResults(sstate, 0);
	            publishProgress(results);
	            if(isCancelled())
	                break;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return true;
    }
 
    @Override
    protected void onProgressUpdate(SimResults... values) {
        sim.updateSimulation(values[0].spacecraftState, values[0].sim_progress);
    }
 
    @Override
    protected void onPreExecute() {
    	try {
    		socket = new Socket(dstAddress, dstPort);
    		byteArrayOutputStream = new ByteArrayOutputStream(1024);
    		buffer = new byte[1024];
    	    inputStream = socket.getInputStream();
    	    simulator.setSimulatorStatus(SimulatorStatus.Connected);
    	} catch (UnknownHostException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		response = "UnknownHostException: " + e.toString();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		response = "IOException: " + e.toString();
    	}finally{
    		if(socket != null){
    			try {
    				socket.close();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}
    }
 
    @Override
    protected void onPostExecute(Boolean result) {
    	if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	simulator.setSimulatorStatus(SimulatorStatus.Disconnected);
    }
 
    @Override
    protected void onCancelled() {
        /*Toast.makeText(MainHilos.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();*/
    }

}