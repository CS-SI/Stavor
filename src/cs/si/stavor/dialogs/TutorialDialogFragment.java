package cs.si.stavor.dialogs;

import cs.si.stavor.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Welcome dialog to inform of the installation paths
 * @author Xavier Gibert
 *
 */
public class TutorialDialogFragment extends DialogFragment {
	public static TutorialDialogFragment newInstance(String pref_key, String title, String message) {
		TutorialDialogFragment f = new TutorialDialogFragment();

	    // Supply num input as an argument.
	    Bundle args = new Bundle();
	    args.putString("message", message);
	    args.putString("pref_key", pref_key);
	    args.putString("title", title);
	    f.setArguments(args);

	    return f;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = getArguments().getString("title");
	    String message = getArguments().getString("message");
	    final String pref_key = getArguments().getString("pref_key");
        // Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(title)
        		.setMessage(message)
               .setPositiveButton(getString(R.string.tutorial_continue), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Dummy
                   }
               })
               .setNegativeButton(R.string.tutorial_dont_show_again, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                	   sharedPref.edit().putBoolean(pref_key, false).commit();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
