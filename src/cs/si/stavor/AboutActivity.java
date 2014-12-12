package cs.si.stavor;

import java.sql.Date;
import java.text.SimpleDateFormat;

import cs.si.stavor.R;
import cs.si.stavor.app.Parameters;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Activity to show the app About screen
 * @author Xavier Gibert
 *
 */
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);
		
		//Load Views
		TextView tx_package = (TextView) findViewById(R.id.TextViewVersionPackage);
		TextView tx_flavor = (TextView) findViewById(R.id.TextViewVersionFlavor);
		TextView tx_platform = (TextView) findViewById(R.id.TextViewAboutPlatform);
		TextView tx_license = (TextView) findViewById(R.id.TextViewProjectLicense);
		TextView tx_start_date = (TextView) findViewById(R.id.TextViewProjectStart);
		TextView tx_install_date = (TextView) findViewById(R.id.TextViewVersionInstallDate);
		TextView tx_version_date = (TextView) findViewById(R.id.TextViewVersionDate);
		TextView tx_version_num = (TextView) findViewById(R.id.TextViewVersionNum);
		TextView tx_version_orekit = (TextView) findViewById(R.id.TextViewVersionOrekit);
		TextView tx_version_xwalk = (TextView) findViewById(R.id.TextViewVersionXwalk);
		TextView tx_version_three = (TextView) findViewById(R.id.TextViewVersionThree);
		TextView tx_version_openlayers = (TextView) findViewById(R.id.TextViewVersionXwalk);
		TextView tx_version_gson = (TextView) findViewById(R.id.TextViewVersionGson);
		TextView tx_version_color = (TextView) findViewById(R.id.TextViewVersionColor);
		TextView tx_version_loader = (TextView) findViewById(R.id.TextViewVersionLoader);
		TextView tx_version_http = (TextView) findViewById(R.id.TextViewVersionHttp);
		TextView tx_xwalk_package = (TextView) findViewById(R.id.TextView08);
		TextView tx_openlayers_package = (TextView) findViewById(R.id.TextView08);//XGGDEBUG:MERGE duplicated textview8
		
		//Fill Views
		try {
			tx_package.setText(getPackageManager().getPackageInfo(getPackageName(), 0).packageName);
			if(Parameters.App.pro_version)
				tx_flavor.setText(getString(R.string.about_flavor_pro));
			else
				tx_flavor.setText(getString(R.string.about_flavor_public));
			tx_version_num.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			String code = Integer.toString(versionCode);
			if(code.substring(code.length()-1).equals("0")){
				tx_platform.setText("ARM");
				tx_xwalk_package.setText(getString(R.string.about_table_crosswalk)+"ARM");
			}else{
				tx_platform.setText("x86");
				tx_xwalk_package.setText(getString(R.string.about_table_crosswalk)+"x86");
			}
			tx_openlayers_package.setText(getString(R.string.about_table_openlayers));
			tx_start_date.setText(Parameters.About.project_start_date);
			tx_license.setText(getString(R.string.about_project_license_value));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

	        Date resultdate = new Date(getPackageManager().getPackageInfo(getPackageName(), 0).lastUpdateTime);
			tx_version_date.setText(sdf.format(resultdate));
			
			Date resultdate2 = new Date(getPackageManager().getPackageInfo(getPackageName(), 0).firstInstallTime);
			tx_install_date.setText(sdf.format(resultdate2));
			
			tx_version_orekit.setText(Parameters.About.version_orekit);
			tx_version_xwalk.setText(Parameters.About.version_xwalk);
			tx_version_three.setText(Parameters.About.version_threejs);
			tx_version_openlayers.setText(Parameters.About.version_openlayers);
			tx_version_gson.setText(Parameters.About.version_gson);
			tx_version_color.setText(Parameters.About.version_androidcolorpicker);
			tx_version_loader.setText(Parameters.About.version_loader);
			tx_version_http.setText(Parameters.About.version_http);
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }


}
