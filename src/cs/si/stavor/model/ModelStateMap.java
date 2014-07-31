package cs.si.stavor.model;

import java.io.Serializable;

import cs.si.stavor.model.ModelSimulation.MapPoint;
import cs.si.stavor.station.LatLon;
import cs.si.stavor.station.StationArea;

/**
 * Parameters for the periodic model representation.
 * @author Xavier Gibert
 *
 */
public class ModelStateMap implements Serializable{
	public MapPoint point;
	public LatLon[] fov, terminator;
	public double sun_lat=0, sun_lon=0;
	
	public StationArea[] stations;
	
	public ModelStateMap(MapPoint mapPathBufferLast, LatLon[] terminator, LatLon[] fov, StationArea[] stations, double sun_lat,
			double sun_lon) {
		this.point = mapPathBufferLast;
		this.sun_lat = sun_lat;
		this.sun_lon = sun_lon;
		this.stations = stations;
		this.fov = fov;
		this.terminator = terminator;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6931304300114881770L;
	
}
