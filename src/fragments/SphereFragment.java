package fragments;

import web.WebAppInterface;
import model.ModelSimulation;
import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
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

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SphereFragment newInstance(int sectionNumber) {	
		SphereFragment fragment = new SphereFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SphereFragment() {
	}
	
	public ModelSimulation sim;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.sphere, container,
				false);
		
		WebView browser = (WebView) rootView.findViewById(R.id.browser);
    	
    	WebSettings browserSettings = browser.getSettings();
    	
    	browserSettings.setJavaScriptEnabled(true);
    	browserSettings.setUseWideViewPort(false);
    	browserSettings.setLoadWithOverviewMode(true);
    	browserSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
    	browserSettings.setAllowUniversalAccessFromFileURLs(true);
    	//browserSettings.setBuiltInZoomControls(true);
    	//browserSettings.setDisplayZoomControls(true);
    	//browserSettings.setSupportZoom(true);
    	
    	browser.clearCache(true);
    	
      	browser.setWebChromeClient(new webclient.UAChrome(null) {
      		public void onProgressChanged(WebView view, int progress) {
      			// Activities and WebViews measure progress with different scales.
      			// The progress meter will automatically disappear when we reach 100%
      			getActivity().setProgress(progress * 100);
      		}
      	});
      	browser.setWebViewClient(new webclient.UAWebViewClient(null) {
      		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      			Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_LONG).show();
      		}
      	});
    	
    	browser.addJavascriptInterface(new webclient.UAJscriptHandler(null), "unlockingandroid");
    	browser.addJavascriptInterface(new UANOOP() {}, "unlockingandroid");
    	browser.addJavascriptInterface(null, "unlockingandroid");
    	
    	sim = new ModelSimulation(container.getContext());
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), sim), "Android");
    	
    	browser.loadUrl(STARTING_PAGE);
		
		/*TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(
				ARG_SECTION_NUMBER)));*/
		return rootView;
	}
	
	public static final String STARTING_PAGE = "file:///android_asset/index.html";
    
    private class UANOOP {
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
