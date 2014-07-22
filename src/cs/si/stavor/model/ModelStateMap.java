package cs.si.stavor.model;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import cs.si.stavor.model.ModelSimulation.MapPoint;
import cs.si.stavor.station.StationArea;

/**
 * Parameters for the periodic model representation.
 * @author Xavier Gibert
 *
 */
public class ModelStateMap implements Serializable{
	public MapPoint[] points;
	public double sun_lat=0, sun_lon=0;
	
	public StationArea[] stations;
	
	public ModelStateMap(MapPoint[] mapPathBufferLast, StationArea[] stations, double sun_lat,
			double sun_lon) {
		this.points = mapPathBufferLast;
		this.sun_lat = sun_lat;
		this.sun_lon = sun_lon;
		this.stations = stations;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6931304300114881770L;
	
}
