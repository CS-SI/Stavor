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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A sphere fragment containing a web view.
 */
public final class SphereFullFragment extends Fragment {
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
	public static SphereFullFragment newInstance(int sectionNumber, ModelSimulation simulation) {	
		SphereFullFragment fragment = new SphereFullFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putSerializable(ARG_SIM_OBJ, (Serializable) simulation);
		fragment.setArguments(args);
		return fragment;
	}

	public SphereFullFragment() {
	}
	
	public ModelSimulation sim;
	private XWalkView browser;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.hud, container,
				false);
		
		TextView fps = ((TextView) rootView.findViewById(R.id.textViewFPS));
		fps.setAlpha((float)0.0);
		
		Button buttonView = ((Button) rootView.findViewById(R.id.buttonView));
		buttonView.setOnClickListener(new View.OnClickListener() {
		   @Override
		   public void onClick(View v) {
		      // button 1 was clicked!
			   view_mode = view_mode +1;
				if(view_mode>=7)
					view_mode=0;
				Button bview = ((Button) v);
				switch(view_mode){
					case 0:
						bview.setText("XYZ");
						break;
					case 1:
						bview.setText("+X");
						break;
					case 2:
						bview.setText("-X");
						break;
					case 3:
						bview.setText("+Y");
						break;
					case 4:
						bview.setText("-Y");
						break;
					case 5:
						bview.setText("+Z");
						break;
					case 6:
						bview.setText("-Z");
						break;
					default:
						bview.setText("XYZ");
						break;
				}
				browser.loadUrl("javascript:changeView("+view_mode+")");
		   }
		  });
		
		//browser = (WebView) rootView.findViewById(R.id.browser);
		//browser.setBackgroundResource(R.color.black);
		browser = (XWalkView)rootView.findViewById(R.id.browser);
		
    	
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
    	
    	/*browser.setWebChromeClient(new webclient.UAChrome(null) {
      		public void onProgressChanged(WebView view, int progress) {
      			// Activities and WebViews measure progress with different scales.
      			// The progress meter will automatically disappear when we reach 100%
      			getActivity().setProgress(progress * 50);
      		}
      	});
      	browser.setWebViewClient(new webclient.UAWebViewClient(null) {
      		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      			Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_LONG).show();
      		}
      	});
    	
    	browser.setWebChromeClient(new webclient.UAChrome(null));
    	browser.setWebViewClient(new webclient.UAWebViewClient(null));
    	*/
    	browser.addJavascriptInterface(new webclient.UAJscriptHandler(null), "unlockingandroid");
    	browser.addJavascriptInterface(new UANOOP() {}, "unlockingandroid");
    	browser.addJavascriptInterface(null, "unlockingandroid");
    	
    	//sim = new ModelSimulation(container.getContext());
    	sim = (ModelSimulation) getArguments().getSerializable(ARG_SIM_OBJ);
    	sim.setCurrentView(rootView);
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), sim), "Android");
    	
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
	
	private int view_mode = 0;

	public void updateFPS(String stats) {
		// TODO Auto-generated method stub
		TextView fps = ((TextView) this.getView().findViewById(R.id.textViewFPS));
		fps.setText(stats);
		fps.setAlpha((float)1.0);
	}
}
