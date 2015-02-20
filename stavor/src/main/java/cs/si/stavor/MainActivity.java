package cs.si.stavor;

//import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

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
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
	private XWalkView browser;
    private Gson gson = new Gson();

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
        return browser;
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

            Simulator simulator = new Simulator(this);


            XWalkView browser = new XWalkView(this, this);
            // turn on debugging
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, Parameters.App.debug);
            //browser.setBackgroundResource(R.color.black);
            browser.setBackgroundColor(0x00000000);
            browser.setResourceClient(new MyResourceClient(browser));
            browser.setUIClient(new MyUIClient(browser));
            browser.clearCache(true);
            browser.addJavascriptInterface(
                    new StavorInterface(browser, simulator),
                    "Android");
            browser.load(Parameters.Web.STARTING_PAGE, null);

            dataFragment.setData(
                    browser,
                    simulator
            );

            RatingSystem.verify(this);
        }

        //Load Retained Objects
        this.simulator = dataFragment.getSimulator();
        this.browser = dataFragment.getBrowser();

        LinearLayout browserLayout = (LinearLayout) findViewById(R.id.activity_main);
        ViewGroup.LayoutParams browser_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.browser.setLayoutParams(browser_params);
        browserLayout.addView(this.browser);
	}

    public void updateGuiControls() {
        runOnUiThread(new Runnable() {
            public void run() {
                String json_state = gson.toJson(simulator.getControlsStatus());
                browser.evaluateJavascript("global_simulator.updateSimulatorState('"+json_state+"')",null);
            }
        });
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
        if (browser != null) {
            browser.pauseTimers();
            browser.onHide();
        }
    }

    @Override
    protected void onResume() {//Resume browser
        super.onResume();
        if (browser != null) {
            browser.resumeTimers();
            browser.onShow();
        }
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//super.onActivityResult(requestCode, resultCode, data);
        if (browser != null) {
            browser.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (browser != null) {
            browser.onNewIntent(intent);
        }
    	//super.onNewIntent(intent);
    }
	
	@Override
    public void onDestroy() {//Disconnect simulator, close database and browser
        // store the data in the fragment
        if(isFinishing()){
        	simulator.disconnect();
            //Prevent onDestroy to avoid exception of illegalArgument: receiver not registered
            if (browser != null) {
                browser.onDestroy();
            }
        }else{
        	dataFragment.setData(
                    this.browser,
                    this.simulator
            );
            LinearLayout browserLayout = (LinearLayout) findViewById(R.id.activity_main);
            browserLayout.removeView(this.browser);
        }
        super.onDestroy();
    }

}
