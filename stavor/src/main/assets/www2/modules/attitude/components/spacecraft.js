function initSpacecraft(){
	//-----------------------------------------------------------------------------------------------------------------------
	//			SPACECRAFT
	//-----------------------------------------------------------------------------------------------------------------------
	if(true){
		spacecraft = new THREE.Object3D();
		if(show_sc_axis){
			var sc_axis = new THREE.AxisHelper( sc_axis_lenght );
			sc_axis.position.set( 0, 0, 0 );
			spacecraft.add( sc_axis );
		}
		if(!canvas_mode)
			var sc_material = new THREE.MeshLambertMaterial( { color: sc_body_color, metal: true, shading: THREE.SmoothShading, blending: THREE.AdditiveBlending, vertexColors: THREE.VertexColors } );
		else
			var sc_material = new THREE.MeshBasicMaterial( { color: sc_body_color } );
		var sc_geometry = new THREE.CylinderGeometry( 6, 1, 15, sc_body_segments );
		var sc = new THREE.Mesh( sc_geometry, sc_material );
		sc.position.set( 0, 0, 0 );
		sc.rotation.x = -Math.PI/2;
		spacecraft.add(sc);
		//scene.add( sc );
		
		if(!canvas_mode)
			var mat_window = new THREE.MeshPhongMaterial( { color: sc_window_color, metal: true, side: THREE.FrontSide } );
		else
			var mat_window = new THREE.MeshBasicMaterial( { color: sc_window_color, side: THREE.FrontSide } );
		var sc_window = new THREE.Mesh(new THREE.SphereGeometry( 3, sc_window_segments, sc_window_segments ), mat_window);
		sc_window.position.set( 0, 1.5, -2 );
		spacecraft.add(sc_window);
		//scene.add( sc_window );
		
		var eng_geometry = new THREE.CylinderGeometry( 2, 2.5, 2, sc_engine_segments );
		if(!canvas_mode)
			var eng_material = new THREE.MeshPhongMaterial( { color: sc_engine_color, metal: true, side: THREE.FrontSide } );
		else
			var eng_material = new THREE.MeshBasicMaterial( { color: sc_engine_color, side: THREE.FrontSide } );
		var eng = new THREE.Mesh( eng_geometry, eng_material );
		eng.rotation.x = -Math.PI/2;
		eng.position.set( -2.5, 0, -8 );
		spacecraft.add(eng);
		//scene.add( eng );
		
		var eng2 = eng.clone();
		eng2.rotation.x = -Math.PI/2;
		eng2.position.set( 2.5, 0, -8 );
		spacecraft.add(eng2);
		//scene.add( eng2 );
		
		if (sc_show_eng_texture){
			var noiseTexture = new THREE.ImageUtils.loadTexture( 'modules/attitude/textures/lava/cloud.png' );
			noiseTexture.wrapS = noiseTexture.wrapT = THREE.RepeatWrapping;	
			var waterTexture = new THREE.ImageUtils.loadTexture( 'modules/attitude/textures/water/engine.jpg' );
			waterTexture.wrapS = waterTexture.wrapT = THREE.RepeatWrapping; 
			// use "this." to create global object
			this.customUniforms2 = {
				baseTexture: 	{ type: "t", value: waterTexture },
				baseSpeed: 		{ type: "f", value: 1.15 },
				noiseTexture: 	{ type: "t", value: noiseTexture },
				noiseScale:		{ type: "f", value: 0.5 },
				alpha: 			{ type: "f", value: 0.8 },
				time: 			{ type: "f", value: 1.0 }
			};

			// create custom material from the shader code above
			//   that is within specially labeled script tags
			var customMaterial2 = new THREE.ShaderMaterial( 
			{
				uniforms: customUniforms2,
				vertexShader:   THREE.ShaderEngine.vertexShader,
				fragmentShader: THREE.ShaderEngine.fragmentShader
			}   );
		 
			// other material properties
			
			//customMaterial2.transparent = true;
		}else{
			if(!canvas_mode)
				var customMaterial2 = new THREE.MeshPhongMaterial( { color: sc_eng_solid_color, metal: true } );
			else
				var customMaterial2 = new THREE.MeshBasicMaterial( { color: sc_eng_solid_color } );
		}
		customMaterial2.side = THREE.BackSide;
		// apply the material to a surface    innerRadius, outerRadius, thetaSegments, phiSegments, thetaStart, thetaLength)
		var flatGeometry = new THREE.RingGeometry( 0.5, 2, 15 );
		var surface = new THREE.Mesh( flatGeometry, customMaterial2 );
		//surface.rotation.z = -Math.PI/2;
		surface.position.set( 2.5, 0, -9.1 );
		spacecraft.add(surface);
		//scene.add( surface );
		var engine_surface2 = surface.clone(); 
		engine_surface2.position.set( -2.5, 0, -9.1 );
		spacecraft.add(engine_surface2);
		//scene.add( engine_surface2 );

		spacecraft.scale.multiplyScalar(sc_scale);
		scene.add(spacecraft);
	}
}
function updateSpacecraft(){
	//-----------------------------------------------------------------------------------------------------------------------
	//			SPACECRAFT UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	
	if(true && sc_show_eng_texture){
		customUniforms2.time.value += delta;
	}
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
}
