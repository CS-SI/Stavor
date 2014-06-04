package dialogs;

import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Dialog to prevent unwilling resets of app configuration
 * @author Xavier Gibert
 *
 */
public class ResetAppDialogFragment extends DialogFragment {
	
	public static ResetAppDialogFragment newInstance() {	
		ResetAppDialogFragment fragment = new ResetAppDialogFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_reset_title))
        		.setMessage(getString(R.string.dialog_reset_message))
        		.setCancelable(true)
               .setPositiveButton(getString(R.string.dialog_reset_confirm), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   resetUserConfig();
                   }

               });
               builder.setNegativeButton(getString(R.string.dialog_reset_cancel), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	/**
	 * Resets the application configuration to default values
	 */
	private void resetUserConfig() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		boolean prev_value = preferences.getBoolean(getString(R.string.pref_key_database_installed), false);
		preferences.edit().clear().commit();
		preferences.edit().putBoolean(getString(R.string.pref_key_database_installed), prev_value).commit();
		resetApplication();
	}
	
	/**
	 * Restarts the application in order to reload all configurations
	 */
	private void resetApplication(){
		Context context = getActivity().getBaseContext();
		Intent mStartActivity = new Intent(context, MainActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
		System.exit(0);
	}

}
