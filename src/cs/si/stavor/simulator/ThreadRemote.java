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
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.methods.CloseableHttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.conn.ssl.SSLConnectionSocketFactory;
import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;
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
	private CloseableHttpClient client = null;
	private Simulator simulator;
	private ObjectInputStream inputOStream;
	private HttpGet get;
	
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
			if(client != null){
				try {
					client.close();
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
    				
    	            //Generate random client_id
    				Random randomGenerator = new Random();
    				long client_id = randomGenerator.nextLong();
    				
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

    	            /*URL url = new URL("https", dstAddress, dstPort, "");
    	            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
    	            urlConnection.setSSLSocketFactory(context.getSocketFactory());
    	            urlConnection.setHostnameVerifier(hostnameVerifier);

    	            inputOStream = new ObjectInputStream( urlConnection.getInputStream() );
    	            */
    	            //----------------------------------
    	         // HTTP client
    	    		client = HttpClients.custom()
    	    				.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
    	    				.setSslcontext(context)
    	    				.build();
    	    		
    	    		/*RequestConfig requestConfig = RequestConfig.custom()
    	    		        .setSocketTimeout(Parameters.Simulator.Remote.remote_connection_timeout_ms)
    	    		        .setConnectTimeout(Parameters.Simulator.Remote.remote_connection_timeout_ms)
    	    		        .build();*/
    	    		
    	    		// GET request to execute
    	    		/*HttpGet get = new HttpGet(
    	    				"https://127.0.0.1:8443/OrekitWebServer/StavorServlet");*/

    	    		URL uri = new URL("https", dstAddress, dstPort, "OrekitWebServer/StavorServlet");
    	    		get = new HttpGet(uri.toURI()+"?client_id="+client_id);
    	    		
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
        		e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
				setDisconnected();
  	        }
    	}
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
		    try {
				while (true){//Infinite simulation loop
					if(simulator.getSimulatorStatus().equals(SimulatorStatus.Disconnected))
						break;
					SpacecraftState sstate = null;
					if(!use_ssl){
						sstate = (SpacecraftState) inputOStream.readObject();
					}else{
						sstate = getSimObject();
					}
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
	/*HostnameVerifier hostnameVerifier = new HostnameVerifier() {
	    @Override
	    public boolean verify(String hostname, SSLSession session) {
	        HostnameVerifier hv =
	            HttpsURLConnection.getDefaultHostnameVerifier();//XGGDEBUG:SECURITY
	        boolean result = hv.verify("192.168.43.10", session);//Needs DNS name to work properly
	        return true;//Set to no hostname verification
	    }
	};*/
    
    private SpacecraftState getSimObject(){
    	SpacecraftState sstate = null;
    	try{
			// Execute the HTTP request
			CloseableHttpResponse response = client.execute(get);
			StatusLine status = response.getStatusLine();
			HttpEntity entity = response.getEntity();
	
			// Test the status code before extracting the data
			if (status.getStatusCode() == HttpStatus.SC_OK) {
	
				// Extract the object from the response
				ObjectInputStream is = new ObjectInputStream(
						entity.getContent());
				sstate = (SpacecraftState) is.readObject();
	
				// Display the object retrieved
				System.out.println("Retrieved sim object");

				// Close the connection
				response.close();
			} else {
				// Unexpected response
				System.out.println("ERROR : " + status.getReasonPhrase());
		        	//XGGDEBUG: insert error message
				setDisconnected();
			}
    	} catch (ClassNotFoundException e) {
			// Cannot retrieve the object
			e.printStackTrace();
			simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			setDisconnected();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			setDisconnected();
		} catch (IOException e) {
			e.printStackTrace();
			simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
			setDisconnected();
		}
        return sstate;
    }
	
}
