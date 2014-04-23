package webclient;

import cs.si.satatt.SatAttApplication;

import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class UAJscriptHandler {
	private String tag = "UAJscriptHandler";
	private Context context = null;

	public UAJscriptHandler(Context context) {
		Log.i(tag,"script handler created");
		this.context = context;
	}
	
	public void Log(String s) {
		Log.i(tag,s);
	}
	
	public void Info(String s) {
		Toast.makeText(context,s,Toast.LENGTH_LONG).show();
	}
	
	public void PlaceCall(String number) {
		Log.i(tag,"Placing a phone call to [" + number + "]");
		String url = "tel:" + number;
		Intent callIntent = new Intent(Intent.ACTION_DIAL,Uri.parse(url));
		context.startActivity(callIntent);
	}
	public void SetSearchTerm(String searchTerm) {
		SatAttApplication app = (SatAttApplication) context.getApplicationContext();
		app.setSearchTerm(searchTerm);
	}
	
}

