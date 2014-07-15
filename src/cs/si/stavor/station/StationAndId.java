package cs.si.stavor.station;


/**
 * Includes the mission object and the database id
 * @author Xavier Gibert
 *
 */
public class StationAndId{
	public GroundStation station;
	public int id;
	
	public StationAndId(GroundStation gs, int identif){
		station=gs;
		id=identif;
	}
}
