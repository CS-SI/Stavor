package cs.si.stavor.model;

import java.util.ArrayList;

import cs.si.stavor.R;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import cs.si.stavor.model.ModelSimulation.MapPoint;
import cs.si.stavor.station.GroundStation;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

/**
 * Initialization parameters of the WebGL model
 * @author Xavier Gibert
 *
 */
public class ModelConfigurationMap {
	
	public ModelConfigurationMap(Context ctx, SQLiteDatabase db, MapPoint[] path, int follow_sc_view, int map_zoom, float map_lon, float map_lat){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		/*try{
			payload_beamwidth = Double.parseDouble(sharedPref.getString(ctx.getString(R.string.pref_key_payload_beamwidth), Double.toString(payload_beamwidth)));
			
		}catch(NumberFormatException e){
			System.err.println("Error loading configuration parameter: "+e.getMessage());
		}*/
		show_fov = sharedPref.getBoolean(ctx.getString(R.string.pref_key_map_show_fov), show_fov);
		show_track = sharedPref.getBoolean(ctx.getString(R.string.pref_key_map_show_track), show_track);
		show_sun_icon = sharedPref.getBoolean(ctx.getString(R.string.pref_key_map_show_sun_icon), show_sun_icon);
		show_sun_terminator = sharedPref.getBoolean(ctx.getString(R.string.pref_key_map_show_sun_terminator), show_sun_terminator);
		
		points = path;
		if(follow_sc_view == R.id.menu_mapviews_free)
			follow_sc = false;
		else if(follow_sc_view == R.id.menu_mapviews_locked){
			follow_sc = true;
		}
		
		zoom = map_zoom;
		lon = map_lon;
		lat = map_lat;
		//track_max_length = Integer.parseInt(sharedPref.getString(ctx.getString(R.string.pref_key_path_length), Integer.toString(track_max_length)));
		
		//Load enabled stations:
		if(db!=null){
			ArrayList<GroundStation> stationsList = new ArrayList<GroundStation>();
			String[] projection = {
					StationEntry.COLUMN_NAME_NAME,
				    StationEntry.COLUMN_NAME_LATITUDE,
				    StationEntry.COLUMN_NAME_LONGITUDE,
				    StationEntry.COLUMN_NAME_ALTITUDE,
				    StationEntry.COLUMN_NAME_ELEVATION
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
				for(int i = 0; i<c.getCount(); i++){
					c.moveToPosition(i);
					
					String station_name = c.getString(c.getColumnIndex(StationEntry.COLUMN_NAME_NAME));
					double station_lat = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_LATITUDE));
					double station_lon = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_LONGITUDE));
					double station_alt = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_ALTITUDE));
					double station_elev = c.getDouble(c.getColumnIndex(StationEntry.COLUMN_NAME_ELEVATION));
					GroundStation gs = new GroundStation(true, station_name, station_lat, station_lon, station_alt, station_elev);
					stationsList.add(gs);
				}
			}
			stations = stationsList.toArray(new GroundStation[stationsList.size()]);
		}
	}
	
	public GroundStation[] stations = null;
	public MapPoint[] points = null;
	public boolean follow_sc = false;
	public int zoom = 1;
	public float lon = (float) 0.0;
	public float lat = (float) 0.0;
	//public double payload_beamwidth = 5.0;
	//public int track_max_length = 5000;
	public boolean show_fov = true;
	public boolean show_track = true;
	public boolean show_sun_icon = true;
	public boolean show_sun_terminator = true;

}
