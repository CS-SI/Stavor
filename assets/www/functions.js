function showAndroidToast(toast) {
		if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.showToast!= "undefined") { // check the method
			 Android.showToast(toast);
		  }
	   }
    }
	function setLoadingProgress(progress) {// from 0 to 100 
		if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.setProgress!= "undefined") { // check the method
			 Android.setProgress(progress);
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
	function getInitialization(){
		if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.getInitializationJSON!= "undefined") { // check the method
			 
			var config = JSON.parse(Android.getInitializationJSON());
		
			show_fps = config.show_fps;
			fps_update_skips = config.fps_update_skips;

			show_sky = config.show_sky;
			show_sphere = config.show_sphere;
			//show_mini_spheres = config.show_mini_spheres; //option blocked for robustness (axis labels go clipped there)
			show_circles = config.show_circles;
			show_axis = config.show_axis;
			show_axis_labels = config.show_axis_labels;
			show_planes = config.show_planes;

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
			show_vector_a = config.show_vector_a;
			color_vector_a = config.color_vector_a;
			limit_vector_a = config.limit_vector_a;// In the same units of the provided value
			show_direction_a = config.show_direction_a;
			color_direction_a = config.color_direction_a;

			performance_level = config.performance_level;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;
		  }
	   }
	}
	function canvasMode(){
		canvas_mode = true;//To prevent putting reflective materials
	
		show_sky = false;
		show_sphere = false;
		//show_mini_spheres = true;
		show_circles = true;
		show_axis = true;
		show_axis_labels = true;
		show_planes = false;

		//show_spacecraft = true;//If set to false, instead of a S/C it will be a miniSphere in the reference position.
		show_sc_axis = true;		
		sc_show_eng_texture = false;
		
		show_sun = false;
		sun_rotates = false;
		sun_rotation_speed = 0;
		show_sun_texture = false;
		sun_simple_glow = true;//Recomended to not use the shader glow, problems in android
		sun_show_line = false;
		sun_show_dist = false;

		show_earth = false;
		earth_rotates = false;
		earth_rotation_speed = 0;
		show_earth_texture = false;
		earth_show_line = false;
		earth_show_dist = false;
		

		performance_level = 1;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;
		
		// Segments
		segments_scale = performance_level;//Multiply segments of all geometries: 
		sc_body_segments = 4 * segments_scale;
		sc_window_segments = 8 * segments_scale;
		sc_engine_segments = 8 * segments_scale;
		sc_eng_disk_segments = sc_engine_segments;
		sun_seg = 10 * segments_scale;
		earth_seg = 12 * segments_scale;
		sphere_segments = 20 * segments_scale;
		miniSphere_seg = 7 * segments_scale;
		torus_seg_r = 4 * segments_scale;
		torus_seg_t = 32 * segments_scale;
		arrow_segments = 4 * segments_scale;
		momentum_segments = 4 * segments_scale;
		target_segments = 8 * segments_scale;
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
			value_target_a  = new THREE.Vector3( state.value_target_a[0], state.value_target_a[1], state.value_target_a[2] ); // direction --> will be normalized
			value_vector_a  = new THREE.Vector3( state.value_vector_a[0], state.value_vector_a[1], state.value_vector_a[2] ); // will be normalized by Limit in same units
			value_direction_a  = new THREE.Vector3( state.value_direction_a[0], state.value_direction_a[1], state.value_direction_a[2] ); // direction --> will be normalized
		  //}
	   //}
	}
	function getCamDistance(){
		return Math.sqrt(camera.position.x*camera.position.x+camera.position.y*camera.position.y+camera.position.z*camera.position.z);
	}
	function getCamEquilater(){
		return getCamDistance()/Math.sqrt(3);
	}
	function restoreMiniSpheres(){
		if(show_mini_spheres){
			miniSphereX.visible=true;
			miniSphereXX.visible=true;
			miniSphereY.visible=true;
			miniSphereYY.visible=true;
			miniSphereZ.visible=true;
			miniSphereZZ.visible=true;
		}
	}
	function restorePlanets(){
		if(show_earth){
			earth.visible=true;
		}
		if(show_sun){
			sun.visible=true;
			sunGlow.visible=true;
		}
	}
	var selected_view = "XYZ";
	function changeView(view_mode){
		restoreMiniSpheres();
		restorePlanets();
		switch(view_mode){
			case "XYZ"://xyz
				camera.position = new THREE.Vector3(getCamEquilater(),getCamEquilater(),getCamEquilater());
				camera.up = new THREE.Vector3(-0.577,0.577,-0.577);
				break;
			case "X"://+X
				miniSphereX.visible=false;
				camera.position = new THREE.Vector3(getCamDistance(),0,0);
				camera.up = new THREE.Vector3(0,1,0);
				break;
			case "-X"://-X
				miniSphereXX.visible=false;
				camera.position = new THREE.Vector3(-getCamDistance(),0,0);
				camera.up = new THREE.Vector3(0,1,0);
				break;
			case "Y"://+Y
				miniSphereY.visible=false;
				camera.position = new THREE.Vector3(0,getCamDistance(),0);
				camera.up = new THREE.Vector3(0,0,1);
				break;
			case "-Y"://-Y
				miniSphereYY.visible=false;
				camera.position = new THREE.Vector3(0,-getCamDistance(),0);
				camera.up = new THREE.Vector3(0,0,1);
				break;
			case "Z"://+Z
				miniSphereZ.visible=false;
				camera.position = new THREE.Vector3(0,0,getCamDistance());
				camera.up = new THREE.Vector3(0,1,0);
				break;
			case "-Z"://-Z
				miniSphereZZ.visible=false;
				camera.position = new THREE.Vector3(0,0,-getCamDistance());
				camera.up = new THREE.Vector3(0,1,0);
				break;
			case "Earth"://Earth
				earth.visible = false;
				camera.position = earth.position.clone().normalize().multiplyScalar(getCamDistance());
				break;
			case "Sun"://Sun
				sun.visible = false;
				sunGlow.visible=false;
				camera.position = sun.position.clone().normalize().multiplyScalar(getCamDistance());
				break;
			case "S/C-XYZ"://Spacecraft
				camera.position = init_sc_dir_xyz.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
				camera.up = init_sc_up_xyz.clone().applyQuaternion(spacecraft.quaternion.clone().normalize());
				break;
			case "S/C-Rear"://Spacecraft
				camera.position = init_sc_dir_rear.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
				camera.up = init_sc_up_rear.clone().applyQuaternion(spacecraft.quaternion.clone().normalize());
				break;
			case "S/C-Front"://Spacecraft
				camera.position = init_sc_dir_front.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
				camera.up = init_sc_up_front.clone().applyQuaternion(spacecraft.quaternion.clone().normalize());
				break;
			case "S/C-Top"://Spacecraft
				camera.position = init_sc_dir_top.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
				camera.up = init_sc_up_top.clone().applyQuaternion(spacecraft.quaternion.clone().normalize());
				break;
			case "S/C-Bottom"://Spacecraft
				camera.position = init_sc_dir_bottom.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
				camera.up = init_sc_up_bottom.clone().applyQuaternion(spacecraft.quaternion.clone().normalize());
				break;
			case "S/C-Left"://Spacecraft
				camera.position = init_sc_dir_left.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
				camera.up = init_sc_up_left.clone().applyQuaternion(spacecraft.quaternion.clone().normalize());
				break;
			case "S/C-Right"://Spacecraft
				camera.position = init_sc_dir_right.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
				camera.up = init_sc_up_right.clone().applyQuaternion(spacecraft.quaternion.clone().normalize());
				break;
			default://xyz
				camera.position = new THREE.Vector3(getCamEquilater(),getCamEquilater(),getCamEquilater());
				break;
		}
		selected_view = view_mode;
		camera.lookAt(scene.position);
	}
	function onWindowResize() {

		camera.aspect = window.innerWidth / window.innerHeight;
		camera.updateProjectionMatrix();

		renderer.setSize( window.innerWidth, window.innerHeight );

		controls.handleResize();

		render();

	}


