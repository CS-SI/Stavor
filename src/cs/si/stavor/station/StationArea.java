package cs.si.stavor.station;



public class StationArea {
	public String name = "Station";
	public double station_longitude = 0.0;
	public LatLon[] points;
	
	public StationArea(String name, double longitude, LatLon[] points){
		this.name = name;
		this.points = points;
		this.station_longitude = longitude;
	}
}
