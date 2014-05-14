package app;

import java.io.File;

import org.orekit.data.DataProvider;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;


public class OrekitInit {

	public static void init(File root) {
		
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
				
				DataProvider provider;
				try {
					provider = new DirectoryCrawler(root);
					providers_manager.addProvider(provider);
				} catch (OrekitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//Log.d(TAG, "dataset: provider found");
		
				
			}
		/*} catch (OrekitException e) {
			e.printStackTrace();
		}*/
	}
}
