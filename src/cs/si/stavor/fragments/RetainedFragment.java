package cs.si.stavor.fragments;

import org.xwalk.core.XWalkView;

import cs.si.stavor.database.ReaderDbHelper;
import cs.si.stavor.simulator.Simulator;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * Fragment to store information when restarting app (e.g. rotating device)
 * @author Xavier Gibert
 *
 */
public class RetainedFragment extends Fragment {
	// data object we want to retain
	private XWalkView mXwalkView;
	//Flag used to not reload the browser if only device orientation changed 
    private boolean loadBrowser;
    private XWalkView mXwalkViewOrbit;
	//Flag used to not reload the browser if only device orientation changed 
    private boolean loadBrowserOrbit;
    private Simulator sim;
    private ReaderDbHelper db_help;
    private SQLiteDatabase db;
    private boolean hud_panel_open;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(XWalkView xwalkView, boolean load, XWalkView xwalkViewOrbit, boolean loadOrbit, Simulator data, ReaderDbHelper missionReaderDbHelper, SQLiteDatabase sqLiteDatabase, boolean hud_panel_is_open) {
        this.mXwalkView = xwalkView;
        this.loadBrowser = load;
        this.mXwalkViewOrbit = xwalkViewOrbit;
        this.loadBrowserOrbit = loadOrbit;
    	this.sim = data;
        this.db_help = missionReaderDbHelper;
        this.db = sqLiteDatabase;
        this.hud_panel_open = hud_panel_is_open;
    }
    
    public XWalkView getBrowser() {
        return mXwalkView;
    }
    
    public boolean getLoadBrowser(){
    	return loadBrowser;
    }
    
    public XWalkView getBrowserOrbit() {
        return mXwalkViewOrbit;
    }
    
    public boolean getLoadBrowserOrbit(){
    	return loadBrowserOrbit;
    }

    public Simulator getSim() {
        return sim;
    }

	public ReaderDbHelper getDbHelp() {
		return db_help;
	}

	public SQLiteDatabase getDb() {
		return db;
	}
	
	public boolean getHudPanelOpen(){
		return hud_panel_open;
	}


}
