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
	
	public ModelConfigurationMap(Context ctx, MapPoint[] path, int follow_sc_view){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		try{
			payload_beamwidth = Double.parseDouble(sharedPref.getString(ctx.getString(R.string.pref_key_payload_beamwidth), Double.toString(payload_beamwidth)));
			
		}catch(NumberFormatException e){
			System.err.println("Error loading configuration parameter: "+e.getMessage());
		}
		points = path;
		if(follow_sc_view == R.id.menu_mapviews_free)
			follow_sc = false;
		else if(follow_sc_view == R.id.menu_mapviews_locked){
			follow_sc = true;
		}
		
	}
	
	public MapPoint[] points = null;
	public boolean follow_sc = false;
	public double payload_beamwidth = 5.0;

}
