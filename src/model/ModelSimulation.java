package model;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import app.Parameters;

import com.google.gson.Gson;

import cs.si.satatt.OrekitInit;
import cs.si.satatt.R;

public class ModelSimulation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3936082374216129814L;
	private Gson gson = new Gson();
    private ModelConfiguration config;
    private ModelState state;
    private ModelInfo info;
    private Activity activity;
    private View view;
    
    public ModelSimulation(Activity acv){
    	OrekitInit.init(R.raw.orekitdata,acv);
    	activity=acv;
    	config = new ModelConfiguration(activity.getApplicationContext());
    	state = new ModelState();
    	info = new ModelInfo();
    }
    
    public void preInitialize(){
    	//XGGDEBUG:user selection of the inertial ref frame
    	try {
			if(sunFrame==null){
				sunFrame = CelestialBodyFactory.getSun().getInertiallyOrientedFrame();
			}
			if(utc==null){
				utc = TimeScalesFactory.getUTC();
			}
    	} catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setHud(View hud){
    	view = hud;
    }
    
    public synchronized String getInitializationJSON() {
    	config = new ModelConfiguration(activity.getApplicationContext());
        return gson.toJson(config);
    }
    
    public synchronized String getStateJSON() {
        return gson.toJson(state);
    }
    
    /**
     * Method used by the simulator to update the simulation state. 
     * It triggers the update of the Java UI HUD parameters.
     * @param st
     */
    private synchronized void updateState(ModelState st){
    	Log.d("Sim",System.currentTimeMillis()+": "+"pre update 2");
    	state = st;
    	Log.d("Sim",System.currentTimeMillis()+": "+"post update 2");
    }
    
    private synchronized void updateInfo(ModelInfo inf){
    	Log.d("Sim",System.currentTimeMillis()+": "+"pre update 3");
    	info = inf; 
    	Log.d("Sim",System.currentTimeMillis()+": "+"post update 3");
    }
    
    private AbsoluteDate tmp_time;
    private Vector3D tmp_vel;
    private TimeScale utc;
    private Frame sunFrame;
    public void updateSimulation(SpacecraftState scs, int sim_progress){
    	Log.d("Sim",System.currentTimeMillis()+": "+"pre update 1");
    	ModelState new_state = new ModelState();
    	ModelInfo new_info = new ModelInfo();
    	
    	Vector3D velocity = scs.getPVCoordinates().getVelocity();
    	new_state.value_velocity[0] = velocity.getX()/1000;
    	new_state.value_velocity[1] = velocity.getY()/1000;
    	new_state.value_velocity[2] = velocity.getZ()/1000;
    	new_info.velocity = scs.getPVCoordinates().getVelocity().getNorm()/1000;
    	//XGGDEBUG: vector, direction, target
    	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	new_info.progress = sim_progress;
    	if(new_info.progress>100)
    		new_info.progress=100;
    	if(new_info.progress<0)
    		new_info.progress=0;
    	
		AbsoluteDate date = scs.getAttitude().getDate();
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

    	double[] angles = scs.getAttitude().getOrientation().getRotation().getAngles(RotationOrder.XYZ);
    	new_info.roll = angles[0];
    	new_info.pitch = angles[1];
    	new_info.yaw = angles[2];

    	new_info.mass = scs.getMass();
    	
    	updateState(new_state);
    	updateInfo(new_info);
    	Log.d("Sim",System.currentTimeMillis()+": "+"post update 1");
    }
    
    public synchronized void updateHUD(){
    	Log.d("Sim",System.currentTimeMillis()+": "+"pre update gui 1");
    	activity.runOnUiThread( new Runnable() {
	        @SuppressLint("ResourceAsColor")
			public void run() {
	        	if(view != null){
	        		TextView panel_time = (TextView)view.findViewById(R.id.textViewPanelTime);
	        		if(panel_time != null)
	        			panel_time.setText(info.time.replace("T", "  "));
	        		ProgressBar panel_progress = (ProgressBar)view.findViewById(R.id.progressBarPanelProgress);
	        		if(panel_progress != null)
	        			panel_progress.setProgress(info.progress);
	        		TextView panel_vel = (TextView)view.findViewById(R.id.textViewPanelVel);
	        		if(panel_vel != null){
	        			panel_vel.setText("Vel. "+String.format("%.2f", info.velocity)+" Km/s");
	        			if(info.velocity>config.limit_velocity)
	        				panel_vel.setTextColor(activity.getResources().getColor(R.color.panel_limit));
	        			else
	        				panel_vel.setTextColor(activity.getResources().getColor(R.color.panel_value));
	        		}
	        		TextView panel_accel = (TextView)view.findViewById(R.id.textViewPanelAccel);
	        		if(panel_accel != null){
	        			panel_accel.setText("Accel. "+String.format("%.2f", info.acceleration)+" Km/s2");
	        			if(info.acceleration>config.limit_acceleration)
	        				panel_accel.setTextColor(activity.getResources().getColor(R.color.panel_limit));
	        			else
	        				panel_accel.setTextColor(activity.getResources().getColor(R.color.panel_value));
	        		}
	        		TextView panel_radium = (TextView)view.findViewById(R.id.textViewPanelRadium);
	        		if(panel_radium != null)
	        			panel_radium.setText("Orbit radium: "+String.format("%.1f", info.orbit_radium)+" Km");
	        		TextView panel_mass = (TextView)view.findViewById(R.id.textViewPanelMass);
	        		if(panel_mass != null)
	        			panel_mass.setText("Mass: "+String.format("%.3f", info.mass)+" Kg");
	        		TextView panel_roll = (TextView)view.findViewById(R.id.textViewPanelRoll);
	        		if(panel_roll != null)
	        			panel_roll.setText("Rol: "+String.format("%.3f", info.roll));
	        		TextView panel_pitch = (TextView)view.findViewById(R.id.textViewPanelPitch);
	        		if(panel_pitch != null)
	        			panel_pitch.setText("Pitch: "+String.format("%.3f", info.pitch));
	        		TextView panel_yaw = (TextView)view.findViewById(R.id.textViewPanelYaw);
	        		if(panel_yaw != null)
	        			panel_yaw.setText("Yaw: "+String.format("%.3f", info.yaw));
	        	}
	        }
	    });
    	Log.d("Sim",System.currentTimeMillis()+": "+"post update gui 1");
    }
}
