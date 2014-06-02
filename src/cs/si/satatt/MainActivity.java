package cs.si.satatt;

/*import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;*/

import mission.MissionAndId;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;


import dialogs.ErrorDialogFragment;
import dialogs.ResetAppDialogFragment;
import dialogs.ResetDbDialogFragment;
import dialogs.WelcomeDialogFragment;
import settings.SettingsBasicFragment;
import settings.SettingsExtraFragment;
import settings.SettingsGeneralFragment;
import settings.SettingsMeasuresFragment;
import settings.SettingsModelsFragment;
import simulator.Simulator;
import fragments.NavigationDrawerFragment;
import fragments.HudFragment;
import fragments.RetainedFragment;
import fragments.SimulatorFragment;
import fragments.TestFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import app.Installer;
import app.OrekitInit;
import app.Parameters;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	private RetainedFragment dataFragment;

	
	private Simulator simulator;
	public Simulator getSimulator(){
		return simulator;
	}
    public XWalkView mXwalkView;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long start_time = System.nanoTime();

		Installer.installApkData(this);
		
		OrekitInit.init(Installer.getOrekitDataRoot(this));
		
		((SatAttApplication)getApplication()).db_help = Installer.installApkDatabase(this);
		((SatAttApplication)getApplication()).db = ((SatAttApplication)getApplication()).db_help.getWritableDatabase();

		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		setProgressBarVisibility(true);
		
		mXwalkView = new XWalkView(this.getApplicationContext(), this);
		mXwalkView.setResourceClient(new MyResourceClient(mXwalkView));
        mXwalkView.setUIClient(new MyUIClient(mXwalkView));

		
		// find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();
            // load the data from the web
            dataFragment.setData(new Simulator(this));
        }

        // the data is available in dataFragment.getData()
        this.simulator = dataFragment.getData();
		
        
        // NAVIGATION
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		long diff_time = System.nanoTime()-start_time;
		if(diff_time<Parameters.App.splash_min_time_ns){
			try {
				Thread.sleep((Parameters.App.splash_min_time_ns-diff_time)/1000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {

		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		if(position==0){// selection of tabs content
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					SimulatorFragment.newInstance(position + 1)).commit();
		}else if(position==1){
			/*fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					PlaceholderFragment.newInstance(position + 1)).commit();*/
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					HudFragment.newInstance(position + 1)).commit();
		}else if(position==2){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsBasicFragment.newInstance(position +1)).commit();
		}else if(position==3){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsExtraFragment.newInstance(position +1)).commit();
		}else if(position==4){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsMeasuresFragment.newInstance(position +1)).commit();
		}else if(position==5){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsModelsFragment.newInstance(position +1)).commit();
		}else if(position==6){
			// Display the fragment as the main content.
	        fragmentManager    
	        .beginTransaction()
	        .replace(R.id.container, 
	        		SettingsGeneralFragment.newInstance(position +1)).commit();
		}else if(position==7){
			// Display the fragment as the main content.
	        fragmentManager
	        .beginTransaction()
	        .replace(R.id.container, TestFragment.newInstance(position + 1)).commit();
		}else{
			
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		case 5:
			mTitle = getString(R.string.title_section5);
			break;
		case 6:
			mTitle = getString(R.string.title_section6);
			break;
		case 7:
			mTitle = getString(R.string.title_section7);
			break;
		case 8:
			mTitle = getString(R.string.title_section8);
			break;
		}
	}
    
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			showAbout();
			return true;
		}
		if (id == R.id.action_reset_conf) {
			resetUserConfigShowDialog();
			return true;
		}
		if (id == R.id.action_reset_db) {
			resetMissionsDbShowDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showAbout() {
		Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
		MainActivity.this.startActivity(myIntent);
	}
	
	public void showMissionCreator() {
		Intent myIntent = new Intent(MainActivity.this, MissionActivity.class);
		MainActivity.this.startActivity(myIntent);
	}
	
	public void showMissionEditor(MissionAndId mission){
		Intent myIntent = new Intent(MainActivity.this, MissionActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("MISSION",mission);
		myIntent.putExtras(b);
		MainActivity.this.startActivity(myIntent);
	}

	public void showSection(final int sel) {
		runOnUiThread( new Runnable() {
			public void run() {    
				mNavigationDrawerFragment.select(sel);
				onSectionAttached(sel+1);
				restoreActionBar();
	        }
		});
	}
	
	public void showSection() {
		showSection(mNavigationDrawerFragment.getSelectedPosition());
	}
	
    private Toast toast;
    private long lastBackPressTime = 0;


    @Override
    public void onBackPressed() {
    	if (!mNavigationDrawerFragment.isDrawerOpen()) {
	    	int sel = mNavigationDrawerFragment.getSelectedPosition();
	    	if(sel>1){
	    		showSection(1);
	    	}else if (sel==1){
	    		showSection(0);
	    	}else if (sel==0){
		    	if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
		    		toast = Toast.makeText(this, getString(R.string.app_exit_prevent_message), 4000);
		    		toast.show();
		    		this.lastBackPressTime = System.currentTimeMillis();
		    	} else {
		    		if (toast != null) {
		    			toast.cancel();
		    		}
		    		super.onBackPressed();
		    	}
	    	}
    	}else{
    		showSection();	
	    }
    }
    
    // Dialogs
    public void showWelcomeMessage() {
        DialogFragment newFragment = new WelcomeDialogFragment();
        newFragment.show(getFragmentManager(), "welcome");
    }
    
    public void showErrorDialog(String message, boolean canIgnore) {
    	DialogFragment newFragment = ErrorDialogFragment.newInstance(message, canIgnore);
    	newFragment.setCancelable(false);
    	newFragment.show(getFragmentManager(), "error");
    }
    
    private void resetUserConfigShowDialog() {
    	DialogFragment newFragment = ResetAppDialogFragment.newInstance();
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "reset");
	}
    
    private void resetMissionsDbShowDialog() {
    	DialogFragment newFragment = ResetDbDialogFragment.newInstance();
    	newFragment.setCancelable(true);
    	newFragment.show(getFragmentManager(), "reset_db");
	}
    
    
    //XWalk
    
    class MyResourceClient extends XWalkResourceClient {
        MyResourceClient(XWalkView view) {
            super(view);
        }
        /*@Override
  		public void onProgressChanged(XWalkView view, int progress) {
  			// Activities and WebViews measure progress with different scales.
  			// The progress meter will automatically disappear when we reach 100%
  			try{
  				if(progress==100)
  					simulator.setBrowserLoaded(true);
  				setProgress(progress * 100);
  			}catch(NullPointerException nulle){
  				
  			}
  		}*/
    }
	
    class MyUIClient extends XWalkUIClient {
        MyUIClient(XWalkView view) {
            super(view);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mXwalkView != null) {
            mXwalkView.pauseTimers();
            mXwalkView.onHide();
        }
        if(simulator!=null){
        	simulator.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXwalkView != null) {
            mXwalkView.resumeTimers();
            mXwalkView.onShow();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mXwalkView != null) {
            mXwalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXwalkView != null) {
            mXwalkView.onNewIntent(intent);
        }
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
        if(isFinishing())
        	simulator.disconnect();
        else
        	dataFragment.setData(this.simulator);
        ((SatAttApplication)getApplication()).db_help.close();
        //XWalk
        if (mXwalkView != null) {
            mXwalkView.onDestroy();
        }
    }



}
