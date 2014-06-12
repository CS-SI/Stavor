package cs.si.stavor.fragments;


import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.web.MyResourceClient;
import cs.si.stavor.web.MyUIClient;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.simulator.Simulator;
import cs.si.stavor.web.WebAppInterface;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

/**
 * Fragment with the visualization browser and hud panel
 * @author Xavier Gibert
 *
 */
@SuppressWarnings("deprecation")
public final class HudFragment extends Fragment {
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
	LinearLayout browserLayout, slider_content;
	Button views_menu;
	SlidingDrawer drawer;
	
	/**
	 * WebView from XWalk project to increase compatibility of WebGL
	 */
    public XWalkView mXwalkView;
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.hud_display, container,
				false);
		
		((MainActivity)getActivity()).showTutorialDisplay();
		
		//Browser
		//Initialize WebView
		if(mXwalkView==null){
			mXwalkView = new XWalkView(this.getActivity().getApplicationContext(), this.getActivity());
			mXwalkView.setBackgroundResource(R.color.black);
			mXwalkView.setResourceClient(new MyResourceClient(mXwalkView));
	        mXwalkView.setUIClient(new MyUIClient(mXwalkView));
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
		
    	XWalkSettings browserSettings = mXwalkView.getSettings();
    	
    	browserSettings.setJavaScriptEnabled(true);
    	browserSettings.setUseWideViewPort(false);
    	//browserSettings.setLoadWithOverviewMode(true);
    	browserSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
    	browserSettings.setAllowUniversalAccessFromFileURLs(true);
    	//browserSettings.setBuiltInZoomControls(true);
    	//browserSettings.setDisplayZoomControls(true);
    	//browserSettings.setSupportZoom(true);, OnMenuItemClickListener
    	
    	mXwalkView.clearCache(true);
    	
    	simulator = ((MainActivity)getActivity()).getSimulator();
    	simulator.setHudView(rootView, mXwalkView);
    	
    	mXwalkView.addJavascriptInterface(new WebAppInterface(getActivity(), simulator.getSimulationResults()), "Android");
    	
    	
    	browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	LayoutParams browser_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	mXwalkView.setLayoutParams(browser_params);
    	
    	browserLayout.addView(mXwalkView);
    	
    	//browser.load(Parameters.Web.STARTING_PAGE,null);
    	
    	views_menu = (Button) rootView.findViewById(R.id.buttonMissionNew);
    	views_menu.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				showPopup(arg0);
			}
    		
    	});
    	
    	//Play/Pause/Stop buttons
    	ImageButton but_play = (ImageButton)rootView.findViewById(R.id.imageButtonPlay);
    	ImageButton but_stop = (ImageButton)rootView.findViewById(R.id.imageButtonStop);
    	simulator.setControlButtons(but_play,but_stop);
    	simulator.setCorrectSimulatorControls();
    	
    	/*if(Parameters.Hud.start_panel_open)
    		drawer.getHandle().callOnClick();*/

		//needs to have browser defined but not loaded yet
    	rootView.post(new Runnable()
    	{
    	    @Override
    	    public void run()
    	    {
    	    	if(((MainActivity)getActivity()).getHudPanelOpen())
    	    		drawer.open();
    	        mXwalkView.load(Parameters.Web.STARTING_PAGE,null);
    	    }
    	});
    	
		return rootView;
	}
	
	@Override
	public void onDestroyView(){
		simulator.setBrowserLoaded(false);
		simulator.pause();
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
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
                switch (item.getItemId()) {
                    case R.id.menu_views_earth:
                    	command = getString(R.string.key_views_earth);
                    	break;
                    case R.id.menu_views_sun:
                    	command = getString(R.string.key_views_sun);
                    	break;
                    case R.id.menu_views_ref_frame_xyz:
                    	command = getString(R.string.key_views_ref_frame_xyz);
                    	break;
                    case R.id.menu_views_ref_frame_x:
                    	command = getString(R.string.key_views_ref_frame_x);
                    	break;
                    case R.id.menu_views_ref_frame_xx:
                    	command = getString(R.string.key_views_ref_frame_xx);
                    	break;
                    case R.id.menu_views_ref_frame_y:
                    	command = getString(R.string.key_views_ref_frame_y);
                    	break;
                    case R.id.menu_views_ref_frame_yy:
                    	command = getString(R.string.key_views_ref_frame_yy);
                    	break;
                    case R.id.menu_views_ref_frame_z:
                    	command = getString(R.string.key_views_ref_frame_z);
                    	break;
                    case R.id.menu_views_ref_frame_zz:
                    	command = getString(R.string.key_views_ref_frame_zz);
                    	break;
                    case R.id.menu_views_spacecraft_xyz:
                    	command = getString(R.string.key_views_spacecraft_xyz);
                    	break;
                    case R.id.menu_views_spacecraft_rear:
                    	command = getString(R.string.key_views_spacecraft_rear);
                    	break;
                    case R.id.menu_views_spacecraft_front:
                    	command = getString(R.string.key_views_spacecraft_front);
                    	break;
                    case R.id.menu_views_spacecraft_top:
                    	command = getString(R.string.key_views_spacecraft_top);
                    	break;
                    case R.id.menu_views_spacecraft_bottom:
                    	command = getString(R.string.key_views_spacecraft_bottom);
                    	break;
                    case R.id.menu_views_spacecraft_left:
                    	command = getString(R.string.key_views_spacecraft_left);
                    	break;
                    case R.id.menu_views_spacecraft_right:
                    	command = getString(R.string.key_views_spacecraft_right);
                    	break;
                    default:
                        return false;
                }
                views_menu.setText(com_view);
                mXwalkView.load("javascript:changeView('"+command+"')", null);
                return true;
            }
        });
        popup.inflate(R.menu.views);
        popup.show();

    }
    
    @Override
	public void onPause() {//Pause simulator and browser
        super.onPause();
        if (mXwalkView != null) {
            mXwalkView.pauseTimers();
            mXwalkView.onHide();
        }
    }

    @Override
	public void onResume() {//Resume browser
        super.onResume();
        if (mXwalkView != null) {
            mXwalkView.resumeTimers();
            mXwalkView.onShow();
        }
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        if (mXwalkView != null) {
            mXwalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        if (mXwalkView != null) {
            mXwalkView.onNewIntent(intent);
        }
    }*/
	
	@Override
    public void onDestroy() {//Disconnect simulator, close database and browser
        super.onDestroy();
        // store the data in the fragment
        //XWalk
        /*if (mXwalkView != null) {
            mXwalkView.onDestroy();
        }*/
    }
	
	@Override
	public void onDetach() {
		//XWalk
        if (mXwalkView != null) {
            mXwalkView.onDestroy();
        }
	    super.onDetach();
	}
}
