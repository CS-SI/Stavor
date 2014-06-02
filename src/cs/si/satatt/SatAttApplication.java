package cs.si.satatt;

import android.app.Application;

public class SatAttApplication extends Application {
	private String searchTerm = "";
	
	
	
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
	public String getSearchTerm() {
		return this.searchTerm;
	}
}