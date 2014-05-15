package fragments;


//import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
//import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import simulator.Simulator;
import web.WebAppInterface;
import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import app.Parameters;

/**
 * A sphere fragment containing a web view.
 */
public final class HudFragment extends Fragment implements OnItemSelectedListener {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 */
	public static HudFragment newInstance(int sectionNumber) {	
		HudFragment fragment = new HudFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public HudFragment() {
	}
	
	private Simulator simulator;
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
		
		browser = ((MainActivity)getActivity()).mXwalkView;
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
    	
    	//XGGDEBUG:XWALK5
    	/*
      	browser.setXWalkWebChromeClient(new org.xwalk.core.XWalkWebChromeClient() {
      		@Override
      		public void onProgressChanged(XWalkView view, int progress) {
      			// Activities and WebViews measure progress with different scales.
      			// The progress meter will automatically disappear when we reach 100%
      			try{
      				getActivity().setProgress(progress * 100);
      			}catch(NullPointerException nulle){
      				
      			}
      		}
      	});*/
      	
      	/*browser.setXWalkClient(new org.xwalk.core.XWalkUIClient(rootView.getContext(), browser) {
      		public void onReceivedError(XWalkView view, int errorCode, String description, String failingUrl) {
      			Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_LONG).show();
      		}
      	});*/
    	
    	/*browser.addJavascriptInterface(new webclient.UAJscriptHandler(null), "unlockingandroid");
    	browser.addJavascriptInterface(new UANOOP() {}, "unlockingandroid");
    	browser.addJavascriptInterface(null, "unlockingandroid");*/
    	
    	simulator = ((MainActivity)getActivity()).getSimulator();
    	simulator.setHudView(rootView, browser);
    	
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), simulator.getSimulationResults()), "Android");
    	
    	
    	browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	LayoutParams browser_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	browser.setLayoutParams(browser_params);
    	
    	browserLayout.addView(browser);
    	
    	//browser.loadUrl(Parameters.Web.STARTING_PAGE);
    	browser.load(Parameters.Web.STARTING_PAGE,null);
		
		/*TextView textView = (TextView) rootView
				.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(
				ARG_SECTION_NUMBER)));*/
    	
    	Spinner spinner = (Spinner) rootView.findViewById(R.id.spinnerView);
    	spinner.setOnItemSelectedListener(this);
    	
    	//Play/Pause/Stop buttons
    	ImageButton but_play = (ImageButton)rootView.findViewById(R.id.imageButtonPlay);
    	ImageButton but_stop = (ImageButton)rootView.findViewById(R.id.imageButtonStop);
    	simulator.setControlButtons(but_play,but_stop);
    	simulator.setCorrectSimulatorControls();
    	
    	if(Parameters.Hud.start_panel_open)
    		drawer.getHandle().callOnClick();
		return rootView;
	}
	
	@Override
	public void onDestroyView(){
		simulator.setBrowserLoaded(false);
		super.onDestroyView();
	}

	/*private class UANOOP {
    }*/

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	public void updateFPS(String stats) {
		// TODO Auto-generated method stub
		TextView fps = ((TextView) this.getView().findViewById(R.id.textViewFPS));
		fps.setText(stats);
		fps.setAlpha((float)1.0);
	}
	
	private boolean first = true;
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String sel_view = (String) parent.getItemAtPosition(pos);
        if(!first)
        	//browser.loadUrl("javascript:changeView('"+sel_view+"')");
        	browser.load("javascript:changeView('"+sel_view+"')", null);
        first=false;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
 


}
