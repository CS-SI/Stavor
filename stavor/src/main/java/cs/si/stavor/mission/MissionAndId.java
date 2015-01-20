package cs.si.stavor.mission;

import java.io.Serializable;

/**
 * Includes the mission object and the database id
 * @author Xavier Gibert
 *
 */
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
