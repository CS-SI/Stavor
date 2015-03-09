package cs.si.stavor.model;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import cs.si.stavor.station.LatLon;
import cs.si.stavor.station.StationArea;

/**
 * Parameters for the periodic model representation.
 * @author Xavier Gibert
 *
 */
public class ModelState implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6931304300114881770L;

    //Info_Panel
    public double orbit_radius = 0;
    public double velocity;
    public double acceleration;
    public double mass = 0;// Kg
    public String time = "2014-01-01 23:55:10";
    public int progress = 0;// 0->100
    public double roll = 0;// Rad
    public double pitch = 0;// Rad
    public double yaw = 0;// Rad
    public double period = 0;// Seconds
    public double mean_anomaly = 0;// Deg

    //Attitude
	public Quat value_attitude = new Quat(new Rotation(1.0,0.0,0.0,0.0, false));
	public double[] value_sun = {-1.1414775124432093E7,-1.464188429948789E8,7716114.240559303};
	public double[] value_earth = {7.337791634217176E-12,42164.0,-2.010800849831316E-12};
	public double[] value_velocity = {2.83195518282009,-5.49945264687097E-16,-1.1973314470667253};
	public double[] value_acceleration = {-4.517719883627554E-4,0.22420886513859567,1.9100613309698625E-4};
	public double[] value_momentum = {5.048428313412141E10,-3.091270787372526E-6,1.1940655832842628E11};

    //Orbit
    public Quat value_earth_rotation= new Quat(new Rotation(1.0,0.0,0.0,0.0, false));
    public double[] value_spacecraft = {42164000.0,7.337791634217176E-12,-2.010800849831316E-12};
    public double value_orbit_a = 24396159;
    public double value_orbit_e = 0.73;
    public double value_orbit_i = Math.PI/9;
    public double value_orbit_w = Math.PI;
    public double value_orbit_raan = 0;

    //Map
    public MapPoint point;
    public LatLon[] fov, fov_terminator, terminator;
    public int fov_type = 0;
    public double sun_lat=0, sun_lon=0;

    public StationArea[] stations;
}
