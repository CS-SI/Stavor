package fragments;

import java.io.Serializable;

import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import web.WebAppInterface;
import model.ModelSimulation;
import cs.si.satatt.MainActivity;
import cs.si.satatt.Parameters;
import cs.si.satatt.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;

/**
 * A sphere fragment containing a web view.
 */
public final class SphereFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_SIM_OBJ = "simulation_object";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 */
	public static SphereFragment newInstance(int sectionNumber, ModelSimulation simulation) {	
		SphereFragment fragment = new SphereFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putSerializable(ARG_SIM_OBJ, (Serializable) simulation);
		fragment.setArguments(args);
		return fragment;
	}

	public SphereFragment() {
	}
	
	public ModelSimulation sim;
	XWalkView browser;
	LinearLayout commentsLayout;

	@SuppressWarnings("deprecation")
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.sphere_merged, container,
				false);
		
		SlidingDrawer drawer = (SlidingDrawer) rootView.findViewById(R.id.slidingDrawer1);
        drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            public void onDrawerOpened() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(browser.getLayoutParams());
            	layoutParams.height = browser.getLayoutParams().height/2;
            	browser.setLayoutParams(layoutParams);
            }
        });
       
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            public void onDrawerClosed() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(browser.getLayoutParams());
            	layoutParams.height = LayoutParams.MATCH_PARENT;
            	browser.setLayoutParams(layoutParams);
            }
        });
		
		//XWalkView browser = (XWalkView) rootView.findViewById(R.id.xbrowser);
		browser = new XWalkView(this.getActivity().getApplicationContext(), this.getActivity());
		
    	XWalkSettings browserSettings = browser.getSettings();
    	
    	browserSettings.setJavaScriptEnabled(true);
    	browserSettings.setUseWideViewPort(false);
    	//browserSettings.setLoadWithOverviewMode(true);
    	browserSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
    	browserSettings.setAllowUniversalAccessFromFileURLs(true);
    	//browserSettings.setBuiltInZoomControls(true);
    	//browserSettings.setDisplayZoomControls(true);
    	//browserSettings.setSupportZoom(true);
    	
    	browser.clearCache(true);
    	
      	/*browser.setXWalkWebChromeClient(new org.xwalk.core.client.XWalkDefaultWebChromeClient(rootView.getContext(), browser) {
      		public void onProgressChanged(WebView view, int progress) {
      			// Activities and WebViews measure progress with different scales.
      			// The progress meter will automatically disappear when we reach 100%
      			getActivity().setProgress(progress * 100);
      		}
      	});
      	browser.setXWalkClient(new org.xwalk.core.client.XWalkDefaultClient(rootView.getContext(), browser) {
      		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      			Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_LONG).show();
      		}
      	});
    	*/
    	browser.addJavascriptInterface(new webclient.UAJscriptHandler(null), "unlockingandroid");
    	browser.addJavascriptInterface(new UANOOP() {}, "unlockingandroid");
    	browser.addJavascriptInterface(null, "unlockingandroid");
    	
    	sim = (ModelSimulation) getArguments().getSerializable(ARG_SIM_OBJ);
    	sim.setCurrentView(rootView);
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), sim), "Android");
    	commentsLayout=(LinearLayout)rootView.findViewById(R.id.commentsLayout);
    	commentsLayout.addView(browser);
    	
    	browser.loadUrl(Parameters.Web.STARTING_PAGE);
		
		/*TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(
				ARG_SECTION_NUMBER)));*/
		return rootView;
	}
    
    private class UANOOP {
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
