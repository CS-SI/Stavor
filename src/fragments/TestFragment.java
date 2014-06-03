package fragments;


import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import app.Parameters;

/**
 * Fragment with browser to test WebGL compatibility
 * @author Xavier Gibert
 *
 */
public final class TestFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 * @param simulation 
	 */
	public static TestFragment newInstance(int sectionNumber) {	
		TestFragment fragment = new TestFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TestFragment() {
	}
	
	XWalkView browser;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.test, container,
				false);
		
		Button button1 = ((Button) rootView.findViewById(R.id.buttonTest1));
		button1.setOnClickListener(new View.OnClickListener() {
		   @Override
		   public void onClick(View v) {
			   // button 1 was clicked!
			   //browser.loadUrl(Parameters.Web.TEST_PAGE_1);
			   browser.load(Parameters.Web.TEST_PAGE_1,null);
		   }
		  });
		Button button2 = ((Button) rootView.findViewById(R.id.buttonTest2));
		button2.setOnClickListener(new View.OnClickListener() {
		   @Override
		   public void onClick(View v) {
			   // button 1 was clicked!
			   browser.load(Parameters.Web.TEST_PAGE_2,null);
		   }
		  });
		Button button3 = ((Button) rootView.findViewById(R.id.buttonTest3));
		button3.setOnClickListener(new View.OnClickListener() {
		   @Override
		   public void onClick(View v) {
			   // button 1 was clicked!
			   browser.load(Parameters.Web.TEST_PAGE_3,null);
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
    	
    	LinearLayout browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	browserLayout.addView(browser);
    	
    	browser.load(Parameters.Web.TEST_PAGE_1,"");
    	
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
