package simulator;

import org.orekit.propagation.SpacecraftState;

public class SimResults {
	public SpacecraftState spacecraftState;
	public int sim_progress = 0;
	
	public SimResults(SpacecraftState scs, int sim_prog){
		spacecraftState = scs;
		sim_progress = sim_prog;
	}
}
