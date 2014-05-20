package fragments;


import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import simulator.Simulator;
import web.WebAppInterface;
import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import app.Parameters;

/**
 * A sphere fragment containing a web view.
 */
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
	XWalkView browser;
	LinearLayout browserLayout, slider_content;
	Button views_menu;
	
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
    	//browserSettings.setSupportZoom(true);, OnMenuItemClickListener
    	
    	browser.clearCache(true);
    	
    	simulator = ((MainActivity)getActivity()).getSimulator();
    	simulator.setHudView(rootView, browser);
    	
    	browser.addJavascriptInterface(new WebAppInterface(getActivity(), simulator.getSimulationResults()), "Android");
    	
    	
    	browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	LayoutParams browser_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	browser.setLayoutParams(browser_params);
    	
    	browserLayout.addView(browser);
    	
    	//browser.loadUrl(Parameters.Web.STARTING_PAGE);
    	browser.load(Parameters.Web.STARTING_PAGE,null);
    	
    	views_menu = (Button) rootView.findViewById(R.id.buttonViews);
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
    	
    	if(Parameters.Hud.start_panel_open)
    		drawer.getHandle().callOnClick();
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

	public void updateFPS(String stats) {
		// TODO Auto-generated method stub
		TextView fps = ((TextView) this.getView().findViewById(R.id.textViewFPS));
		fps.setText(stats);
		fps.setAlpha((float)1.0);
	}
	
	//private boolean first = true;
/*	public void onItemSelected(AdapterView<?> parent, View view, 
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
    }*/
 
    private void showPopup(View v) {
    	PopupMenu popup = new PopupMenu(getActivity(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
        	@Override
            public boolean onMenuItemClick(MenuItem item) {
        		String com_view = (String)item.getTitle();
                switch (item.getItemId()) {
                    case R.id.menu_views_earth:
                    	break;
                    case R.id.menu_views_sun:
                    	break;
                    case R.id.menu_views_ref_frame_xyz:
                    	break;
                    case R.id.menu_views_ref_frame_x:
                    	break;
                    case R.id.menu_views_ref_frame_xx:
                    	break;
                    case R.id.menu_views_ref_frame_y:
                    	break;
                    case R.id.menu_views_ref_frame_yy:
                    	break;
                    case R.id.menu_views_ref_frame_z:
                    	break;
                    case R.id.menu_views_ref_frame_zz:
                    	break;
                    case R.id.menu_views_spacecraft_xyz:
                    	com_view = "S/C-" + com_view;
                    	break;
                    case R.id.menu_views_spacecraft_rear:
                    	com_view = "S/C-" + com_view;
                    	break;
                    case R.id.menu_views_spacecraft_front:
                    	com_view = "S/C-" + com_view;
                    	break;
                    case R.id.menu_views_spacecraft_top:
                    	com_view = "S/C-" + com_view;
                    	break;
                    case R.id.menu_views_spacecraft_bottom:
                    	com_view = "S/C-" + com_view;
                    	break;
                    case R.id.menu_views_spacecraft_left:
                    	com_view = "S/C-" + com_view;
                    	break;
                    case R.id.menu_views_spacecraft_right:
                    	com_view = "S/C-" + com_view;
                    	break;
                    default:
                        return false;
                }
                views_menu.setText(com_view);
                browser.load("javascript:changeView('"+com_view+"')", null);
                return true;
            }
        });
        popup.inflate(R.menu.views);
        popup.show();

    }
}
