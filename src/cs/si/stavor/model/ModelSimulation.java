package cs.si.stavor.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.xwalk.core.XWalkView;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.webkit.WebView;

import com.google.gson.Gson;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;

/**
 * Contains and handles both the model information and configuration
 * @author Xavier Gibert
 *
 */
public class ModelSimulation {
	private Gson gson = new Gson();
    private ModelConfiguration config;
    private ModelConfigurationOrbit config_orbit;
    private ModelState state;
    private ModelStateOrbit state_orbit;
    private ModelInfo info;
    private MainActivity activity;
    private View view;
    private XWalkView browser;
    private WebView browser2;
    private boolean isBrowserLoaded;
    private Browsers selectedBrowser = Browsers.None;
    
    public ModelSimulation(MainActivity acv){
    	isBrowserLoaded = false;
    	activity=acv;
    	config = new ModelConfiguration(activity.getApplicationContext());
    	config_orbit = new ModelConfigurationOrbit(activity.getApplicationContext());
    	state = new ModelState();
    	state_orbit = new ModelStateOrbit();
    	info = new ModelInfo();
    }
    
    /**
     * Initialize the required elements for the simulation, 
     * before the simulator is played to save time
     */
    public void preInitialize(){
    	try {
			if(sunFrame==null){
				sunFrame = CelestialBodyFactory.getSun().getInertiallyOrientedFrame();
			}
			if(earthFixedFrame==null){
				earthFixedFrame = CelestialBodyFactory.getEarth().getBodyOrientedFrame();
			}
			if(utc==null){
				utc = TimeScalesFactory.getUTC();
			}
    	} catch (OrekitException e) {
			e.printStackTrace();
			activity.showErrorDialog(activity.getString(R.string.error_initializing_orekit), false);
		}
    }
    
    /**
     * Establish the Hud View
     * @param hud
     * @param mBrowser
     */
    public void setHud(Browsers type, View hud, View mBrowser){
    	selectedBrowser = type;
		view = hud;
    	if(selectedBrowser.equals(Browsers.Attitude)){
        	initViews();
        	browser = (XWalkView)mBrowser;
    	}else if(selectedBrowser.equals(Browsers.Orbit)){
    		uninitViews();
        	browser = (XWalkView)mBrowser;
    	}else if(selectedBrowser.equals(Browsers.Map)){
    		uninitViews();
        	browser2 = (WebView)mBrowser;
    	}
    }
    
    public void clearHud(){
      	selectedBrowser = Browsers.None;
      	view = null;
      	uninitViews();
    	browser = null;
    	browser2 = null;
    }
    
    /**
     * Set the loaded status of the browser
     * @param is
     */
    public void setBrowserloaded(boolean is) {
    	isBrowserLoaded = is;
	}

    /**
     * Returns the Initialization for the WebGL model in a JavaScript readable format
     * @return
     */
	public synchronized String getInitializationJSON() {
    	config = new ModelConfiguration(activity.getApplicationContext());
        return gson.toJson(config);
    }
	
	public synchronized String getInitializationOrbitJSON() {
		config_orbit = new ModelConfigurationOrbit(activity.getApplicationContext());
        return gson.toJson(config_orbit);
	}
    
    /**
     * Method used by the simulator to update the simulation state. 
     * It triggers the update of the Java UI HUD parameters.
     * @param st
     */
    private synchronized void updateState(ModelState st){
    	state = st;
    }
    
    private synchronized void updateStateOrbit(ModelStateOrbit st){
    	state_orbit = st;
    }
    
    /**
     * Pushes the new simulation step to the WebGL model
     */
    public void pushSimulationModel(){
    	if(browser!=null && isBrowserLoaded){
    		if(state!=null && selectedBrowser.equals(Browsers.Attitude))
    			browser.load("javascript:updateModelState('"+gson.toJson(state)+"')",null);
    		else if(state_orbit!=null && selectedBrowser.equals(Browsers.Orbit)){
    			browser.load("javascript:updateModelState('"+gson.toJson(state_orbit)+"')",null);
    		}
    	}else if(browser2!=null && isBrowserLoaded){
    		if(selectedBrowser.equals(Browsers.Map)){
    			if(mapPathBuffer.size()!=0){
    				browser2.loadUrl("javascript:updateModelState('"+gson.toJson(getMapPathBuffer())+"')");
    			}
    		}
    	}
	}
    
    private synchronized void updateInfo(ModelInfo inf){
    	info = inf; 
    }
    
