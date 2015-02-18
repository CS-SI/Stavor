package cs.si.stavor;

import java.util.ArrayList;

//import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import cs.si.stavor.dialogs.ErrorDialogFragment;
import cs.si.stavor.fragments.RetainedFragment;
import cs.si.stavor.StavorApplication.TrackerName;
import cs.si.stavor.app.Installer;
import cs.si.stavor.app.OrekitInit;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.app.RatingSystem;
import cs.si.stavor.simulator.Simulator;
import cs.si.stavor.web.MyResourceClient;
import cs.si.stavor.web.MyUIClient;
import cs.si.stavor.web.StavorInterface;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Main activity of the application, managing persistent objects and fragments
 * @author Xavier Gibert
 *
 */
public class MainActivity extends Activity {
	
	/**
	 * Browser object
	 */
	private XWalkView mXwalkView;

    /**
     * Used to store information during application restart due to configuration
     * changes
     */
    private RetainedFragment dataFragment;

    /**
     * Simulator object
     */
    private Simulator simulator;

    /**
     * Returns the simulator object
     * @return
     */
    public Simulator getSimulator(){
        return simulator;
    }
    /**
     * Returns the simulator object
     * @return
     */
    public XWalkView getBrowser(){
        return mXwalkView;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Installer.installApkData(this);
		
		//Initialize Orekit with the data files
		OrekitInit.init(Installer.getOrekitDataRoot(this));
		
		//Configure application window
		//requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		//setProgressBarVisibility(true);

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();

            Simulator simu = new Simulator(this);

            dataFragment.setData(
                    simu
            );

            RatingSystem.verify(this);
        }

        // the data is available in dataFragment.getData()
        this.simulator = dataFragment.getSim();


        // turn on debugging
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, Parameters.App.debug);

        mXwalkView = (XWalkView) findViewById(R.id.activity_main);
		//mXwalkView.setBackgroundResource(R.color.black);
        mXwalkView.setBackgroundColor(0x00000000);
        mXwalkView.setResourceClient(new MyResourceClient(mXwalkView));
        mXwalkView.setUIClient(new MyUIClient(mXwalkView));
        mXwalkView.clearCache(true);

    	mXwalkView.addJavascriptInterface(
                new StavorInterface(mXwalkView, simulator),
                "Android");
    	
    	mXwalkView.load(Parameters.Web.STARTING_PAGE,null);
	}
	
	private void launchMarket() {
		//********** Google Analytics ***********
        // Get tracker.
        Tracker t = ((StavorApplication) getApplication()).getTracker(
            TrackerName.APP_TRACKER);
        t.setScreenName("Menu");
        t.send(new HitBuilders.EventBuilder()
        	.setCategory("Menu")
        	.setAction("Rate")
        	.setLabel("Rate")
        	.setValue(1)
        	.build());
        //***************************************
        
	    Uri uri = Uri.parse("market://details?id=" + getPackageName());
	    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
	    try {
	        startActivity(myAppLinkToMarket);
	    } catch (ActivityNotFoundException e) {
	        Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
	    }
	}

    /**
     * Displays an error dialog
     * @param message
     * @param canIgnore
     */
    public void showErrorDialog(String message, boolean canIgnore) {
        DialogFragment newFragment = ErrorDialogFragment.newInstance(message, canIgnore);
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "error");
    }

    Dialog rate_dialog;
    public void setRateDialog(Dialog dialog){
    	rate_dialog = dialog;
    }
    
    @Override
    protected void onPause() {//Pause simulator and browser
        super.onPause();
        if(rate_dialog!=null){
        	rate_dialog.dismiss();
        }
        if (mXwalkView != null) {
            mXwalkView.pauseTimers();
            mXwalkView.onHide();
        }
    }

    @Override
    protected void onResume() {//Resume browser
        super.onResume();
        if (mXwalkView != null) {
            mXwalkView.resumeTimers();
            mXwalkView.onShow();
        }
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//super.onActivityResult(requestCode, resultCode, data);
        if (mXwalkView != null) {
            mXwalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXwalkView != null) {
            mXwalkView.onNewIntent(intent);
        }
    	//super.onNewIntent(intent);
    }
	
	@Override
    public void onDestroy() {//Disconnect simulator, close database and browser
        super.onDestroy();
        // store the data in the fragment
        if(isFinishing()){
        	simulator.disconnect();
            //((StavorApplication)getApplication()).db_help.close();
        }else{
        	dataFragment.setData(
        			this.simulator
        			);
        	//Recycle background [NOT USED SINCE NEW ACTIVITY SPLASH SCREEN BUT SAVE FOR FUTURE IMPLEMENTATIONS]
            /*BitmapDrawable bd = (BitmapDrawable)getWindow().getDecorView().getBackground();
            Bitmap mBitmap = bd.getBitmap();
            if (mBitmap != null && !mBitmap.isRecycled()) {
            	getWindow().getDecorView().setBackgroundResource(0);
                bd.setCallback(null);
                mBitmap.recycle();
                mBitmap = null; 
            }*/
        }
        //Prevent onDestroy to avoid exception of illegalArgument: receiver not registered
        if (mXwalkView != null) {
            mXwalkView.onDestroy();
        }
    }

}
