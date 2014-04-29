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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Toast;

/**
 * A sphere fragment containing a web view.
 */
public final class HudFragment extends Fragment {
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
	public static HudFragment newInstance(int sectionNumber, ModelSimulation simulation) {	
		HudFragment fragment = new HudFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putSerializable(ARG_SIM_OBJ, (Serializable) simulation);
		fragment.setArguments(args);
		return fragment;
	}

	public HudFragment() {
	}
	
	public ModelSimulation sim;
	XWalkView browser;
	LinearLayout browserLayout, slider_content;

	@SuppressWarnings("deprecation")
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.hud_display, container,
				false);
		
		SlidingDrawer drawer = (SlidingDrawer) rootView.findViewById(R.id.slidingDrawer1);
        drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            public void onDrawerOpened() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(browser.getLayoutParams());
            	if(getResources().getConfiguration().orientation==android.content.res.Configuration.ORIENTATION_PORTRAIT){
                	layoutParams.height = browser.getHeight()-slider_content.getHeight();
                	layoutParams.width = LayoutParams.MATCH_PARENT;
            	}else{
            		layoutParams.width = browser.getWidth()-slider_content.getWidth();
                	layoutParams.height = LayoutParams.MATCH_PARENT;
            	}
            	browser.setLayoutParams(layoutParams);
            }
        });
       
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            public void onDrawerClosed() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(browser.getLayoutParams());
            	layoutParams.height = LayoutParams.MATCH_PARENT;
            	layoutParams.width = LayoutParams.MATCH_PARENT;
            	browser.setLayoutParams(layoutParams);
            }
        });
		
		slider_content = (LinearLayout) rootView.findViewById(R.id.content);
		
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
    	
      	browser.setXWalkWebChromeClient(new org.xwalk.core.client.XWalkDefaultWebChromeClient(rootView.getContext(), browser) {
      		@Override
      		public void onProgressChanged(XWalkView view, int progress) {
      			// Activities and WebViews measure progress with different scales.
      			// The progress meter will automatically disappear when we reach 100%
      			getActivity().setProgress(progress * 100);
      		}
      	});
      	browser.setXWalkClient(new org.xwalk.core.client.XWalkDefaultClient(rootView.getContext(), browser) {
      		public void onReceivedError(XWalkView view, int errorCode, String description, String failingUrl) {
      			Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_LONG).show();
      		}
      	});
    	
    	browser.addJavascriptInterface(new webclient.UAJscriptHandler(null), "unlockingandroid");
    	browser.addJavascriptInterface(new UANOOP() {}, "unlockingandroid");
    	browser.addJavascriptInterface(null, "unlockingandroid");
    	
    	sim = (ModelSimulation) getArguments().getSerializable(ARG_SIM_OBJ);
    	sim.setCurrentView(rootView);
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), sim), "Android");
    	
    	browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	browserLayout.addView(browser);
    	
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
