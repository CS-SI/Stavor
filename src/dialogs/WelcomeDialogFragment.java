package dialogs;

import cs.si.satatt.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import app.Installer;

/**
 * Welcome dialog to inform of the installation paths
 * @author Xavier Gibert
 *
 */
public class WelcomeDialogFragment extends DialogFragment {
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		String data_path = Installer.getOrekitDataRoot(this.getActivity()).getPath();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_welcome))
        		.setMessage(getString(R.string.dialog_install_message)+" "+data_path)
               .setPositiveButton(getString(R.string.dialog_continue), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Dummy
                   }
               });
               /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });*/
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
