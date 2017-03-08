package cs.si.stavor;



import cs.si.stavor.database.StationsReaderContract.StationEntry;
import cs.si.stavor.station.GroundStation;
import cs.si.stavor.station.StationAndId;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity to edit or create the stations of the database
 * @author Xavier Gibert
 *
 */
public class StationActivity extends Activity{

	boolean isEdit = false;
	EditText tx_name, tx_lat, tx_lon, tx_alt, tx_elev;
	Button button;
	StationAndId station;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.station_editor);
		
		//Load mission, in case of edit mode, and the mode flag
		Bundle b = this.getIntent().getExtras();
		if(b!=null){
		    station = (StationAndId) b.getSerializable("STATION");
		    isEdit = true;
		}else{
			station = new StationAndId(new GroundStation(), -1);
			isEdit=false;
		}
		
		button = (Button) findViewById(R.id.buttonStationSave);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(tx_name.getText().toString().isEmpty()){
					Toast.makeText(getApplicationContext(), getString(R.string.station_name_is_mandatory), Toast.LENGTH_LONG).show();
				}else{
					//Save Station
					try{
						station.station.name = tx_name.getText().toString();
						
						station.station.latitude = Double.parseDouble(tx_lat.getText().toString());
						station.station.longitude = Double.parseDouble(tx_lon.getText().toString());
						station.station.altitude = Double.parseDouble(tx_alt.getText().toString());
						station.station.elevation = Double.parseDouble(tx_elev.getText().toString());
						
						if(station.station.elevation < -5.0 || station.station.elevation > 90.0){//Check elevation is between limits -5º and 90º
							Toast.makeText(getApplicationContext(), getString(R.string.station_elevation_out_of_range), Toast.LENGTH_LONG).show();
						}else{
							if(isEdit){
								//Update register with new name and serialized
								if(editStation()){

							        
									//((StavorApplication)getApplication()).loader.reset();
									finish();
								}else{
									Toast.makeText(getApplicationContext(), getString(R.string.station_error_edit), Toast.LENGTH_LONG).show();
								}
							}else{
								//Create new register in db
								if(addStation()){

							        
									//((StavorApplication)getApplication()).loader.reset();
									finish();
								}else{
									Toast.makeText(getApplicationContext(), getString(R.string.station_error_create), Toast.LENGTH_LONG).show();
								}
							}
						}
						
						
					}catch(Exception e){
						Toast.makeText(getApplicationContext(), getString(R.string.station_format_error), Toast.LENGTH_LONG).show();
					}
				}
			}
    		
    	});
		
		//Load Views
		tx_name = (EditText) findViewById(R.id.editTextStationName);
		tx_name.requestFocus();
		
		tx_lat = (EditText) findViewById(R.id.editTextStationLat);
		tx_lon = (EditText) findViewById(R.id.editTextStationLon);
		tx_alt = (EditText) findViewById(R.id.editTextStationAlt);
		tx_elev = (EditText) findViewById(R.id.editTextStationElev);
		

		//Fill Views
		tx_lat.setText(Double.toString(station.station.latitude));
		tx_lon.setText(Double.toString(station.station.longitude));
		tx_alt.setText(Double.toString(station.station.altitude));
		tx_elev.setText(Double.toString(station.station.elevation));

		if(isEdit){
			button.setText(getString(R.string.station_edit));
			
			tx_name.setText(station.station.name);
			
		}else{
			button.setText(getString(R.string.station_create));
		}
		
	}
	
	private boolean editStation(){
		ContentValues values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, station.station.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, station.station.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, station.station.longitude);
		values.put(StationEntry.COLUMN_NAME_ALTITUDE, station.station.altitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, station.station.elevation);
		values.put(StationEntry.COLUMN_NAME_ENABLED, station.station.enabled);
		
		// Edit the row
		((StavorApplication)getApplication()).loader.update(
				StationEntry.TABLE_NAME,
				values,
				"_id "+"="+station.id, 
				null);
		return true;
	}
	
	private boolean addStation(){
		ContentValues values = new ContentValues();
		values.put(StationEntry.COLUMN_NAME_NAME, station.station.name);
		values.put(StationEntry.COLUMN_NAME_LATITUDE, station.station.latitude);
		values.put(StationEntry.COLUMN_NAME_LONGITUDE, station.station.longitude);
		values.put(StationEntry.COLUMN_NAME_ALTITUDE, station.station.altitude);
		values.put(StationEntry.COLUMN_NAME_ELEVATION, station.station.elevation);
		values.put(StationEntry.COLUMN_NAME_ENABLED, station.station.enabled);
		
		// Insert the new row
		((StavorApplication)getApplication()).loader.insert(
				StationEntry.TABLE_NAME,
				null,
		         values);
		return true;
		
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }


}