    private AbsoluteDate tmp_time;
    private Vector3D tmp_vel;
    private TimeScale utc;
    private Frame sunFrame, earthFixedFrame;
    public void updateSimulation(SpacecraftState scs, int sim_progress){
    	if(selectedBrowser.equals(Browsers.Attitude)){
	    	ModelState new_state = new ModelState();
	    	ModelInfo new_info = new ModelInfo();
	    	
	    	//Basic indicators and Attitude
	    	Attitude sc_att = scs.getAttitude();
	    	new_state.value_attitude = new Quat(sc_att.getOrientation().getRotation());
	    	
	    	Vector3D velocity = scs.getPVCoordinates().getVelocity();
	    	new_state.value_velocity[0] = velocity.getX()/1000;
	    	new_state.value_velocity[1] = velocity.getY()/1000;
	    	new_state.value_velocity[2] = velocity.getZ()/1000;
	    	new_info.velocity = scs.getPVCoordinates().getVelocity().getNorm()/1000;
	    	
	    	new_state.value_momentum[0] = scs.getPVCoordinates().getMomentum().getX();
	    	new_state.value_momentum[1] = scs.getPVCoordinates().getMomentum().getY();
	    	new_state.value_momentum[2] = scs.getPVCoordinates().getMomentum().getZ();
	    	
	    	Vector3D earth = scs.getPVCoordinates().getPosition().negate();
	    	new_state.value_earth[0] = earth.getX()/1000;
	    	new_state.value_earth[1] = earth.getY()/1000;
	    	new_state.value_earth[2] = earth.getZ()/1000;
	    	new_info.orbit_radium = earth.getNorm()/1000;
	    	
	    	try {
	    		Vector3D sun = scs.getPVCoordinates(sunFrame).getPosition().negate();
				
				new_state.value_sun[0] = sun.getX()/1000;
		    	new_state.value_sun[1] = sun.getY()/1000;
		    	new_state.value_sun[2] = sun.getZ()/1000;
		    	
			} catch (OrekitException e) {
				e.printStackTrace();
				activity.showErrorDialog(activity.getString(R.string.error_computing_orekit), false);
			}
	    	
	    	new_info.progress = sim_progress;
	    	if(new_info.progress>100)
	    		new_info.progress=100;
	    	if(new_info.progress<0)
	    		new_info.progress=0;
	    	
			AbsoluteDate date = sc_att.getDate();
			new_info.time = date.getComponents(utc).toString();
	
			//Compute acceleration
			if(tmp_time != null){
				double delay = date.offsetFrom(tmp_time,utc);
				Vector3D acceleration = new Vector3D(
						(velocity.getX()-tmp_vel.getX())/delay,
						(velocity.getY()-tmp_vel.getY())/delay,
						(velocity.getZ()-tmp_vel.getZ())/delay);
				new_state.value_acceleration[0] = acceleration.getX();
				new_state.value_acceleration[1] = acceleration.getY();
				new_state.value_acceleration[2] = acceleration.getZ(); 
				if(Double.isNaN(new_state.value_acceleration[0]))
					new_state.value_acceleration[0]=0.0;
				if(Double.isNaN(new_state.value_acceleration[1]))
					new_state.value_acceleration[1]=0.0;
				if(Double.isNaN(new_state.value_acceleration[2]))
					new_state.value_acceleration[2]=0.0;
				new_info.acceleration = acceleration.getNorm();
			}
			
			//Update temporal variables for acceleration computation
	    	tmp_vel = velocity;
	    	tmp_time = date;
	
	    	double[] angles = sc_att.getOrientation().getRotation().getAngles(RotationOrder.XYZ);
	    	new_info.roll = angles[0];
	    	new_info.pitch = angles[1];
	    	new_info.yaw = angles[2];
	
	    	new_info.mass = scs.getMass();
	    	
	    	updateState(new_state);
	    	updateInfo(new_info);
    	}else if(selectedBrowser.equals(Browsers.Orbit)){
    		ModelStateOrbit new_state = new ModelStateOrbit();
    		

			AbsoluteDate date = scs.getAttitude().getDate();
    		
    		try {
				Rotation rot = scs.getFrame().getTransformTo(earthFixedFrame, date).getRotation();
	    		new_state.value_earth_rotation = new Quat(rot);
			} catch (OrekitException e) {
				e.printStackTrace();
			}
    		
    		Vector3D spacecraft = scs.getPVCoordinates().getPosition();
	    	new_state.value_spacecraft[0] = spacecraft.getX();
	    	new_state.value_spacecraft[1] = spacecraft.getY();
	    	new_state.value_spacecraft[2] = spacecraft.getZ();
	    	
	    	new_state.value_orbit_a = scs.getA();
	    	new_state.value_orbit_e = scs.getE();
	    	new_state.value_orbit_i = scs.getI();
	    	KeplerianOrbit keplerOrb = ((KeplerianOrbit)scs.getOrbit());
	    	new_state.value_orbit_w = keplerOrb.getPerigeeArgument();
	    	new_state.value_orbit_raan = keplerOrb.getRightAscensionOfAscendingNode();
    		
    		updateStateOrbit(new_state);
    	}else if(selectedBrowser.equals(Browsers.Map)){
    		
    		OneAxisEllipsoid earth = new OneAxisEllipsoid(
    				Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
    		 		Constants.WGS84_EARTH_FLATTENING,
    		 		earthFixedFrame);
    		 try {
    		 	GeodeticPoint gp = earth.transform(scs.getPVCoordinates().getPosition(), scs.getFrame(), scs.getDate());
    		 	double lat = gp.getLatitude()*180/Math.PI;
    		 	double lon = gp.getLongitude()*180/Math.PI;
    		 	if(!Double.isNaN(lat)&&!Double.isNaN(lon))
    		 		addToMapPathBuffer(lat, lon);
    		} catch (OrekitException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * Update the Hud panel with the new simulation step values
     */
    public synchronized void updateHUD(){
    	if(selectedBrowser.equals(Browsers.Attitude)){
    		if(panel_time != null)
    			panel_time.setText(info.time.replace("T", "  "));
    		if(panel_progress != null)
    			panel_progress.setProgress(info.progress);
    		if(panel_vel != null){
    			panel_vel.setText(activity.getString(R.string.panel_vel)+" "+String.format("%.2f", info.velocity)+" Km/s");
    			if(info.velocity>config.limit_velocity)
    				panel_vel.setTextColor(activity.getResources().getColor(R.color.panel_limit));
    			else
    				panel_vel.setTextColor(activity.getResources().getColor(R.color.panel_value));
    		}
    		if(panel_accel != null){
    			panel_accel.setText(activity.getString(R.string.panel_accel)+" "+String.format("%.2f", info.acceleration)+" Km/s2");
    			if(info.acceleration>config.limit_acceleration)
    				panel_accel.setTextColor(activity.getResources().getColor(R.color.panel_limit));
    			else
    				panel_accel.setTextColor(activity.getResources().getColor(R.color.panel_value));
    		}
    		if(panel_radium != null)
    			panel_radium.setText(activity.getString(R.string.panel_radium)+" "+String.format("%.1f", info.orbit_radium)+" Km");
    		if(panel_mass != null)
    			panel_mass.setText(activity.getString(R.string.panel_mass)+" "+String.format("%.1f", info.mass)+" Kg");
    		if(panel_roll != null)
    			panel_roll.setText("Rol: "+String.format("%.1f", (180*info.roll/Math.PI))+"º");
    		if(panel_pitch != null)
    			panel_pitch.setText("Pitch: "+String.format("%.1f", (180*info.pitch/Math.PI))+"º");
    		if(panel_yaw != null)
    			panel_yaw.setText("Yaw: "+String.format("%.1f", (180*info.yaw/Math.PI))+"º");
    	}
    }
    
    TextView panel_time;
	ProgressBar panel_progress;
	TextView panel_vel;
	TextView panel_accel;
	TextView panel_radium;
	TextView panel_mass;
	TextView panel_roll;
	TextView panel_pitch;
	TextView panel_yaw;
	
	private void initViews() {
		if(view != null){
	    	panel_time = (TextView)view.findViewById(R.id.textViewPanelTime);
			panel_progress = (ProgressBar)view.findViewById(R.id.progressBarPanelProgress);
			panel_vel = (TextView)view.findViewById(R.id.textViewPanelVel);
			panel_accel = (TextView)view.findViewById(R.id.textViewPanelAccel);
			panel_radium = (TextView)view.findViewById(R.id.textViewPanelRadium);
			panel_mass = (TextView)view.findViewById(R.id.textViewPanelMass);
			panel_roll = (TextView)view.findViewById(R.id.textViewPanelRoll);
			panel_pitch = (TextView)view.findViewById(R.id.textViewPanelPitch);
			panel_yaw = (TextView)view.findViewById(R.id.textViewPanelYaw);
		}
	}
	private void uninitViews() {
    	panel_time = null;
		panel_progress = null;
		panel_vel = null;
		panel_accel = null;
		panel_radium = null;
		panel_mass = null;
		panel_roll = null;
		panel_pitch = null;
		panel_yaw = null;
	}

	private ArrayList<MapPoint> mapPathBuffer = new ArrayList<MapPoint>();
	public synchronized void resetMapPathBuffer() {
		mapPathBuffer.clear();
	}
	public synchronized void addToMapPathBuffer(double lat, double lon) {
		mapPathBuffer.add(new MapPoint(lat,lon));
	}
	public synchronized MapPoint[] getMapPathBuffer(){
		MapPoint[] r =
				  (MapPoint[])mapPathBuffer.toArray(new MapPoint[mapPathBuffer.size()]);
		resetMapPathBuffer();
		return r;
	}
	
	class MapPoint{
		public MapPoint(double lat, double lon){
			latitude = lat;
			longitude = lon;
		}
		double latitude = 0;
		double longitude = 0;
	}

	
}
