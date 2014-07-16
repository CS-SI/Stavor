package cs.si.stavor.database;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;

import cs.si.satcor.R;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class StationsCursorAdapter extends CursorAdapter {
    private SQLiteCursorLoader loader;
    private int mSelectedPosition;
    LayoutInflater mInflater;

    public StationsCursorAdapter(Context context, Cursor c, SQLiteCursorLoader cloader) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        mInflater = LayoutInflater.from(context);
        loader = cloader;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        // something has changed.
        notifyDataSetChanged();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView list_item_id = (TextView)view.findViewById(R.id.textViewStationId);
        TextView list_item_name = (TextView)view.findViewById(R.id.textViewStationName);
        CheckBox list_item_enabled = (CheckBox)view.findViewById(R.id.checkBoxStationEnabled);
        
        list_item_enabled.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				String entry = "0";
				if(arg1){
					entry = "1";
				}
				String db_id = list_item_id.getText().toString();
				updateStationEnabled(db_id, entry);
			}
        });
        
        list_item_id.setText(cursor.getString(cursor.getColumnIndex(StationEntry._ID)));
        list_item_name.setText(cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME_NAME)));
        String boolean_str = cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME_ENABLED));
        boolean boolean_val = false;
        if(boolean_str.equals("1"))
        	boolean_val=true;
        list_item_enabled.setChecked(boolean_val);
        
        int position = cursor.getPosition(); // that should be the same position
        if (mSelectedPosition == position) {
        	//view.setBackgroundColor(Color.RED);
        	view.setBackgroundResource(R.drawable.mission_item_sel);
        } else {
        	//view.setBackgroundColor(Color.WHITE);
        	view.setBackgroundResource(R.drawable.mission_item);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.station_list_item, parent, false);
        // edit: no need to call bindView here. That's done automatically
        return v;
    }
    
    private void updateStationEnabled(String db_id, String entry) {
    	ContentValues values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_ENABLED, entry);
		
		// Edit the row
		loader.update(
				StationEntry.TABLE_NAME,
				values,
				"_id "+"="+db_id, 
				null);
		
	}

}
