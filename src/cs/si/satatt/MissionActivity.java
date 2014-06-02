package cs.si.satatt;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.orekit.errors.OrekitException;
import org.orekit.time.DateComponents;
import org.orekit.time.DateTimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import mission.Mission;
import mission.MissionAndId;
import cs.si.satatt.R;
import cs.si.satatt.R.id;
import cs.si.satatt.R.layout;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import app.Parameters;

public class MissionActivity extends Activity{

	boolean isEdit = false;
	EditText tx_name, tx_description, tx_duration, tx_step, tx_orbit_a, tx_orbit_e, tx_orbit_i, tx_orbit_omega, tx_orbit_raan, tx_orbit_lm;
	DatePicker datePicker;
	TimePicker timePicker;
	MissionAndId mission;
	TimeScale utc;
	
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
		
		tx_name = (EditText) findViewById(R.id.editTextMissionName);
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
		
		if(isEdit){
			tx_name.setText(mission.mission.name);
			tx_description.setText(mission.mission.description);
			tx_duration.setText(Double.toString(mission.mission.sim_duration));
			tx_step.setText(Double.toString(mission.mission.sim_step));
			
			DateTimeComponents dateComps = mission.mission.initial_date.getComponents(utc);
			
			datePicker.updateDate(dateComps.getDate().getYear(), dateComps.getDate().getMonth(), dateComps.getDate().getDay());
			timePicker.setCurrentHour(dateComps.getTime().getHour());
			timePicker.setCurrentMinute(dateComps.getTime().getMinute());
			
			tx_orbit_a.setText(Double.toString(mission.mission.initial_orbit.a));
			tx_orbit_e.setText(Double.toString(mission.mission.initial_orbit.e));
			tx_orbit_i.setText(Double.toString(mission.mission.initial_orbit.i));
			tx_orbit_omega.setText(Double.toString(mission.mission.initial_orbit.omega));
			tx_orbit_raan.setText(Double.toString(mission.mission.initial_orbit.raan));
			tx_orbit_lm.setText(Double.toString(mission.mission.initial_orbit.lM));
			
		}

	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }


}
