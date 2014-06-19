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
	setLoadingProgress(10);
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
	setLoadingProgress(35);
	initReference();
	initAngles();
	setLoadingProgress(65);
	initIndicators();
	setLoadingProgress(75);
	initSun();
	setLoadingProgress(85);
	initEarth();
	changeView(selected_view);
	setLoadingProgress(100);
}
function getInitialization(){
	if (typeof Android != "undefined"){ // check the bridge 
	  if (Android.getInitializationJSON!= "undefined") { // check the method
		 
		var config = JSON.parse(Android.getInitializationJSON());
	
		show_fps = config.show_fps;
		fps_update_skips = config.fps_update_skips;

		show_sky = config.show_sky;
		show_sphere = config.show_sphere;
		show_mini_spheres = config.show_mini_spheres;
		show_circles = config.show_circles;
		show_axis = config.show_axis;
		show_axis_labels = config.show_axis_labels;
		show_planes = config.show_planes;

		//Angles and planes
		show_orbital_plane = config.show_orbital_plane;
		plane_xy_color = config.plane_xy_color;
		plane_orb_color = config.plane_orb_color;
		show_inclination = config.show_inclination;

		show_spheric_coords = config.show_spheric_coords;
		spheric_coords_selection = config.spheric_coords_selection;

		show_vectors_angle = config.show_vectors_angle;
		vectors_angle_sel1 = config.vectors_angle_sel1;
		vectors_angle_sel2 = config.vectors_angle_sel2;

		//Option blocked for robustness (S/C will show the attitude and a view will follow it)
		//show_spacecraft = config.show_spacecraft;//If set to false, instead of a S/C it will be a miniSphere in the reference position.
		show_sc_axis = config.show_sc_axis;
		sc_show_eng_texture = config.sc_show_eng_texture;
		
		show_sun = config.show_sun;
		sun_rotates = config.sun_rotates;
		sun_rotation_speed = config.sun_rotation_speed;
		show_sun_texture = config.show_sun_texture;
		sun_simple_glow = config.sun_simple_glow;//Recomended to not use the shader glow, problems in android
		sun_show_line = config.sun_show_line;
		sun_show_dist = config.sun_show_dist;

		show_earth = config.show_earth;
		earth_rotates = config.earth_rotates;
		earth_rotation_speed = config.earth_rotation_speed;
		show_earth_texture = config.show_earth_texture;
		earth_show_line = config.earth_show_line;
		earth_show_dist = config.earth_show_dist;
		
		show_velocity = config.show_velocity;
		color_velocity = config.color_velocity;
		limit_velocity = config.limit_velocity; //Km/s value corresponding to the full length arrow (touching the sphere)
		show_acceleration = config.show_acceleration;
		color_acceleration = config.color_acceleration;
		limit_acceleration = config.limit_acceleration; //Km/s2 value corresponding to the full length arrow (touching the sphere)
		show_momentum = config.show_momentum;
		color_momentum = config.color_momentum;
		show_target_a = config.show_target_a;
		color_target_a = config.color_target_a;
		value_target_a = new THREE.Vector3(config.value_target_a[0],config.value_target_a[1],config.value_target_a[2]);
		show_vector_a = config.show_vector_a;
		color_vector_a = config.color_vector_a;
		limit_vector_a = config.limit_vector_a;// In the same units of the provided value
		value_vector_a = new THREE.Vector3(config.value_vector_a[0],config.value_vector_a[1],config.value_vector_a[2]);
		show_direction_a = config.show_direction_a;
		color_direction_a = config.color_direction_a;
		value_direction_a = new THREE.Vector3(config.value_direction_a[0],config.value_direction_a[1],config.value_direction_a[2]);

		performance_level = config.performance_level;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;

		// Segments
		if(performance_level<1)
			performance_level=1;
		var segments_scale = performance_level;//Multiply segments of all geometries: 
		var sc_body_segments = 8 * segments_scale;
		var sc_window_segments = 10 * segments_scale;
		var sc_engine_segments = 10 * segments_scale;
		var sc_eng_disk_segments = sc_engine_segments;
		var sun_seg = 10 * segments_scale;
		var earth_seg = 12 * segments_scale;
		var sphere_segments = 20 * segments_scale;
		var miniSphere_seg = 7 * segments_scale;
		var torus_seg_r = 4 * segments_scale;
		var torus_seg_t = 32 * segments_scale;
		var arc_seg_r = 4 * segments_scale;
		var arc_seg_t = 32 * segments_scale;
		var arrow_segments = 4 * segments_scale;
		var momentum_segments = 4 * segments_scale;
		var target_segments = 8 * segments_scale;
		// smooth my curve over this many points
		var arc_resolution = 30*performance_level;
		var plane_resolution = 20*performance_level;
	  }
   }
}
function canvasMode(perfo){
	canvas_mode = true;//To prevent putting reflective materials

	if(perfo<1)
		perfo=1;

	if(perfo==1){
		show_sky = false;
		show_sphere = false;
	}
	//show_mini_spheres = true;
	//show_circles = true;
	//show_axis = true;
	//show_axis_labels = true;
	//show_planes = false;

	//Angles and planes
	//show_orbital_plane = false;
	//show_spheric_coords = false;
	//show_vectors_angle = false;

	//show_spacecraft = true;//If set to false, instead of a S/C it will be a miniSphere in the reference position.
	//show_sc_axis = true;		
	sc_show_eng_texture = false;

	//show_sun = false;
	sun_rotates = false;
	sun_rotation_speed = 0;
	show_sun_texture = false;
	sun_simple_glow = true;//Recomended to not use the shader glow, problems in android
	//sun_show_line = false;
	//sun_show_dist = false;

	//show_earth = false;
	earth_rotates = false;
	earth_rotation_speed = 0;
	show_earth_texture = false;
	//earth_show_line = false;
	//earth_show_dist = false;


	performance_level = perfo;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;
	
	// Segments
	var segments_scale = performance_level;//Multiply segments of all geometries: 
	var sc_body_segments = 8 * segments_scale;
	var sc_window_segments = 10 * segments_scale;
	var sc_engine_segments = 10 * segments_scale;
	var sc_eng_disk_segments = sc_engine_segments;
	var sun_seg = 10 * segments_scale;
	var earth_seg = 12 * segments_scale;
	var sphere_segments = 20 * segments_scale;
	var miniSphere_seg = 7 * segments_scale;
	var torus_seg_r = 4 * segments_scale;
	var torus_seg_t = 32 * segments_scale;
	var arc_seg_r = 4 * segments_scale;
	var arc_seg_t = 32 * segments_scale;
	var arrow_segments = 4 * segments_scale;
	var momentum_segments = 4 * segments_scale;
	var target_segments = 8 * segments_scale;
	// smooth my curve over this many points
	var arc_resolution = 30*performance_level;
	var plane_resolution = 20*performance_level;
}
function updateModelState(new_state){
	//if (typeof Android != "undefined"){ // check the bridge 
	  //if (Android.getStateJSON!= "undefined") { // check the method
		var state = JSON.parse(new_state);
		value_attitude = new THREE.Quaternion(state.value_attitude.x,state.value_attitude.y,state.value_attitude.z,state.value_attitude.w);	
				
		value_sun  = new THREE.Vector3( state.value_sun[0], state.value_sun[1], state.value_sun[2] ); //Km
		value_earth  = new THREE.Vector3( state.value_earth[0], state.value_earth[1], state.value_earth[2] ); //Km
		value_velocity  = new THREE.Vector3( state.value_velocity[0], state.value_velocity[1], state.value_velocity[2] ); //Km/s
		value_acceleration  = new THREE.Vector3( state.value_acceleration[0], state.value_acceleration[1], state.value_acceleration[2] ); //Km/s2
		value_momentum  = new THREE.Vector3( state.value_momentum[0], state.value_momentum[1], state.value_momentum[2] ); // direction --> will be normalized
	  //}
   //}
}
