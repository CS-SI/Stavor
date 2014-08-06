package cs.si.stavor.simulator;

import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.NadirPointing;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;

import cs.si.satcor.R;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.mission.Mission;
import android.os.Handler;

public class ThreadLocal extends Thread{
	private final Handler mHandler;
	
	private Simulator simulator;
	private Mission mission;
	
	ThreadLocal(Handler handler, Simulator simu, Mission mis) {
        mHandler = handler;

		simulator = simu;
		mission = mis;
    }
	
	@Override public void run() {
    	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Disconnected)){
    		//initialize simulation
    		try {
    			simulator.setProgress(60 * 100);
    			setSimulationParameters();
    			simulator.setProgress(80 * 100);
				setConnected();
				simulator.goToHud();
        	    simulator.showMessage(simulator.getContext().getString(R.string.sim_local_simulator_connected));
        	} catch (OrekitException e) {
        		e.printStackTrace();
        		simulator.showMessage(simulator.getContext().getString(R.string.sim_orekit_init_error)+": "+e.getMessage());
        		setDisconnected();
        	}
    	}
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
		    try {
				while (true){//Infinite simulation loop
					if(simulator.reset){
						simulator.reset=false;
						setSimulationParameters();
						simulator.pause();
						clearBrowserPath();
					}
					simulator.playCondition.block();
					if(simulator.cancel){
		            	simulator.cancel=false;
		                break;
		            }
					
					//Fix simulation speed to desired FPS
					long dur = (System.nanoTime()-time_tmp_data);
					if(dur<(Parameters.Simulator.min_hud_model_refreshing_interval_ns-Parameters.Simulator.model_refreshing_interval_safe_guard_ns)){
						try {
							long sleep_dur = (Parameters.Simulator.min_hud_model_refreshing_interval_ns-dur)/1000000;
							if(sleep_dur>0){
								Thread.sleep(sleep_dur);		
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else{
						try {
							Thread.sleep(Parameters.Simulator.model_refreshing_interval_safe_guard_ns/1000000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					time_tmp_data = System.nanoTime();
					
					
					SpacecraftState sstate = propagate();
					int progress = (int)(((mission.sim_duration+extrapDate.durationFrom(finalDate))/mission.sim_duration)*100); 

					if(sstate!=null){
						simulator.getSimulationResults().updateSimulation(sstate, progress);
						
			            publishProgress();
					}else{
						simulator.stop();
						simulator.showMessage(simulator.getContext().getString(R.string.sim_mission_ended));
					}
		            if(simulator.cancel){
		            	simulator.cancel=false;
		                break;
		            }
		            Thread.yield();
				}
			} catch (OrekitException e) {
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_orekit_prop_error)+": "+e.getMessage());
			}
    	}
    	setDisconnected();
    }

	private long time_tmp_data = 0;
    private long time_tmp_gui = 0;
	private void publishProgress(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
        		//Update model by push
            	simulator.getSimulationResults().pushSimulationModel();

            	//Update GUI HUD
            	if(time_tmp_gui==0 || (System.nanoTime()-time_tmp_gui)>Parameters.Simulator.min_hud_panel_refreshing_interval_ns){
            		
            		time_tmp_gui = System.nanoTime();
            		simulator.getSimulationResults().updateHUD();
            	}
            }
        });
	}
	
	private void clearBrowserPath(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
        		//Clear model by push
            	simulator.getSimulationResults().resetMapPathBuffer();
            	
            }
        });
	}

    private void setConnected(){
    	//Log.d("Sim",System.currentTimeMillis()+": "+"Simulator connected");
    	simulator.setSimulatorStatus(SimulatorStatus.Connected);
    }
    
    public void setDisconnected(){
		//Log.d("Sim",System.currentTimeMillis()+": "+"Simulator disconnected");
    	simulator.setSimulatorStatus(SimulatorStatus.Disconnected);
    	simulator.resetSelectedMissionId();
    }
    
    private Frame inertialFrame; 
    //private Frame rotatingFrame;
    private Propagator propagator;
    private AbsoluteDate extrapDate, finalDate;
    /**
     * Initialize simulation
     * @throws OrekitException
     */
    private void setSimulationParameters() throws OrekitException{
		switch(mission.inertialFrame){
			case GCRF:
				inertialFrame = FramesFactory.getGCRF();
				break;
			case EME2000:
				inertialFrame = FramesFactory.getEME2000();
				break;
			case MOD:
				inertialFrame = FramesFactory.getMOD(true);
				break;
			case TOD:
				inertialFrame = FramesFactory.getTOD(true);
				break;
			case TEME:
				inertialFrame = FramesFactory.getTEME();
				break;
			case Veis1959:
				inertialFrame = FramesFactory.getVeis1950();
				break;
			default:
				inertialFrame = FramesFactory.getEME2000();
				break;
		}
		/*switch(mission.rotatingFrame){
			case ITRF:
				rotatingFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
				break;
			case GTOD:
				rotatingFrame = FramesFactory.getGTOD(true);
				break;
			default:
				rotatingFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
				break;
		}*/
		Orbit initialOrbit = new KeplerianOrbit(mission.initial_orbit.a, mission.initial_orbit.e, mission.initial_orbit.i, mission.initial_orbit.omega, mission.initial_orbit.raan, mission.initial_orbit.lM, PositionAngle.MEAN, inertialFrame, mission.initial_date, mission.initial_orbit.mu);
		BodyShape earth = new OneAxisEllipsoid(org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS,org.orekit.utils.Constants.WGS84_EARTH_FLATTENING,CelestialBodyFactory.getEarth().getBodyOrientedFrame());
		AttitudeProvider attitudeProvider = new NadirPointing(earth);
		
		SpacecraftState old_st;
		switch(mission.propagatorType){
			case Keplerian://FIXME:PROPAGATOR implement other propagators and put a flag to use each one
				//kepler = new KeplerianPropagator(initialOrbit,null,mission.initial_orbit.mu,mission.initial_mass);
				propagator = new KeplerianPropagator(initialOrbit,attitudeProvider,mission.initial_orbit.mu, mission.initial_mass);
				propagator.setSlaveMode();
				old_st = propagator.getInitialState();
				propagator.resetInitialState(new SpacecraftState(old_st.getOrbit(), old_st.getAttitude() , mission.initial_mass));
				break;
			default:
				//kepler = new KeplerianPropagator(initialOrbit,"DEFAULT_LAW",mission.initial_orbit.mu,mission.initial_mass);
				propagator = new KeplerianPropagator(initialOrbit,attitudeProvider,mission.initial_orbit.mu, mission.initial_mass);
				propagator.setSlaveMode();
				old_st = propagator.getInitialState();
				propagator.resetInitialState(new SpacecraftState(old_st.getOrbit(), old_st.getAttitude() , mission.initial_mass));
		}
		
		//Apply EventDetectors for maneuvers
		/*for(Entry<String, ManeuverImpulse> ma : simulation.mission.maneuvers.entrySet()){
			ManeuverImpulse mi = (ManeuverImpulse) ma.getValue();
			propagator.addEventDetector(mi.getOrekitManeuver());
		}*/
		
		extrapDate = mission.initial_date;
		finalDate =  new AbsoluteDate(mission.initial_date, mission.sim_duration);
		
		//step=simulation.mission.sim_step;
			
	}
    
    /**
     * Propagate simulation
     * @return
     * @throws PropagationException
     */
    private SpacecraftState propagate() throws PropagationException{
		if(extrapDate.compareTo(finalDate) <= 0){
			SpacecraftState currentState = propagator.propagate(extrapDate);
			extrapDate = new AbsoluteDate(extrapDate, mission.sim_step);
			return currentState;
		}else{
			return null;
		}
	}
}
