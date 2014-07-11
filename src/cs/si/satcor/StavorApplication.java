package cs.si.satcor;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import cs.si.stavor.database.MissionReaderDbHelper;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Application
 * @author Xavier Gibert
 *
 */
public class StavorApplication extends Application {
	private String searchTerm = "";
	
	//Global database objects (for multi-activity access)
	public MissionReaderDbHelper db_help;
    public SQLiteCursorLoader loader = null;
    public SQLiteDatabase db;
    
    public boolean follow_sc = false;
	
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public String getSearchTerm() {
		return this.searchTerm;
	}
}