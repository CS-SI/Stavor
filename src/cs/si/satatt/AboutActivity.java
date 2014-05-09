package cs.si.satatt;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);
		setProgressBarVisibility(true);
				
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        // store the data in the fragment
    }


}
