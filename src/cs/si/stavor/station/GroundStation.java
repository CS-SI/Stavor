package cs.si.stavor.station;

import java.io.Serializable;

public class GroundStation  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7297608100756290938L;
	public GroundStation(boolean station_enabled, String station_name,
			double station_lat, double station_lon, double station_alt, double station_elev) {
		enabled = station_enabled;
		name = station_name;
		latitude = station_lat;
		longitude = station_lon;
		altitude = station_alt;
		elevation = station_elev;
		
	}
	public GroundStation() {
	}
	public boolean enabled = false;
	public String name = "";
	public double latitude = 0.0;
	public double longitude = 0.0;
	public double altitude = 0.0;//m
	public double elevation = 5.0;
}
