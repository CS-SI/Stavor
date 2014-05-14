package simulator;

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
import org.orekit.utils.IERSConventions;

import cs.si.satatt.R;
import mission.Mission;
import model.ModelSimulation;
import android.os.AsyncTask;
import android.util.Log;
import app.Parameters;

public class SimulatorThread extends AsyncTask<ModelSimulation, Void, Boolean>{
 
	private Simulator simulator;
	private Mission mission;
	
	public SimulatorThread(Simulator simu, Mission mis){
		simulator = simu;
		mission = mis;
	}
	
    @Override
    protected Boolean doInBackground(ModelSimulation... params) {
    	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Disconnected)){
    		//Log.d("Sim",System.currentTimeMillis()+": "+"simulator connecting socket");
    		try {
    			setSimulationParameters();
				setConnected();
				simulator.goToHud();
        	    simulator.showMessage(simulator.getContext().getString(R.string.sim_local_simulator_connected));
        	    //Log.d("Sim",System.currentTimeMillis()+": "+"socket openend");
        	} catch (OrekitException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        		simulator.showMessage(simulator.getContext().getString(R.string.sim_orekit_init_error)+": "+e.getMessage());
        		setDisconnected();
        	}
    	}
    	if(simulator.getSimulatorStatus().equals(SimulatorStatus.Connected)){
		    try {
				while (true){
					
					//TODO Propagate
					long dur = (System.nanoTime()-time_tmp_data);
					if(dur<(Parameters.Simulator.min_hud_model_refreshing_interval_ns-Parameters.Simulator.model_refreshing_interval_safe_guard_ns)){
						try {
							long sleep_dur = (Parameters.Simulator.min_hud_model_refreshing_interval_ns-dur)/1000000;
							if(sleep_dur>0){
								Thread.sleep(sleep_dur);		
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						//Log.d("SimLocal", "Simulation step longer than available time: "+dur);
						try {
							Thread.sleep(Parameters.Simulator.model_refreshing_interval_safe_guard_ns/1000000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					time_tmp_data = System.nanoTime();
					
					
					SpacecraftState sstate = propagate();
					int progress = (int)((extrapDate.durationFrom(finalDate)/mission.sim_duration)*100); 

					if(sstate!=null){
						simulator.getSimulationResults().updateSimulation(sstate, progress);
						
			            publishProgress();
					}
		            if(isCancelled())
		                break;
		            Thread.yield();
				}
			} catch (OrekitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				simulator.showMessage(simulator.getContext().getString(R.string.sim_orekit_prop_error)+": "+e.getMessage());
			}
    	}
        return true;
    }

	private long time_tmp_data = 0;
    private long time_tmp_gui = 0;
    @Override
    protected void onProgressUpdate(Void... values) {
    	//Update model by push
    	simulator.getSimulationResults().pushSimulationModel();
    	//Update GUI HUD
    	if(time_tmp_gui==0 || (System.nanoTime()-time_tmp_gui)>Parameters.Simulator.min_hud_panel_refreshing_interval_ns){
    		
    		time_tmp_gui = System.nanoTime();
    		//simulator.getSimulationResults().updateHUD();
    		simulator.getSimulationResults().test();
    	}
    }
 
    @Override
    protected void onPreExecute() {
    	
    }
 
    @Override
    protected void onPostExecute(Boolean result) {
    	setDisconnected();
    }
 
    @Override
    protected void onCancelled() {
        
    }
    
    private void setConnected(){
    	Log.d("Sim",System.currentTimeMillis()+": "+"Simulator connected");
    	simulator.setSimulatorStatus(SimulatorStatus.Connected);
    }
    
    public void setDisconnected(){
		Log.d("Sim",System.currentTimeMillis()+": "+"Simulator disconnected");
    	simulator.setSimulatorStatus(SimulatorStatus.Disconnected);
    }
    
    private Frame inertialFrame, rotatingFrame;
    private Propagator propagator;
    private AbsoluteDate extrapDate, finalDate;
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
		
		SpacecraftState old_st;
		switch(mission.propagatorType){
			case Keplerian://FIXME:PROPAGATOR implement other propagators and put a flag to use each one
				//kepler = new KeplerianPropagator(initialOrbit,null,mission.initial_orbit.mu,mission.initial_mass);
				propagator = new KeplerianPropagator(initialOrbit,mission.initial_orbit.mu);
				propagator.setSlaveMode();
				old_st = propagator.getInitialState();
				propagator.resetInitialState(new SpacecraftState(old_st.getOrbit(), old_st.getAttitude() , mission.initial_mass));
				break;
			default:
				//kepler = new KeplerianPropagator(initialOrbit,"DEFAULT_LAW",mission.initial_orbit.mu,mission.initial_mass);
				propagator = new KeplerianPropagator(initialOrbit,mission.initial_orbit.mu);
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
    
    private SpacecraftState propagate() throws PropagationException{
		if(extrapDate.compareTo(finalDate) <= 0){
			SpacecraftState currentState = propagator.propagate(extrapDate);
			extrapDate = new AbsoluteDate(extrapDate, mission.sim_step);
			return currentState;
		}else{
			return null;
			//TODO stop simulator
		}
	}

}