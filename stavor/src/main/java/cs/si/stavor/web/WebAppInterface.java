package cs.si.stavor.web;

import cs.si.stavor.model.ModelSimulation;
import android.app.Activity;
import android.widget.Toast;


import org.xwalk.core.JavascriptInterface;

/**
 * JavaScript-to-Android bridge functions
 * @author Xavier Gibert
 *
 */
public final class WebAppInterface {
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


    /** get model state */
    /*@JavascriptInterface
    public String getStateJSON() {
        return sim.getStateJSON();
    }*/

	public void reconstruct(Activity act,
			ModelSimulation s) {
		activity = act;
        sim = s;
	}
    
    
}