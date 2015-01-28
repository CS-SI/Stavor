var Orbit = function()
{	
	var orbit;
	var spacecraft;
	var projection, starSphere, containerEarth;
	// MAIN
	//***********************************************************************************************************************
	//		GLOBAL VARIABLES
	//***********************************************************************************************************************
	setLoadingProgress(5);
	var container, scene, camera, renderer, controls, stats, light, delta;
	var clock = new THREE.Clock();
	var onRenderFcts= [];
	var fps_update_counter = 0;
	//-----------------------------------------------------------------------------------------------------------------------
	//			SCENE PARAMS (Hard-coded parameters)
	//-----------------------------------------------------------------------------------------------------------------------
	var canvas_mode = false;
	var cam_init_pos  = new THREE.Vector3(0,0,13);
	var cam_view_angle = 25;
	var cam_rend_near = 0.1;
	var cam_rend_far = 20000;

	var accel_time = 10;

	var earth_radius = 0.5;
	var axis_radius = earth_radius*1.35;
	var axis_earth_radius = earth_radius*1.15;
	var dist_scale = earth_radius/6371000;
	
	var planes_radius = axis_radius;
	var planes_width = axis_radius*4;
	
	var spacecraft_radius = 0.03;
	
	
	
//-----------------------------------------------------------------------------------------------------------------------
//			PERFORMANCE VALUES (Set at initialization)
//-----------------------------------------------------------------------------------------------------------------------
	
	/*var show_fps = true;//Show FPS stats in Android
	var fps_update_skips = 60;

	var show_sky = true;

	var show_axis = true;
	var show_axis_labels = true;

	var show_earth = true;
	var show_earth_axis = true;
	var show_earth_atmosphere = true;
	var show_earth_clouds = true;
	var show_xy_plane = true;
	var color_xy_plane = 0xff0094;
	
	var show_spacecraft = true;
	var spacecraft_color = 0xfff200;
	var show_projection = true;
	
	var orbit_color = 0x00ff00;
	
	var performance_level = 3;//1: VeryLow, 2: Low, 3: Normal, 4: High, 5: VeryHigh, 6: Ultra ...;
	
	getInitialization();//If used in Android, update the init params with the Android configuration
	
	// Segments
	if(performance_level<1)
		performance_level=1;
	var segments_scale = performance_level;//Multiply segments of all geometries: 
	
	var earth_seg = 32 * segments_scale;
	var plane_resolution = 20*segments_scale;
	var spacecraft_seg = 16*segments_scale;*/
//-----------------------------------------------------------------------------------------------------------------------
//			DYNAMIC VALUES (Updated at each cycle)
//-----------------------------------------------------------------------------------------------------------------------
	
	/*var value_spacecraft  = new THREE.Vector3(42164000.0,7.337791634217176E-12,-2.010800849831316E-12); //Km
	var value_earth_rotation = new THREE.Quaternion(0,0,0,1);
	var value_orbit_a = 24396159;
	var value_orbit_e = 0.73;
	var value_orbit_i = Math.PI/9;
	var value_orbit_w = Math.PI;
	var value_orbit_raan = 0;*/

	var locked_view = false;
	
//-----------------------------------------------------------------------------------------------------------------------

	setLoadingProgress(1);
	initScene();
	setLoadingProgress(3);
	setSky();
	setAxis();
	setLoadingProgress(6);
	includeEarth();
	setLoadingProgress(8);
	setXYplane();
	setLoadingProgress(10);
	includeSpacecraft();
	setLoadingProgress(14);
	includeProjection();
	setLoadingProgress(18);
	includeOrbit();
	
	
	setLoadingProgress(100);

	
	//////////////////////////////////////////////////////////////////////////////////
	//		render the scene						//
	//////////////////////////////////////////////////////////////////////////////////
	onRenderFcts.push(function(){
		renderer.render( scene, camera );		
	})

	//////////////////////////////////////////////////////////////////////////////////
	//		VIEWS        						        //
	//////////////////////////////////////////////////////////////////////////////////
	onRenderFcts.push(function(){
		if(locked_view){
			camera.position = value_spacecraft.clone().normalize().multiplyScalar(getCamDistance());			
			camera.lookAt(scene.position);
		}		
	})	

	//////////////////////////////////////////////////////////////////////////////////
	//		loop runner							//
	//////////////////////////////////////////////////////////////////////////////////
	var lastTimeMsec= null
	requestAnimationFrame(function animate(nowMsec){
		// keep looping
		requestAnimationFrame( animate );
		// measure time
		/*lastTimeMsec	= lastTimeMsec || nowMsec-1000/60
		var deltaMsec	= Math.min(200, nowMsec - lastTimeMsec)
		lastTimeMsec	= nowMsec*/
		
		delta = clock.getDelta();
		
		controls.update();

		// call each update function
		onRenderFcts.forEach(function(onRenderFct){
			//onRenderFct(deltaMsec/1000, nowMsec/1000)
			onRenderFct(delta, 0)
		})
	})


	function render() 
	{
		renderer.render( scene, camera );
	}
	function includeOrbit(){
		//////////////////////////////////////////////////////////////////////////////////
		//		Orbit						//
		//////////////////////////////////////////////////////////////////////////////////
		
		/*
		var a=3;
		var e=0.7;
		var i = Math.PI/6;
		var w = Math.PI/9;
		var raan = Math.PI/2;
		*/
		
		
		//if(!orbit_init){
			
			createOrbit();
			orbit_init = true;
			onRenderFcts.push(function(delta, now){
				scene.remove(orbit);
				createOrbit();
			});
		//}
	}

	function createOrbit(){
		var a = value_orbit_a*dist_scale;
		var f=a*value_orbit_e;
		var b = Math.sqrt(a*a-f*f);	

		// Ellipse
		var material = new THREE.LineBasicMaterial({color:orbit_color, opacity:1});
		var ellipse = new THREE.EllipseCurve(0, f, b, a, 0, 2.0 * Math.PI, false);
		var ellipsePath = new THREE.CurvePath();
		ellipsePath.add(ellipse);
		var ellipseGeometry = ellipsePath.createPointsGeometry(100);
		ellipseGeometry.computeTangents();
		orbit = new THREE.Line(ellipseGeometry, material);
		// Argument of the perigee
		orbit.rotation.z = value_orbit_w+Math.PI/2+value_orbit_raan;
		//Inclination
		var nodeA = new THREE.Vector3(Math.cos(value_orbit_raan),Math.sin(value_orbit_raan),0);
		var quat = new THREE.Quaternion();
		quat.setFromAxisAngle(nodeA,value_orbit_i);
		orbit.quaternion.multiplyQuaternions(quat, orbit.quaternion);
		
		scene.add( orbit );
	}

	function includeSpacecraft(){
		//////////////////////////////////////////////////////////////////////////////////
		//		Spacecraft						//
		//////////////////////////////////////////////////////////////////////////////////
		var material = new THREE.MeshBasicMaterial({color:spacecraft_color, opacity:1});
		var geometry	= new THREE.SphereGeometry(spacecraft_radius, spacecraft_seg, spacecraft_seg);
		spacecraft	= new THREE.Mesh(geometry, material );
		spacecraft.name = "SPACECRAFT";
		scene.add(spacecraft);
		spacecraft_init=true;
		onRenderFcts.push(function(delta, now){
			spacecraft.position = new THREE.Vector3(
				value_spacecraft.x*dist_scale,
				value_spacecraft.y*dist_scale,
				value_spacecraft.z*dist_scale
			);					
		});
		spacecraft.visible = show_spacecraft;
	}


	function includeProjection(){
		//////////////////////////////////////////////////////////////////////////////////
		//		Projection						//
		//////////////////////////////////////////////////////////////////////////////////
			var lineGeometry = new THREE.Geometry();
			var vertArray = lineGeometry.vertices;
			vertArray.push( new THREE.Vector3(spacecraft.position.x,spacecraft.position.y,spacecraft.position.z), new THREE.Vector3(0, 0, 0) );
			lineGeometry.computeLineDistances();
			var lineMaterial = new THREE.LineDashedMaterial( { color: spacecraft_color, dashSize: 0.02, gapSize: 0.04 } );
			projection = new THREE.Line( lineGeometry, lineMaterial );
			projection.name = "PROJECTION";
			scene.add(projection);
			projection_init = true;
			onRenderFcts.push(function(delta, now){
				projection.geometry.vertices[0].set(spacecraft.position.x,spacecraft.position.y,spacecraft.position.z);
				projection.geometry.computeLineDistances();
				projection.geometry.verticesNeedUpdate = true;
			});
			projection.visible = show_projection;
	}

	function setXYplane(){
		if(show_xy_plane){
			// points that define shape
			var pts = [], hls = [];
			var radius = planes_radius;
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
				amount: 0.01,              // default 100, only used when path is null
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
				new THREE.MeshBasicMaterial( { color: color_xy_plane, transparent: true, opacity: 0.2 } )
			);
			scene.add( plane_xy );
		}
	}

	function includeEarth(){
		
		containerEarth	= new THREE.Object3D();
		//containerEarth.rotateX(Math.PI/2);
		//containerEarth.rotateZ(-23.4 * Math.PI/180);
		containerEarth.position.z	= 0;
		containerEarth.name = "EARTH";
		scene.add(containerEarth);
		
		/*
		var geometry	= new THREE.SphereGeometry(1737000*dist_scale, 32, 32);
		var material	= new THREE.MeshPhongMaterial({
			map	: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'images/moonmap1k.jpg'),
			bumpMap	: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'images/moonbump1k.jpg'),
			bumpScale: 0.002,
		});
		var moonMesh	= new THREE.Mesh(geometry, material);
		moonMesh.position.set(384400000*dist_scale,0.5,0.5);
		moonMesh.scale.multiplyScalar(1/5);
		moonMesh.receiveShadow	= true;
		moonMesh.castShadow	= true;
		containerEarth.add(moonMesh);
		*/

		var geometry	= new THREE.SphereGeometry(earth_radius, earth_seg, earth_seg);
		var material	= new THREE.MeshPhongMaterial({
			map		: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'www2/modules/orbit/textures/earthmap1k.jpg'),
			bumpMap		: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'www2/modules/orbit/textures/earthbump1k.jpg'),
			bumpScale	: 0.05,
			specularMap	: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'www2/modules/orbit/textures/earthspec1k.jpg'),
			specular	: new THREE.Color('grey'),
		});
		var earthMesh	= new THREE.Mesh(geometry, material);
		
		earthMesh.receiveShadow	= true
		earthMesh.castShadow	= true
		earthMesh.name = "EARTH-PLANET";
		containerEarth.add(earthMesh)
		onRenderFcts.push(function(delta, now){
			//earthMesh.rotation.y += 1/32 * delta * accel_time;
			var offset_quat = new THREE.Quaternion().setFromUnitVectors( new THREE.Vector3(0,1,0), new THREE.Vector3(0,0,1) );
			earthMesh.quaternion.multiplyQuaternions(value_earth_rotation,offset_quat);
		})

		//Earth axis
		var axis_earth = new THREE.AxisHelper( axis_earth_radius );
		axis_earth.rotation.x = -Math.PI/2;
		axis_earth.position.set( 0, 0, 0 );
		axis_earth.name = "EARTH-AXIS"
		earthMesh.add(axis_earth);
		if(show_earth_axis){
			axis_earth.visible = true;
		}else{
			axis_earth.visible = false;
		}

		//Earth atmosphere
		var geometry	= new THREE.SphereGeometry(earth_radius, earth_seg, earth_seg);
		var material	= THREEx.createAtmosphereMaterial();
		material.uniforms.glowColor.value.set(0x00b3ff);
		material.uniforms.coeficient.value	= 0.8;
		material.uniforms.power.value		= 2.0;
		var mesh	= new THREE.Mesh(geometry, material );
		mesh.scale.multiplyScalar(1.01);
		mesh.name = "EARTH-ATM-1";
		containerEarth.add( mesh );
		// new THREEx.addAtmosphereMaterial2DatGui(material, datGUI)
		
		var geometry	= new THREE.SphereGeometry(earth_radius, earth_seg, earth_seg);
		var material	= THREEx.createAtmosphereMaterial();
		material.side	= THREE.BackSide;
		material.uniforms.glowColor.value.set(0x00b3ff);
		material.uniforms.coeficient.value	= 0.5;
		material.uniforms.power.value		= 4.0;
		var mesh2	= new THREE.Mesh(geometry, material );
		mesh2.scale.multiplyScalar(1.15);
		mesh2.name = "EARTH-ATM-2";
		containerEarth.add( mesh2 );
		// new THREEx.addAtmosphereMaterial2DatGui(material, datGUI)
		if(show_earth_atmosphere){
			mesh.visible=true;
			mesh2.visible=true;
		}else{
			mesh.visible=false;
			mesh2.visible=false;
		}
		
		//Earth clouds
		var earthCloud	= THREEx.Planets.createEarthCloud();
		earthCloud.receiveShadow	= true;
		earthCloud.castShadow	= true;
		earthCloud.name = "EARTH-CLOUDS";
		containerEarth.add(earthCloud);
		onRenderFcts.push(function(delta, now){
			earthCloud.rotation.z += 1/32 * delta * accel_time;		
		});

		if(show_earth_clouds){
			earthCloud.visible = true;
		}else{
			earthCloud.visible = false;
		}
		
		if(show_earth){
			containerEarth.visible = true;
		}else{
			containerEarth.visible = false;
		}
	}

	function setAxis(){
		//-----------------------------------------------------------------------------------------------------------------------
		//			REFERENCE AXIS
		//-----------------------------------------------------------------------------------------------------------------------
		if(show_axis){
			var axis = new THREE.AxisHelper( axis_radius );
			axis.position.set( 0, 0, 0 );
			scene.add( axis );
		}
		if(show_axis_labels){
			var sprite_X = makeTextSprite( 0, " X ", 
			{ fontsize: 128, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:255, g:174, b:0, a:1.0} } );
			sprite_X.position.set( axis_radius, 0, 0 );
			scene.add( sprite_X );
			
			var sprite_Y = makeTextSprite( 0, " Y ", 
			{ fontsize: 128, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:16, g:219, b:2, a:1.0} } );
			sprite_Y.position.set( 0, axis_radius, 0 );
			scene.add( sprite_Y );
			
			var sprite_Z = makeTextSprite( 0, " Z ", 
			{ fontsize: 128, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:50, g:119, b:255, a:1.0} } );
			sprite_Z.position.set( 0, 0, axis_radius );
			scene.add( sprite_Z );
		}
	}

	function setSky(){
		
		starSphere = THREEx.Planets.createStarfield();
		starSphere.name = "STARS";
		scene.add(starSphere);
		if(!show_sky){
			starSphere.visible = false;
		}else{
			starSphere.visible = true;
		}
	}

	function initScene(){
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
			renderer = new THREE.WebGLRenderer( { antialias:true } );
			//renderer.autoClear = true;
			//renderer.autoClearColor = true;
			//renderer.setClearColor(0xff0000, 1);
			if(performance_level<=2)
				canvasMode(performance_level);
		}else{
			renderer = new THREE.CanvasRenderer();
			alert('WebGL not supported in this device');
			canvasMode(1);
		}
		renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		container = document.getElementById( 'DivOrbit' );
		container.appendChild( renderer.domElement );
		renderer.context.canvas.addEventListener("webglcontextlost", function(event) {
			event.preventDefault();
			// animationID would have been set by your call to requestAnimationFrame
			cancelAnimationFrame(); 
		}, false);

		renderer.context.canvas.addEventListener("webglcontextrestored", function(event) {
		   // Do something 
			alert("WebGL Context Lost");
		}, false);
		
		// EVENTS
		//THREEx.WindowResize(renderer, camera);
		//THREEx.FullScreen.bindKey({ charCode : 'm'.charCodeAt(0) });
		window.addEventListener( 'resize', onWindowResize, false );

		// CONTROLS
		//controls = new THREE.OrbitControls( camera, renderer.domElement );
		controls = new THREE.TrackballControls( camera );
		controls.rotateSpeed = 1.0;
		controls.zoomSpeed = 1.2;
		controls.panSpeed = 0.8;
		controls.noZoom = false;
		controls.maxDistance = 100;
		controls.minDistance = 1;
		controls.noPan = true;
		controls.staticMoving = true;
		controls.dynamicDampingFactor = 0.3;
		controls.keys = [ 65, 83, 68 ];
		controls.addEventListener( 'change', render );

		// STATS
		stats = new Stats();
		stats.domElement.style.position = 'absolute';
		stats.domElement.style.top = '0px';
		stats.domElement.style.zIndex = 100;
		//stats.domElement.style.webkitTransform = 0;
		//container.appendChild( stats.domElement );
		// LIGHT
		//light = new THREE.PointLight(0xE0E0E0);
		//light.position.set(200,200,200);
		//scene.add(light);

		// ambient
		var ambient = new THREE.AmbientLight( 0xFFFFFF );
		scene.add( ambient );
		
		onRenderFcts.push(function(delta, now){
			if(show_fps){
				stats.update();
				fps_update_counter=fps_update_counter+1;
				if(fps_update_counter>fps_update_skips){
					fps_update_counter=0;	
					updateFPS();
				}
			}
		})
	}

	function onWindowResize() {

		camera.aspect = window.innerWidth / window.innerHeight;
		camera.updateProjectionMatrix();

		renderer.setSize( window.innerWidth, window.innerHeight );

		controls.handleResize();

		render();

	}
}