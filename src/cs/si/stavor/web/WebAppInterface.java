package cs.si.stavor.web;

import cs.si.stavor.MainActivity;
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

    /** Instantiate the interface and set the context */
    public WebAppInterface(Activity a) {
        activity = a;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(activity.getApplicationContext(), toast, Toast.LENGTH_LONG).show();
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


	public void reconstruct(Activity act,
			ModelSimulation s) {
		activity = act;
	}
    
    
}