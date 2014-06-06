function initIndicators() {
		//-----------------------------------------------------------------------------------------------------------------------
	//			ARROWS
	//-----------------------------------------------------------------------------------------------------------------------
	
	// ARROWS
	var direction;
	
	// BASIC
	if(show_velocity){
		direction = new THREE.Vector3().subVectors(value_velocity, origin).normalize();		
		arrow_vel = new THREE.VectorHelper(direction, origin, arrow_max_length, color_velocity, arrow_head_length, arrow_head_width, arrow_segments, canvas_mode);
		scene.add(arrow_vel);
	}
	
	if(show_acceleration){
		direction = new THREE.Vector3().subVectors(value_acceleration, origin).normalize();
		arrow_accel = new THREE.VectorHelper(direction, origin, arrow_max_length, color_acceleration, arrow_head_length, arrow_head_width, arrow_segments, canvas_mode);
		scene.add(arrow_accel);
	}
	
	if(show_momentum){
		direction = new THREE.Vector3().subVectors(value_momentum, origin).normalize();
		arrow_momentum = new THREE.MomentumHelper(direction, origin, momentum_length, color_momentum, momentum_head_length, momentum_head_width, momentum_segments, canvas_mode);
		scene.add(arrow_momentum);
	}
	
	// EXTRA
	if(show_target_a){
		direction = new THREE.Vector3().subVectors(value_target_a, origin).normalize();
		target_a = new THREE.TargetHelper(direction, origin, target_length, color_target_a, target_head_length, target_head_width, target_segments, canvas_mode);
		scene.add(target_a);
	}
	
	if(show_vector_a){
		direction = new THREE.Vector3().subVectors(value_vector_a, origin).normalize();
		vector_a = new THREE.VectorHelper(direction, origin, arrow_max_length, color_vector_a, arrow_head_length, arrow_head_width, arrow_segments, canvas_mode);
		scene.add(vector_a);
	}
	
	if(show_direction_a){
		direction = new THREE.Vector3().subVectors(value_direction_a, origin).normalize();
		direction_a = new THREE.MomentumHelper(direction, origin, momentum_length, color_direction_a, momentum_head_length, momentum_head_width, momentum_segments, canvas_mode);
		scene.add(direction_a);
	}

}
function updateIndicators() {
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
