package cs.si.satatt;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import database.MissionReaderDbHelper;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Application
 * @author Xavier Gibert
 *
 */
public class SatAttApplication extends Application {
	private String searchTerm = "";
	
	public MissionReaderDbHelper db_help;
    public SQLiteCursorLoader loader = null;
    public SQLiteDatabase db;
	
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public String getSearchTerm() {
		return this.searchTerm;
	}
}