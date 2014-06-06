package cs.si.stavor.model;

import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.xwalk.core.XWalkView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private ModelState state;
    private ModelInfo info;
    private MainActivity activity;
    private View view;
    private XWalkView browser;
    private boolean isBrowserLoaded;
    
    public ModelSimulation(MainActivity acv){
    	isBrowserLoaded = false;
    	activity=acv;
    	config = new ModelConfiguration(activity.getApplicationContext());
    	state = new ModelState();
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
    public void setHud(View hud, XWalkView mBrowser){
    	view = hud;
    	browser = mBrowser;
    	initViews();
    	
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
    
	/**
     * Returns the simulation step for the WebGL model in a JavaScript readable format
     * @return
     */
    public synchronized String getStateJSON() {
        return gson.toJson(state);
    }
    
    /**
     * Method used by the simulator to update the simulation state. 
     * It triggers the update of the Java UI HUD parameters.
     * @param st
     */
    private synchronized void updateState(ModelState st){
    	state = st;
    }
    
    /**
     * Pushes the new simulation step to the WebGL model
     */
    public void pushSimulationModel(){
    	if(browser!=null && state!=null && isBrowserLoaded){
    		browser.load("javascript:updateModelState('"+gson.toJson(state)+"')",null);
    	}
	}
    
    private synchronized void updateInfo(ModelInfo inf){
    	info = inf; 
    }
    
    private AbsoluteDate tmp_time;
    private Vector3D tmp_vel;
    private TimeScale utc;
    private Frame sunFrame;
    public void updateSimulation(SpacecraftState scs, int sim_progress){
    	
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
    }
    
    /**
     * Update the Hud panel with the new simulation step values
     */
    public synchronized void updateHUD(){
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
	
}
