package cs.si.stavor.model;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

/**
 * Object representing a Quaternion
 * @author Xavier Gibert
 *
 */
public class Quat {
	public double w = 1;
	public double x = 0;
	public double y = 0;
	public double z = 0;
	
	public Quat(Rotation rot){
		w = rot.getQ0();
		x = rot.getQ1();
		y = rot.getQ2();
		z = rot.getQ3();
	}
}
