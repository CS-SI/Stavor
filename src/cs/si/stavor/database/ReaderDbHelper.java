package cs.si.stavor.database;

import cs.si.stavor.MainActivity;
import cs.si.stavor.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

/**
 * Reader of missions database information
 * @author Xavier Gibert
 *
 */
public class ReaderDbHelper extends SQLiteOpenHelper {
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Missions.db";
    private MainActivity activity;
    
    public ReaderDbHelper(Context context, MainActivity activity) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.activity = activity;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MissionReaderContract.SQL_CREATE_ENTRIES);
        db.execSQL(StationsReaderContract.SQL_CREATE_ENTRIES);
        
        //Reset install database flag
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        prefs.edit().putBoolean(activity.getString(R.string.pref_key_database_installed), false).commit();
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
    	
    	//Check if the user has introduced new entries
    		//Get all entries
    	
		Cursor result =
			    db.rawQuery("select * from "+MissionReaderContract.MissionEntry.TABLE_NAME+" order by "+MissionReaderContract.MissionEntry._ID+" asc",null);
		
		activity.userMissions.clear();
		if(result.getCount()>3){
			for(int i = 3; i < result.getCount(); i++){
				result.moveToPosition(i);
				String name = result.getString(result.getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_NAME));
				String description = result.getString(result.getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_DESCRIPTION));
				byte[] serialclass = result.getBlob(result.getColumnIndex(MissionReaderContract.MissionEntry.COLUMN_NAME_CLASS));
				activity.userMissions.add(new UserMission(name, description, serialclass));
			}
		}
    	
        db.execSQL(MissionReaderContract.SQL_DELETE_ENTRIES);
        db.execSQL(StationsReaderContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
