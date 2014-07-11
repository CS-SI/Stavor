function setLoadingProgress(progress) {// from 0 to 100 
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.setProgressMap!= "undefined") { // check the method
			 Android.setProgressMap(progress);
		  }
   	}
}
function updateModelState(new_state){
	var state = JSON.parse(new_state);
	addPoints(state);
}
function showAndroidToast(toast) {
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.showToast!= "undefined") { // check the method
			 Android.showToast(toast);
		  }
   	}
}
function getInitialization(){
	if (typeof Android != "undefined"){ // check the bridge 
	  if (Android.getInitializationMapJSON!= "undefined") { // check the method
		 
		var config = JSON.parse(Android.getInitializationMapJSON());
			
		stations = config.stations;
		payload_aperture = config.payload_aperture;
		follow_sc = follow_sc;
		
	  }
   }
}
