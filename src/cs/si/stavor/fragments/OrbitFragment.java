package cs.si.stavor.fragments;


import org.xwalk.core.XWalkView;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.model.Browsers;
import cs.si.stavor.simulator.Simulator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

/**
 * Fragment with the visualization browser for the orbit
 * @author Xavier Gibert
 *
 */
public final class OrbitFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 */
	public static OrbitFragment newInstance(int sectionNumber) {	
		OrbitFragment fragment = new OrbitFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public OrbitFragment() {
	}
	
	private Simulator simulator;
	LinearLayout browserLayout, slider_content;
	Button views_menu;
	SlidingDrawer drawer;
	
	/**
	 * WebView from XWalk project to increase compatibility of WebGL
	 */
    private XWalkView mXwalkView;
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.orb_display, container,
				false);
		
		((MainActivity)getActivity()).refreshActionBarIcons();
		
		//((MainActivity)getActivity()).showTutorialOrbit();
		
		//Browser
		if(mXwalkView==null){
			mXwalkView = ((MainActivity)getActivity()).getBrowserOrbit();
		}
		//Hud Panel
		drawer = (SlidingDrawer) rootView.findViewById(R.id.slidingDrawer1);
        drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
            public void onDrawerOpened() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mXwalkView.getLayoutParams());
            	if(getResources().getConfiguration().orientation==android.content.res.Configuration.ORIENTATION_PORTRAIT){
                	layoutParams.height = mXwalkView.getHeight()-slider_content.getHeight();
                	layoutParams.width = LayoutParams.MATCH_PARENT;
            	}else{
            		layoutParams.width = mXwalkView.getWidth()-slider_content.getWidth();
                	layoutParams.height = LayoutParams.MATCH_PARENT;
            	}
            	mXwalkView.setLayoutParams(layoutParams);
            	((MainActivity)getActivity()).setHudPanelOpen(true);
            }
        });
       
        drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
            public void onDrawerClosed() {
            	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mXwalkView.getLayoutParams());
            	layoutParams.height = LayoutParams.MATCH_PARENT;
            	layoutParams.width = LayoutParams.MATCH_PARENT;
            	mXwalkView.setLayoutParams(layoutParams);
            	((MainActivity)getActivity()).setHudPanelOpen(false);
            }
        });
        
		slider_content = (LinearLayout) rootView.findViewById(R.id.content);
		
		TextView fps = ((TextView) rootView.findViewById(R.id.textViewFPS));
		fps.setAlpha((float)0.0);
		
    	simulator = ((MainActivity)getActivity()).getSimulator();
    	simulator.setHudView(Browsers.Orbit,rootView, mXwalkView);
    	
    	browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	LayoutParams browser_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	mXwalkView.setLayoutParams(browser_params);
    	
    	browserLayout.addView(mXwalkView);
    	
    	views_menu = (Button) rootView.findViewById(R.id.buttonMissionNew);
    	views_menu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				showPopup(arg0);
			}
    		
    	});

    	views_menu.setText(titleOfViewId(((StavorApplication)getActivity().getApplication()).modelOrbitViewId));
    	
    	//Play/Pause/Stop buttons
    	ImageButton but_play = (ImageButton)rootView.findViewById(R.id.imageButtonPlay);
    	ImageButton but_stop = (ImageButton)rootView.findViewById(R.id.imageButtonStop);
    	simulator.setControlButtons(but_play,but_stop);
    	simulator.setCorrectSimulatorControls();
    	
    	ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarBrowser);
    	FrameLayout progressBarLayout = (FrameLayout) rootView.findViewById(R.id.frameLayoutProgress);
    	progressBar.setProgress(0);
    	((MainActivity)getActivity()).setBrowserProgressBarOrbit(progressBar,progressBarLayout);
    	
		//needs to have browser defined but not loaded yet
    	rootView.post(new Runnable()
    	{
    	    @Override
    	    public void run()
    	    {
    	    	if(((MainActivity)getActivity()).getHudPanelOpen())
    	    		drawer.open();
    	    	if(((MainActivity)getActivity()).getLoadBrowserFlagOrbit()){
    	    		//mXwalkView.load(Parameters.Web.STARTING_PAGE,null);
    	    		//mXwalkView.load("javascript:showLoadingScreen()",null);
    	    		
    	    		mXwalkView.load("javascript:reloadModel()",null);
    	    		((MainActivity)getActivity()).resetLoadBrowserFlagOrbit();
    	    	}else{
    	    		mXwalkView.load("javascript:setLoaded()",null);
    	    	}
    	    }
    	});
    	
		return rootView;
	}

	/**
	 * Updates the FPS stats of the visualization
	 * @param stats
	 */
	public void updateFPS(String stats) {
		TextView fps = ((TextView) this.getView().findViewById(R.id.textViewFPS));
		fps.setText(stats);
		fps.setAlpha((float)1.0);
	}
	

	private String titleOfViewId(int id){
		switch (id) {
	        case R.id.menu_orbviews_free:
	        	return getString(R.string.menu_orbviews_free);
	        case R.id.menu_orbviews_locked:
	        	return getString(R.string.menu_orbviews_locked);
	        default:
	        	return getString(R.string.menu_orbviews_free);
	    }
	}
	
	/**
	 * Shows the visualization Views menu
	 * @param v
	 */
    private void showPopup(View v) {
    	PopupMenu popup = new PopupMenu(getActivity(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        	@Override
            public boolean onMenuItemClick(MenuItem item) {
        		String com_view = (String)item.getTitle();
        		String command;
            	((StavorApplication)getActivity().getApplication()).modelOrbitViewId = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.menu_orbviews_free:
                    	command = getString(R.string.key_orbviews_free);
                    	break;
                    case R.id.menu_orbviews_locked:
                    	command = getString(R.string.key_orbviews_locked);
                    	break;
                    default:
                        return false;
                }
                views_menu.setText(com_view);
                mXwalkView.load("javascript:changeView('"+command+"')", null);
                return true;
            }
        });
        popup.inflate(R.menu.views_orb);
        popup.show();

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
        if (mXwalkView != null) {
            //mXwalkView.pauseTimers();
            //mXwalkView.onHide();
        }
    }

    @Override
	public void onResume() {//Resume browser
        super.onResume();
        if (mXwalkView != null) {
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
        if (mXwalkView != null) {
            //mXwalkView.onDestroy();
			//System.gc();
        	browserLayout.removeView(mXwalkView);
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
