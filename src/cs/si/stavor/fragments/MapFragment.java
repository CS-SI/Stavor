package cs.si.stavor.fragments;


import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.simulator.Simulator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * Fragment with the visualization browser for the orbit
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
	
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.map, container,
				false);
		
		((MainActivity)getActivity()).showTutorialOrbit();
		
		
		
    	simulator = ((MainActivity)getActivity()).getSimulator();
    	
    	ResourceProxyImpl mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        MapView mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        //Disable hardware acceleration for the map view
        mMapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        LayoutParams browser_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(browser_params);
        LinearLayout mapLayout=(LinearLayout)rootView;
        mapLayout.addView(mMapView);

    	
		return rootView;
	}

	
	@Override
	public void onDestroyView(){
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
    }

    @Override
	public void onResume() {//Resume browser
        super.onResume();
        if(simulator!=null){
        	simulator.resumeTemporaryPause();
        }
    }
	
	@Override
	public void onDetach() {
	    super.onDetach();
	}
	
}
