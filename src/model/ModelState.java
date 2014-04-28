package model;

import java.io.Serializable;

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
	public double[] value_sun = {87000000,87000000,87000000};
	public double[] value_earth = {36000,12000,5};
	public double[] value_velocity = {0,0,-15};
	public double[] value_acceleration = {-5,0,0};
	public double[] value_momentum = {-5,0,-5};
	public double[] value_target_a = {-5,-5,-5};
	public double[] value_vector_a = {-7,-5,-5};
	public double[] value_direction_a = {-5,-5,-7};
}
