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
		  if (Android.setProgressOrbit!= "undefined") { // check the method
			 Android.setProgressOrbit(progress);
		  }
   	}
}
function setLoaded() {// from 0 to 100 
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.setProgressOrbit!= "undefined") { // check the method
			 Android.setProgressOrbit(100);
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
	getInitialization();
	setLoadingProgress(30);
	//remove everything
	while (l--) {
		if(scene.children[l] instanceof THREE.Camera) continue; //leave camera in the scene
		if(scene.children[l] instanceof THREE.AmbientLight) continue; //leave light in the scene
		if(scene.children[l].name == "STARS"){ 
			scene.children[l].visible = show_sky;
			continue; //leave sky
		}
		if(scene.children[l].name == "PROJECTION"){ 
			scene.children[l].visible = show_projection;
			continue; //leave sky
		}
		if(scene.children[l].name == "SPACECRAFT"){ 
			scene.children[l].visible = show_spacecraft;
			continue; //leave sky
		}
		if(scene.children[l].name == "EARTH"){ 
			scene.children[l].visible = show_earth;
			scene.children[l].getObjectByName("EARTH-PLANET",true).visible = show_earth;
			if(show_earth){
				scene.children[l].getObjectByName("EARTH-AXIS",true).visible = show_earth_axis;
				scene.children[l].getObjectByName("EARTH-ATM-1",true).visible = show_earth_atmosphere;
				scene.children[l].getObjectByName("EARTH-ATM-2",true).visible = show_earth_atmosphere;
				scene.children[l].getObjectByName("EARTH-CLOUDS",true).visible = show_earth_clouds;
			}else{
				scene.children[l].getObjectByName("EARTH-AXIS",true).visible = false;
				scene.children[l].getObjectByName("EARTH-ATM-1",true).visible = false;
				scene.children[l].getObjectByName("EARTH-ATM-2",true).visible = false;
				scene.children[l].getObjectByName("EARTH-CLOUDS",true).visible = false;
			}
			continue; //leave earth
		}
		scene.remove(scene.children[l]);
	}
	if(performance_level<=2)
		canvasMode(performance_level);
	else
		canvas_mode = false;
	setLoadingProgress(40);
	//setSky();
	setAxis();
	//includeEarth();
	setLoadingProgress(60);
	setXYplane();
	setLoadingProgress(80);
	includeOrbit();
	//includeSpacecraft();
	//includeProjection();
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

		show_earth = config.show_earth;
		show_earth_axis = config.show_earth_axis;
		show_earth_atmosphere = config.show_earth_atmosphere;
		show_earth_clouds = config.show_earth_clouds;
		show_xy_plane = config.show_xy_plane;
		color_xy_plane = config.color_xy_plane;
		
		show_spacecraft = config.show_spacecraft;
		spacecraft_color = config.spacecraft_color;
		show_projection = config.show_projection;
		
		orbit_color = config.orbit_color;

		ref_orbit_color = config.ref_orbit_color;
        show_ref_orbit = config.show_ref_orbit;
        ref_orbit_a = config.ref_orbit_a;
        ref_orbit_e = config.ref_orbit_e;
        ref_orbit_i = config.ref_orbit_i * (Math.PI/180);
        ref_orbit_o = config.ref_orbit_o * (Math.PI/180);
        ref_orbit_r = config.ref_orbit_r * (Math.PI/180);
		
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
function changeView(view_mode){
	switch(view_mode){
		case "Free"://free
			locked_view = false;
			break;
		case "Locked"://locked
			locked_view = true;
			break;
	}
}
function getCamDistance(){
	return Math.sqrt(camera.position.x*camera.position.x+camera.position.y*camera.position.y+camera.position.z*camera.position.z);
}
