function setLoadingProgress(progress) {// from 0 to 100 
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.setProgressMap!= "undefined") { // check the method
			 Android.setProgressMap(progress);
		  }
   	}
}
function updateModelState(new_state){
	var state = JSON.parse(new_state);
	sun_lat = state.sun_lat;//In degrees
	sun_lon = state.sun_lon;//In degrees
	station_areas = state.stations;
	fov = state.fov;
	fov_terminator = state.fov_terminator;
	solarTerminator = state.terminator;
	if (typeof state.point != "undefined"){
		addPathPoint(state.point);
	}
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
		show_fov = config.show_fov;
		show_track = config.show_track;
		show_sun_icon = config.show_sun_icon;
		show_sun_terminator = config.show_sun_terminator;
		//param_path_max_length = config.track_max_length;
		//payload_beamwidth = config.payload_beamwidth;
		follow_sc = config.follow_sc;
		addPathPoints(config.points);
	  }
   }
}
function clearPath(){
	myMultyPath = new Paths();
	lineLayer.removeAllFeatures();
	sc_layer.removeAllFeatures();
	//stations_layer.removeAllFeatures();
	stations_area_layer.removeAllFeatures();
	//reDraw();
}
function addPathPoints(points){
	
	for(var i in points){
		myMultyPath.addPointToPath(points[i]);
	}

	if(points.length>1){
		sc_latitude = points[points.length-1].latitude;
		sc_longitude = points[points.length-1].longitude;
		sc_altitude = points[points.length-1].altitude;
		reDraw();
	}
}
function addPathPoint(point){
	
	myMultyPath.addPointToPath(point);
	sc_latitude = point.latitude;
	sc_longitude = point.longitude;
	sc_altitude = point.altitude;
	reDraw();
}
function changeView(view_mode){
	switch(view_mode){
		case "Free"://free
			follow_sc = false;
			break;
		case "Locked"://locked
			follow_sc = true;
			break;
		default://xyz
			follow_sc = false;
			break;
	}
}
