package model;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

/**
 * Parameters for the model representation.
 * @author xavier
 *
 */
public class ModelState implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6931304300114881770L;
	public Quat value_attitude = new Quat(new Rotation(1.0,0.0,0.0,0.0, false));
	public double[] value_sun = {-1.1414775124432093E7,-1.464188429948789E8,7716114.240559303};
	public double[] value_earth = {7.337791634217176E-12,42164.0,-2.010800849831316E-12};
	public double[] value_velocity = {2.83195518282009,-5.49945264687097E-16,-1.1973314470667253};
	public double[] value_acceleration = {-4.517719883627554E-4,0.22420886513859567,1.9100613309698625E-4};
	public double[] value_momentum = {5.048428313412141E10,-3.091270787372526E-6,1.1940655832842628E11};
}
