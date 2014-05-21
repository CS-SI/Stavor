package database;

import android.provider.BaseColumns;

public final class MissionReaderContract {
	public MissionReaderContract(){}
	
	/* Inner class that defines the table contents */
    public static abstract class MissionEntry implements BaseColumns {
        public static final String TABLE_NAME = "mission";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_CLASS = "class";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String SERIALIZED_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + MissionEntry.TABLE_NAME + " (" +
        		MissionEntry._ID + " INTEGER PRIMARY KEY," +
        		MissionEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
        		MissionEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
        		MissionEntry.COLUMN_NAME_CLASS + SERIALIZED_TYPE + 
        " )";

    static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + MissionEntry.TABLE_NAME;

}
