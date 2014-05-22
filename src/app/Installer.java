package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import mission.Mission;
import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import database.MissionReaderContract.MissionEntry;
import database.MissionReaderDbHelper;
import database.SerializationUtil;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

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
	public static void installApkData(MainActivity activity){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		if(!prefs.getBoolean(activity.getString(R.string.pref_key_data_installed), false)){
			Log.d("INSTALLER", "Installing Orekit data files...");
			if(isExternalStorageWritable()){
				if(copyAssets(activity)){
					prefs.edit().putBoolean(activity.getString(R.string.pref_key_data_installed), true).commit();
					Log.d("INSTALLER", "Installing Orekit data files... OK");
					activity.showWelcomeMessage();
				}else{
					Log.d("INSTALLER", "Installing Orekit data files... FAIL");
					activity.showErrorDialog(activity.getString(R.string.error_installing_orekit_default_data), true);
				}
			}else{
				Log.d("INSTALLER", "Cannot install Orekit data files, external storage not accessible");
				activity.showErrorDialog(activity.getString(R.string.error_installing_orekit_default_data_external_storage_not_accessible), true);
			}
		}else{
			Log.d("INSTALLER", "Orekit data files are already installed...");
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
		        Log.e("tag", "Failed to get asset file list.", e);
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
		            Log.e("tag", "Failed to copy asset file: " + filename, e);
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
	
	public static MissionReaderDbHelper installApkDatabase(MainActivity activity){
		MissionReaderDbHelper mDbHelper = new MissionReaderDbHelper(activity.getApplicationContext());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		if(!prefs.getBoolean(activity.getString(R.string.pref_key_database_installed), false)){
			Log.d("INSTALLER", "Installing Missions database...");
			
			if(addMissionEntry(db)){
				prefs.edit().putBoolean(activity.getString(R.string.pref_key_database_installed), true).commit();
				Log.d("INSTALLER", "Installing Missions database... OK");
				//activity.showWelcomeMessage();
			}else{
				Log.d("INSTALLER", "Installing Missions database... FAIL");
				activity.showErrorDialog(activity.getString(R.string.error_installing_missions_database), true);
			}
		}else{
			Log.d("INSTALLER", "Missions database is already installed...");
		}
		
		return mDbHelper;
	}
	
	private static boolean addMissionEntry(SQLiteDatabase db){
		// Create a new map of values, where column names are the keys
		boolean result = true;
		
		Mission mission = new Mission();
		mission.name="Example GTO";
		mission.description="GTO mission example";
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
		mission.name="Example GEO";
		mission.description="GEO mission example";
		mission.initial_orbit.a=4.2164E7;
		mission.initial_orbit.e=0.0;
		mission.initial_orbit.i=0.0;
		
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
		
		
		return result;
	}
}
