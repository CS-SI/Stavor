package cs.si.satcor;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import cs.si.stavor.database.ReaderDbHelper;
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
	public ReaderDbHelper db_help;
    public SQLiteCursorLoader loader = null;
    public SQLiteDatabase db;
    
    public int follow_sc = R.id.menu_mapviews_free;
    public int zoom = 1;
    public float lon = (float) 0.0;
    public float lat = (float) 0.0;
	
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public String getSearchTerm() {
		return this.searchTerm;
	}
}