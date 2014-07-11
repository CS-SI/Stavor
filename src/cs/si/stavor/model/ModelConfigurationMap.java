package cs.si.stavor.model;

import cs.si.satcor.R;
import cs.si.stavor.model.ModelSimulation.MapPoint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Initialization parameters of the WebGL model
 * @author Xavier Gibert
 *
 */
public class ModelConfigurationMap {
	
	public ModelConfigurationMap(Context ctx, MapPoint[] path, boolean follow_sc_view){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		try{
			payload_aperture = Double.parseDouble(sharedPref.getString(ctx.getString(R.string.pref_key_payload_aperture), Double.toString(payload_aperture)));
			
		}catch(NumberFormatException e){
			System.err.println("Error loading configuration parameter: "+e.getMessage());
		}
		points = path;
		follow_sc = follow_sc_view;
		
	}
	
	public MapPoint[] points = null;
	public boolean follow_sc = false;
	public double payload_aperture = 5.0;

}
