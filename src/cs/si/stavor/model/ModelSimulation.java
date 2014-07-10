package cs.si.stavor.model;

import java.util.ArrayList;

import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import android.view.View;
import android.webkit.WebView;

import com.google.gson.Gson;

import cs.si.satcor.MainActivity;
import cs.si.satcor.R;

/**
 * Contains and handles both the model information and configuration
 * @author Xavier Gibert
 *
 */
public class ModelSimulation {
	private Gson gson = new Gson();
    private ModelConfiguration config;
    private MainActivity activity;
    private WebView browser;
    private boolean isBrowserLoaded;
    private Browsers selectedBrowser = Browsers.None;
    
    public ModelSimulation(MainActivity acv){
    	isBrowserLoaded = false;
    	activity=acv;
    	config = new ModelConfiguration(activity.getApplicationContext());
    }
    
    /**
     * Initialize the required elements for the simulation, 
     * before the simulator is played to save time
     */
    public void preInitialize(){
    	try {
			if(sunFrame==null){
				sunFrame = CelestialBodyFactory.getSun().getInertiallyOrientedFrame();
			}
			if(earthFixedFrame==null){
				earthFixedFrame = CelestialBodyFactory.getEarth().getBodyOrientedFrame();
			}
			if(utc==null){
				utc = TimeScalesFactory.getUTC();
			}
    	} catch (OrekitException e) {
			e.printStackTrace();
			activity.showErrorDialog(activity.getString(R.string.error_initializing_orekit), false);
		}
    }
    
    /**
     * Establish the Hud View
     * @param hud
     * @param mBrowser
     */
    public void setHud(Browsers type, View mBrowser){
    	selectedBrowser = type;
    	if(selectedBrowser.equals(Browsers.Map)){
        	browser = (WebView)mBrowser;
    	}
    }
    
    public void clearHud(){
      	selectedBrowser = Browsers.None;
    	browser = null;
    }
    
    /**
     * Set the loaded status of the browser
     * @param is
     */
    public void setBrowserloaded(boolean is) {
    	isBrowserLoaded = is;
	}

    /**
     * Returns the Initialization for the WebGL model in a JavaScript readable format
     * @return
     */
	public synchronized String getInitializationJSON() {
    	config = new ModelConfiguration(activity.getApplicationContext());
        return gson.toJson(config);
    }
    
    /**
     * Pushes the new simulation step to the WebGL model
     */
    public void pushSimulationModel(){
    	if(browser!=null && isBrowserLoaded){
    		if(selectedBrowser.equals(Browsers.Map)){
    			if(mapPathBuffer.size()!=0){
    				browser.loadUrl("javascript:updateModelState('"+gson.toJson(getMapPathBuffer())+"')");
    			}
    		}
    	}
	}
    
    private TimeScale utc;
    private Frame sunFrame, earthFixedFrame;
    public void updateSimulation(SpacecraftState scs, int sim_progress){
    	if(selectedBrowser.equals(Browsers.Map)){
    		
    		OneAxisEllipsoid earth = new OneAxisEllipsoid(
    				Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
    		 		Constants.WGS84_EARTH_FLATTENING,
    		 		earthFixedFrame);
    		 try {
    		 	GeodeticPoint gp = earth.transform(scs.getPVCoordinates().getPosition(), scs.getFrame(), scs.getDate());
    		 	double lat = gp.getLatitude()*180/Math.PI;
    		 	double lon = gp.getLongitude()*180/Math.PI;
    		 	if(!Double.isNaN(lat)&&!Double.isNaN(lon))
    		 		addToMapPathBuffer(lat, lon);
    		} catch (OrekitException e) {
    			e.printStackTrace();
    		}
    	}
    }


	private ArrayList<MapPoint> mapPathBuffer = new ArrayList<MapPoint>();
	public synchronized void resetMapPathBuffer() {
		mapPathBuffer.clear();
	}
	public synchronized void addToMapPathBuffer(double lat, double lon) {
		mapPathBuffer.add(new MapPoint(lat,lon));
	}
	public synchronized MapPoint[] getMapPathBuffer(){
		MapPoint[] r =
				  (MapPoint[])mapPathBuffer.toArray(new MapPoint[mapPathBuffer.size()]);
		resetMapPathBuffer();
		return r;
	}
	
	class MapPoint{
		public MapPoint(double lat, double lon){
			latitude = lat;
			longitude = lon;
		}
		double latitude = 0;
		double longitude = 0;
	}

	
}
