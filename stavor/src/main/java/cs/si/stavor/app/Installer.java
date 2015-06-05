package cs.si.stavor.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.math3.util.FastMath;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.database.ReaderDbHelper;
import cs.si.stavor.database.SerializationUtil;
import cs.si.stavor.database.MissionReaderContract.MissionEntry;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import cs.si.stavor.database.UserMission;
import cs.si.stavor.mission.Mission;
import cs.si.stavor.station.GroundStation;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * Provides initial app installation functions
 * @author Xavier Gibert
 *
 */
public class Installer {
	/* Checks if external storage is available for read and write */
	private static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	@SuppressWarnings("unused")
	private static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Returns the File pointing to the root of the Orekit data in the device storage
	 * @param activity
	 * @return
	 */
	public static File getOrekitDataRoot(Activity activity){
		return new File(activity.getExternalFilesDir(null)+File.separator+orekitDataPath);
	}
	
	private static String orekitDataPath = "orekit";
	private static String[] orekitDataFolders = {
		"Others",
		"DE-406-ephemerides",
		"Earth-Orientation-Parameters"+File.separator+"IAU-1980",
		"Earth-Orientation-Parameters"+File.separator+"IAU-2000",
		"MSAFE",
		"Potential"
	};
	
	/**
	 * Installs the default Orekit data files in the device
	 * @param activity
	 */
	public static void installApkData(MainActivity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		if(!prefs.getBoolean(activity.getString(R.string.pref_key_data_installed), false)){
			//Log.d("INSTALLER", "Installing Orekit data files...");
			if(isExternalStorageWritable()){
				if(copyAssets(activity)){
					prefs.edit().putBoolean(activity.getString(R.string.pref_key_data_installed), true).commit();
					//Log.d("INSTALLER", "Installing Orekit data files... OK");
					//activity.showWelcomeMessage();
					activity.flag_show_welcome=true;
				}else{
					//Log.d("INSTALLER", "Installing Orekit data files... FAIL");
					activity.showErrorDialog(activity.getString(R.string.error_installing_orekit_default_data), true);
				}
			}else{
				//Log.d("INSTALLER", "Cannot install Orekit data files, external storage not accessible");
				activity.showErrorDialog(activity.getString(R.string.error_installing_orekit_default_data_external_storage_not_accessible), true);
			}
		}else{
			//Log.d("INSTALLER", "Orekit data files are already installed...");
		}
	}
	private static boolean copyAssets(Activity activity) {
		boolean installed = true;
	    AssetManager assetManager = activity.getAssets();
	    for(String foldername : orekitDataFolders) {
		    String[] files = null;
		    try {
		        files = assetManager.list(orekitDataPath+File.separator+foldername);
		    } catch (IOException e) {
		        //Log.e("tag", "Failed to get asset file list.", e);
		        installed=false;
		    }
		    for(String filename : files) {
		        InputStream in = null;
		        OutputStream out = null;
		        try {
		          in = assetManager.open(orekitDataPath+File.separator+foldername+File.separator+filename);
		          File outFile = new File(activity.getExternalFilesDir(null)+File.separator+orekitDataPath+File.separator+foldername, filename);
		          outFile.getParentFile().mkdirs();
		          out = new FileOutputStream(outFile);
		          copyFile(in, out);
		          in.close();
		          in = null;
		          out.flush();
		          out.close();
		          out = null;
		        } catch(IOException e) {
		            //Log.e("tag", "Failed to copy asset file: " + filename, e);
		            installed=false;
		        }       
		    }
	    }
	    return installed;
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	/**
	 * Installs the missions database including some examples
	 * @param activity
	 * @return
	 */
	public static ReaderDbHelper installApkDatabase(MainActivity activity){
		ReaderDbHelper mDbHelper = new ReaderDbHelper(activity.getApplicationContext(), activity);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		if(!prefs.getBoolean(activity.getString(R.string.pref_key_database_installed), false)){
			//Log.d("INSTALLER", "Installing Missions database...");
			
			if(addMissionEntry(db, activity)){
				prefs.edit().putBoolean(activity.getString(R.string.pref_key_database_installed), true).commit();
				//Log.d("INSTALLER", "Installing Missions database... OK");
			}else{
				//Log.d("INSTALLER", "Installing Missions database... FAIL");
				activity.showErrorDialog(activity.getString(R.string.error_installing_missions_database), true);
			}
		}else{
			//Log.d("INSTALLER", "Missions database is already installed...");
		}
		
		return mDbHelper;
	}
	
	private static boolean addMissionEntry(SQLiteDatabase db, MainActivity activity){
		// Create a new map of values, where column names are the keys
		boolean result = true;
		
		Mission mission = new Mission();
		mission.name="Example GTO";
		mission.description="Geostationary Transfer Orbit mission example.";
		mission.initial_orbit.a=2.4396159E7;
		mission.initial_orbit.e=0.72831215;
		
		ContentValues values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
		
		//Second Example
		mission = new Mission();
		mission.name="Example GSO";
		mission.description="Geosynchronous mission example.";
		mission.initial_orbit.a=4.2164E7;
		mission.initial_orbit.e=0.0;
		mission.initial_orbit.i=0.5;
		mission.initial_orbit.raan=Math.PI/2;
		try {
			mission.initial_date = new AbsoluteDate(2008,7,4,0,0,0.0,TimeScalesFactory.getUTC());
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (OrekitException e1) {
			e1.printStackTrace();
		}
		
		values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
		
		//Third Example
		mission = new Mission();
		mission.name="Example GEO";
		mission.description="Geostationary mission example.";
		mission.initial_orbit.a=4.2164E7;
		mission.initial_orbit.e=0.0;
		mission.initial_orbit.i=0.0;
		mission.initial_orbit.raan=0;
		try {
			mission.initial_date = new AbsoluteDate(2008,7,4,0,0,0.0,TimeScalesFactory.getUTC());
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (OrekitException e1) {
			e1.printStackTrace();
		}
		
		values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
		
		//Fourth Example
		mission = new Mission();
		mission.name="Example LEO-Polar";
		mission.description="Polar Low Earth Orbit mission example";
		mission.initial_orbit.a=7.0E6;
		mission.initial_orbit.e=0.0;
		mission.initial_orbit.i=1.57;
		mission.initial_orbit.raan=0.0;
		mission.sim_duration=100000.0;
		mission.sim_step=10.0;
		try {
			mission.initial_date = new AbsoluteDate(2008,1,3,0,0,0.0,TimeScalesFactory.getUTC());
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (OrekitException e1) {
			e1.printStackTrace();
		}
		
		values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
		
		//Fifth Example
		mission = new Mission();
		mission.name="Example MEO";
		mission.description="Medium Earth Orbit mission example";
		mission.initial_orbit.a=2.9E7;
		mission.initial_orbit.e=0.0;
		mission.initial_orbit.i=0.90;
		mission.initial_orbit.raan=0.0;
		mission.sim_duration=100000.0;
		mission.sim_step=10.0;
		try {
			mission.initial_date = new AbsoluteDate(2008,1,3,0,0,0.0,TimeScalesFactory.getUTC());
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (OrekitException e1) {
			e1.printStackTrace();
		}
		
		values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
		
		//Sixth Example
		mission = new Mission();
		mission.name="Example HEO";
		mission.description="High Earth Orbit mission example.";
		mission.initial_orbit.a=5.2164E7;
		mission.initial_orbit.e=0.0;
		mission.initial_orbit.i=0.0;
		mission.initial_orbit.raan=0;
		try {
			mission.initial_date = new AbsoluteDate(2008,7,4,0,0,0.0,TimeScalesFactory.getUTC());
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (OrekitException e1) {
			e1.printStackTrace();
		}
		
		values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;

		//Seventh Example
		mission = new Mission();
		mission.name="Example Galileo #5";
		mission.description="Galileo Constellation Satellite #5 expected orbit.";
		mission.initial_orbit.a=2.9900E7;
		mission.initial_orbit.e=0.0;
		mission.initial_orbit.i= FastMath.toRadians(55);
		mission.initial_orbit.raan=0;
		mission.sim_duration = 1000000.;
		mission.sim_step = 100.;

		try {
			mission.initial_date = new AbsoluteDate(2014,8,23,0,0,0.0,TimeScalesFactory.getUTC());
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (OrekitException e1) {
			e1.printStackTrace();
		}

		values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				MissionEntry.TABLE_NAME,
				null,
				values);
		if(newRowId==-1)
			result=false;
		

		//ADD USER MISSIONS
		for(UserMission mis : activity.userMissions){
			values = new ContentValues();
			values.put(MissionEntry.COLUMN_NAME_NAME, mis.name);
			values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mis.description);
			values.put(MissionEntry.COLUMN_NAME_CLASS, mis.serialclass);
			
			// Insert the new row, returning the primary key value of the new row
			newRowId = db.insert(
					MissionEntry.TABLE_NAME,
					null,
			         values);
			if(newRowId==-1)
				result=false;
		}
		activity.userMissions.clear();
			
		//******** GROUND STATIONS ************
		//First Example VIL-2
		GroundStation gs = new GroundStation();
		gs.enabled = false;
		gs.name="Villafranca";
		gs.latitude=40.442592;
		gs.longitude=-3.951583;
		gs.altitude=664.8;
		gs.elevation=1;
		
		values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
		values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
		values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				StationEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
		
		//Second Example KRU
		gs = new GroundStation();
		gs.enabled = true;
		gs.name="Kourou";
		gs.latitude=5.251439;
		gs.longitude=-52.804664;
		gs.altitude=-14.6709;
		gs.elevation=1;
		
		values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
		values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
		values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				StationEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
		
		//Third Example Cebreros
		gs = new GroundStation();
		gs.enabled = false;
		gs.name="Cebreros";
		gs.latitude=40.452689;
		gs.longitude=-4.36755;
		gs.altitude=-794.095;
		gs.elevation=1;
		
		values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
		values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
		values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				StationEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
	

		//Fourth Example Kiruna
		gs = new GroundStation();
		gs.enabled = false;
		gs.name="Kiruna";
		gs.latitude=67.857128;
		gs.longitude=20.964325;
		gs.altitude=402.1724;
		gs.elevation=1;
		
		values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
		values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
		values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

		
		// Insert the new row, returning the primary key value of the new row
		newRowId = db.insert(
				StationEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			result=false;
	
		//Fifth Example Maspalomas
			gs = new GroundStation();
			gs.enabled = false;
			gs.name="Maspalomas";
			gs.latitude=27.762889;
			gs.longitude=-15.6338;
			gs.altitude=205.1177;
			gs.elevation=1;
			
			values = new ContentValues();
			values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
			values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
			values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
			values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
			values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
			values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

			
			// Insert the new row, returning the primary key value of the new row
			newRowId = db.insert(
					StationEntry.TABLE_NAME,
					null,
			         values);
			if(newRowId==-1)
				result=false;
			
			//sixth Example Poker flat
			gs = new GroundStation();
			gs.enabled = false;
			gs.name="Poker Flat";
			gs.latitude=65.116667;
			gs.longitude=-147.461667;
			gs.altitude=430.34;
			gs.elevation=1;
			
			values = new ContentValues();
			values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
			values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
			values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
			values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
			values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
			values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

			
			// Insert the new row, returning the primary key value of the new row
			newRowId = db.insert(
					StationEntry.TABLE_NAME,
					null,
			         values);
			if(newRowId==-1)
				result=false;
			
			//seventh Example Santiago, Chile
			gs = new GroundStation();
			gs.enabled = false;
			gs.name="Santiago";
			gs.latitude=-33.151794;
			gs.longitude=-70.667312;
			gs.altitude=730.0;
			gs.elevation=1;
			
			values = new ContentValues();
			values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
			values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
			values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
			values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
			values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
			values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

			
			// Insert the new row, returning the primary key value of the new row
			newRowId = db.insert(
					StationEntry.TABLE_NAME,
					null,
			         values);
			if(newRowId==-1)
				result=false;
			
			//eigth Example Canberra
			gs = new GroundStation();
			gs.enabled = false;
			gs.name="Canberra";
			gs.latitude=-39.018556;
			gs.longitude=148.983058;
			gs.altitude=680.0;
			gs.elevation=1;
			
			values = new ContentValues();
			values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
			values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
			values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
			values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
			values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
			values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

			
			// Insert the new row, returning the primary key value of the new row
			newRowId = db.insert(
					StationEntry.TABLE_NAME,
					null,
			         values);
			if(newRowId==-1)
				result=false;
			
			//nineth Example Goldstone
			gs = new GroundStation();
			gs.enabled = false;
			gs.name="Goldstone";
			gs.latitude=35.339907;
			gs.longitude=-116.883019;
			gs.altitude=956.059;
			gs.elevation=1;
			
			values = new ContentValues();
			values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
			values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
			values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
			values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
			values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
			values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

			
			// Insert the new row, returning the primary key value of the new row
			newRowId = db.insert(
					StationEntry.TABLE_NAME,
					null,
			         values);
			if(newRowId==-1)
				result=false;
			
			//tenth Example Tokyo
			gs = new GroundStation();
			gs.enabled = true;
			gs.name="Tokyo";
			gs.latitude=35.708762;
			gs.longitude=139.491778;
			gs.altitude=-641.245;
			gs.elevation=1;
			
			values = new ContentValues();
			values.put(StationEntry.COLUMN_NAME_NAME, gs.name);
			values.put(StationEntry.COLUMN_NAME_LATITUDE, gs.latitude);
			values.put(StationEntry.COLUMN_NAME_LONGITUDE, gs.longitude);
			values.put(StationEntry.COLUMN_NAME_ELEVATION, gs.elevation);
			values.put(StationEntry.COLUMN_NAME_ENABLED, gs.enabled);
			values.put(StationEntry.COLUMN_NAME_ALTITUDE, gs.altitude);

			
			// Insert the new row, returning the primary key value of the new row
			newRowId = db.insert(
					StationEntry.TABLE_NAME,
					null,
			         values);
			if(newRowId==-1)
				result=false;
		
		return result;
	}
}
