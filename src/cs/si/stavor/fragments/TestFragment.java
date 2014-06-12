package cs.si.stavor.fragments;


import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkView;

import cs.si.stavor.R;
import cs.si.stavor.MainActivity;
import cs.si.stavor.app.Parameters;
import cs.si.stavor.web.MyResourceClient;
import cs.si.stavor.web.MyUIClient;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
	
	/**
	 * WebView from XWalk project to increase compatibility of WebGL
	 */
    public XWalkView mXwalkView;

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.test, container,
				false);
		
		//Browser
		//Initialize WebView
		if(mXwalkView==null){
			mXwalkView = new XWalkView(this.getActivity().getApplicationContext(), this.getActivity());
			mXwalkView.setBackgroundColor(0);
			mXwalkView.setResourceClient(new MyResourceClient(mXwalkView));
	        mXwalkView.setUIClient(new MyUIClient(mXwalkView));
		}
		
		Button button1 = ((Button) rootView.findViewById(R.id.buttonTest1));
		button1.setOnClickListener(new View.OnClickListener() {
		   @Override
		   public void onClick(View v) {
			   // button 1 was clicked!
			   //browser.loadUrl(Parameters.Web.TEST_PAGE_1);
			   mXwalkView.load(Parameters.Web.TEST_PAGE_1,null);
		   }
		  });
		Button button2 = ((Button) rootView.findViewById(R.id.buttonTest2));
		button2.setOnClickListener(new View.OnClickListener() {
		   @Override
		   public void onClick(View v) {
			   // button 1 was clicked!
			   mXwalkView.load(Parameters.Web.TEST_PAGE_2,null);
		   }
		  });
		Button button3 = ((Button) rootView.findViewById(R.id.buttonTest3));
		button3.setOnClickListener(new View.OnClickListener() {
		   @Override
		   public void onClick(View v) {
			   // button 1 was clicked!
			   mXwalkView.load(Parameters.Web.TEST_PAGE_3,null);
		   }
		  });
		
		//mXwalkView = new XWalkView(this.getActivity().getApplicationContext(), this.getActivity());
		
    	XWalkSettings browserSettings = mXwalkView.getSettings();
    	
    	browserSettings.setJavaScriptEnabled(true);
    	browserSettings.setUseWideViewPort(false);
    	//browserSettings.setLoadWithOverviewMode(true);
    	browserSettings.setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
    	browserSettings.setAllowUniversalAccessFromFileURLs(true);
    	//browserSettings.setBuiltInZoomControls(true);
    	//browserSettings.setDisplayZoomControls(true);
    	//browserSettings.setSupportZoom(true);
    	
    	mXwalkView.clearCache(true);
    	
    	LinearLayout browserLayout=(LinearLayout)rootView.findViewById(R.id.simLayout);
    	browserLayout.addView(mXwalkView);
    	
    	mXwalkView.load(Parameters.Web.TEST_PAGE_1,"");
    	
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
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
