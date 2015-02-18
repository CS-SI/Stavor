package cs.si.stavor.web;

import java.io.Serializable;

/**
 * Created by xgibert on 17/02/2015.
 */
public class Mission implements Serializable {
    public String name = "CustomMission";
    public String description = "";
    public double duration = 600000.0;
    public double step = 60.0;
    public int attitude_provider = 0;
    public SimDate initial_date = new SimDate();
    public double initial_mass = 2000.0;
    public KeplerianOrbit initial_orbit = new KeplerianOrbit();
}
