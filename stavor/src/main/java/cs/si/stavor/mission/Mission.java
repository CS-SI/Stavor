package cs.si.stavor.mission;

import java.io.Serializable;

import org.orekit.time.AbsoluteDate;

import cs.si.stavor.mission.Orbit;

/**
 * Mission object
 * @author Xavier Gibert
 *
 */
public class Mission implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1987145766082906026L;
	
	public String name = "CustomMission";
	public String description = "Mission description";
	//public String initial_stage = "First Stage";
	public PropagatorType propagatorType = PropagatorType.Keplerian;
	public double sim_duration = 600000.;
	public double sim_step = 60.;
	public double initial_mass = 2000;
	public InertialFrames inertialFrame = InertialFrames.EME2000;
	//public RotatingFrames rotatingFrame = RotatingFrames.GTOD;
	public AbsoluteDate initial_date = new AbsoluteDate();
	public Orbit initial_orbit = new Orbit();
    public int attitude_provider = 0;
}
