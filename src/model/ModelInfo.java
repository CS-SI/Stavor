package model;

import java.io.Serializable;

/**
 * Parameters for the information panel of the HUD.
 * @author Xavier Gibert
 *
 */
public class ModelInfo  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8803689341409024139L;
	public double orbit_radium = 0;
	public double velocity;
	public double acceleration;
	public double mass = 0;// Kg
	public String time = "2014-01-01 23:55:10";
	public int progress = 0;// 0->100
	public double roll = 0;// Rad
	public double pitch = 0;// Rad
	public double yaw = 0;// Rad
}
