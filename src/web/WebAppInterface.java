package web;

import cs.si.satatt.R;
import fragments.HudFragment;
import model.ModelSimulation;
import android.app.Activity;
import android.widget.Toast;

//import org.chromium.content.browser.JavascriptInterface;
import org.xwalk.core.JavascriptInterface;

//XGGDEBUG:TEST-WAITING use ( org.xwalk.core.JavascriptInterface ) when the code is merged in: https://github.com/crosswalk-project/crosswalk/pull/1876/files
//import android.webkit.JavascriptInterface; Used for WebView not XWalkView

public class WebAppInterface {
    Activity activity;
    private ModelSimulation sim;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Activity a, ModelSimulation s) {
        activity = a;
        sim = s;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(activity.getApplicationContext(), toast, Toast.LENGTH_LONG).show();
    }
    
    /** Set loading progress (0-100) from the web page */
    @JavascriptInterface
    public void setProgress(final int progress) {
    	activity.runOnUiThread( new Runnable() {
	        public void run() {
            	activity.setProgress(progress * 100);
	        }
	    });
    }
    
    /** Update the stats of the web page */
    @JavascriptInterface
    public void updateFPS(final String stats) {
    	activity.runOnUiThread( new Runnable() {
	        public void run() {
	        	try{
	        		((HudFragment)activity.getFragmentManager().findFragmentById(R.id.container)).updateFPS(stats);
	        	}catch(Exception e){
	        		
	        	}
	        }
	    });
    }
    
    /** get model initialization */
    @JavascriptInterface
    public String getInitializationJSON() {
        return sim.getInitializationJSON();
    }
    
    /** get model staten */
    @JavascriptInterface
    public String getStateJSON() {
        return sim.getStateJSON();
    }
    
    
}