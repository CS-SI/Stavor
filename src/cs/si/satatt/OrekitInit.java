package cs.si.satatt;

import java.io.File;

import org.orekit.data.DataProvider;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;

import android.content.Context;

public class OrekitInit {
	
	private final static String TAG = "OrekitInit";

	public static void init(int source_, Context context_) {
		
		final DataProvidersManager providers_manager = DataProvidersManager.getInstance();
		providers_manager.clearProviders();
		
       // try {        	
			if (providers_manager.getProviders().size() == 0) {
				
				/*
				File orekit_data = new File(Donnee.getDonneeAdresse());
				
				if (orekit_data.isDirectory()) {
					DataProvider provider = new DirectoryCrawler(orekit_data);
					providers_manager.addProvider(provider);
					
					Log.d(TAG, "dataset: external provider found : "+orekit_data.getAbsolutePath());
				}
				else {
					DataProvider classpath_provider = new ZipJarCrawler(orekit_data);
					providers_manager.addProvider(classpath_provider);
					
					Log.d(TAG, "dataset: embedded provider found");
				}*/
				
				
				DataProvider provider = new AndroidZipCrawler(source_,context_);
				providers_manager.addProvider(provider);
				
				//Log.d(TAG, "dataset: provider found");
		
				
			}
		/*} catch (OrekitException e) {
			e.printStackTrace();
		}*/
	}
}
