package mission;

import java.io.Serializable;

/**
 * Contains all the values of an orbit for a mission.
 * @author Xavier Gibert
 *
 */
public class Orbit implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5454548599446807545L;
	
	public double mu =  3.986004415e+14;
	/**
	 * Semi major axis in meters
	 */
	public double a = 24396159;
	/**
	 * Eccentricity
	 */
	public double e = 0.72831215;
	/**
	 * Inclination
	 */
	public double i = Math.toRadians(7);
	/**
	 * Perigee argument
	 */
	public double omega = Math.toRadians(180);
	/**
	 * Right ascension of ascending node
	 */
	public double raan = Math.toRadians(0);
	/**
	 * Mean anomaly
	 */
	public double lM = 0;
	
	public Orbit(){
		
	}
	public Orbit(double o_mu,double o_a,double o_e,double o_i,double o_omega,double o_raan,double o_lM){
		mu=o_mu;
		a=o_a;
		e=o_e;
		i=o_i;
		omega=o_omega;
		raan=o_raan;
		lM=o_lM;
	}
}
