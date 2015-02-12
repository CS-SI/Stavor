package cs.si.stavor;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.support.multidex.MultiDexApplication;

/**
 * Application
 * @author Xavier Gibert
 *
 */
public class StavorApplication extends MultiDexApplication {
	private String searchTerm = "";

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
        GoogleAnalytics.getInstance(this).setAppOptOut(BuildConfig.DEBUG);
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