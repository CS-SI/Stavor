package service;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;

public class ServiceHandler extends Activity{
	
	public ServiceHandler(){
		
	}
	
	private PendingIntent pendingIntent;
	
	public void connect(){
		Intent myIntent = new Intent(ServiceHandler.this, MySimulatorService.class);
		pendingIntent = PendingIntent.getService(ServiceHandler.this, 0, myIntent, 0);
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 2);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		Toast.makeText(ServiceHandler.this, "Start Alarm", Toast.LENGTH_LONG).show();

	}
	
	public void disconnect(){
		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		// Tell the user about what we did.
		Toast.makeText(ServiceHandler.this, "Cancel!", Toast.LENGTH_LONG).show();
	}
}
