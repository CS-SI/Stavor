package cs.si.stavor.dialogs;

import cs.si.satcor.MainActivity;
import cs.si.satcor.R;
import cs.si.satcor.StavorApplication;
import cs.si.stavor.database.MissionReaderContract.MissionEntry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Dialog to prevent unwilling resets of mission database
 * @author Xavier Gibert
 *
 */
public class ResetDbDialogFragment extends DialogFragment {
	
	public static ResetDbDialogFragment newInstance() {	
		ResetDbDialogFragment fragment = new ResetDbDialogFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_reset_db_title))
        		.setMessage(getString(R.string.dialog_reset_db_message))
        		.setCancelable(true)
               .setPositiveButton(getString(R.string.dialog_reset_db_confirm), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Dummy
                	   resetDb();
                   }

               });
               builder.setNegativeButton(getString(R.string.dialog_reset_db_cancel), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	/**
	 * Deletes the database and restarts the application so it is installed again with
	 * default values
	 */
	private void resetDb() {
		//Delete database
		((StavorApplication)((MainActivity)getActivity()).getApplication()).db.delete(MissionEntry.TABLE_NAME, "1", null);
		//((MainActivity)getActivity()).loader.execSQL("delete * from "+ MissionEntry.TABLE_NAME, null);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		preferences.edit().putBoolean(getString(R.string.pref_key_database_installed), false).commit();
		resetApplication();
	}
	
	/**
	 * Restarts the application in order to install the database if it has been
	 * deleted
	 */
	private void resetApplication(){
		/*Context context = getActivity().getBaseContext();
		Intent mStartActivity = new Intent(context, MainActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
		System.exit(0);*/
		

		/*Intent i = getActivity().getBaseContext().getPackageManager()
	             .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);*/

		getActivity().recreate();
	}

}
