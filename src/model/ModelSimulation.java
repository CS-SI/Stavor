package model;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import cs.si.satatt.Parameters;
import cs.si.satatt.R;

public class ModelSimulation implements Serializable {
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
    	System.setProperty("orekit.data.path", Parameters.Orekit_config.orekit_data);
    	activity=acv;
    	config = new ModelConfiguration(activity.getApplicationContext());
    	state = new ModelState();
    	info = new ModelInfo();
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
    	state = st;
    }
    
    private synchronized void updateInfo(ModelInfo inf){
    	info = inf;
    	updateHUD();
    }
    
    private AbsoluteDate tmp_time;
    private Vector3D tmp_vel;
    public void updateSimulation(SpacecraftState scs, int sim_progress){
    	ModelState new_state = new ModelState();
    	ModelInfo new_info = new ModelInfo();
    	
    	Vector3D velocity = scs.getPVCoordinates().getVelocity();
    	new_state.value_velocity[0] = velocity.getX();
    	new_state.value_velocity[1] = velocity.getY();
    	new_state.value_velocity[2] = velocity.getZ();
    	new_info.velocity = scs.getPVCoordinates().getVelocity().getNorm();
    	
    	//XGGDEBUG: vector, direction, target
    	
    	new_state.value_momentum[0] = scs.getPVCoordinates().getMomentum().getX();
    	new_state.value_momentum[1] = scs.getPVCoordinates().getMomentum().getY();
    	new_state.value_momentum[2] = scs.getPVCoordinates().getMomentum().getZ();
    	
    	Vector3D earth = scs.getPVCoordinates().getPosition().negate();
    	new_state.value_earth[0] = earth.getX();
    	new_state.value_earth[1] = earth.getY();
    	new_state.value_earth[2] = earth.getZ();
    	new_info.orbit_radium = earth.getNorm();
    	
    	try {//XGGDEBUG:user selection of the inertial ref frame
			Vector3D sun = scs.getPVCoordinates(FramesFactory.getEME2000()).getPosition().negate();
			new_state.value_sun[0] = sun.getX();
	    	new_state.value_sun[1] = sun.getY();
	    	new_state.value_sun[2] = sun.getZ();
		} catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	new_info.progress = sim_progress;
    	if(new_info.progress>100)
    		new_info.progress=100;
    	if(new_info.progress<0)
    		new_info.progress=0;
    	
    	try {
    		TimeScale utc = TimeScalesFactory.getUTC();
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
        	
		} catch (OrekitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	double[] angles = scs.getAttitude().getOrientation().getRotation().getAngles(RotationOrder.XYZ);
    	new_info.roll = angles[0];
    	new_info.pitch = angles[1];
    	new_info.yaw = angles[2];

    	new_info.mass = scs.getMass();
    	
    	updateState(new_state);
    	updateInfo(new_info);
    }
    
    private void updateHUD(){
    	activity.runOnUiThread( new Runnable() {
	        @SuppressLint("ResourceAsColor")
			public void run() {
	        	if(view != null){
	        		TextView panel_time = (TextView)view.findViewById(R.id.textViewPanelTime);
	        		if(panel_time != null)
	        			panel_time.setText(info.time);
	        		ProgressBar panel_progress = (ProgressBar)view.findViewById(R.id.progressBarPanelProgress);
	        		if(panel_progress != null)
	        			panel_progress.setProgress(info.progress);
	        		TextView panel_vel = (TextView)view.findViewById(R.id.textViewPanelVel);
	        		if(panel_vel != null){
	        			panel_vel.setText("Vel. "+info.velocity+" Km/s");
	        			if(info.velocity>config.limit_velocity)
	        				panel_vel.setTextColor(R.color.panel_limit);
	        			else
	        				panel_vel.setTextColor(R.color.panel_value);
	        		}
	        		TextView panel_accel = (TextView)view.findViewById(R.id.textViewPanelAccel);
	        		if(panel_accel != null){
	        			panel_accel.setText("Accel. "+info.acceleration+" Km/s2");
	        			if(info.acceleration>config.limit_acceleration)
	        				panel_accel.setTextColor(R.color.panel_limit);
	        			else
	        				panel_accel.setTextColor(R.color.panel_value);
	        		}
	        		TextView panel_radium = (TextView)view.findViewById(R.id.textViewPanelRadium);
	        		if(panel_radium != null)
	        			panel_radium.setText("Orbit radium: "+info.orbit_radium+" Km");
	        		TextView panel_mass = (TextView)view.findViewById(R.id.textViewPanelMass);
	        		if(panel_mass != null)
	        			panel_mass.setText("Mass: "+info.mass+" Kg");
	        		TextView panel_roll = (TextView)view.findViewById(R.id.textViewPanelRoll);
	        		if(panel_roll != null)
	        			panel_roll.setText("Rol: "+info.roll);
	        		TextView panel_pitch = (TextView)view.findViewById(R.id.textViewPanelPitch);
	        		if(panel_pitch != null)
	        			panel_pitch.setText("Pitch: "+info.pitch);
	        		TextView panel_yaw = (TextView)view.findViewById(R.id.textViewPanelYaw);
	        		if(panel_yaw != null)
	        			panel_yaw.setText("Yaw: "+info.yaw);
	        	}
	        }
	    });
    }

	public void setCurrentView(View rootView) {
		// TODO Auto-generated method stub
		view = rootView;
	}
}
