package cs.si.stavor.web;

import java.io.Serializable;

import cs.si.stavor.station.GroundStation;

/**
 * Created by xgibert on 24/02/2015.
 */
public class SimConfig implements Serializable {
    public double aperture_angle = 5;
    public double sensor_direction_x = 0;
    public double sensor_direction_y = 0;
    public double sensor_direction_z = 1;
    public GroundStation[] stations = new GroundStation[0];
}
