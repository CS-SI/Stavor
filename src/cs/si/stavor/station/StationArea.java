package cs.si.stavor.station;



public class StationArea {
	public String name = "Station";
	public LatLon[] points;
	
	public StationArea(String name, LatLon[] points){
		this.name = name;
		this.points = points;
	}
}
