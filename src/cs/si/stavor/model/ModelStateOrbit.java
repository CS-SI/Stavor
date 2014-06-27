package cs.si.stavor.model;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

/**
 * Parameters for the periodic model representation.
 * @author Xavier Gibert
 *
 */
public class ModelStateOrbit implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6931304300114881770L;
	public Quat value_earth_rotation= new Quat(new Rotation(1.0,0.0,0.0,0.0, false));
	public double[] value_spacecraft = {42164000.0,7.337791634217176E-12,-2.010800849831316E-12};
	public double value_orbit_a = 24396159;
	public double value_orbit_e = 0.73;
	public double value_orbit_i = Math.PI/9;
	public double value_orbit_w = Math.PI;
	public double value_orbit_raan = 0;
}
