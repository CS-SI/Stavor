package cs.si.stavor.fragments;

import cs.si.stavor.database.MissionReaderDbHelper;
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
    private Simulator sim;
    private MissionReaderDbHelper db_help;
    private SQLiteDatabase db;
    private boolean hud_panel_open;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(Simulator data, MissionReaderDbHelper missionReaderDbHelper, SQLiteDatabase sqLiteDatabase, boolean hud_panel_is_open) {
    	this.sim = data;
        this.db_help = missionReaderDbHelper;
        this.db = sqLiteDatabase;
        this.hud_panel_open = hud_panel_is_open;
    }

    public Simulator getSim() {
        return sim;
    }

	public MissionReaderDbHelper getDbHelp() {
		return db_help;
	}

	public SQLiteDatabase getDb() {
		return db;
	}
	
	public boolean getHudPanelOpen(){
		return hud_panel_open;
	}


}
