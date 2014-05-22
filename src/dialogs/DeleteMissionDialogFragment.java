package dialogs;

import cs.si.satatt.MainActivity;
import cs.si.satatt.R;
import database.MissionReaderContract.MissionEntry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DeleteMissionDialogFragment extends DialogFragment {
	private static final String ARG_NAME = "mission_name";
	private static final String ARG_ID = "mission_id";
	
	public static DeleteMissionDialogFragment newInstance(int id, String name) {	
		DeleteMissionDialogFragment fragment = new DeleteMissionDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_NAME, name);
		args.putInt(ARG_ID, id);
		fragment.setArguments(args);
		return fragment;
	}
	int mission_id;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		String mis_name = getArguments().getString(ARG_NAME);
		int mis_id = getArguments().getInt(ARG_ID);
		mission_id = mis_id;
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_delete_title))
        		.setMessage(getString(R.string.dialog_delete_message)+" "+mis_name)
        		.setCancelable(true)
               .setPositiveButton(getString(R.string.dialog_delete_confirm), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Dummy
                	   deleteMission(mission_id);
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
	
	private void deleteMission(int mission_id) {
		// TODO Auto-generated method stub
		MainActivity act = ((MainActivity)getActivity());
		//XGGDEBUG:solve, this query is not well formed
		act.loader.delete(MissionEntry.TABLE_NAME, MissionEntry._ID+"="+mission_id, null);
	}

}
