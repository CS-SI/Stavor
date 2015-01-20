package cs.si.stavor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.apache.commons.math3.geometry.spherical.twod.S2Point;
import org.apache.commons.math3.geometry.spherical.twod.SphericalPolygonsSet;
import org.apache.commons.math3.util.FastMath;
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
import org.xwalk.core.XWalkView;
import org.orekit.utils.Constants;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.station.LatLon;
import cs.si.stavor.station.StationArea;
import cs.si.stavor.station.VisibilityCircle;

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
    private ModelConfigurationMap config_map;
    private ModelStateMap state_map;
    private ModelInfo info;
    private MainActivity activity;
    private View view;
    private XWalkView browser;
    private boolean isBrowserLoaded;
    private Browsers selectedBrowser = Browsers.None;
    
    public ModelSimulation(MainActivity acv){
    	isBrowserLoaded = false;
    	activity=acv;
    	config = new ModelConfiguration(activity.getApplicationContext());
    	config_orbit = new ModelConfigurationOrbit(activity.getApplicationContext());
    	config_map = new ModelConfigurationMap(activity.getApplicationContext(),
    			((StavorApplication)activity.getApplication()).db,
    			getMapPathBuffer(),
    			((StavorApplication)activity.getApplication()).follow_sc,
    			((StavorApplication)activity.getApplication()).zoom,
    			((StavorApplication)activity.getApplication()).lon,
    			((StavorApplication)activity.getApplication()).lat);
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
			if(earth==null && earthFixedFrame!=null){
				earth = new OneAxisEllipsoid(
	    				Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
	    		 		Constants.WGS84_EARTH_FLATTENING,
	    		 		earthFixedFrame);
				
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
    public void setHud(Browsers type, View hud, XWalkView mBrowser){
    	selectedBrowser = type;
		view = hud;
    	if(selectedBrowser.equals(Browsers.Attitude) || selectedBrowser.equals(Browsers.Orbit) || selectedBrowser.equals(Browsers.Map)){
        	initViews();
    	}else{
    		uninitViews();
    	}
    	browser = mBrowser;
    }
    
    public void clearHud(){
      	selectedBrowser = Browsers.None;
      	view = null;
      	uninitViews();
    	browser = null;
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
	public synchronized String getInitializationMapJSON() {
		//Init local config values for FOV and solar terminator
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		try{
			double x = Double.parseDouble(sharedPref.getString(activity.getString(R.string.pref_key_payload_x), Double.toString(sensor_sc_direction.getX())));
			double y = Double.parseDouble(sharedPref.getString(activity.getString(R.string.pref_key_payload_y), Double.toString(sensor_sc_direction.getY())));
			double z = Double.parseDouble(sharedPref.getString(activity.getString(R.string.pref_key_payload_z), Double.toString(sensor_sc_direction.getZ())));
		 	sensor_sc_direction = new Vector3D(x,y,z);
		 	sensor_aperture = Double.parseDouble(sharedPref.getString(activity.getString(R.string.pref_key_payload_beamwidth), Double.toString(sensor_aperture)));
			
		}catch(NumberFormatException e){
			System.err.println("Error loading configuration parameter: "+e.getMessage());
		}
		
		date_tmp = null;
		
		//Return config JSON
    	config_map = new ModelConfigurationMap(activity.getApplicationContext(),
    			((StavorApplication)activity.getApplication()).db,
    			getMapPathBuffer(),
    			((StavorApplication)activity.getApplication()).follow_sc,
    			((StavorApplication)activity.getApplication()).zoom,
    			((StavorApplication)activity.getApplication()).lon,
    			((StavorApplication)activity.getApplication()).lat);
        return gson.toJson(config_map);
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
    private synchronized void updateState(ModelStateMap st){
    	state_map = st;
    }
    private synchronized void updateState(ModelStateOrbit st){
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
    		}else if(selectedBrowser.equals(Browsers.Map)){
				if(mapPathBuffer.size()!=0){
    				browser.load("javascript:updateModelState('"+gson.toJson(state_map)+"')",null);
    			}
			}
    	}
	}
    
    private synchronized void updateInfo(ModelInfo inf){
    	info = inf; 
    }

	private void clearSimulationModel(){
    	if(browser!=null && isBrowserLoaded){
    		if(selectedBrowser.equals(Browsers.Map)){
				browser.load("javascript:clearPath()",null);
    		}
    	}
	}
    
    private AbsoluteDate tmp_time;
    private Vector3D tmp_vel;
    private TimeScale utc;
    private Frame sunFrame, earthFixedFrame;
    private OneAxisEllipsoid earth;
 	private double sensor_aperture = 3;
 	Vector3D sensor_sc_direction = new Vector3D(0,0,1);
    private AbsoluteDate date_tmp = null;
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
	    	ModelInfo new_info = new ModelInfo();
    		

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
    		
    		updateState(new_state);
    		
    		//INFO RETRACTABLE PANEL
    		//Basic indicators and Attitude
	    	Attitude sc_att = scs.getAttitude();
	    	
	    	Vector3D velocity = scs.getPVCoordinates().getVelocity();
	    	new_info.velocity = scs.getPVCoordinates().getVelocity().getNorm()/1000;
	    	
	    	Vector3D earth = scs.getPVCoordinates().getPosition().negate();
	    	new_info.orbit_radium = earth.getNorm()/1000;
	    	
	    	new_info.progress = sim_progress;
	    	if(new_info.progress>100)
	    		new_info.progress=100;
	    	if(new_info.progress<0)
	    		new_info.progress=0;
	    	
			new_info.time = date.getComponents(utc).toString();
	
			//Compute acceleration
			if(tmp_time != null){
				double delay = date.offsetFrom(tmp_time,utc);
				Vector3D acceleration = new Vector3D(
						(velocity.getX()-tmp_vel.getX())/delay,
						(velocity.getY()-tmp_vel.getY())/delay,
						(velocity.getZ()-tmp_vel.getZ())/delay);
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
	    	updateInfo(new_info);
    	}else if(selectedBrowser.equals(Browsers.Map)){
    		 try {
    			 
    			 //Sat_Pos
    		 	GeodeticPoint gp = earth.transform(scs.getPVCoordinates(earthFixedFrame).getPosition(), earthFixedFrame, scs.getDate());
    		 	double lat = gp.getLatitude()*180/Math.PI;
    		 	double lon = gp.getLongitude()*180/Math.PI;
    		 	double alt = gp.getAltitude();
    		 	if(!Double.isNaN(lat)&&!Double.isNaN(lon))
    		 		addToMapPathBuffer(lat, lon, alt);
    		 	
    		 	//Sun_Pos
    		 	GeodeticPoint gp2 = earth.transform(CelestialBodyFactory.getSun().getPVCoordinates(scs.getDate(), earthFixedFrame).getPosition(), earthFixedFrame, scs.getDate());
    		 	double lat2 = gp2.getLatitude()*180/Math.PI;
    		 	double lon2 = gp2.getLongitude()*180/Math.PI;
    		 	if(!Double.isNaN(lat)&&!Double.isNaN(lon)){
    		 		sun_lat = lat2;
    		 		sun_lon = lon2;
    		 	}
    		 	
    		 	//Solar terminator
    		 	ArrayList<LatLon> solarTerminator = new ArrayList<LatLon>();
    		 	if(date_tmp == null || Math.abs(scs.getDate().durationFrom(date_tmp))>Parameters.Map.solar_terminator_threshold){
    		 		date_tmp = scs.getDate();
    		 		
	    		 	Vector3D s = CelestialBodyFactory.getSun().getPVCoordinates(
	    		 				scs.getDate(), 
	    		 				earthFixedFrame
	    		 			).getPosition().normalize();
	    		 	Vector3D t = s.orthogonal();
	    		 	Vector3D u = Vector3D.crossProduct(t, s);
	    		 	
	    		 	double alpha_o = Math.atan((-t.getY())/(u.getY()));
	    		 	Vector3D test_point = (t.scalarMultiply(Math.cos(alpha_o))).add(u.scalarMultiply(Math.sin(alpha_o)));
	    		 	if(test_point.getX()>0)
	    		 		alpha_o = alpha_o + FastMath.PI;
	    		 	
	    		 	double alpha_margin = 0.02;
	    		 	double alpha = alpha_margin;
	    		 	double d_alpha = 2*FastMath.PI/Parameters.Map.solar_terminator_points;
	    		 	for(int i = 0; i<Parameters.Map.solar_terminator_points; i++){
	    		 		Vector3D point = (t.scalarMultiply(Math.cos(alpha+alpha_o))).add(u.scalarMultiply(Math.sin(alpha+alpha_o))).scalarMultiply(Constants.WGS84_EARTH_EQUATORIAL_RADIUS);
	    		 		GeodeticPoint gpoint = earth.transform(point, earthFixedFrame, scs.getDate());
	    		 		solarTerminator.add(new LatLon(gpoint.getLatitude()*180/Math.PI,gpoint.getLongitude()*180/Math.PI));
	    		 		alpha = alpha + d_alpha;
	    		 		if(alpha>(2*FastMath.PI)-alpha_margin)
	    		 			alpha=2*FastMath.PI-alpha_margin;
	    		 	}
    		 	}
    		 	
    		 	
    		 	//Station Areas
    		 	ArrayList<StationArea> stations = new ArrayList<StationArea>();
    		 	for(int i = 0; i < config_map.stations.length; i++){
    		 		if(config_map.stations[i].enabled){
    		 			List<LatLon> circle = VisibilityCircle.computeCircle(
    		 					earth,
	    		 				config_map.stations[i].latitude, 
	    		 				config_map.stations[i].longitude, 
	    		 				config_map.stations[i].altitude, 
	    		 				config_map.stations[i].name, 
	    		 				config_map.stations[i].elevation, 
	    		 				scs.getPVCoordinates().getPosition().getNorm(), 
	    		 				Parameters.Map.station_visibility_points);
    		 			
    		 			//Find polygon type
    		 			int type = VisibilityCircle.computeType(
    		 					earth,
	    		 				config_map.stations[i].latitude, 
	    		 				config_map.stations[i].longitude, 
	    		 				config_map.stations[i].altitude, 
	    		 				config_map.stations[i].elevation, 
	    		 				scs.getPVCoordinates().getPosition().getNorm()
	    		 				);
    		 			
    		 			//------------------------
    		 			
	    		 		stations.add(new StationArea(
	    		 				config_map.stations[i].name,
	    		 				config_map.stations[i].longitude,
	    		 				circle.toArray(new LatLon[circle.size()]),
	    		 				type
	    		 				));
    		 		}
    		 	}
    		 	
    		 	//Satellite FOV
    		 	//data
    		 	Rotation attitude = scs.getAttitude().withReferenceFrame(earthFixedFrame).getRotation();
    		 	Vector3D close = scs.getPVCoordinates(earthFixedFrame).getPosition();
    		 	//step
    		 	sensor_sc_direction = sensor_sc_direction.normalize();
    		 	Vector3D axis = attitude.applyInverseTo(sensor_sc_direction);
    		 	Vector3D ortho = axis.orthogonal();
    		 	Rotation rot_aperture = new Rotation(ortho, sensor_aperture*Math.PI/360);
    		 	Vector3D start = rot_aperture.applyTo(axis);
    		 	//points
    		 	
    		 	double angle_step = 2.0*Math.PI/Parameters.Map.satellite_fov_points;
    		 	double angle = 0;
    		 	ArrayList<LatLon> fov = new ArrayList<LatLon>();
    		 	ArrayList<S2Point> fov2d = new ArrayList<S2Point>();
    		 	int fov_type = 0;//0 no poles, 1 north pole, 2 south pole
    		 	for(int j = 0; j < Parameters.Map.satellite_fov_points; j++){
    		 		Rotation r_circle = new Rotation(axis, angle);
    		 		Vector3D dir = r_circle.applyTo(start);
    		 		dir = dir.add(close);
    		 		GeodeticPoint intersec = earth.getIntersectionPoint(new Line(dir, close, 0.0), close, earthFixedFrame, scs.getDate());
    		 		if(intersec!=null){
	    		 		fov.add(new LatLon(intersec.getLatitude()*180/Math.PI,intersec.getLongitude()*180/Math.PI));
	    		 		//Vector3D p3d = earth.transform(intersec);
	    		 		try{
	    		 			double azim = intersec.getLongitude();
	    		 			/*if(azim < 0)
	    		 				azim = (2*FastMath.PI) - azim;*/
	    		 			fov2d.add(new S2Point(azim,(FastMath.PI/2)-intersec.getLatitude()));
	    		 			//Log.d("INSIDE","azim: "+azim+" , lat: "+((FastMath.PI/2)-intersec.getLatitude()));
	    		 		}catch(Exception e ){
	    		 			e.printStackTrace();
	    		 		}
    		 		}
    		 		angle += angle_step;
    		 	}
    		 	
    		 	//Check if one of the poles is inside the FOV
    		 	if(fov2d.size()>1){
    		 		try{
    		 			S2Point[] vec = fov2d.toArray(new S2Point[fov2d.size()]);
	    		 		SphericalPolygonsSet zone = new SphericalPolygonsSet(0.0001,vec);
	    		 		Location loc = zone.checkPoint(new S2Point(0,0));
	    		 		if (loc.equals(Location.OUTSIDE)){//Outside because the order of the points is the oposite
	    		 			fov_type = 1;
	    		 		}
	    		 		loc = zone.checkPoint(new S2Point(0,FastMath.PI));
	    		 		if (loc.equals(Location.OUTSIDE)){//Outside because the order of the points is the oposite
	    		 			fov_type = 2;
	    		 		}
	    		 		//Log.d("INSIDE",loc.toString()+"-------------------------");
    		 		}catch(Exception e){
    		 			//Log.d("INSIDE","EXCEPTION");
    		 		}
    		 	}
    		 	

    		 	//FOV terminator
    		 	ArrayList<LatLon> fovTerminator = new ArrayList<LatLon>();
    		 	//Check if FOV contains the whole earth
    		 	if(fov.size()==0){//No intersection with Earth
    		 		//Check if the center of the sensor intersects the Earth
    		 		GeodeticPoint intersec = earth.getIntersectionPoint(new Line(axis, close, 0.0), close, earthFixedFrame, scs.getDate());
    		 		if(intersec!=null){//Case whole earth inside FOV
    	    		 	Vector3D s = scs.getPVCoordinates( 
	    		 				earthFixedFrame
	    		 			).getPosition().normalize();
		    		 	Vector3D t = s.orthogonal();
		    		 	Vector3D u = Vector3D.crossProduct(t, s);
		    		 	
		    		 	double alpha_o = Math.atan((-t.getY())/(u.getY()));
		    		 	Vector3D test_point = (t.scalarMultiply(Math.cos(alpha_o))).add(u.scalarMultiply(Math.sin(alpha_o)));
		    		 	if(test_point.getX()>0)
		    		 		alpha_o = alpha_o + FastMath.PI;
		    		 	
		    		 	double alpha_margin = 0.02;
		    		 	double alpha = alpha_margin;
		    		 	double d_alpha = 2*FastMath.PI/(Parameters.Map.satellite_fov_points*3);
		    		 	for(int i = 0; i<(Parameters.Map.satellite_fov_points*3); i++){
		    		 		Vector3D point = (t.scalarMultiply(Math.cos(alpha+alpha_o))).add(u.scalarMultiply(Math.sin(alpha+alpha_o))).scalarMultiply(Constants.WGS84_EARTH_EQUATORIAL_RADIUS);
		    		 		GeodeticPoint gpoint = earth.transform(point, earthFixedFrame, scs.getDate());
		    		 		fovTerminator.add(new LatLon(gpoint.getLatitude()*180/Math.PI,gpoint.getLongitude()*180/Math.PI));
		    		 		alpha = alpha + d_alpha;
		    		 		if(alpha>(2*FastMath.PI)-alpha_margin)
		    		 			alpha=2*FastMath.PI-alpha_margin;
		    		 	}
    		 		}else{//CASE NOT POINTING EARTH BUT FOV TAKES EARTH INSIDE (DEPOINTING A LARGLY APERTURE SENSOR)
    		 			//XGGDEBUG: Handle case
    		 		}
    		 		
    		 	}
    		 	
    		 	updateState(new ModelStateMap(getMapPathBufferLast(), solarTerminator.toArray(new LatLon[solarTerminator.size()]), fov.toArray(new LatLon[fov.size()]), fov_type, fovTerminator.toArray(new LatLon[fovTerminator.size()]), stations.toArray(new StationArea[stations.size()]), sun_lat, sun_lon));


    	    	ModelInfo new_info = new ModelInfo();
    	    	
    	    	//Basic indicators and Attitude
    	    	Attitude sc_att = scs.getAttitude();
    	    	
    	    	Vector3D velocity = scs.getPVCoordinates().getVelocity();
    	    	new_info.velocity = scs.getPVCoordinates().getVelocity().getNorm()/1000;
    	    	
    	    	Vector3D earth = scs.getPVCoordinates().getPosition().negate();
    	    	new_info.orbit_radium = earth.getNorm()/1000;
    	    	
    	    	
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
    		 	
    	    	updateInfo(new_info);
    		 	
    		} catch (OrekitException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * Update the Hud panel with the new simulation step values
     */
    public synchronized void updateHUD(){
    	if(selectedBrowser.equals(Browsers.Attitude) || selectedBrowser.equals(Browsers.Orbit) || selectedBrowser.equals(Browsers.Map)){
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
    			panel_roll.setText("Roll: "+String.format("%.1f", (180*info.roll/Math.PI))+"º");
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

	double sun_lat, sun_lon;

	private ArrayList<MapPoint> mapPathBuffer = new ArrayList<MapPoint>();
	public synchronized void resetMapPathBuffer() {
		mapPathBuffer.clear();
		clearSimulationModel();
	}
	
	//private double tmp_lat=0, tmp_lon=0;
	public synchronized void addToMapPathBuffer(double lat, double lon, double alt) {
		//if(Math.abs(tmp_lat-lat)>Parameters.Map.marker_pos_threshold || Math.abs(tmp_lon-lon)>Parameters.Map.marker_pos_threshold){
			//tmp_lat = lat;
			//tmp_lon = lon;
			mapPathBuffer.add(new MapPoint(lat,lon,alt));
		//}
	}
	public synchronized MapPoint[] getMapPathBuffer(){
		MapPoint[] r =
				  (MapPoint[])mapPathBuffer.toArray(new MapPoint[mapPathBuffer.size()]);
		//resetMapPathBuffer();
		return r;
	}
	public synchronized MapPoint getMapPathBufferLast(){
		if(mapPathBuffer.size()>0){
			return mapPathBuffer.get(mapPathBuffer.size()-1);
		}else
			return null;
	}
	
	class MapPoint{
		public MapPoint(double lat, double lon, double alt){
			latitude = lat;
			longitude = lon;
			altitude = alt;
		}
		double latitude = 0;
		double longitude = 0;
		double altitude = 0;
	}
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

	
}
