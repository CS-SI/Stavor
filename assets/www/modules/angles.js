function initAngles(){
	//-----------------------------------------------------------------------------------------------------------------------
	//			REFERENCE PLANES
	//-----------------------------------------------------------------------------------------------------------------------

	if(show_orbital_plane){

		// points that define shape
		var pts = [], hls = [];
		var radius = sphere_radius;
		var radius_ext = planes_width;

		for ( i = 0; i < plane_resolution; i ++ ) {
			var a = 2*Math.PI * i / plane_resolution;
			pts.push( new THREE.Vector2 ( Math.cos( a ) * radius_ext, Math.sin( a ) * radius_ext ) );
			hls.push( new THREE.Vector2 ( Math.cos( a ) * radius, Math.sin( a ) * radius ) );
		}

		// shape to extrude
		var shape = new THREE.Shape( pts );
		var holesPath = new THREE.Path(hls);
		shape.holes.push(holesPath);

		// extrude options
		var options = { 
			amount: 1,              // default 100, only used when path is null
			bevelEnabled: false, 
			bevelSegments: 2, 
			steps: 1,                // default 1, try 3 if path defined
			extrudePath: null        // or path
		};

		// geometry
		var geometry = new THREE.ExtrudeGeometry( shape, options );

		// mesh
		var plane_xy = new THREE.Mesh( 
			geometry, 
			new THREE.MeshBasicMaterial( { color: plane_xy_color, transparent: true, opacity: 0.2 } )
		);
		scene.add( plane_xy );

		// mesh
		plane_orb = new THREE.Mesh( 
			geometry, 
			new THREE.MeshBasicMaterial( { color: plane_orb_color, transparent: true, opacity: 0.2 } )
		);
		scene.add( plane_orb );



		//XY plane
		//var material_plane_xy = new THREE.MeshPhongMaterial({color: plane_xy_color, transparent: true/*, depthWrite: false, depthTest: false, alphaTest: 0.1*/, opacity: 0.2, side: THREE.DoubleSide });
		/*var plane_xy = new THREE.Mesh( new THREE.RingGeometry( sphere_radius, planes_width, plane_theta_seg, plane_phi_seg, 0, Math.PI * 2 ), material_plane_xy );
		plane_xy.position.set( 0, 0, 0 );
		scene.add( plane_xy );*/


		//Orbital plane
		//var material_plane_orb = new THREE.MeshPhongMaterial({color: plane_orb_color, transparent: true/*, depthWrite: false, depthTest: false, alphaTest: 0.1*/, opacity: 0.2, side: THREE.DoubleSide });
		/*var ring_geom = new THREE.RingGeometry( sphere_radius, planes_width, plane_theta_seg, plane_phi_seg, 0, Math.PI * 2 )
		plane_orb = new THREE.Mesh( ring_geom, material_plane_orb );
		plane_orb.position.set( 0, 0, 0 );*/

		//Compute inclination quaternion
		var norm_orbital_plane = value_velocity.clone().normalize().cross(value_earth.clone().normalize());
		var norm_rotation_earth = new THREE.Vector3(0,0,1);
		var incl_quat = new THREE.Quaternion().setFromUnitVectors( norm_rotation_earth, norm_orbital_plane );

		//Rotate orbital plane
		plane_orb.quaternion.copy(incl_quat);
		plane_orb.matrixWorldNeedsUpdate = true;
		plane_orb.updateMatrix();

		if(show_inclination){
			//Compute instant inclination angle
			var inclination = Math.asin(value_earth.z/value_earth.length());
			//console.debug(inclination);

			//Compute arc angle clip points
			var clip1 = value_earth.clone().normalize().multiplyScalar(sphere_radius);
			var clip2 = value_earth.clone().setZ(0).normalize().multiplyScalar(sphere_radius);
			var clip_mid = clip1.clone().add(clip2.clone());

			//Draw Arc
			var spline = new THREE.QuadraticBezierCurve3(clip1.clone(),
			   clip_mid.clone(),
			   clip2.clone());

			var material = new THREE.LineBasicMaterial({
			    color: arc_color,
			});

			var geometry = new THREE.Geometry();
			var splinePoints = spline.getPoints(arc_resolution);

			for(var i = 0; i < splinePoints.length; i++){
			    geometry.vertices.push(splinePoints[i]);  
			}

			incl_arc = new THREE.Line(geometry, material);
			scene.add( incl_arc );

			//Sprite
			spriteInclination = makeTextSprite( 4, " i=0º ", 
				{ fontsize: 40, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:255, g:255, b:0, a:1.0} } );
			spriteInclination.position = clip_mid.clone();
			scene.add(spriteInclination);

			scene.add( incl_arc );
		} 

		//plane_orb.rotation.x += 0.3;
		scene.add( plane_orb );
	}

	//Spheric coordinates
	if(show_spheric_coords){

		var sphcoord_target = value_earth.clone().normalize().multiplyScalar(arc_sphcoord_radius);
		var sphcoord_cross = sphcoord_target.clone().setZ(0).normalize().multiplyScalar(arc_sphcoord_radius);
		var sphcoord_ref = axis_x.clone().multiplyScalar(arc_sphcoord_radius);
		var sphcoord_long_mid = sphcoord_ref.clone().add(sphcoord_cross.clone());
		var sphcoord_lat_mid = sphcoord_cross.clone().add(sphcoord_target.clone());  

		var lat=Math.atan2(sphcoord_target.z,1);
		var lng=Math.atan2(sphcoord_target.y,sphcoord_target.x);

		//Set discontinued lines
		var lineGeometry = new THREE.Geometry();
		var vertArray = lineGeometry.vertices;
		vertArray.push( sphcoord_target.clone(), new THREE.Vector3(0, 0, 0) );
		lineGeometry.computeLineDistances();
		var lineMaterial = new THREE.LineDashedMaterial( { color: arc_color, dashSize: 1, gapSize: 3 } );
		lineSpheric = new THREE.Line( lineGeometry, lineMaterial );
		scene.add(lineSpheric);
		//Set discontinued lines
		var lineGeometry = new THREE.Geometry();
		var vertArray = lineGeometry.vertices;
		vertArray.push( sphcoord_cross.clone(), new THREE.Vector3(0, 0, 0) );
		lineGeometry.computeLineDistances();
		var lineMaterial = new THREE.LineDashedMaterial( { color: arc_color, dashSize: 1, gapSize: 3 } );
		lineSpheric2 = new THREE.Line( lineGeometry, lineMaterial );
		scene.add(lineSpheric2);

		//create longitude arc

		var spline = new THREE.QuadraticBezierCurve3(sphcoord_cross.clone(),
		   sphcoord_long_mid.clone(),
		   sphcoord_ref.clone());

		var material = new THREE.LineBasicMaterial({
		    color: arc_color,
		});

		var geometry = new THREE.Geometry();
		var splinePoints = spline.getPoints(arc_resolution);

		for(var i = 0; i < splinePoints.length; i++){
		    geometry.vertices.push(splinePoints[i]);  
		}

		long_arc = new THREE.Line(geometry, material);
		scene.add( long_arc );

		//Set longitude arc sprite
		long_sprite = makeTextSprite( 5, " "+parseFloat(Math.round(lng * 180) / Math.PI).toFixed(1)+"º ", 
				{ fontsize: 40, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:255, g:255, b:0, a:1.0} } );
		long_sprite.position.set( sphcoord_long_mid.x, sphcoord_long_mid.y, sphcoord_long_mid.z);
		scene.add(long_sprite);

		//create latittude arc
		var spline = new THREE.QuadraticBezierCurve3(sphcoord_target.clone(),
		   sphcoord_lat_mid.clone(),
		   sphcoord_cross.clone());

		var material = new THREE.LineBasicMaterial({
		    color: arc_color,
		});

		var geometry = new THREE.Geometry();
		var splinePoints = spline.getPoints(arc_resolution);

		for(var i = 0; i < splinePoints.length; i++){
		    geometry.vertices.push(splinePoints[i]);  
		}

		lat_arc = new THREE.Line(geometry, material);
		scene.add( lat_arc );

		//Set latittude arc sprite
		lat_sprite = makeTextSprite( 6, " "+parseFloat(Math.round(lat * 180) / Math.PI).toFixed(1)+"º ", 
				{ fontsize: 40, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:255, g:255, b:0, a:1.0} } );
		lat_sprite.position = sphcoord_lat_mid.clone();
		scene.add(lat_sprite);

	}
	if(show_vectors_angle){
		
		var angle_vector_start = value_earth.clone().normalize().multiplyScalar(arc_vectors_radius);
		var angle_vector_end = value_momentum.clone().normalize().multiplyScalar(arc_vectors_radius);
		var angle_vector_mid = angle_vector_start.clone().add(angle_vector_end.clone());

		var dist_angle = angle_vector_start.clone().angleTo(angle_vector_end);

		//Set discontinued lines
		var lineGeometry = new THREE.Geometry();
		var vertArray = lineGeometry.vertices;
		vertArray.push( angle_vector_start.clone(), new THREE.Vector3(0, 0, 0) );
		lineGeometry.computeLineDistances();
		var lineMaterial = new THREE.LineDashedMaterial( { color: arc_color, dashSize: 1, gapSize: 3 } );
		lineAngle = new THREE.Line( lineGeometry, lineMaterial );
		scene.add(lineAngle);

		var lineGeometry2 = new THREE.Geometry();
		var vertArray2 = lineGeometry2.vertices;
		vertArray2.push( angle_vector_end.clone(), new THREE.Vector3(0, 0, 0) );
		lineGeometry2.computeLineDistances();
		var lineMaterial2 = new THREE.LineDashedMaterial( { color: arc_color, dashSize: 1, gapSize: 3 } );
		lineAngle2 = new THREE.Line( lineGeometry2, lineMaterial2 );
		scene.add(lineAngle2);

		var spline = new THREE.QuadraticBezierCurve3(angle_vector_start.clone(),
		   angle_vector_mid.clone(),
		   angle_vector_end.clone());

		var material = new THREE.LineBasicMaterial({
		    color: arc_color,
		});

		var geometry = new THREE.Geometry();
		var splinePoints = spline.getPoints(arc_resolution);

		for(var i = 0; i < splinePoints.length; i++){
		    geometry.vertices.push(splinePoints[i]);  
		}

		vectors_arc = new THREE.Line(geometry, material);
		scene.add( vectors_arc );

		//Set angles arc sprite
		vectors_sprite = makeTextSprite( 7, " "+parseFloat(Math.round(dist_angle * 180) / Math.PI).toFixed(1)+"º ", 
				{ fontsize: 40, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:255, g:255, b:0, a:1.0} } );
		vectors_sprite.position = angle_vector_mid.clone().multiplyScalar(0.7);
		scene.add(vectors_sprite);

	}
}
function updateAngles(){
	//-----------------------------------------------------------------------------------------------------------------------
	//			PLANES UPDATE
	//-----------------------------------------------------------------------------------------------------------------------

	if(show_orbital_plane){
		//Compute inclination quaternion
		var norm_orbital_plane = value_velocity.clone().normalize().cross(value_earth.clone().normalize());
		var norm_rotation_earth = new THREE.Vector3(0,0,1);
		var incl_quat = new THREE.Quaternion().setFromUnitVectors( norm_rotation_earth, norm_orbital_plane );

		//Rotate orbital plane
		plane_orb.quaternion.copy(incl_quat);

		if(show_inclination){
			//Compute instant inclination angle
			var inclination = Math.asin(value_earth.z/value_earth.length());
			
			//ReDraw Arc
			scene.remove(incl_arc);

			
			//Compute arc angle clip points
			var clip1 = value_earth.clone().normalize().multiplyScalar(sphere_radius);
			var clip2 = value_earth.clone().setZ(0).normalize().multiplyScalar(sphere_radius);
			var clip_mid = clip1.clone().add(clip2.clone()).normalize().multiplyScalar(sphere_radius);

			//Draw Arc
			var spline = new THREE.QuadraticBezierCurve3(clip1.clone(),
			   clip_mid.clone(),
			   clip2.clone());

			var material = new THREE.LineBasicMaterial({
			    color: arc_color,
			});

			var geometry = new THREE.Geometry();
			var splinePoints = spline.getPoints(arc_resolution);

			for(var i = 0; i < splinePoints.length; i++){
			    geometry.vertices.push(splinePoints[i]);  
			}

			incl_arc = new THREE.Line(geometry, material);
			scene.add( incl_arc );

			updateInclinationSprite(inclination);
			spriteInclination.position = clip_mid.clone().multiplyScalar(1.15);
		}
	}
	if(show_spheric_coords){
		
		var sphcoord_target;
		switch(spheric_coords_selection){
			case "Earth":
				sphcoord_target = value_earth.clone().normalize().multiplyScalar(arc_sphcoord_radius);
				break;
			case "Sun":
				sphcoord_target = value_sun.clone().normalize().multiplyScalar(arc_sphcoord_radius);
				break;
			case "Velocity":
				sphcoord_target = value_velocity.clone().normalize().multiplyScalar(arc_sphcoord_radius);
				break;
			case "Acceleration":
				sphcoord_target = value_acceleration.clone().normalize().multiplyScalar(arc_sphcoord_radius);
				break;
			case "Momentum":
				sphcoord_target = value_momentum.clone().normalize().multiplyScalar(arc_sphcoord_radius);
				break;
			default:
				sphcoord_target = value_velocity.clone().normalize().multiplyScalar(arc_sphcoord_radius);
				break;
		}


		var sphcoord_cross = sphcoord_target.clone().setZ(0).normalize().multiplyScalar(arc_sphcoord_radius);
		var sphcoord_ref = axis_x.clone().multiplyScalar(arc_sphcoord_radius);
		var sphcoord_long_mid = sphcoord_ref.clone().add(sphcoord_cross.clone()).normalize().multiplyScalar(arc_sphcoord_radius);
		var sphcoord_lat_mid = sphcoord_cross.clone().add(sphcoord_target.clone()).normalize().multiplyScalar(arc_sphcoord_radius);  

		var lat=Math.atan2(sphcoord_target.z,1);
		var lng=Math.atan2(sphcoord_target.y,sphcoord_target.x);

		// LINES
		lineSpheric.geometry.vertices[0].set(sphcoord_target.x,sphcoord_target.y,sphcoord_target.z);
		lineSpheric.geometry.computeLineDistances();
		lineSpheric.geometry.verticesNeedUpdate = true;

		lineSpheric2.geometry.vertices[0].set(sphcoord_cross.x,sphcoord_cross.y,sphcoord_cross.z);
		lineSpheric2.geometry.computeLineDistances();
		lineSpheric2.geometry.verticesNeedUpdate = true;

		//update longitude arc
		scene.remove( long_arc );
		var spline = new THREE.QuadraticBezierCurve3(sphcoord_cross.clone(),
		   sphcoord_long_mid.clone(),
		   sphcoord_ref.clone());

		var material = new THREE.LineBasicMaterial({
		    color: arc_color,
		});

		var geometry = new THREE.Geometry();
		var splinePoints = spline.getPoints(arc_resolution);

		for(var i = 0; i < splinePoints.length; i++){
		    geometry.vertices.push(splinePoints[i]);  
		}

		long_arc = new THREE.Line(geometry, material);
		scene.add( long_arc );

		updateLongitudeSprite(lng);
		long_sprite.position = sphcoord_long_mid.clone();

		//create latittude arc
		scene.remove( lat_arc );
		var spline = new THREE.QuadraticBezierCurve3(sphcoord_target.clone(),
		   sphcoord_lat_mid.clone(),
		   sphcoord_cross.clone());

		var material = new THREE.LineBasicMaterial({
		    color: arc_color,
		});

		var geometry = new THREE.Geometry();
		var splinePoints = spline.getPoints(arc_resolution);

		for(var i = 0; i < splinePoints.length; i++){
		    geometry.vertices.push(splinePoints[i]);  
		}

		lat_arc = new THREE.Line(geometry, material);
		scene.add( lat_arc );

		updateLatitudeSprite(lat);
		lat_sprite.position = sphcoord_lat_mid.clone();
	}
	if(show_vectors_angle){
		var angle_vector_start;
		switch(vectors_angle_sel1){
			case "Earth":
				angle_vector_start = value_earth.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Sun":
				angle_vector_start = value_sun.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Velocity":
				angle_vector_start = value_velocity.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Acceleration":
				angle_vector_start = value_acceleration.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Momentum":
				angle_vector_start = value_momentum.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			default:
				angle_vector_start = value_earth.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
		}
		var angle_vector_end;
		switch(vectors_angle_sel2){
			case "Earth":
				angle_vector_end = value_earth.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Sun":
				angle_vector_end = value_sun.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Velocity":
				angle_vector_end = value_velocity.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Acceleration":
				angle_vector_end = value_acceleration.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			case "Momentum":
				angle_vector_end = value_momentum.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
			default:
				angle_vector_end = value_earth.clone().normalize().multiplyScalar(arc_vectors_radius);
				break;
		}
		var angle_vector_mid = angle_vector_start.clone().add(angle_vector_end.clone()).normalize().multiplyScalar(arc_vectors_radius);

		var dist_angle = angle_vector_start.clone().angleTo(angle_vector_end);

		// LINES
		lineAngle.geometry.vertices[0].set(angle_vector_start.x,angle_vector_start.y,angle_vector_start.z);
		lineAngle.geometry.computeLineDistances();
		lineAngle.geometry.verticesNeedUpdate = true;

		lineAngle2.geometry.vertices[0].set(angle_vector_end.x,angle_vector_end.y,angle_vector_end.z);
		lineAngle2.geometry.computeLineDistances();
		lineAngle2.geometry.verticesNeedUpdate = true;

		scene.remove( vectors_arc );
		var spline = new THREE.QuadraticBezierCurve3(angle_vector_start.clone(),
		   angle_vector_mid.clone(),
		   angle_vector_end.clone());

		var material = new THREE.LineBasicMaterial({
		    color: arc_color,
		});

		var geometry = new THREE.Geometry();
		var splinePoints = spline.getPoints(arc_resolution);

		for(var i = 0; i < splinePoints.length; i++){
		    geometry.vertices.push(splinePoints[i]);  
		}

		vectors_arc = new THREE.Line(geometry, material);
		scene.add( vectors_arc );

		updateAnglesSprite(dist_angle);
		vectors_sprite.position = angle_vector_mid.clone().multiplyScalar(0.7);

	}

}
