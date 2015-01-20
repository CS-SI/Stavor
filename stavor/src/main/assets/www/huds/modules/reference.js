function initReference() {

	
	//-----------------------------------------------------------------------------------------------------------------------
	//			REFERENCE SPHERE
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_sphere){
		var mat_sphere = new THREE.MeshPhongMaterial( { 
			color: 0x282400,
			transparent: true,
			side: THREE.FrontSide,
			metal: true,
			depthWrite: false, depthTest: false, alphaTest: 0.1,
			opacity: 0.4,
			} );	
		var mat_sphere2 = new THREE.MeshBasicMaterial( { 
			color: 0xBBBBBB,
			transparent: true,
			side: THREE.FrontSide,
			metal: true,
			depthWrite: false, depthTest: false, alphaTest: 0.1,
			opacity: 0.11,
			} );
		var mats_sphere = [mat_sphere, mat_sphere2];
		var sphere = THREE.SceneUtils.createMultiMaterialObject(new THREE.SphereGeometry( sphere_radius, sphere_segments, sphere_segments ), mats_sphere);
		
		
		sphere.position.set( 0, 0, 0 );
		sphere.renderDepth = -0.1;
		scene.add( sphere );
	}
	//-----------------------------------------------------------------------------------------------------------------------
	//			MINI SPHERES
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_mini_spheres){
		if(!canvas_mode)
			var mat_mini = new THREE.MeshPhongMaterial( { color: 0xAAAAAA, metal: true } );
		else
			var mat_mini = new THREE.MeshBasicMaterial( { color: 0xAAAAAA } );
		var miniSphere = new THREE.Mesh(new THREE.SphereGeometry( miniSphere_radius, miniSphere_seg, miniSphere_seg ), mat_mini);
		miniSphere.position.set( 0, 0, 0 );
		//if(!show_spacecraft){
			//scene.add( miniSphere );
		//}
		
		miniSphereX = miniSphere.clone();
		miniSphereX.position.set( sphere_radius+miniSphere_margin, 0, 0 );
		scene.add( miniSphereX );
		
		miniSphereXX = miniSphere.clone();
		miniSphereXX.position.set( -sphere_radius-miniSphere_margin, 0, 0 );
		scene.add( miniSphereXX );
		
		miniSphereY = miniSphere.clone();
		miniSphereY.position.set( 0, sphere_radius+miniSphere_margin, 0 );
		scene.add( miniSphereY );
		
		miniSphereYY = miniSphere.clone();
		miniSphereYY.position.set( 0, -sphere_radius-miniSphere_margin, 0 );
		scene.add( miniSphereYY );
		
		miniSphereZ = miniSphere.clone();
		miniSphereZ.position.set( 0, 0, sphere_radius+miniSphere_margin);
		scene.add( miniSphereZ );
		
		miniSphereZZ = miniSphere.clone();
		miniSphereZZ.position.set( 0, 0, -sphere_radius-miniSphere_margin);
		scene.add( miniSphereZZ );
	}
	initSpacecraft();
	
	//-----------------------------------------------------------------------------------------------------------------------
	//			SPHERE CIRCLES
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_circles){
		if(!canvas_mode)
			var mat_torus = new THREE.MeshPhongMaterial( { color: 0xAAAAAA, metal: true, transparent: false, opacity: 1.0, side: THREE.BackSide } );
		else
			var mat_torus = new THREE.MeshBasicMaterial( { color: 0xAAAAAA,  side: THREE.BackSide } );
	
		var sphere_y = new THREE.Mesh( new THREE.TorusGeometry( torus_radius, torus_tube, torus_seg_r, torus_seg_t ), mat_torus );
		sphere_y.position.set( 0, 0, 0 );
		scene.add( sphere_y );
		
		var sphere_z = new THREE.Mesh( new THREE.TorusGeometry( torus_radius, torus_tube, torus_seg_r, torus_seg_t ), mat_torus );
		sphere_z.position.set( 0, 0, 0 );
		sphere_z.rotation.x = Math.PI/2;
		scene.add( sphere_z );
		
		var sphere_x = new THREE.Mesh( new THREE.TorusGeometry( torus_radius, torus_tube, torus_seg_r, torus_seg_t ), mat_torus );
		sphere_x.position.set( 0, 0, 0 );
		sphere_x.rotation.y = Math.PI/2;
		scene.add( sphere_x );
	}
	//-----------------------------------------------------------------------------------------------------------------------
	//			REFERENCE AXIS
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_axis){
		var axis = new THREE.AxisHelper( sphere_radius );
		axis.position.set( 0, 0, 0 );
		scene.add( axis );
	}
	if(show_axis_labels){
		var sprite_X = makeTextSprite( 0, " X ", 
		{ fontsize: 48, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:255, g:174, b:0, a:1.0} } );
		sprite_X.position.set( sphere_radius+miniSphere_margin, 0, 0 );
		scene.add( sprite_X );
		
		var sprite_Y = makeTextSprite( 0, " Y ", 
		{ fontsize: 48, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:16, g:219, b:2, a:1.0} } );
		sprite_Y.position.set( 0, sphere_radius+miniSphere_margin, 0 );
		scene.add( sprite_Y );
		
		var sprite_Z = makeTextSprite( 0, " Z ", 
		{ fontsize: 48, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:50, g:119, b:255, a:1.0} } );
		sprite_Z.position.set( 0, 0, sphere_radius+miniSphere_margin );
		scene.add( sprite_Z );
	}
}
