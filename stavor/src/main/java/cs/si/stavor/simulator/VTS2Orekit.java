package cs.si.stavor.simulator;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.orekit.attitudes.Attitude;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;

import cs.si.stavor.mission.Mission;

public class VTS2Orekit {
	
	public static String[] generateVtsMessage(SpacecraftState state){
		Vector3D position = state.getPVCoordinates().getPosition();
		Vector3D velocity = state.getPVCoordinates().getVelocity();
		Rotation quaternion = state.getAttitude().getRotation();
		String[] messages = {//XGGDEBUG: the date has to bee in julianday cnes
				"DATA "+state.getDate().toString()+" pos \""+position.getX()+" "+position.getY()+" "+position.getZ()+"\"",
				"DATA "+state.getDate().toString()+" vel \""+velocity.getX()+" "+velocity.getY()+" "+velocity.getZ()+"\"",
				"DATA "+state.getDate().toString()+" quat \""+quaternion.getQ0()+" "+quaternion.getQ1()+" "+quaternion.getQ2()+" "+quaternion.getQ3()+"\"",
				"DATA "+state.getDate().toString()+" quat \""+state.getMass()+"\""
				};
		return messages;
	}
	
	public static SpacecraftState processVtsMessage(String message, SpacecraftState old_state){
		old_state = initializeState(old_state);
		
		String[] parts = message.split(" ", 3);
		if(parts[0].equals("DATA")){
			
			//Get date
			AbsoluteDate date = AbsoluteDate.createJulianEpoch(
					Double.parseDouble(parts[1]));
			
			Vector3D position = old_state.getPVCoordinates().getPosition();
			Vector3D velocity = old_state.getPVCoordinates().getVelocity();
			double mass = old_state.getMass();
			Rotation quaternion = old_state.getAttitude().getRotation();
			char sign = '"';
			parts[3] = parts[3].replace(Character.toString(sign),"");
			String[] values = parts[3].split(" ");
			if(parts[2].equals("pos")){
				position = new Vector3D(
						Double.parseDouble(values[0]),
						Double.parseDouble(values[1]),
						Double.parseDouble(values[2])
						);
			}else if(parts[2].equals("quat")){
				quaternion = new Rotation(
						Double.parseDouble(values[0]),
						Double.parseDouble(values[1]),
						Double.parseDouble(values[2]),
						Double.parseDouble(values[3]),
						false
						);
			}else if(parts[2].equals("vel")){
				velocity = new Vector3D(
						Double.parseDouble(values[0]),
						Double.parseDouble(values[1]),
						Double.parseDouble(values[2])
						); 
			}else if(parts[2].equals("mass")){
				mass = Double.parseDouble(values[0]);
			}
			PVCoordinates pv = new PVCoordinates(position, velocity);
			Orbit orbit = new KeplerianOrbit(pv, old_state.getFrame(), date, old_state.getMu());
			Attitude attitude = new Attitude(date, old_state.getFrame(), quaternion, null, null);
			
			SpacecraftState state = new SpacecraftState(orbit, attitude, mass);
			old_state = state;
			return old_state;
		}
		return null;
	}
	
	private static SpacecraftState initializeState(SpacecraftState old_state){
		if(old_state == null){
			try {
				Mission mission = new Mission();
				old_state = new SpacecraftState(new KeplerianOrbit(mission.initial_orbit.a, mission.initial_orbit.e, mission.initial_orbit.i, mission.initial_orbit.omega, mission.initial_orbit.raan, mission.initial_orbit.lM, PositionAngle.MEAN, FramesFactory.getEME2000(), mission.initial_date, mission.initial_orbit.mu));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (OrekitException e) {
				e.printStackTrace();
			}
		}
		return old_state;
	}
}
