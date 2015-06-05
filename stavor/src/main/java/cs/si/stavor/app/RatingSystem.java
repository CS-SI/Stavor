package cs.si.stavor.app;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import cs.si.stavor.MainActivity;
import cs.si.stavor.R;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.StavorApplication.TrackerName;
import android.app.Activity;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RatingSystem {

	private static int runs_of_application_before_rate_suggestion = 5;
	private static int runs_of_application_after_rate_suggestion = 2;//Remind me later
	private static int rate_suggestion_show_delay = 5000;//ms
	
	private static String screenName = "RatingDialog";
	
	public static void verify(Activity activity){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		boolean show_again = sharedPref.getBoolean(activity.getString(R.string.pref_key_rate_app_show_again), true);
		if(show_again){
			int counter = sharedPref.getInt(activity.getString(R.string.pref_key_rate_app_counter), 0);
			int new_counter = counter+1;
			if(new_counter >= runs_of_application_before_rate_suggestion){
				showSuggestion(activity, sharedPref.edit());
				//new_counter = 0;
			}else{
				sharedPref.edit().putInt(activity.getString(R.string.pref_key_rate_app_counter), new_counter).commit();
			}
		}
	}
	
	private static void showSuggestion(final Activity activity, final SharedPreferences.Editor editor){
		//********** Google Analytics ***********
        // Get tracker.
        Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
            TrackerName.APP_TRACKER);
        t.setScreenName(screenName);
        t.send(new HitBuilders.AppViewBuilder().build());
        //***************************************
        
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
			  final Dialog dialog = new Dialog(activity);
			  ((MainActivity)activity).setRateDialog(dialog);//To close it if activity is destroyed
			  
			  	dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		        dialog.setTitle(activity.getString(R.string.rate_dialog_rate_app));

		        LinearLayout ll = new LinearLayout(activity);
		        ll.setOrientation(LinearLayout.VERTICAL);
		        //ll.setPadding(20, 0, 20, 0);//XGG
		        //ll.setBackgroundColor(activity.getResources().getColor(R.color.red));
		        //ll.setBackgroundResource(R.drawable.stars_dialog);
		        
		        TextView tv = new TextView(activity);
		        tv.setText(activity.getString(R.string.rate_dialog_rate_message));
		        tv.setWidth(350);
		        tv.setPadding(20, 0, 20, 10);
		        tv.setTextAppearance(activity, android.R.style.TextAppearance_Medium);
		        ll.addView(tv);
		        
		        Button b1 = new Button(activity);
		        b1.setText(activity.getString(R.string.rate_dialog_rate_app));
		        b1.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	if (editor != null) {
		                    editor.putInt(activity.getString(R.string.pref_key_rate_app_counter), 0);
		                    editor.commit();
		                }
		                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
		                dialog.dismiss();
		                
		                //********** Google Analytics ***********
		                // Get tracker.
		                Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
		                    TrackerName.APP_TRACKER);
		                //t.setScreenName(screenName);
		                t.send(new HitBuilders.EventBuilder()
		                	.setCategory(screenName)
		                	.setAction("Rate")
		                	.setLabel("Rate")
		                	.setValue(1)
		                	.build());
		                //***************************************
		            }
		        });        
		        ll.addView(b1);

		        Button b2 = new Button(activity);
		        b2.setText(activity.getString(R.string.rate_dialog_remind_me_later));
		        b2.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	if (editor != null) {
		                    editor.putInt(activity.getString(R.string.pref_key_rate_app_counter), runs_of_application_before_rate_suggestion-runs_of_application_after_rate_suggestion);
		                    editor.commit();
		                }
		                dialog.dismiss();

		              //********** Google Analytics ***********
		                // Get tracker.
		                Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
		                    TrackerName.APP_TRACKER);
		                //t.setScreenName(screenName);
		                t.send(new HitBuilders.EventBuilder()
		                	.setCategory(screenName)
		                	.setAction("Remind")
		                	.setLabel("Remind")
		                	.setValue(1)
		                	.build());
		                //***************************************
		            }
		        });
		        ll.addView(b2);

		        Button b3 = new Button(activity);
		        b3.setText(activity.getString(R.string.rate_dialog_no_thanks));
		        b3.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		                if (editor != null) {
		                    editor.putBoolean(activity.getString(R.string.pref_key_rate_app_show_again), false);
		                    editor.commit();
		                }
		                dialog.dismiss();

		              //********** Google Analytics ***********
		                // Get tracker.
		                Tracker t = ((StavorApplication) activity.getApplication()).getTracker(
		                    TrackerName.APP_TRACKER);
		                //t.setScreenName(screenName);
		                t.send(new HitBuilders.EventBuilder()
		                	.setCategory(screenName)
		                	.setAction("No")
		                	.setLabel("No")
		                	.setValue(1)
		                	.build());
		                //***************************************
		            }
		        });
		        ll.addView(b3);
		        
		        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);//XGG
		        ll.setLayoutParams(params);

		        dialog.setContentView(ll);   
		        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
		        try{
			        dialog.show();      
			        /*Window window = dialog.getWindow();
			        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);*/
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
		    }
		}, rate_suggestion_show_delay);//ms
	}
}
