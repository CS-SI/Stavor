package cs.si.stavor.database;

import android.provider.BaseColumns;

/**
 * Definition of missions database structure
 * @author Xavier Gibert
 *
 */
public final class StationsReaderContract {
	public StationsReaderContract(){}
	
	/* Inner class that defines the table contents */
    public static abstract class StationEntry implements BaseColumns {
        public static final String TABLE_NAME = "station";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ELEVATION = "elevation";
        public static final String COLUMN_NAME_BEAMWIDTH = "beamwidth";
    }

    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String TEXT_TYPE = " TEXT";
    //private static final String SERIALIZED_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    
    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + StationEntry.TABLE_NAME + " (" +
        		StationEntry._ID + " INTEGER PRIMARY KEY," +
        		StationEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
        		StationEntry.COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
        		StationEntry.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE + COMMA_SEP +
        		StationEntry.COLUMN_NAME_ELEVATION + DOUBLE_TYPE + COMMA_SEP +
        		StationEntry.COLUMN_NAME_BEAMWIDTH + DOUBLE_TYPE + 
        " )";

    static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + StationEntry.TABLE_NAME;

}
