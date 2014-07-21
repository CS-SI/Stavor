function setLoadingProgress(progress) {// from 0 to 100 
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.setProgressMap!= "undefined") { // check the method
			 Android.setProgressMap(progress);
		  }
   	}
}
function updateModelState(new_state){
	var state = JSON.parse(new_state);
	addPoints(state.points);
	sun_lat = state.sun_lat;//In degrees
	sun_lon = state.sun_lon;//In degrees
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
		param_path_max_length = config.track_max_length;
		payload_beamwidth = config.payload_beamwidth;
		follow_sc = config.follow_sc;
		if(config.points.length>0){
			addPoints(config.points);
		}
	  }
   }
}
