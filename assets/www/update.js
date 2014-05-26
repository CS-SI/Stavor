function update()
{	

	//Views
	controls.update();
	switch(selected_view){
		case "Earth"://Earth
			camera.position = earth.position.clone().normalize().multiplyScalar(getCamDistance());
			break;
		case "Sun"://Sun
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
		default:
			break;
	}
	camera.lookAt(scene.position);

	//Ligts
	light.position.set(camera.position.x,camera.position.y,camera.position.z);
	/*if ( keyboard.pressed("z") ) 
	{ 
		// do something
	}*/
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
		if(!auto_rotate_sc){		
			spacecraft.quaternion.copy(value_attitude);
			spacecraft.matrixWorldNeedsUpdate = true;
			spacecraft.updateMatrix();
		}else{
			spacecraft.rotation.x += 0.01;
			spacecraft.rotation.y += 0.01;
		}
	//}
	//-----------------------------------------------------------------------------------------------------------------------
	//			PLANES UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_planes){
		//Compute inclination quaternion
		var norm_orbital_plane = value_velocity.clone().normalize().cross(value_earth.clone().normalize());
		var norm_rotation_earth = new THREE.Vector3(0,0,1);
		var incl_quat = new THREE.Quaternion().setFromUnitVectors( norm_rotation_earth, norm_orbital_plane );

		//Rotate orbital plane
		plane_orb.quaternion.copy(incl_quat);

		if(show_inclination){
			//Compute instant inclination angle
			var inclination = Math.asin(value_earth.z/value_earth.length());
			updateInclinationArc(inclination);

			updateInclinationSprite(inclination);
		}
	}
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
function updateInclinationSprite(inclination){
	//Update Sprite
	var messageInclination = " i="+parseFloat(Math.round(inclination * 180) / Math.PI).toFixed(1)+"º ";
	contextInclination.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
	contextInclination.fill(); // FILL THE CONTEXT
	// get size data (height depends only on font size)
	var metricsInclination = contextInclination.measureText( messageInclination );
	var textWidthInclination = metricsInclination.width;
	// background color
	contextInclination.fillStyle   = "rgba(" + backgroundColorInclination.r + "," + backgroundColorInclination.g + ","
								  + backgroundColorInclination.b + "," + backgroundColorInclination.a + ")";
	// border color
	contextInclination.strokeStyle = "rgba(" + borderColorInclination.r + "," + borderColorInclination.g + ","
								  + borderColorInclination.b + "," + borderColorInclination.a + ")";
	contextInclination.lineWidth = borderThicknessInclination;
	roundRect(contextInclination, borderThicknessInclination/2, borderThicknessInclination/2, textWidthInclination + borderThicknessInclination, fontsizeInclination * 1.4 + borderThicknessInclination, 6);
	// 1.4 is extra height factor for text below baseline: g,j,p,q.
	// text color
	contextInclination.fillStyle   = "rgba(" + fontColorInclination.r + "," + fontColorInclination.g + ","
								  + fontColorInclination.b + "," + fontColorInclination.a + ")";
	contextInclination.fillText( messageInclination, borderThicknessInclination, fontsizeInclination + borderThicknessInclination);
	spriteInclination.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
	spriteInclination.position = value_earth.clone().setZ(0).normalize().multiplyScalar(arc_sprite_radius);
}
function updateInclinationArc(inclination){
	//ReDraw Arc
	scene.remove(incl_arc);

	incl_arc = new THREE.Mesh( new THREE.TorusGeometry( arc_radius, arc_tube, arc_seg_r, arc_seg_t, inclination ), mat_arc );

	var incl_inst_rot = new THREE.Quaternion().setFromUnitVectors( axis_x, value_earth.clone().normalize() );

	incl_arc.quaternion.copy(incl_inst_rot.multiply(incl_offset));

	scene.add(incl_arc);
}
