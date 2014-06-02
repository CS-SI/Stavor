package cs.si.satatt;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateComponents;
import org.orekit.time.DateTimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import mission.Mission;
import mission.MissionAndId;
import cs.si.satatt.R;
import cs.si.satatt.R.id;
import cs.si.satatt.R.layout;
import database.SerializationUtil;
import database.MissionReaderContract.MissionEntry;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import app.Parameters;

public class MissionActivity extends Activity{

	boolean isEdit = false;
	EditText tx_name, tx_description, tx_duration, tx_step, tx_orbit_a, tx_orbit_e, tx_orbit_i, tx_orbit_omega, tx_orbit_raan, tx_orbit_lm;
	DatePicker datePicker;
	TimePicker timePicker;
	Button button;
	MissionAndId mission;
	TimeScale utc;
	TextView speed, duration;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mission);
		

		Bundle b = this.getIntent().getExtras();
		if(b!=null){
		    mission = (MissionAndId) b.getSerializable("MISSION");
		    isEdit = true;
		}else{
			mission = new MissionAndId(new Mission(), -1);
			isEdit=false;
		}
		
		if(utc==null){
			try {
				utc = TimeScalesFactory.getUTC();
			} catch (OrekitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				onBackPressed();
			}
		}
		speed = (TextView) findViewById(R.id.textViewSpeed);
		duration = (TextView) findViewById(R.id.textViewDuration);
		
		button = (Button) findViewById(R.id.buttonMissionSave);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(tx_name.getText().toString().isEmpty()){
					Toast.makeText(getApplicationContext(), getString(R.string.mission_name_is_mandatory), Toast.LENGTH_LONG).show();
				}else{
					try{
						mission.mission.name = tx_name.getText().toString();
						mission.mission.description = tx_description.getText().toString();
						
						mission.mission.sim_duration = Double.parseDouble(tx_duration.getText().toString());
						mission.mission.sim_step = Double.parseDouble(tx_step.getText().toString());
						
						mission.mission.initial_date = new AbsoluteDate(
								datePicker.getYear(),
								datePicker.getMonth(),
								datePicker.getDayOfMonth(),
								timePicker.getCurrentHour(),
								timePicker.getCurrentMinute(),0.0,utc);
						
						mission.mission.initial_orbit.a = Double.parseDouble(tx_orbit_a.getText().toString());
						mission.mission.initial_orbit.e = Double.parseDouble(tx_orbit_e.getText().toString());
						mission.mission.initial_orbit.i = Double.parseDouble(tx_orbit_i.getText().toString());
						mission.mission.initial_orbit.omega = Double.parseDouble(tx_orbit_omega.getText().toString());
						mission.mission.initial_orbit.raan = Double.parseDouble(tx_orbit_raan.getText().toString());
						mission.mission.initial_orbit.lM = Double.parseDouble(tx_orbit_lm.getText().toString());
						
						if(isEdit){
							//Update register with new name and serialized
							if(editMission()){
								((SatAttApplication)getApplication()).loader.reset();
								finish();
							}else{
								Toast.makeText(getApplicationContext(), getString(R.string.mission_error_edit), Toast.LENGTH_LONG).show();
							}
						}else{
							//Create new register in db
							if(addMission()){
								((SatAttApplication)getApplication()).loader.reset();
								finish();
							}else{
								Toast.makeText(getApplicationContext(), getString(R.string.mission_error_create), Toast.LENGTH_LONG).show();
							}
						}
						
						
					}catch(Exception e){
						Toast.makeText(getApplicationContext(), getString(R.string.mission_format_error), Toast.LENGTH_LONG).show();
					}
				}
			}
    		
    	});
		
		tx_name = (EditText) findViewById(R.id.editTextMissionName);
		tx_name.requestFocus();
		
		tx_description = (EditText) findViewById(R.id.editTextMissionDescription);
		tx_duration = (EditText) findViewById(R.id.editTextMissionDuration);
		tx_step = (EditText) findViewById(R.id.editTextMissionStep);
		
		datePicker = (DatePicker) findViewById(R.id.datePicker1);
		datePicker.setCalendarViewShown(false);

		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(true);
		
		tx_orbit_a = (EditText) findViewById(R.id.EditTextMissionA);
		tx_orbit_e = (EditText) findViewById(R.id.EditTextMissionE);
		tx_orbit_i = (EditText) findViewById(R.id.EditTextMissionI);
		tx_orbit_omega = (EditText) findViewById(R.id.EditTextMissionOmega);
		tx_orbit_raan = (EditText) findViewById(R.id.EditTextMissionRaan);
		tx_orbit_lm = (EditText) findViewById(R.id.editTextMissionLm);
		
		tx_duration.setText(Double.toString(mission.mission.sim_duration));
		tx_duration.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	updateSpeedAndDuration();
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    });
		tx_step.setText(Double.toString(mission.mission.sim_step));
		tx_step.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	updateSpeedAndDuration();
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 

		tx_orbit_a.setText(Double.toString(mission.mission.initial_orbit.a));
		tx_orbit_e.setText(Double.toString(mission.mission.initial_orbit.e));
		tx_orbit_i.setText(Double.toString(mission.mission.initial_orbit.i));
		tx_orbit_omega.setText(Double.toString(mission.mission.initial_orbit.omega));
		tx_orbit_raan.setText(Double.toString(mission.mission.initial_orbit.raan));
		tx_orbit_lm.setText(Double.toString(mission.mission.initial_orbit.lM));
		
		if(isEdit){
			button.setText(getString(R.string.mission_edit));
			
			tx_name.setText(mission.mission.name);
			tx_description.setText(mission.mission.description);
			
			DateTimeComponents dateComps = mission.mission.initial_date.getComponents(utc);
			
			datePicker.updateDate(dateComps.getDate().getYear(), dateComps.getDate().getMonth(), dateComps.getDate().getDay());
			timePicker.setCurrentHour(dateComps.getTime().getHour());
			timePicker.setCurrentMinute(dateComps.getTime().getMinute());
			
			
		}else{
			button.setText(getString(R.string.mission_create));
		}
		updateSpeedAndDuration();
	}
	
	private void updateSpeedAndDuration(){
		try{
			double step = Double.parseDouble(tx_step.getText().toString());
			double aprox_speed = step/(Parameters.Simulator.min_hud_model_refreshing_interval_ns/1e9);
			double mission_dur = Double.parseDouble(tx_duration.getText().toString());
			double sim_duration = mission_dur/aprox_speed;
			speed.setText(Double.toString(aprox_speed));
			duration.setText(Double.toString(sim_duration));
		}catch(Exception e){
			speed.setText(getString(R.string.mission_no_val));
			duration.setText(getString(R.string.mission_no_val));
		}		
	}
	
	private boolean editMission(){
		ContentValues values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.mission.description);
		
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission.mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = ((SatAttApplication)getApplication()).db.update(
				MissionEntry.TABLE_NAME,
				values,
				"_id "+"="+mission.id, 
				null);
		if(newRowId==-1)
			return false;
		else
			return true;
	}
	
	private boolean addMission(){
		ContentValues values = new ContentValues();
		values.put(MissionEntry.COLUMN_NAME_NAME, mission.mission.name);
		values.put(MissionEntry.COLUMN_NAME_DESCRIPTION, mission.mission.description);
		
		try {
			values.put(MissionEntry.COLUMN_NAME_CLASS, SerializationUtil.serialize(mission.mission));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = ((SatAttApplication)getApplication()).db.insert(
				MissionEntry.TABLE_NAME,
				null,
		         values);
		if(newRowId==-1)
			return false;
		else
			return true;
		
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }


}
