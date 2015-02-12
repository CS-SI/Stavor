package cs.si.stavor.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import cs.si.stavor.R;

/**
 * Dialog to sho errors
 * @author Xavier Gibert
 *
 */
public class ErrorDialogFragment extends DialogFragment {
	private static final String ARG_MESSAGE = "error_message";
	private static final String ARG_IGNORE = "can_be_ignored";
	
	public static ErrorDialogFragment newInstance(String message, boolean canIgnore) {
		ErrorDialogFragment fragment = new ErrorDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_MESSAGE, message);
		args.putBoolean(ARG_IGNORE, canIgnore);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		String message = getArguments().getString(ARG_MESSAGE);
		boolean canIgnore = getArguments().getBoolean(ARG_IGNORE);
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_error))
        		.setMessage(message)
        		.setCancelable(false)
               .setPositiveButton(getString(R.string.dialog_exit), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   getActivity().finish();
                   }
               });
        if(canIgnore){
               builder.setNegativeButton(getString(R.string.dialog_ignore), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
