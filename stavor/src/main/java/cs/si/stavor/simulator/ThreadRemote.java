package cs.si.stavor.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.config.RequestConfig;
import ch.boye.httpclientandroidlib.client.methods.CloseableHttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;
import ch.boye.httpclientandroidlib.conn.ssl.SSLConnectionSocketFactory;
import ch.boye.httpclientandroidlib.impl.client.CloseableHttpClient;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
				/*try {
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		}
	}
	
	@Override public void run() {
		ArrayList<SpacecraftState> init_states = new ArrayList<SpacecraftState>();
		if(simulator.getSimulatorStatus().equals(SimulatorStatus.Disconnected)){
    		//Connect remote simulator
    		try {
    			simulator.setProgress(60 * 100);
    			if(!use_ssl){
	        		socket = new Socket();
	        		socket.connect(new InetSocketAddress(dstAddress, dstPort), Parameters.Simulator.Remote.remote_connection_timeout_ms);
					inputOStream = new ObjectInputStream( socket.getInputStream() );
					
	    			simulator.setProgress(80 * 100);
					setConnected();
	        	    simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_simulator_connected));
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

	    			simulator.setProgress(80 * 100);
	    			
    	         // HTTP client
    	    		client = HttpClients.custom()
    	    				.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
    	    				.setSslcontext(context)
    	    				.build();
    	    		
    	    		RequestConfig requestConfig = RequestConfig.custom()
    	    				.setConnectionRequestTimeout(Parameters.Simulator.Remote.remote_connection_timeout_ms)
    	    		        .setSocketTimeout(Parameters.Simulator.Remote.remote_connection_timeout_ms)
    	    		        .setConnectTimeout(Parameters.Simulator.Remote.remote_connection_timeout_ms)
    	    		        .build();
    	    		
    	    		final HttpParams httpParams = new BasicHttpParams();
    	    		HttpConnectionParams.setSoTimeout(httpParams, Parameters.Simulator.Remote.remote_connection_timeout_ms);
    	    		HttpConnectionParams.setConnectionTimeout(httpParams, Parameters.Simulator.Remote.remote_connection_timeout_ms);
    	    		
    	    		// GET request to execute
    	    		/*get = new HttpGet(
    	    				"https://192.168.43.10:8443/OrekitWebServer/StavorServlet");*/
    	    		
    	    		String[] path = dstAddress.split("/",2);
    	    		String host = path[0];
    	    		String files = "";
    	    		if(path.length==2)
    	    			files = path[1];

    	    		//URL uri = new URL("https", host, dstPort, files);
    	    		
    	    		URI uri = new URIBuilder()
    	    	    .setScheme("https")
    	    	    .setHost(host)
    	    	    .setPort(dstPort)
    	    	    .setPath("/"+files)
    	    	    .addParameter("clientId", String.valueOf(client_id))
    	    	    .addParameter("xObjects", Integer.toString(Parameters.Simulator.Remote.objects_per_ssl_request))
    	    	    .build();
    	    		
    	    		
    	    		get = new HttpGet(uri);//+"?clientId="+client_id
    	    		get.setConfig(requestConfig);
    	    		
    	    		init_states = getSimObject();
					if(init_states.size()!=0){
						setConnected();
		        	    simulator.showMessage(simulator.getContext().getString(R.string.sim_remote_simulator_connected));
					}else{
						setDisconnected();
					}
    	    		
    			}
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
					if(!use_ssl){
						SpacecraftState sstate = (SpacecraftState) inputOStream.readObject();
						if(sstate!=null){
							simulator.getSimulationResults().updateSimulation(sstate, 0);
				            publishProgress();
						}
					}else{
						if(init_states.size()!=0){
							for(int j = 0; j < init_states.size(); j++){
								simulator.getSimulationResults().updateSimulation(init_states.get(j), 0);
					            publishProgress();
					            timer();
							}
							init_states.clear();
						}else{
							ArrayList<SpacecraftState> loop_states = getSimObject();
							for(int j = 0; j < loop_states.size(); j++){
								simulator.getSimulationResults().updateSimulation(loop_states.get(j), 0);
					            publishProgress();
					            timer();
							}
						}
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
			} catch (Exception e) {
        		e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+e.getMessage());
  	        }
    	}
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected))
    		setDisconnected();
    }
	
	private long time_tmp_data = 0;
    private long time_tmp_gui = 0;
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
    	//if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
    		closeSocket();
	    	simulator.setSimulatorStatus(SimulatorStatus.Disconnected);
	    	simulator.resetSelectedMissionId();
    	//}
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
    
    private long old_time = 0;
    private void timer(){
    	if(old_time!=0){
    		long new_time = System.nanoTime();
	    	long dur = new_time-old_time;
	    	if(dur<Parameters.Simulator.min_hud_model_refreshing_interval_ns){
	    		try {
					Thread.sleep((Parameters.Simulator.min_hud_model_refreshing_interval_ns-dur)/1000000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
    		old_time = new_time;
    	}else{
    		old_time = System.nanoTime();
    	}
    }
    
    private ArrayList<SpacecraftState> getSimObject(){
    	ArrayList<SpacecraftState> states = new ArrayList<SpacecraftState>();
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
				
				for(int k = 0; k < Parameters.Simulator.Remote.objects_per_ssl_request; k++){
					states.add((SpacecraftState) is.readObject());
				}
	
				// Display the object retrieved
				//System.out.println("Retrieved sim object");

				// Close the connection
				response.close();
			} else {
				// Unexpected response
				System.out.println("ERROR : " + status.getReasonPhrase());
				simulator.showMessage(simulator.getContext().getString(R.string.sim_error)+": "+status.getReasonPhrase());
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
        return states;
    }
	
}
