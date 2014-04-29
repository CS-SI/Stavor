package simulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import model.ModelSimulation;
import android.os.AsyncTask;
import android.widget.Toast;

public class SocketsThread extends AsyncTask<ModelSimulation, SimResults, Boolean> {
 
	ModelSimulation sim;
	String dstAddress;
	int dstPort;
	String response = "";
	
	SocketsThread(String addr, int port){
		dstAddress = addr;
		dstPort = port;
	}
	
    @Override
    protected Boolean doInBackground(ModelSimulation... params) {
    	Socket socket = null;
    	try {
    		socket = new Socket(dstAddress, dstPort);
    	    
    		ByteArrayOutputStream byteArrayOutputStream = 
    	                  new ByteArrayOutputStream(1024);
    		byte[] buffer = new byte[1024];
    	    
    	    int bytesRead;
    	    InputStream inputStream = socket.getInputStream();
    	    
    	    /*
    	     * notice:
    	     * inputStream.read() will block if no data return
    	     */
    	    while ((bytesRead = inputStream.read(buffer)) != -1){
    	    	byteArrayOutputStream.write(buffer, 0, bytesRead);
    	    	response += byteArrayOutputStream.toString("UTF-8");
    	    }

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
    	
    	
    	
    	sim = params[0];
        while(true) {
        	SimResults results = simulate();
            publishProgress(results);
            if(isCancelled())
                break;
        }
 
        return true;
    }
 
    @Override
    protected void onProgressUpdate(SimResults... values) {
        sim.updateSimulation(values[0].spacecraftState, values[0].sim_progress);
    }
 
    @Override
    protected void onPreExecute() {
    }
 
    @Override
    protected void onPostExecute(Boolean result) {
    	/*if(result)
            Toast.makeText(MainHilos.this, "Tarea finalizada!",
                    Toast.LENGTH_SHORT).show();*/
    }
 
    @Override
    protected void onCancelled() {
        /*Toast.makeText(MainHilos.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();*/
    }
}