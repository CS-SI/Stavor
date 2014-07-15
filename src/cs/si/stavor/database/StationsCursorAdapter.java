package cs.si.stavor.database;

import cs.si.satcor.R;
import cs.si.stavor.database.StationsReaderContract.StationEntry;
import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class StationsCursorAdapter extends CursorAdapter {
    private Context context;
    private int mSelectedPosition;
    LayoutInflater mInflater;

    public StationsCursorAdapter(Context context, Cursor c) {
        // that constructor should be used with loaders.
        super(context, c, 0);
        mInflater = LayoutInflater.from(context);
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        // something has changed.
        notifyDataSetChanged();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView list_item_id = (TextView)view.findViewById(R.id.textViewStationId);
        TextView list_item_name = (TextView)view.findViewById(R.id.textViewStationName);
        CheckBox list_item_enabled = (CheckBox)view.findViewById(R.id.checkBoxStationEnabled);
        
        list_item_id.setText(cursor.getString(cursor.getColumnIndex(StationEntry._ID)));
        list_item_name.setText(cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME_NAME)));
        String boolean_str = cursor.getString(cursor.getColumnIndex(StationEntry.COLUMN_NAME_ENABLED));
        boolean boolean_val = false;
        if(boolean_str.equals("1"))
        	boolean_val=true;
        list_item_enabled.setChecked(boolean_val);
        /*
        int position = cursor.getPosition(); // that should be the same position
        if (mSelectedPosition == position) {
           view.setBackgroundColor(Color.RED);
        } else {
           view.setBackgroundColor(Color.WHITE);
        }*/
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.station_list_item, parent, false);
        // edit: no need to call bindView here. That's done automatically
        return v;
    }

}
