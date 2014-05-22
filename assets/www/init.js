function init() 
{
	//***********************************************************************************************************************
	//		SCENE ELEMENTS
	//***********************************************************************************************************************
	setLoadingProgress(15);
	// SCENE
	scene = new THREE.Scene();
	// CAMERA
	var SCREEN_WIDTH = window.innerWidth, SCREEN_HEIGHT = window.innerHeight;
	var VIEW_ANGLE = cam_view_angle, ASPECT = SCREEN_WIDTH / SCREEN_HEIGHT, NEAR = cam_rend_near, FAR = cam_rend_far;
	camera = new THREE.PerspectiveCamera( VIEW_ANGLE, ASPECT, NEAR, FAR);
	scene.add(camera);
	camera.position = cam_init_pos;
	camera.lookAt(scene.position);	
	// RENDERER
	if ( Detector.webgl ){
		renderer = new THREE.WebGLRenderer( {antialias:true} );
	}else{
		renderer = new THREE.CanvasRenderer();
		alert('WebGL not supported in this device');
		canvasMode();
	}
	renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	container = document.getElementById( 'ThreeJS' );
	container.appendChild( renderer.domElement );
	// EVENTS
	THREEx.WindowResize(renderer, camera);
	THREEx.FullScreen.bindKey({ charCode : 'm'.charCodeAt(0) });
	// CONTROLS
	controls = new THREE.OrbitControls( camera, renderer.domElement );
	// STATS
	stats = new Stats();
	stats.domElement.style.position = 'absolute';
	stats.domElement.style.top = '0px';
	stats.domElement.style.zIndex = 100;
	//stats.domElement.style.webkitTransform = 0;
	//container.appendChild( stats.domElement );
	// LIGHT
	light = new THREE.PointLight(0xE0E0E0);
	//light.position.set(200,200,200);
	scene.add(light);
	
	if(!canvas_mode){
		sunLight = new THREE.PointLight(0xffef7a);
		//sunLight.position.set(200,200,200);
		scene.add(sunLight);
	}
	// ambient
	var ambient = new THREE.AmbientLight( 0xFFFFFF );
	//scene.add( ambient );
	
	
	
	//***********************************************************************************************************************
	//		STATIC ELEMENTS
	//***********************************************************************************************************************
	setLoadingProgress(35);
	
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
		if(!show_spacecraft){
			scene.add( miniSphere );
		}
		
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
	//-----------------------------------------------------------------------------------------------------------------------
	//			SPACECRAFT
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_spacecraft){
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
			var noiseTexture = new THREE.ImageUtils.loadTexture( 'textures/lava/cloud.png' );
			noiseTexture.wrapS = noiseTexture.wrapT = THREE.RepeatWrapping;	
			var waterTexture = new THREE.ImageUtils.loadTexture( 'textures/water/engine.jpg' );
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
		miniSphereX.add( sprite_X );
		
		var sprite_X = makeTextSprite( 0, " Y ", 
		{ fontsize: 48, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:16, g:219, b:2, a:1.0} } );
		miniSphereY.add( sprite_X );
		
		var sprite_Z = makeTextSprite( 0, " Z ", 
		{ fontsize: 48, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:50, g:119, b:255, a:1.0} } );
		miniSphereZ.add( sprite_Z );
	}
	//-----------------------------------------------------------------------------------------------------------------------
	//			REFERENCE PLANES
	//-----------------------------------------------------------------------------------------------------------------------
	
	// IMPLEMENT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	if(show_planes){
		var material_op = new THREE.MeshNormalMaterial({color: 0x0000FF, transparent: true, depthWrite: false, depthTest: false, alphaTest: 0.1, opacity: 0.1, side: THREE.DoubleSide });
		orbital_plane = new THREE.Mesh( new THREE.RingGeometry( sphere_radius/3, sphere_radius, 30, 10, 0, Math.PI * 2 ), material_op );
		orbital_plane.position.set( 0, 0, 0 );
		scene.add( orbital_plane );
	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	//			SKY
	//-----------------------------------------------------------------------------------------------------------------------
	
	if(show_sky){
		// create the geometry sphere
		var sky_geometry  = new THREE.SphereGeometry(1000, 32, 32);
		// create the material, using a texture of startfield
		var sky_material  = new THREE.MeshBasicMaterial();
		sky_material.map   = THREE.ImageUtils.loadTexture('textures/sky/stars.jpg');
		sky_material.map.wrapS = sky_material.map.wrapT = THREE.RepeatWrapping;
		sky_material.map.repeat.set( 8, 8 ); 
		sky_material.side  = THREE.BackSide;
		// create the mesh based on geometry and material
		var sky_mesh  = new THREE.Mesh(sky_geometry, sky_material);
		scene.add(sky_mesh);
	}
	
	//***********************************************************************************************************************
	//		DYNAMIC ELEMENTS
	//***********************************************************************************************************************
	setLoadingProgress(65);
	//-----------------------------------------------------------------------------------------------------------------------
	//			ARROWS
	//-----------------------------------------------------------------------------------------------------------------------
	
	// ARROWS
	var direction;
	
	// BASIC
	if(show_velocity){
		direction = new THREE.Vector3().subVectors(value_velocity, origin).normalize();		
		arrow_vel = new THREE.ArrowHelper(direction, origin, arrow_max_length, color_velocity, arrow_head_length, arrow_head_width, arrow_segments, canvas_mode);
		scene.add(arrow_vel);
	}
	
	if(show_acceleration){
		direction = new THREE.Vector3().subVectors(value_acceleration, origin).normalize();
		arrow_accel = new THREE.ArrowHelper(direction, origin, arrow_max_length, color_acceleration, arrow_head_length, arrow_head_width, arrow_segments, canvas_mode);
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
		vector_a = new THREE.ArrowHelper(direction, origin, arrow_max_length, color_vector_a, arrow_head_length, arrow_head_width, arrow_segments, canvas_mode);
		scene.add(vector_a);
	}
	
	if(show_direction_a){
		direction = new THREE.Vector3().subVectors(value_direction_a, origin).normalize();
		direction_a = new THREE.MomentumHelper(direction, origin, momentum_length, color_direction_a, momentum_head_length, momentum_head_width, momentum_segments, canvas_mode);
		scene.add(direction_a);
	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	//			SUN
	//-----------------------------------------------------------------------------------------------------------------------
	setLoadingProgress(75);
	if(show_sun){
		if(show_sun_texture){
			// base image texture for mesh
			var lavaTexture = new THREE.ImageUtils.loadTexture( 'textures/lava/lava.jpg');
			lavaTexture.wrapS = lavaTexture.wrapT = THREE.RepeatWrapping; 
			// multiplier for distortion speed 		
			var baseSpeed = 0.02;
			// number of times to repeat texture in each direction
			var repeatS = repeatT = 2.0;
			// texture used to generate "randomness", distort all other textures
			var noiseTexture = new THREE.ImageUtils.loadTexture( 'textures/lava/cloud.png' );
			noiseTexture.wrapS = noiseTexture.wrapT = THREE.RepeatWrapping; 
			// magnitude of noise effect
			var noiseScale = 0.5;
			// texture to additively blend with base image texture
			var blendTexture = new THREE.ImageUtils.loadTexture( 'textures/lava/lava.jpg' );
			blendTexture.wrapS = blendTexture.wrapT = THREE.RepeatWrapping; 
			// multiplier for distortion speed 
			var blendSpeed = 0.08;
			// adjust lightness/darkness of blended texture
			var blendOffset = 0.45;
			// texture to determine normal displacement
			var bumpTexture = noiseTexture;
			bumpTexture.wrapS = bumpTexture.wrapT = THREE.RepeatWrapping; 
			// multiplier for distortion speed 		
			var bumpSpeed   = 0.5;
			// magnitude of normal displacement
			var bumpScale   = 2.0;
			
			// use "this." to create global object
			this.customUniforms = {
				baseTexture: 	{ type: "t", value: lavaTexture },
				baseSpeed:		{ type: "f", value: baseSpeed },
				repeatS:		{ type: "f", value: repeatS },
				repeatT:		{ type: "f", value: repeatT },
				noiseTexture:	{ type: "t", value: noiseTexture },
				noiseScale:		{ type: "f", value: noiseScale },
				blendTexture:	{ type: "t", value: blendTexture },
				blendSpeed: 	{ type: "f", value: blendSpeed },
				blendOffset: 	{ type: "f", value: blendOffset },
				bumpTexture:	{ type: "t", value: bumpTexture },
				bumpSpeed: 		{ type: "f", value: bumpSpeed },
				bumpScale: 		{ type: "f", value: bumpScale },
				alpha: 			{ type: "f", value: 1.0 },
				time: 			{ type: "f", value: 1.0 }
			};
			
			// create custom material from the shader code above
			//   that is within specially labeled script tags
			var customMaterialSun = new THREE.ShaderMaterial( 
			{
				uniforms: customUniforms,
				vertexShader:   THREE.ShaderSun.vertexShader,
				fragmentShader: THREE.ShaderSun.fragmentShader
			}   );
		}else{//Not using texture, solid color instead
			var customMaterialSun = new THREE.MeshPhongMaterial( { color: sun_solid_color, metal: true } );
		}
		
		var sunGeometry = new THREE.SphereGeometry( sun_radius, sun_seg, sun_seg );
		sun = new THREE.Mesh(	sunGeometry, customMaterialSun );
		sun.position.set(0, 85, 85);//Don't remove or the dashed material is not created
		scene.add( sun );
		
		if(!sun_simple_glow){
			// SHADER GLOW EFFECT
			var customMaterialGlow = new THREE.ShaderMaterial( 
			{
				uniforms: 
				{ 
					"c":   { type: "f", value: 0.1 },
					"p":   { type: "f", value: 3.4 },
					glowColor: { type: "c", value: new THREE.Color(0xffff00) },
					viewVector: { type: "v3", value: camera.position }
				},
				vertexShader:   THREE.ShaderGlow.vertexShader,
				fragmentShader: THREE.ShaderGlow.fragmentShader,
				side: THREE.FrontSide,
				blending: THREE.AdditiveBlending,
				transparent: true
			}   );
				
			sunGlow = new THREE.Mesh( sunGeometry.clone(), customMaterialGlow.clone() );
			
			sunGlow.position = sun.position;
			sunGlow.scale.multiplyScalar(1.8);
			scene.add( sunGlow );
		}else{
			// SUPER SIMPLE GLOW EFFECT
			// use sprite because it appears the same from all angles
			var spriteMaterial = new THREE.SpriteMaterial( 
			{ 
				map: new THREE.ImageUtils.loadTexture( 'textures/lava/glow.png' ), 
				useScreenCoordinates: false,// alignment: THREE.SpriteAlignment.center,
				color: 0xf79216, transparent: false, blending: THREE.AdditiveBlending
			});
			sunGlow = new THREE.Sprite( spriteMaterial );
			sunGlow.scale.set(20, 20, 1.0);
			sun.add(sunGlow); // this centers the glow at the mesh
		}
		
		if(sun_show_line){
			// SUN LINE
			var lineGeometrySun = new THREE.Geometry();
			lineGeometrySun.dynamic = true;
			var vertArraySun = lineGeometrySun.vertices;
			vertArraySun.push( new THREE.Vector3(sun.position.x,sun.position.y,sun.position.z), new THREE.Vector3(0, 0, 0) );
			lineGeometrySun.computeLineDistances();
			var lineMaterialSun = new THREE.LineDashedMaterial( { color: 0xffd800, dashSize: 2, gapSize: 2 } );
			lineSun = new THREE.Line( lineGeometrySun, lineMaterialSun );
			scene.add(lineSun);
		}
		
		if(sun_show_dist){
			// Sun Sprite
			spriteSun = makeTextSprite( 1, " 1.05 AU ", 
				{ fontsize: 24, borderColor: {r:255, g:255, b:255, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:1.5}, fontColor: {r:255, g:255, b:255, a:1.0} } );
			sun.add( spriteSun );
		}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	//			EARTH
	//-----------------------------------------------------------------------------------------------------------------------
	setLoadingProgress(85);
	// EARTH
	if(show_earth){
		var earth_geometry = new THREE.SphereGeometry( earth_radius, earth_seg, earth_seg ) ;
		if(show_earth_texture){	 
			var earth_material = new THREE.MeshBasicMaterial( { map: THREE.ImageUtils.loadTexture( 'textures/earth/Land_ocean_ice_cloud_2048.jpg' ), overdraw: true } )
		}else{
			var earth_material = new THREE.MeshPhongMaterial( { color: earth_solid_color, metal: true } );
		}
		earth = new THREE.Mesh( earth_geometry, earth_material ) ;
		earth.position.set(75, 0, 75);//Don't remove or the dashed material is not created
		scene.add( earth );
		
		if(earth_show_line){
			// EARTH LINE
			var lineGeometryEarth = new THREE.Geometry();
			var vertArrayEarth = lineGeometryEarth.vertices;
			vertArrayEarth.push( new THREE.Vector3(earth.position.x,earth.position.y,earth.position.z), new THREE.Vector3(0, 0, 0) );
			lineGeometryEarth.computeLineDistances();
			var lineMaterialEarth = new THREE.LineDashedMaterial( { color: 0x0099ff, dashSize: 2, gapSize: 2 } );
			lineEarth = new THREE.Line( lineGeometryEarth, lineMaterialEarth );
			scene.add(lineEarth);
		}
		if(earth_show_dist){
			// Earth Sprite
			spriteEarth = makeTextSprite( 2, " 36150 Km ", 
				{ fontsize: 20, borderColor: {r:255, g:255, b:255, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:255, g:255, b:255, a:1.0} } );
			earth.add( spriteEarth );
		}
	}
	changeView(selected_view);
	setLoadingProgress(100);
}
