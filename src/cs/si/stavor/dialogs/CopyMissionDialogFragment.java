package cs.si.stavor.dialogs;

import java.io.IOException;

import cs.si.satcor.R;
import cs.si.satcor.MainActivity;
import cs.si.satcor.StavorApplication;
import cs.si.stavor.database.SerializationUtil;
import cs.si.stavor.database.MissionReaderContract.MissionEntry;
import cs.si.stavor.mission.Mission;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Dialog to prevent unwilling mission removals
 * @author Xavier Gibert
 *
 */
public class CopyMissionDialogFragment extends DialogFragment {
	private static final String ARG_NAME = "mission_name";
	private static final String ARG_ID = "mission_id";
	private static final String ARG_CLASS = "mission_class";
	
	public static CopyMissionDialogFragment newInstance(int id, String name, Mission mis_class) {	
		CopyMissionDialogFragment fragment = new CopyMissionDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_NAME, name);
		args.putInt(ARG_ID, id);
		args.putSerializable(ARG_CLASS, mis_class);
		fragment.setArguments(args);
		return fragment;
	}
	int mission_id;
	Mission mission;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
		String mis_name = getArguments().getString(ARG_NAME);
		int mis_id = getArguments().getInt(ARG_ID);
		mission_id = mis_id;
		mission = (Mission) getArguments().getSerializable(ARG_CLASS);
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_copy_title))
        		.setMessage(getString(R.string.dialog_copy_message)+" "+mis_name+"?")
        		.setCancelable(true)
               .setPositiveButton(getString(R.string.dialog_copy_confirm), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Dummy
                	   copyMission();
                   }

               });
               builder.setNegativeButton(getString(R.string.dialog_copy_cancel), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	/**
	 * Delete Mission from database
	 * @param mission_id
	 */
	private void copyMission() {
		MainActivity act = ((MainActivity)getActivity());
		
		ContentValues values = new ContentValues();
		mission.name = mission.name+"_copy";
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.description);
		
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row
		((StavorApplication)act.getApplication()).loader.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		
	}

}
