function showAndroidToast(toast) {
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.showToast!= "undefined") { // check the method
			 Android.showToast(toast);
		  }
   	}
}
function setLoadingProgress(progress) {// from 0 to 100 
	/*if(progress==100)
		hideLoadingScreen();*/
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.setProgress!= "undefined") { // check the method
			 Android.setProgress(progress);
		  }
   	}
}
function setLoaded() {// from 0 to 100 
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.setProgress!= "undefined") { // check the method
			 Android.setProgress(100);
		  }
   	}
}
function updateFPS(){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.updateFPS!= "undefined") { // check the method
			 Android.updateFPS(stats.domElement.children[0].children[0].textContent);
		  }
	}
}
function reloadModel(){
	var l = scene.children.length;
	setLoadingProgress(20);
	//remove everything
	while (l--) {
		if(scene.children[l] instanceof THREE.Camera) continue; //leave camera in the scene
		if(scene.children[l] instanceof THREE.PointLight) continue; //leave light in the scene
		scene.remove(scene.children[l]);
	}
	getInitialization();
	if(performance_level<=2)
		canvasMode(performance_level);
	else
		canvas_mode = false;
	setLoadingProgress(30);
	setSky();
	setAxis();
	setLoadingProgress(40);
	includeEarth();
	setLoadingProgress(50);
	setXYplane();
	setLoadingProgress(60);
	includeOrbit();
	setLoadingProgress(70);
	includeSpacecraft();
	setLoadingProgress(80);
	includeProjection();
	setLoadingProgress(100);
}
function getInitialization(){
	if (typeof Android != "undefined"){ // check the bridge 
	  if (Android.getInitializationOrbitJSON!= "undefined") { // check the method
		 
		var config = JSON.parse(Android.getInitializationOrbitJSON());
	
		show_fps = config.show_fps;
		fps_update_skips = config.fps_update_skips;

		show_sky = config.show_sky;

		show_axis = config.show_axis;
		show_axis_labels = config.show_axis_labels;

		show_earth_axis = config.show_earth_axis;
		show_earth_atmosphere = config.show_earth_atmosphere;
		show_earth_clouds = config.show_earth_clouds;
		show_xy_plane = config.show_xy_plane;
		color_xy_plane = config.color_xy_plane;
		
		show_spacecraft = config.show_spacecraft;
		spacecraft_color = config.spacecraft_color;
		show_projection = config.show_projection;
		
		orbit_color = config.orbit_color;
		
		performance_level = config.performance_level;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;

		// Segments
		if(performance_level<1)
			performance_level=1; 
		
		var earth_seg = 32 * segments_scale;
		var plane_resolution = 20*segments_scale;
		var spacecraft_seg = 16*segments_scale;
		
	  }
   }
}
function canvasMode(perfo){
	canvas_mode = true;//To prevent putting reflective materials

	if(perfo<1)
		perfo=1;

	if(perfo==1){
		show_sky = false;
	}
	
	show_earth_glow = false;
	show_earth_atmosphere = false;


	performance_level = perfo;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;
	
	var earth_seg = 32 * segments_scale;
	var plane_resolution = 20*segments_scale;
	var spacecraft_seg = 16*segments_scale;
	
}
function updateModelState(new_state){
	//if (typeof Android != "undefined"){ // check the bridge 
	  //if (Android.getStateJSON!= "undefined") { // check the method
		var state = JSON.parse(new_state);
		
		value_earth_rotation = new THREE.Quaternion(state.value_earth_rotation.x,state.value_earth_rotation.y,state.value_earth_rotation.z,state.value_earth_rotation.w);	
		value_spacecraft  = new THREE.Vector3( state.value_spacecraft[0], state.value_spacecraft[1], state.value_spacecraft[2] ); //Km
		value_orbit_a = state.value_orbit_a;
		value_orbit_e = state.value_orbit_e;
		value_orbit_i = state.value_orbit_i;
		value_orbit_w = state.value_orbit_w;
		value_orbit_raan = state.value_orbit_raan;
		
	  //}
   //}
}
