package mission;

import java.io.Serializable;

public class MissionAndId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 770862424275385258L;
	public Mission mission;
	public int id;
	
	public MissionAndId(Mission mis, int identif){
		mission=mis;
		id=identif;
	}
}
