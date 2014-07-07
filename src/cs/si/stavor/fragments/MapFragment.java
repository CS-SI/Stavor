package cs.si.stavor.fragments;


import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.model.Browsers;
import cs.si.stavor.simulator.Simulator;
import cs.si.stavor.web.WebAppInterface;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Fragment with the visualization browser for the map
 * @author Xavier Gibert
 *
 */
public final class MapFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 */
	public static MapFragment newInstance(int sectionNumber) {	
		MapFragment fragment = new MapFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public MapFragment() {
	}
	
	private Simulator simulator;
	LinearLayout browserLayout;
	
	/**
	 * WebView from XWalk project to increase compatibility of WebGL
	 */
    private WebView browser;
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map_display, container,
				false);
		//XGGDEBUG:Implemet 
		//TODO
		//((MainActivity)getActivity()).showTutorialMap();
		
		//Browser
		/*if(mXwalkView==null){
			mXwalkView = ((MainActivity)getActivity()).getBrowserMap();
		}*/
		
		browser = new WebView(getActivity().getApplicationContext());
		//mXwalkView.setBackgroundResource(R.color.black);
		browser.setBackgroundColor(0x00000000);
		browser.setWebViewClient(new WebViewClient());
		//xwalkViewMap.setResourceClient(new MyResourceClient(xwalkViewOrbit));
        //xwalkViewMap.setUIClient(new MyUIClient(xwalkViewOrbit));
		browser.clearCache(true);
        
        WebSettings browserSettingsMap = browser.getSettings();
    	
    	browserSettingsMap.setJavaScriptEnabled(true);
    	browserSettingsMap.setUseWideViewPort(false);
    	browserSettingsMap.setAllowFileAccessFromFileURLs(true);
    	browserSettingsMap.setAllowUniversalAccessFromFileURLs(true);
    	

    	simulator = ((MainActivity)getActivity()).getSimulator();
    	
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), simulator.getSimulationResults()), "Android");
    	
		
    	simulator.setHudView(Browsers.Map,rootView, browser);
    	//XGGDEBUG:MODIFY method
    	//TODO
    	
    	browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	LayoutParams browser_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	browser.setLayoutParams(browser_params);
    	
    	browserLayout.addView(browser);
    	
    	
    	//Play/Pause/Stop buttons
    	ImageButton but_play = (ImageButton)rootView.findViewById(R.id.imageButtonPlay);
    	ImageButton but_stop = (ImageButton)rootView.findViewById(R.id.imageButtonStop);
    	simulator.setControlButtons(but_play,but_stop);
    	simulator.setCorrectSimulatorControls();
    	
    	ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBrowser);
    	FrameLayout progressBarLayout = (FrameLayout) rootView.findViewById(R.id.frameLayoutProgress);
    	progressBar.setProgress(10);
    	((MainActivity)getActivity()).setBrowserProgressBarMap(progressBar,progressBarLayout);
    	
		//needs to have browser defined but not loaded yet
    	rootView.post(new Runnable()
    	{
    	    @Override
    	    public void run()
    	    {
    	    	/*
    	    	if(((MainActivity)getActivity()).getLoadBrowserFlagMap()){
    	    		//mXwalkView.load(Parameters.Web.STARTING_PAGE,null);
    	    		//mXwalkView.load("javascript:showLoadingScreen()",null);
    	    		
    	    		mXwalkView.loadUrl("javascript:reloadModel()");
    	    		((MainActivity)getActivity()).resetLoadBrowserFlagMap();
    	    	}else{
    	    		mXwalkView.loadUrl("javascript:setLoaded()");
    	    	}*/

            	browser.loadUrl(Parameters.Web.STARTING_PAGE_MAP);
    	    }
    	});
    	
		return rootView;
	}
	
	@Override
	public void onDestroyView(){
		simulator.setBrowserLoaded(false);
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }
	
    @Override
	public void onPause() {//Pause simulator and browser
        super.onPause();
        if(simulator!=null){
        	simulator.temporaryPause();
        }
        if (browser != null) {
            //mXwalkView.pauseTimers();
            //mXwalkView.onHide();
        }
    }

    @Override
	public void onResume() {//Resume browser
        super.onResume();
        if (browser != null) {
            //mXwalkView.resumeTimers();
            //mXwalkView.onShow();
        }
        if(simulator!=null){
        	simulator.resumeTemporaryPause();
        }
    }
	
	@Override
	public void onDetach() {
		((MainActivity)getActivity()).resetBrowserProgressBarOrbit();
		simulator.clearHud();
		//XWalk
        if (browser != null) {
            //mXwalkView.onDestroy();
			//System.gc();
        	browserLayout.removeView(browser);
        }
        //unbindDrawables(getView());
	    super.onDetach();
	}
	
	/*private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        ((ViewGroup) view).removeAllViews();
        }
    }*/
	
}