/*var lookat = function(vecstart,vecEnd,vecUp){

        var temp = new THREE.Matrix4()
        temp.lookAt(vecEnd,vecstart,vecUp);

var m00 = temp.n11, m10 = temp.n21, m20 = temp.n31,
m01 = temp.n12, m11 = temp.n22, m21 = temp.n32,
m02 = temp.n13, m12 = temp.n23, m22 = temp.n33;

        var t = m00 + m11 + m22,s,x,y,z,w;

        if (t > 0) { 
          s =  Math.sqrt(t+1)*2; 
          w = 0.25 * s;            
          x = (m21 - m12) / s;
          y = (m02 - m20) / s;
          z = (m10 - m01) / s;
        } else if ((m00 > m11) && (m00 > m22)) {
          s =  Math.sqrt(1.0 + m00 - m11 - m22)*2;
          x = s * 0.25;
          y = (m10 + m01) / s;
          z = (m02 + m20) / s;
          w = (m21 - m12) / s;
        } else if (m11 > m22) {
          s =  Math.sqrt(1.0 + m11 - m00 - m22) *2; 
          y = s * 0.25;
          x = (m10 + m01) / s;
          z = (m21 + m12) / s;
          w = (m02 - m20) / s;
        } else {
          s =  Math.sqrt(1.0 + m22 - m00 - m11) *2; 
          z = s * 0.25;
          x = (m02 + m20) / s;
          y = (m21 + m12) / s;
          w = (m10 - m01) / s;
        }

        var rotation = new THREE.Quaternion(x,y,z,w);
        rotation.normalize();
        return rotation;
    };*/
