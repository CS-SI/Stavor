package cs.si.stavor.model;

import java.util.ArrayList;

import cs.si.satcor.MainActivity;
import cs.si.satcor.R;
import cs.si.satcor.StavorApplication;
import cs.si.stavor.database.SerializationUtil;
import cs.si.stavor.database.MissionReaderContract.MissionEntry;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import cs.si.stavor.mission.Mission;
import cs.si.stavor.mission.MissionAndId;
import cs.si.stavor.model.ModelSimulation.MapPoint;
import cs.si.stavor.station.GroundStation;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Initialization parameters of the WebGL model
 * @author Xavier Gibert
 *
 */
public class ModelConfigurationMap {
	
	public ModelConfigurationMap(Context ctx, SQLiteDatabase db, MapPoint[] path, int follow_sc_view){
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
		
		//Load enabled stations:
		if(db!=null){
			ArrayList<GroundStation> stationsList = new ArrayList<GroundStation>();
			String[] projection = {
					StationEntry.COLUMN_NAME_NAME,
				    StationEntry.COLUMN_NAME_LATITUDE,
				    StationEntry.COLUMN_NAME_LONGITUDE,
				    StationEntry.COLUMN_NAME_ELEVATION,
				    StationEntry.COLUMN_NAME_BEAMWIDTH
				    };
	
			Cursor c = db
				.query(
						StationEntry.TABLE_NAME,  // The table to query
					    projection,                               // The columns to return
					    StationEntry.COLUMN_NAME_ENABLED+" = ?",                                // The columns for the WHERE clause
					    new String[]{Integer.toString(1)},                            // The values for the WHERE clause
					    "",                                     // don't group the rows
					    "",                                     // don't filter by row groups
					    null                                 // The sort order
					    );
			if (c != null && c.getCount() > 0) {
				for(int i = 0; i<=c.getCount(); i++){
					c.moveToPosition(i);
					
					String station_name = c.getString(c.getColumnIndex(StationEntry.COLUMN_NAME_NAME));
					double station_lat = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_LATITUDE));
					double station_lon = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_LONGITUDE));
					double station_elev = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_ELEVATION));
					double station_bw = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_BEAMWIDTH));
					GroundStation gs = new GroundStation(true, station_name, station_lat, station_lon, station_elev, station_bw);
					stationsList.add(gs);
				}
			}
			stations = (GroundStation[]) stationsList.toArray();
		}
	}
	
	public GroundStation[] stations = null;
	public MapPoint[] points = null;
	public boolean follow_sc = false;
	public double payload_beamwidth = 5.0;

}
