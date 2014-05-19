package mission;

import mission.Orbit;

import org.orekit.time.AbsoluteDate;

public class Mission {
	public String name = "CustomMission";
	public String description = "Mission description";
	//public String initial_stage = "First Stage";
	public PropagatorType propagatorType = PropagatorType.Keplerian;
	public double sim_duration = 600000.;
	public double sim_step = 60.;
	public double initial_mass = 2000;
	public InertialFrames inertialFrame = InertialFrames.EME2000;
	//public RotatingFrames rotatingFrame = RotatingFrames.GTOD;
	//public UtcDate initial_date = new UtcDate();
	public AbsoluteDate initial_date = new AbsoluteDate();
	public Orbit initial_orbit = new Orbit();
}
