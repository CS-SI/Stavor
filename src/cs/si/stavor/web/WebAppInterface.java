package cs.si.stavor.web;

import cs.si.satcor.MainActivity;
import cs.si.satcor.StavorApplication;
import cs.si.stavor.model.ModelSimulation;
import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;


/**
 * JavaScript-to-Android bridge functions
 * @author Xavier Gibert
 *
 */
public final class WebAppInterface {
    Activity activity;
    ModelSimulation sim;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Activity a, ModelSimulation simu) {
        activity = a;
        sim = simu;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(activity.getApplicationContext(), toast, Toast.LENGTH_LONG).show();
    }
    
    /** store new map zoom */
    @JavascriptInterface
    public void storeNewZoom(int zoom) {
        ((StavorApplication)activity.getApplication()).zoom = zoom;
    }
    
    /** Set loading progress (0-100) from the web page */
    @JavascriptInterface
    public void setProgressMap(final int progress) {
    	if(progress==100)
    		((MainActivity)activity).getSimulator().setBrowserLoaded(true);
    	activity.runOnUiThread( new Runnable() {
	        public void run() {
	        	((MainActivity)activity).setBrowserProgressValueMap(progress * 100);
            	//activity.setProgress(progress * 100);
	        }
	    });
    }


    /** get orbit model initialization */
    @JavascriptInterface
    public String getInitializationMapJSON() {
        return sim.getInitializationMapJSON();
    }
    
	public void reconstruct(Activity act,
			ModelSimulation s) {
		activity = act;
	}
    
    
}