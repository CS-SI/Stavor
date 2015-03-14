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

import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.simulator.Simulator;
import cs.si.stavor.station.LatLon;
import cs.si.stavor.station.StationArea;
import cs.si.stavor.station.VisibilityCircle;
import cs.si.stavor.web.SimConfig;

/**
 * Contains and handles both the model information and configuration
 * @author Xavier Gibert
 *
 */
public class ModelSimulation {
	private Gson gson = new Gson();
    private ModelState state;
    private MainActivity activity;
    private View view;
    private XWalkView browser;
    private Simulator simulator;
    private Visualizations visualization = Visualizations.ATTITUDE;

    public void setCurrentVisualization(Visualizations vis) {
        visualization = vis;
    }

    public ModelSimulation(MainActivity acv){
    	activity=acv;
        browser = activity.getBrowser();
        simulator = activity.getSimulator();
    	state = new ModelState();
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
			if(earthPlanet==null && earthFixedFrame!=null){
				earthPlanet = new OneAxisEllipsoid(
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
        if(browser!=null){
    		if(state!=null) {
                try {
                    browser.load("javascript:global_simulator.updateMissionState('" + gson.toJson(state) + "')", null);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
    	}
	}
    
    private AbsoluteDate tmp_time;
    private Vector3D tmp_vel;
    private TimeScale utc;
    private Frame sunFrame, earthFixedFrame;
    private OneAxisEllipsoid earthPlanet;

 	private double sensor_aperture = 5;
 	Vector3D sensor_sc_direction = new Vector3D(0,0,1);

    private AbsoluteDate date_tmp = null;
    ArrayList<LatLon> solarTerminator = new ArrayList<LatLon>();

    public void updateSimulation(SpacecraftState scs, int sim_progress){
        ModelState new_state = new ModelState();

        //Global
        Attitude sc_att = scs.getAttitude();
        AbsoluteDate date = sc_att.getDate();
        Vector3D earth = scs.getPVCoordinates().getPosition().negate();
        Vector3D velocity = scs.getPVCoordinates().getVelocity();

        //Attitude
        if(visualization.equals(Visualizations.ATTITUDE)) {
            new_state.value_attitude = new Quat(sc_att.getOrientation().getRotation());

            new_state.value_velocity[0] = velocity.getX() / 1000;
            new_state.value_velocity[1] = velocity.getY() / 1000;
            new_state.value_velocity[2] = velocity.getZ() / 1000;

            Vector3D acceleration = scs.getPVCoordinates().getAcceleration();
            new_state.value_acceleration[0] = acceleration.getX() / 1000;
            new_state.value_acceleration[1] = acceleration.getY() / 1000;
            new_state.value_acceleration[2] = acceleration.getZ() / 1000;

            new_state.value_momentum[0] = scs.getPVCoordinates().getMomentum().getX();
            new_state.value_momentum[1] = scs.getPVCoordinates().getMomentum().getY();
            new_state.value_momentum[2] = scs.getPVCoordinates().getMomentum().getZ();

            new_state.value_earth[0] = earth.getX() / 1000;
            new_state.value_earth[1] = earth.getY() / 1000;
            new_state.value_earth[2] = earth.getZ() / 1000;

            try {
                //Vector3D sun = scs.getPVCoordinates(sunFrame).getPosition().negate();
                Vector3D sun = CelestialBodyFactory.getSun().getPVCoordinates(scs.getDate(),scs.getFrame()).getPosition();

                new_state.value_sun[0] = sun.getX() / 1000;
                new_state.value_sun[1] = sun.getY() / 1000;
                new_state.value_sun[2] = sun.getZ() / 1000;

            } catch (OrekitException e) {
                e.printStackTrace();
                activity.showErrorDialog(activity.getString(R.string.error_computing_orekit), false);
            }
        }

        //Info panel
        new_state.velocity = scs.getPVCoordinates().getVelocity().getNorm()/1000;
        new_state.acceleration = scs.getPVCoordinates().getAcceleration().getNorm() / 1000;
        new_state.orbit_radius = earth.getNorm()/1000;
        new_state.progress = sim_progress;
        if(new_state.progress>100)
            new_state.progress=100;
        if(new_state.progress<0)
            new_state.progress=0;

        new_state.time = date.getComponents(utc).toString().replace("T"," ");

        //Compute acceleration
        /*if(tmp_time != null){
            double delay = date.offsetFrom(tmp_time,utc);
            Vector3D acceleration = new Vector3D(
                    (velocity.getX()-tmp_vel.getX())/delay,
                    (velocity.getY()-tmp_vel.getY())/delay,
                    (velocity.getZ()-tmp_vel.getZ())/delay);
            new_state.value_acceleration[0] = acceleration.getX();
            new_state.value_acceleration[1] = acceleration.getY();
            new_state.value_acceleration[2] = acceleration.getZ();
            if(
                    Double.isNaN(new_state.value_acceleration[0]) ||
                    Double.isNaN(new_state.value_acceleration[1]) ||
                    Double.isNaN(new_state.value_acceleration[2])) {
                new_state.value_acceleration[0] = 0.0;
                new_state.value_acceleration[1] = 0.0;
                new_state.value_acceleration[2] = 0.001;
            }
            new_state.acceleration = acceleration.getNorm();
            if (Double.isNaN(new_state.acceleration)) {
                new_state.acceleration = 0;
            }
        }*/

        //Update temporal variables for acceleration computation
        tmp_vel = velocity;
        tmp_time = date;

        double[] angles = sc_att.getOrientation().getRotation().getAngles(RotationOrder.XYZ);
        new_state.roll = angles[0]*180/Math.PI;
        new_state.pitch = angles[1]*180/Math.PI;
        new_state.yaw = angles[2]*180/Math.PI;

        new_state.mass = scs.getMass();
        new_state.period = scs.getKeplerianPeriod();
        new_state.mean_anomaly = Math.toDegrees(scs.getLM());

        //Orbit
        if(visualization.equals(Visualizations.ORBIT)) {
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
            KeplerianOrbit keplerOrb = ((KeplerianOrbit) scs.getOrbit());
            new_state.value_orbit_w = keplerOrb.getPerigeeArgument();
            new_state.value_orbit_raan = keplerOrb.getRightAscensionOfAscendingNode();
        }


        //Map
        try {
            //Sat_Pos
            GeodeticPoint gp = earthPlanet.transform(scs.getPVCoordinates(earthFixedFrame).getPosition(), earthFixedFrame, scs.getDate());
            double lat = gp.getLatitude()*180/Math.PI;
            double lon = gp.getLongitude()*180/Math.PI;
            double alt = gp.getAltitude();
            if(!Double.isNaN(lat)&&!Double.isNaN(lon))
                addToMapPathBuffer(lat, lon, alt);
            new_state.point = getMapPathBufferLast();

            if(visualization.equals(Visualizations.MAP)) {
                //Sun_Pos
                GeodeticPoint gp2 = earthPlanet.transform(CelestialBodyFactory.getSun().getPVCoordinates(scs.getDate(), earthFixedFrame).getPosition(), earthFixedFrame, scs.getDate());
                double lat2 = gp2.getLatitude() * 180 / Math.PI;
                double lon2 = gp2.getLongitude() * 180 / Math.PI;
                if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                    sun_lat = lat2;
                    sun_lon = lon2;
                }

                //Solar terminator
                if (date_tmp == null || Math.abs(scs.getDate().durationFrom(date_tmp)) > Parameters.Map.solar_terminator_threshold) {
                    //date_tmp = scs.getDate();
                    solarTerminator = new ArrayList<LatLon>();

                    Vector3D s = CelestialBodyFactory.getSun().getPVCoordinates(
                            scs.getDate(),
                            earthFixedFrame
                    ).getPosition().normalize();
                    Vector3D t = s.orthogonal();
                    Vector3D u = Vector3D.crossProduct(t, s);

                    double alpha_o = Math.atan((-t.getY()) / (u.getY()));
                    Vector3D test_point = (t.scalarMultiply(Math.cos(alpha_o))).add(u.scalarMultiply(Math.sin(alpha_o)));
                    if (test_point.getX() > 0)
                        alpha_o = alpha_o + FastMath.PI;

                    double alpha_margin = 0.02;
                    double alpha = alpha_margin;
                    double d_alpha = 2 * FastMath.PI / Parameters.Map.solar_terminator_points;
                    for (int i = 0; i < Parameters.Map.solar_terminator_points; i++) {
                        Vector3D point = (t.scalarMultiply(Math.cos(alpha + alpha_o))).add(u.scalarMultiply(Math.sin(alpha + alpha_o))).scalarMultiply(Constants.WGS84_EARTH_EQUATORIAL_RADIUS);
                        GeodeticPoint gpoint = earthPlanet.transform(point, earthFixedFrame, scs.getDate());
                        solarTerminator.add(new LatLon(gpoint.getLatitude() * 180 / Math.PI, gpoint.getLongitude() * 180 / Math.PI));
                        alpha = alpha + d_alpha;
                        if (alpha > (2 * FastMath.PI) - alpha_margin)
                            alpha = 2 * FastMath.PI - alpha_margin;
                    }
                }


                //Station Areas
                ArrayList<StationArea> stations = new ArrayList<StationArea>();
                SimConfig simConfig = simulator.getSimConfig();
                for (int i = 0; i < simConfig.stations.length; i++) {
                    //if(simConfig.stations[i].enabled){
                    try {
                        List<LatLon> circle = VisibilityCircle.computeCircle(
                                earthPlanet,
                                simConfig.stations[i].latitude,
                                simConfig.stations[i].longitude,
                                simConfig.stations[i].altitude,
                                simConfig.stations[i].name,
                                simConfig.stations[i].elevation,
                                scs.getPVCoordinates().getPosition().getNorm(),
                                Parameters.Map.station_visibility_points);

                        //Find polygon type
                        int type = VisibilityCircle.computeType(
                                earthPlanet,
                                simConfig.stations[i].latitude,
                                simConfig.stations[i].longitude,
                                simConfig.stations[i].altitude,
                                simConfig.stations[i].elevation,
                                scs.getPVCoordinates().getPosition().getNorm()
                        );

                        //------------------------

                        stations.add(new StationArea(
                                simConfig.stations[i].name,
                                simConfig.stations[i].longitude,
                                circle.toArray(new LatLon[circle.size()]),
                                type
                        ));
                    } catch (Exception e) {

                    }
                }

                //Satellite FOV
                //data
                Rotation attitude = scs.getAttitude().withReferenceFrame(earthFixedFrame).getRotation();
                Vector3D close = scs.getPVCoordinates(earthFixedFrame).getPosition();
                //step
                sensor_aperture = simConfig.aperture_angle;
                sensor_sc_direction = new Vector3D(
                        simConfig.sensor_direction_x,
                        simConfig.sensor_direction_y,
                        simConfig.sensor_direction_z
                );
                if (sensor_sc_direction.getX() != 0 || sensor_sc_direction.getY() != 0 || sensor_sc_direction.getZ() != 0) {
                    sensor_sc_direction = sensor_sc_direction.normalize();
                } else {
                    sensor_sc_direction = new Vector3D(
                            0,
                            0,
                            1
                    );
                }
                Vector3D axis = attitude.applyInverseTo(sensor_sc_direction);
                Vector3D ortho = axis.orthogonal();
                Rotation rot_aperture = new Rotation(ortho, sensor_aperture * Math.PI / 360);
                Vector3D start = rot_aperture.applyTo(axis);
                //points

                double angle_step = 2.0 * Math.PI / Parameters.Map.satellite_fov_points;
                double angle = 0;
                ArrayList<LatLon> fov = new ArrayList<LatLon>();
                ArrayList<S2Point> fov2d = new ArrayList<S2Point>();
                int fov_type = 0;//0 no poles, 1 north pole, 2 south pole
                for (int j = 0; j < Parameters.Map.satellite_fov_points; j++) {
                    Rotation r_circle = new Rotation(axis, angle);
                    Vector3D dir = r_circle.applyTo(start);
                    dir = dir.add(close);
                    GeodeticPoint intersec = earthPlanet.getIntersectionPoint(new Line(dir, close, 0.0), close, earthFixedFrame, scs.getDate());
                    if (intersec != null) {
                        fov.add(new LatLon(intersec.getLatitude() * 180 / Math.PI, intersec.getLongitude() * 180 / Math.PI));
                        //Vector3D p3d = earth.transform(intersec);
                        try {
                            double azim = intersec.getLongitude();
                            //if(azim < 0) azim = (2*FastMath.PI) - azim;
                            fov2d.add(new S2Point(azim, (FastMath.PI / 2) - intersec.getLatitude()));
                            //Log.d("INSIDE","azim: "+azim+" , lat: "+((FastMath.PI/2)-intersec.getLatitude()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    angle += angle_step;
                }

                //Check if one of the poles is inside the FOV
                if (fov2d.size() > 1) {
                    try {
                        S2Point[] vec = fov2d.toArray(new S2Point[fov2d.size()]);
                        SphericalPolygonsSet zone = new SphericalPolygonsSet(0.0001, vec);
                        Location loc = zone.checkPoint(new S2Point(0, 0));
                        if (loc.equals(Location.OUTSIDE)) {//Outside because the order of the points is the oposite
                            fov_type = 1;
                        }
                        loc = zone.checkPoint(new S2Point(0, FastMath.PI));
                        if (loc.equals(Location.OUTSIDE)) {//Outside because the order of the points is the oposite
                            fov_type = 2;
                        }
                        //Log.d("INSIDE",loc.toString()+"-------------------------");
                    } catch (Exception e) {
                        //Log.d("INSIDE","EXCEPTION");
                    }
                }


                //FOV terminator
                ArrayList<LatLon> fovTerminator = new ArrayList<LatLon>();
                //Check if FOV contains the whole earth
                if (fov.size() == 0) {//No intersection with Earth
                    //Check if the center of the sensor intersects the Earth
                    GeodeticPoint intersec = earthPlanet.getIntersectionPoint(new Line(axis, close, 0.0), close, earthFixedFrame, scs.getDate());
                    if (intersec != null) {//Case whole earth inside FOV
                        Vector3D s = scs.getPVCoordinates(
                                earthFixedFrame
                        ).getPosition().normalize();
                        Vector3D t = s.orthogonal();
                        Vector3D u = Vector3D.crossProduct(t, s);

                        double alpha_o = Math.atan((-t.getY()) / (u.getY()));
                        Vector3D test_point = (t.scalarMultiply(Math.cos(alpha_o))).add(u.scalarMultiply(Math.sin(alpha_o)));
                        if (test_point.getX() > 0)
                            alpha_o = alpha_o + FastMath.PI;

                        double alpha_margin = 0.02;
                        double alpha = alpha_margin;
                        double d_alpha = 2 * FastMath.PI / (Parameters.Map.satellite_fov_points * 3);
                        for (int i = 0; i < (Parameters.Map.satellite_fov_points * 3); i++) {
                            Vector3D point = (t.scalarMultiply(Math.cos(alpha + alpha_o))).add(u.scalarMultiply(Math.sin(alpha + alpha_o))).scalarMultiply(Constants.WGS84_EARTH_EQUATORIAL_RADIUS);
                            GeodeticPoint gpoint = earthPlanet.transform(point, earthFixedFrame, scs.getDate());
                            fovTerminator.add(new LatLon(gpoint.getLatitude() * 180 / Math.PI, gpoint.getLongitude() * 180 / Math.PI));
                            alpha = alpha + d_alpha;
                            if (alpha > (2 * FastMath.PI) - alpha_margin)
                                alpha = 2 * FastMath.PI - alpha_margin;
                        }
                    } else {//CASE NOT POINTING EARTH BUT FOV TAKES EARTH INSIDE (DEPOINTING A LARGLY APERTURE SENSOR)
                        //XGGDEBUG: Handle case
                    }

                }

                new_state.sun_lat = sun_lat;
                new_state.sun_lon = sun_lon;
                new_state.stations = stations.toArray(new StationArea[stations.size()]);
                new_state.fov = fov.toArray(new LatLon[fov.size()]);
                new_state.fov_type = fov_type;
                new_state.fov_terminator = fovTerminator.toArray(new LatLon[fovTerminator.size()]);
                new_state.terminator = solarTerminator.toArray(new LatLon[solarTerminator.size()]);
            }
        } catch (OrekitException e) {
            e.printStackTrace();
        }
        updateState(new_state);
    }

    private void clearSimulationModel(){
        activity.clearDataLogs();
    }

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
	
}
