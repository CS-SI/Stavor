function update()
{	

	//Views
	if(selected_view=="Earth"){
		camera.position = earth.position.clone().normalize().multiplyScalar(getCamDistance());
	}else if(selected_view=="Sun"){
		camera.position = sun.position.clone().normalize().multiplyScalar(getCamDistance());	
	}else if(selected_view=="S/C"){
		camera.position = init_sc_dir.clone().applyQuaternion(spacecraft.quaternion.clone().normalize()).multiplyScalar(getCamDistance());
	}else{
		controls.update();
	}

	//Ligts
	light.position.set(camera.position.x,camera.position.y,camera.position.z);
	if ( keyboard.pressed("z") ) 
	{ 
		// do something
	}
	var delta = clock.getDelta();
	
	if(show_spacecraft && sc_show_eng_texture){
		customUniforms2.time.value += delta;
	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	//			ANDROID STATS UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_fps){
		stats.update();
		fps_update_counter=fps_update_counter+1;
		if(fps_update_counter>fps_update_skips){
			fps_update_counter=0;	
			updateFPS();
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	//			SPACECRAFT UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	//if(show_spacecraft){
		spacecraft.quaternion.copy(value_attitude);
		spacecraft.matrixWorldNeedsUpdate = true;
		spacecraft.updateMatrix();
	//}
	//-----------------------------------------------------------------------------------------------------------------------
	//			SUN UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_sun){
		if(show_sun_texture){
			customUniforms.time.value += delta;
		}
		var sun_obj_pos = value_sun.clone().normalize().multiplyScalar(sun_obj_dist);
		sun.position = sun_obj_pos;
		// change the direction this spotlight is facing
		sunLight.position.set(sun.position.x,sun.position.y,sun.position.z);
		if(sun_show_line){
			// SUN LINE
			lineSun.geometry.vertices[0] = new THREE.Vector3(sun.position.x,sun.position.y,sun.position.z);
			lineSun.geometry.computeLineDistances();
			lineSun.geometry.verticesNeedUpdate = true;
			//lineSun.material.attributes.lineDistances.needsUpdate = true;
		}
		if(sun_show_dist){
			var sun_label_distance = value_sun.length()/149597871;//convert Km to AU
			var messageSun = " "+parseFloat(Math.round(sun_label_distance * 1000) / 1000).toFixed(3)+" AU ";
			contextSun.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
			contextSun.fill(); // FILL THE CONTEXT
			// get size data (height depends only on font size)
			var metrics = contextSun.measureText( messageSun );
			var textWidthSun = metrics.width;
			// background color
			contextSun.fillStyle   = "rgba(" + backgroundColorSun.r + "," + backgroundColorSun.g + ","
										  + backgroundColorSun.b + "," + backgroundColorSun.a + ")";
			// border color
			contextSun.strokeStyle = "rgba(" + borderColorSun.r + "," + borderColorSun.g + ","
										  + borderColorSun.b + "," + borderColorSun.a + ")";
			contextSun.lineWidth = borderThicknessSun;
			roundRect(contextSun, borderThicknessSun/2, borderThicknessSun/2, textWidthSun + borderThicknessSun, fontsizeSun * 1.4 + borderThicknessSun, 6);
			// 1.4 is extra height factor for text below baseline: g,j,p,q.
			// text color
			contextSun.fillStyle   = "rgba(" + fontColorSun.r + "," + fontColorSun.g + ","
										  + fontColorSun.b + "," + fontColorSun.a + ")";
			contextSun.fillText( messageSun, borderThicknessSun, fontsizeSun + borderThicknessSun);
			spriteSun.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
		}
		if(!sun_simple_glow){
			moonGlow.material.uniforms.viewVector.value = 
				new THREE.Vector3().subVectors( camera.position, moonGlow.position );
		}
		if(sun_rotates){
			//sun.rotation.x += 0.005;
			sun.rotation.y += 0.001*sun_rotation_speed;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	//			EARTH UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_earth){
		var earth_obj_pos = value_earth.clone().normalize().multiplyScalar(earth_obj_dist);
		earth.position = earth_obj_pos;
		//XGGDEBUG: maybe it does not need to update the line after updating the object position since it is link to its coordinates.
		if(earth_show_line){
			// EARTH LINE
			lineEarth.geometry.vertices[0].set(earth.position.x,earth.position.y,earth.position.z);
			lineEarth.geometry.computeLineDistances();
			lineEarth.geometry.verticesNeedUpdate = true;
			//lineEarth.material.attributes.lineDistances.needsUpdate = true;
		}
		if(earth_show_dist){
			var earth_label_distance = value_earth.length();//Km
			var messageEarth = " "+parseFloat(Math.round(earth_label_distance * 1) / 1).toFixed(0)+" Km ";
			contextEarth.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
			contextEarth.fill(); // FILL THE CONTEXT
			// get size data (height depends only on font size)
			var metricsEarth = contextEarth.measureText( messageEarth );
			var textWidthEarth = metricsEarth.width;
			// background color
			contextEarth.fillStyle   = "rgba(" + backgroundColorEarth.r + "," + backgroundColorEarth.g + ","
										  + backgroundColorEarth.b + "," + backgroundColorEarth.a + ")";
			// border color
			contextEarth.strokeStyle = "rgba(" + borderColorEarth.r + "," + borderColorEarth.g + ","
										  + borderColorEarth.b + "," + borderColorEarth.a + ")";
			contextEarth.lineWidth = borderThicknessEarth;
			roundRect(contextEarth, borderThicknessEarth/2, borderThicknessEarth/2, textWidthEarth + borderThicknessEarth, fontsizeEarth * 1.4 + borderThicknessEarth, 6);
			// 1.4 is extra height factor for text below baseline: g,j,p,q.
			// text color
			contextEarth.fillStyle   = "rgba(" + fontColorEarth.r + "," + fontColorEarth.g + ","
										  + fontColorEarth.b + "," + fontColorEarth.a + ")";
			contextEarth.fillText( messageEarth, borderThicknessEarth, fontsizeEarth + borderThicknessEarth);
			spriteEarth.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
		}
		if(earth_rotates){
			earth.rotation.y += 0.001*earth_rotation_speed;
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	//			ARROWS UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	var new_direction;
	if(show_velocity){
		new_direction = new THREE.Vector3().subVectors(value_velocity, origin).normalize();
		arrow_vel.setDirection(new_direction);
		arrow_vel.setLength(value_velocity.length()*arrow_max_length/limit_velocity, arrow_head_length, arrow_head_width);
		//arrow_vel.setColor(color_velocity);
	}
	
	if(show_acceleration){
		new_direction = new THREE.Vector3().subVectors(value_acceleration, origin).normalize();
		arrow_accel.setDirection(new_direction);
		arrow_accel.setLength(value_acceleration.length()*arrow_max_length/limit_acceleration, arrow_head_length, arrow_head_width);
		//arrow_accel.setColor(color_acceleration);
	}
	
	if(show_momentum){			
		new_direction = new THREE.Vector3().subVectors(value_momentum, origin).normalize();
		arrow_momentum.setDirection(new_direction);
		//arrow_momentum.setColor(color_momentum);
	}
	
	if(show_target_a){
		new_direction = new THREE.Vector3().subVectors(value_target_a, origin).normalize();
		target_a.setDirection(new_direction);
		//target_a.setColor(color_target_a);
	}
	
	if(show_vector_a){
		new_direction = new THREE.Vector3().subVectors(value_vector_a, origin).normalize();
		vector_a.setDirection(new_direction);
		vector_a.setLength(value_vector_a.length()*arrow_max_length/limit_vector_a, arrow_head_length, arrow_head_width);
		//vector_a.setColor(color_vector_a);
	}
	
	if(show_direction_a){
		new_direction = new THREE.Vector3().subVectors(value_direction_a, origin).normalize();
		direction_a.setDirection(new_direction);
		//direction_a.setColor(color_direction_a);
	}

}
