package cs.si.stavor.model;

/**
 * Created by xgibert on 12/02/2015.
 */
public class MapPoint {
    public MapPoint(double lat, double lon, double alt){
        latitude = lat;
        longitude = lon;
        altitude = alt;
    }
    double latitude = 0;
    double longitude = 0;
    double altitude = 0;
}
