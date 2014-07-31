package cs.si.stavor.station;



public class StationArea {
	public String name = "Station";
	public double station_longitude = 0.0;
	public LatLon[] points;
	public int type = 0;// 0 closed area, 1 nord pole area, 2 south pole area
	
	public StationArea(String name, double longitude, LatLon[] points, int type){
		this.name = name;
		this.points = points;
		this.station_longitude = longitude;
		this.type = type;
	}
}
