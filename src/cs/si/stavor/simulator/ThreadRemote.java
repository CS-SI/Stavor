package cs.si.stavor.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.orekit.propagation.SpacecraftState;

import cs.si.stavor.R;
import cs.si.stavor.app.Parameters;
import android.os.Handler;

public class ThreadRemote extends Thread{
	private final Handler mHandler;
	
	private String dstAddress;
	private int dstPort;
	private boolean use_ssl = false;
	private Socket socket = null;
	private SSLSocket ssl_socket = null;
	private Simulator simulator;
	private ObjectInputStream inputOStream;
	
	ThreadRemote(Handler handler, Simulator simu, String addr, int port, boolean ssl) {
        mHandler = handler;

		simulator = simu;
		dstAddress = addr;
		dstPort = port;
		use_ssl = ssl;
    }
	
	public void closeSocket(){
		if(!use_ssl){
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			if(ssl_socket != null){
				try {
					ssl_socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	@Override public void run() {
		if(simulator.getSimulatorStatus().equals(SimulatorStatus.Disconnected)){
    		//Connect remote simulator
    		try {
    			simulator.setProgress(60 * 100);
    			if(!use_ssl){
	        		socket = new Socket();
	        		socket.connect(new InetSocketAddress(dstAddress, dstPort), Parameters.Simulator.Remote.remote_connection_timeout_ms);
					inputOStream = new ObjectInputStream( socket.getInputStream() );
    			}else{
    	            /*SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    	            ssl_socket = (SSLSocket) sslsocketfactory.createSocket(dstAddress, dstPort);
    	            inputOStream = new ObjectInputStream( ssl_socket.getInputStream() );*/
    	            
    	  	        KeyStore keyStore = KeyStore.getInstance("BKS");
    	  	        InputStream in = simulator.getContext().getResources().openRawResource(R.raw.server_certificate);
    	  	        try {
    	  	        	keyStore.load(in, "cs.si.orekitserver.14".toCharArray());
    	  	        } finally {
    	  	        	in.close();
    	  	        }
	    	  	    
    	            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
    	            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
    	            tmf.init(keyStore);

    	            SSLContext context = SSLContext.getInstance("TLSv1.2");
    	            context.init(null, tmf.getTrustManagers(), null);

    	            URL url = new URL("https", dstAddress, dstPort, "");
    	            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
    	            urlConnection.setSSLSocketFactory(context.getSocketFactory());
    	            urlConnection.setHostnameVerifier(hostnameVerifier);

    	            inputOStream = new ObjectInputStream( urlConnection.getInputStream() );
    			}
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
        	} catch (Exception e) {//SSL sockets
  	        	throw new AssertionError(e);
  	        	//XGGDEBUG: insert error message and remove throw
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
    private long time_tmp_gui = 0;
	private void publishProgress(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
            	if(time_tmp_gui==0 || (System.nanoTime()-time_tmp_gui)>Parameters.Simulator.min_hud_panel_refreshing_interval_ns){
            		time_tmp_gui = System.nanoTime();
            		simulator.getSimulationResults().updateHUD();
            	}
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
    
	// Create an HostnameVerifier that hardwires the expected hostname.
	// Note that is different than the URL's hostname:
    // example.com versus example.org
	HostnameVerifier hostnameVerifier = new HostnameVerifier() {
	    @Override
	    public boolean verify(String hostname, SSLSession session) {
	        HostnameVerifier hv =
	            HttpsURLConnection.getDefaultHostnameVerifier();//XGGDEBUG:SECURITY
	        boolean result = hv.verify("192.168.43.10", session);//Needs DNS name to work properly
	        return true;//Set to no hostname verification
	    }
	};
	
}
