package cs.si.stavor.station;

import java.io.Serializable;


/**
 * Includes the mission object and the database id
 * @author Xavier Gibert
 *
 */
public class StationAndId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public GroundStation station;
	public int id;
	
	public StationAndId(GroundStation gs, int identif){
		station=gs;
		id=identif;
	}
}
