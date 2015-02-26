package cs.si.stavor.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkView;

import cs.si.stavor.MainActivity;
import cs.si.stavor.model.MapPoint;
import cs.si.stavor.simulator.SimulationSense;
import cs.si.stavor.simulator.SimulationStatus;
import cs.si.stavor.simulator.Simulator;

import static org.chromium.base.ThreadUtils.runOnUiThread;

/**
 * JavaScript-to-Android bridge functions
 * @author Xavier Gibert
 *
 */
public final class StavorInterface {
    private XWalkView browser;
    private Simulator simulator;
    private Gson gson = new Gson();
    //JsonParser parser = new JsonParser();
    private TimeScale timeScale;

    /** Instantiate the interface and set the context */
    public StavorInterface(XWalkView browser, Simulator simulator) {
        this.browser = browser;
        this.simulator = simulator;
        try {
            this.timeScale = TimeScalesFactory.getUTC();
        } catch (OrekitException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void missionProgressValueChanged(String json_percentage) {
        MyInteger obj_integer = gson.fromJson(json_percentage,MyInteger.class);
        simulator.setCurrentSimulationProgress(obj_integer.value);
        //updateGuiControls();
    }

    @JavascriptInterface
    public void changeMission(String json_mission) {
        //JsonObject obj = parser.parse(json_mission).getAsJsonObject();
        Mission obj_mission = gson.fromJson(json_mission, Mission.class);
        cs.si.stavor.mission.Mission mission = new cs.si.stavor.mission.Mission();
        //TODO json_mission to mission
        mission.name = obj_mission.name;
        mission.description = obj_mission.description;
        mission.sim_duration = obj_mission.duration;
        mission.sim_step = obj_mission.step;
        //mission. = obj_mission.attitude_provider;
        mission.initial_mass = obj_mission.initial_mass;
        mission.initial_date = new AbsoluteDate(
                obj_mission.initial_date.year,
                obj_mission.initial_date.month,
                obj_mission.initial_date.day,
                obj_mission.initial_date.hour,
                obj_mission.initial_date.minute,
                obj_mission.initial_date.seconds,
                timeScale
                );
        mission.initial_orbit.a = obj_mission.initial_orbit.a;
        mission.initial_orbit.e = obj_mission.initial_orbit.e;
        mission.initial_orbit.i = obj_mission.initial_orbit.i;
        mission.initial_orbit.omega = obj_mission.initial_orbit.omega;
        mission.initial_orbit.raan = obj_mission.initial_orbit.raan;
        mission.initial_orbit.lM = obj_mission.initial_orbit.lM;

        simulator.setSelectedMission(mission);//If is playing, stop it.
        //updateGuiControls(); set in local_thread after connection
    }

    @JavascriptInterface
    public void playButtonClicked() {
        if(simulator.getSimulationStatus() == SimulationStatus.Play)
            simulator.pause();
        else {
            simulator.play();
        }
        simulator.updateGuiControls();
    }

    @JavascriptInterface
    public void stopButtonClicked() {
        simulator.stop();
        //updateGuiControls(); set after stop in local thread
    }

    @JavascriptInterface
    public void reverseButtonClicked() {
        simulator.setSimulationSense(SimulationSense.Reverse);
        simulator.updateGuiControls();
    }

    @JavascriptInterface
    public void forwardButtonClicked() {
        simulator.setSimulationSense(SimulationSense.Forward);
        simulator.updateGuiControls();
    }

    @JavascriptInterface
    public void slowButtonClicked() {
        simulator.doSlowSimulation();
        //updateGuiControls();
    }

    @JavascriptInterface
    public void accelerateButtonClicked() {
        simulator.doAccelerateSimulation();
        //updateGuiControls();
    }

    @JavascriptInterface
    public void sendSimulatorConfiguration(String json_config) {
        //JsonObject obj = parser.parse(json_config).getAsJsonObject();
        SimConfig obj_config = gson.fromJson(json_config, SimConfig.class);

        simulator.setSimulatorConfig(obj_config);//If is playing, stop it.
        //updateGuiControls(); set in local_thread after connection
    }

    /*private void updateGuiControls() {
        runOnUiThread(new Runnable() {
            public void run() {
                String json_state = gson.toJson(simulator.getControlsStatus());
                browser.evaluateJavascript("global_simulator.updateSimulatorState('"+json_state+"')",null);
            }
        });
    }*/
    
    
}