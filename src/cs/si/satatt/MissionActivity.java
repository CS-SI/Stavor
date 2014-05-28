package cs.si.satatt;

import java.sql.Date;
import java.text.SimpleDateFormat;

import cs.si.satatt.R;
import cs.si.satatt.R.id;
import cs.si.satatt.R.layout;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;
import app.Parameters;

public class MissionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mission);
		/*		
		TextView tx_package = (TextView) findViewById(R.id.TextViewVersionPackage);
		TextView tx_license = (TextView) findViewById(R.id.TextViewProjectLicense);
		TextView tx_start_date = (TextView) findViewById(R.id.TextViewProjectStart);
		TextView tx_install_date = (TextView) findViewById(R.id.TextViewVersionInstallDate);
		TextView tx_version_date = (TextView) findViewById(R.id.TextViewVersionDate);
		TextView tx_version_num = (TextView) findViewById(R.id.TextViewVersionNum);
		TextView tx_version_orekit = (TextView) findViewById(R.id.TextViewVersionOrekit);
		TextView tx_version_xwalk = (TextView) findViewById(R.id.TextViewVersionXwalk);
		TextView tx_version_three = (TextView) findViewById(R.id.TextViewVersionThree);
		TextView tx_version_gson = (TextView) findViewById(R.id.TextViewVersionGson);
		TextView tx_version_color = (TextView) findViewById(R.id.TextViewVersionColor);
		TextView tx_version_loader = (TextView) findViewById(R.id.TextViewVersionLoader);
		
		try {
			tx_package.setText(getPackageManager().getPackageInfo(getPackageName(), 0).packageName);
			tx_version_num.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			tx_start_date.setText(Parameters.About.project_start_date);
			tx_license.setText(Parameters.About.project_license);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

	        Date resultdate = new Date(getPackageManager().getPackageInfo(getPackageName(), 0).lastUpdateTime);
			tx_version_date.setText(sdf.format(resultdate));
			
			Date resultdate2 = new Date(getPackageManager().getPackageInfo(getPackageName(), 0).firstInstallTime);
			tx_install_date.setText(sdf.format(resultdate2));
			
			tx_version_orekit.setText(Parameters.About.version_orekit);
			tx_version_xwalk.setText(Parameters.About.version_xwalk);
			tx_version_three.setText(Parameters.About.version_threejs);
			tx_version_gson.setText(Parameters.About.version_gson);
			tx_version_color.setText(Parameters.About.version_androidcolorpicker);
			tx_version_loader.setText(Parameters.About.version_loader);
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }


}
