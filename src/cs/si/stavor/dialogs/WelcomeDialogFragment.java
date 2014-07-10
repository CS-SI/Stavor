package cs.si.stavor.dialogs;

import cs.si.satcor.R;
import cs.si.stavor.app.Installer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

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

        builder.setIcon(R.drawable.ic_launcher);
        TextView tx_path = new TextView(getActivity());
        tx_path.setText(data_path);
        tx_path.setTypeface(null,Typeface.ITALIC);
        int padding_in_dp = 20;  // 6 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        tx_path.setPadding(padding_in_px, 0, padding_in_px, 0);
        builder.setView(tx_path);
        builder.setTitle(getString(R.string.dialog_welcome))
        		.setMessage(getString(R.string.dialog_install_message))
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
