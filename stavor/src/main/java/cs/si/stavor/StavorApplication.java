package cs.si.stavor;

import java.util.HashMap;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import cs.si.stavor.app.Parameters;
import cs.si.stavor.database.ReaderDbHelper;
import cs.si.stavor.web.WebAppInterface;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;

/**
 * Application
 * @author Xavier Gibert
 *
 */
public class StavorApplication extends MultiDexApplication {
	private String searchTerm = "";
	
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	public CharSequence mTitle;
	public int currentSection;
	
	//Global database objects (for multi-activity access)
	public ReaderDbHelper db_help;
    public SQLiteCursorLoader loader = null;
    public SQLiteDatabase db;
    
    public int modelViewId = R.id.menu_views_ref_frame_xyz;
    public int modelOrbitViewId = R.id.menu_orbviews_free;
    
    WebAppInterface jsInterface;

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
	
	//********************** GOOGLE ANALYTICS
	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	 * storing them all in Application object helps ensure that they are created only once per
	 * application instance.
	 */
	public enum TrackerName {
	  APP_TRACKER, // Tracker used only in this app.
	  //GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
	  //ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	
	public synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
	
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			/*Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
							: analytics.newTracker(R.xml.ecommerce_tracker);*/
			Tracker t = analytics.newTracker(R.xml.tracker);
	        mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}
}