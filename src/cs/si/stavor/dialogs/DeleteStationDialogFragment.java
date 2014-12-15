package cs.si.stavor.dialogs;

import cs.si.stavor.MainActivity;
import cs.si.stavor.R;
import cs.si.stavor.StavorApplication;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Dialog to prevent unwilling station removals
 * @author Xavier Gibert
 *
 */
public class DeleteStationDialogFragment extends DialogFragment {
	private static final String ARG_NAME = "station_name";
	private static final String ARG_ID = "station_id";
	
	public static DeleteStationDialogFragment newInstance(int id, String name) {	
		DeleteStationDialogFragment fragment = new DeleteStationDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_NAME, name);
		args.putInt(ARG_ID, id);
		fragment.setArguments(args);
		return fragment;
	}
	int station_id;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		String gs_name = getArguments().getString(ARG_NAME);
		int gs_id = getArguments().getInt(ARG_ID);
		station_id = gs_id;
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_delete_title_station))
        		.setMessage(getString(R.string.dialog_delete_message_station)+" "+gs_name)
        		.setCancelable(true)
               .setPositiveButton(getString(R.string.dialog_delete_confirm), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Dummy
                	   deleteStation(station_id);
                   }

               });
               builder.setNegativeButton(getString(R.string.dialog_delete_cancel), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	/**
	 * Delete Mission from database
	 * @param station_id
	 */
	private void deleteStation(int station_id) {
		MainActivity act = ((MainActivity)getActivity());
		((StavorApplication)act.getApplication()).loader.delete(StationEntry.TABLE_NAME, StationEntry._ID+"="+station_id, null);
	}

}
