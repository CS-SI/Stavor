// RENDERER
var orb_renderer, orb_container;
if ( Detector.webgl ){
	orb_renderer = new THREE.WebGLRenderer( { antialias:true } );
	//orb_renderer.autoClear = true;
	//orb_renderer.autoClearColor = true;
	//orb_renderer.setClearColor(0xff0000, 1);
	/*if(global.performance_level<=2)
		canvasMode(global.performance_level);*/
}else{
	orb_renderer = new THREE.CanvasRenderer();
	alert('WebGL not supported in this device');
	//canvasMode(1);
}
orb_renderer.setSize(window.innerWidth, window.innerHeight);
orb_container = document.getElementById( 'orbit' );
orb_container.innerHTML = "";
orb_container.appendChild( orb_renderer.domElement );
orb_renderer.context.canvas.addEventListener("webglcontextlost", function(event) {
	event.preventDefault();
	// animationID would have been set by your call to requestAnimationFrame
	cancelAnimationFrame(requestId); 
}, false);

orb_renderer.context.canvas.addEventListener("webglcontextrestored", function(event) {
   // Do something 
	alert("WebGL Context Lost");
}, false);

var Orbit = function()
{	
	// Global pointers
	var global = global_simulation.config.global;
	var config = global_simulation.config.orbit;
	var results = global_simulation.results.earth;
	var segments = global_3d_segments.orbit;
	
	var locked_view = global_cameras.orbit.view_locked;
	
	var orbit;
	var spacecraft;
	var projection, starSphere, containerEarth;
	// MAIN
	//***********************************************************************************************************************
	//		GLOBAL VARIABLES
	//***********************************************************************************************************************
	//setLoadingProgress(5);
	var scene, camera, controls, stats, light, delta;
	var clock = new THREE.Clock();
	var onRenderFcts = [];
	var fps_update_counter = 0;
	//-----------------------------------------------------------------------------------------------------------------------
	//			SCENE PARAMS (Hard-coded parameters)
	//-----------------------------------------------------------------------------------------------------------------------
	var canvas_mode = false;
	//var cam_init_pos  = new THREE.Vector3(0,0,13);
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

	//setLoadingProgress(1);
	initScene();
	//setLoadingProgress(3);
	setSky();
	setAxis();
	//setLoadingProgress(6);
	includeEarth();
	//setLoadingProgress(8);
	setXYplane();
	//setLoadingProgress(10);
	includeSpacecraft();
	//setLoadingProgress(14);
	includeProjection();
	//setLoadingProgress(18);
	includeOrbit();
	
	//setLoadingProgress(100);

	
	//////////////////////////////////////////////////////////////////////////////////
	//		render the scene						//
	//////////////////////////////////////////////////////////////////////////////////
	onRenderFcts.push(function(){
		orb_renderer.render( scene, camera );		
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
	
	global_delayed_loading.visualization.orbit = true;
	setLoadingText("Orbit module loaded!");
	hideSplash();
	setOrbitReloaded();

	//////////////////////////////////////////////////////////////////////////////////
	//		loop runner							//
	//////////////////////////////////////////////////////////////////////////////////
	var lastTimeMsec = null;
	var requestId;
	requestAnimationFrame(function animate(nowMsec){
		// keep looping
		requestId = requestAnimationFrame( animate );
		
		// measure time
		/*lastTimeMsec	= lastTimeMsec || nowMsec-1000/60
		var deltaMsec	= Math.min(200, nowMsec - lastTimeMsec)
		lastTimeMsec	= nowMsec*/
		
		if(global_current_visualization == enum_visualizations.ORBIT){
			//console.log("UPDATE ORBIT");
			delta = clock.getDelta();
			
			controls.update();

			// call each update function
			onRenderFcts.forEach(function(onRenderFct){
				//onRenderFct(deltaMsec/1000, nowMsec/1000)
				onRenderFct(delta, 0)
			})
		}
	})


	function render() {
		orb_renderer.render( scene, camera );
	}
	function includeOrbit() {
		createOrbit();
		orbit_init = true;
		onRenderFcts.push(function(delta, now){
			scene.remove(orbit);
			createOrbit();
		});
	}

	function createOrbit() {
		var a = results.osculating_orbit.a*dist_scale;
		var f=a*results.osculating_orbit.e;
		var b = Math.sqrt(a*a-f*f);	

		// Ellipse
		var material = new THREE.LineBasicMaterial({color:config.orbit_color, opacity:1});
		var ellipse = new THREE.EllipseCurve(0, f, b, a, 0, 2.0 * Math.PI, false);
		var ellipsePath = new THREE.CurvePath();
		ellipsePath.add(ellipse);
		var ellipseGeometry = ellipsePath.createPointsGeometry(100);
		ellipseGeometry.computeTangents();
		orbit = new THREE.Line(ellipseGeometry, material);
		// Argument of the perigee
		orbit.rotation.z = results.osculating_orbit.w+Math.PI/2+results.osculating_orbit.raan;
		//Inclination
		var nodeA = new THREE.Vector3(Math.cos(results.osculating_orbit.raan),Math.sin(results.osculating_orbit.raan),0);
		var quat = new THREE.Quaternion();
		quat.setFromAxisAngle(nodeA,results.osculating_orbit.i);
		orbit.quaternion.multiplyQuaternions(quat, orbit.quaternion);
		
		scene.add( orbit );
	}

	function includeSpacecraft(){
		//////////////////////////////////////////////////////////////////////////////////
		//		Spacecraft						//
		//////////////////////////////////////////////////////////////////////////////////
		var material = new THREE.MeshBasicMaterial({color:config.spacecraft_color, opacity:1});
		var geometry	= new THREE.SphereGeometry(spacecraft_radius, segments.spacecraft_seg, segments.spacecraft_seg);
		spacecraft	= new THREE.Mesh(geometry, material );
		spacecraft.name = "SPACECRAFT";
		scene.add(spacecraft);
		spacecraft_init=true;
		onRenderFcts.push(function(delta, now){
			spacecraft.position = new THREE.Vector3(
				results.spacecraft_position.x*dist_scale,
				results.spacecraft_position.y*dist_scale,
				results.spacecraft_position.z*dist_scale
			);					
		});
		spacecraft.visible = config.show_spacecraft;
	}


	function includeProjection(){
		//////////////////////////////////////////////////////////////////////////////////
		//		Projection						//
		//////////////////////////////////////////////////////////////////////////////////
			var lineGeometry = new THREE.Geometry();
			var vertArray = lineGeometry.vertices;
			vertArray.push( new THREE.Vector3(spacecraft.position.x,spacecraft.position.y,spacecraft.position.z), new THREE.Vector3(0, 0, 0) );
			lineGeometry.computeLineDistances();
			var lineMaterial = new THREE.LineDashedMaterial( { color: config.spacecraft_color, dashSize: 0.02, gapSize: 0.04 } );
			projection = new THREE.Line( lineGeometry, lineMaterial );
			projection.name = "PROJECTION";
			scene.add(projection);
			projection_init = true;
			onRenderFcts.push(function(delta, now){
				projection.geometry.vertices[0].set(spacecraft.position.x,spacecraft.position.y,spacecraft.position.z);
				projection.geometry.computeLineDistances();
				projection.geometry.verticesNeedUpdate = true;
			});
			projection.visible = config.show_projection;
	}

	function setXYplane()
	{
		if(config.show_xy_plane){
			// points that define shape
			var pts = [], hls = [];
			var radius = planes_radius;
			var radius_ext = planes_width;

			for ( i = 0; i < segments.plane_resolution; i ++ ) {
				var a = 2*Math.PI * i / segments.plane_resolution;
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
				new THREE.MeshBasicMaterial( { color: config.color_xy_plane, transparent: true, opacity: 0.2 } )
			);
			scene.add( plane_xy );
		}
	}

	function includeEarth()
	{
		
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

		var geometry	= new THREE.SphereGeometry(earth_radius, segments.earth_seg, segments.earth_seg);
		var material	= new THREE.MeshPhongMaterial({
			map		: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'www/modules/orbit/textures/earthmap1k.jpg'),
			bumpMap		: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'www/modules/orbit/textures/earthbump1k.jpg'),
			bumpScale	: 0.05,
			specularMap	: THREE.ImageUtils.loadTexture(THREEx.Planets.baseURL+'www/modules/orbit/textures/earthspec1k.jpg'),
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
			earthMesh.quaternion.multiplyQuaternions(results.earth_rotation,offset_quat);
		})

		//Earth axis
		var axis_earth = new THREE.AxisHelper( axis_earth_radius );
		axis_earth.rotation.x = -Math.PI/2;
		axis_earth.position.set( 0, 0, 0 );
		axis_earth.name = "EARTH-AXIS"
		earthMesh.add(axis_earth);
		if(config.show_earth_axis){
			axis_earth.visible = true;
		}else{
			axis_earth.visible = false;
		}

		//Earth atmosphere
		var geometry	= new THREE.SphereGeometry(earth_radius, segments.earth_seg, segments.earth_seg);
		var material	= THREEx.createAtmosphereMaterial();
		material.uniforms.glowColor.value.set(0x00b3ff);
		material.uniforms.coeficient.value	= 0.8;
		material.uniforms.power.value		= 2.0;
		var mesh	= new THREE.Mesh(geometry, material );
		mesh.scale.multiplyScalar(1.01);
		mesh.name = "EARTH-ATM-1";
		containerEarth.add( mesh );
		// new THREEx.addAtmosphereMaterial2DatGui(material, datGUI)
		
		var geometry	= new THREE.SphereGeometry(earth_radius, segments.earth_seg, segments.earth_seg);
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
		if(config.show_earth_atmosphere){
			mesh.visible=true;
			mesh2.visible=true;
		}else{
			mesh.visible=false;
			mesh2.visible=false;
		}
		
		//Earth clouds
		var earthCloud	= THREEx.Planets.getEarthCloud();
		earthCloud.receiveShadow	= true;
		earthCloud.castShadow	= true;
		earthCloud.name = "EARTH-CLOUDS";
		containerEarth.add(earthCloud);
		onRenderFcts.push(function(delta, now){
			earthCloud.rotation.z += 1/32 * delta * accel_time;		
		});

		if(config.show_earth_clouds){
			earthCloud.visible = true;
		}else{
			earthCloud.visible = false;
		}
		
		if(config.show_earth){
			containerEarth.visible = true;
		}else{
			containerEarth.traverse( function ( child ) { child.visible = false; } );
		}
	}

	function setAxis(){
		//-----------------------------------------------------------------------------------------------------------------------
		//			REFERENCE AXIS
		//-----------------------------------------------------------------------------------------------------------------------
		if(config.show_axis){
			var axis = new THREE.AxisHelper( axis_radius );
			axis.position.set( 0, 0, 0 );
			scene.add( axis );
		}
		if(config.show_axis_labels){
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
		if(!config.show_sky){
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
		camera.position = global_cameras.orbit.position;
		camera.lookAt(scene.position);	
		
		// EVENTS
		//THREEx.WindowResize(orb_renderer, camera);
		//THREEx.FullScreen.bindKey({ charCode : 'm'.charCodeAt(0) });
		
		//window.addEventListener( 'resize', onWindowResize, false );
		//orb_container.addEventListener("transitionend", onWindowResize, false);

		// CONTROLS
		//controls = new THREE.OrbitControls( camera, orb_renderer.domElement );
		controls = new THREE.TrackballControls( camera, orb_renderer.domElement );
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
		stats.domElement.style.top = '50px';
		stats.domElement.style.zIndex = 100;
		if(global.show_fps){
			stats.domElement.style.webkitTransform = 0;
			orb_container.appendChild( stats.domElement );
		}
		// LIGHT
		//light = new THREE.PointLight(0xE0E0E0);
		//light.position.set(200,200,200);
		//scene.add(light);

		// ambient
		ambient = new THREE.AmbientLight( 0xFFFFFF );
		scene.add( ambient );
		
		onRenderFcts.push(function(delta, now){
			if(global.show_fps){
				stats.update();
				/*fps_update_counter=fps_update_counter+1;
				if(fps_update_counter>global.fps_update_skips){
					fps_update_counter=0;	
					updateFPS();
				}*/
			}
		})
	}

	function onWindowResize(){

		/*camera.aspect = window.innerWidth / window.innerHeight;
		camera.updateProjectionMatrix();

		orb_renderer.setSize( window.innerWidth, window.innerHeight );

		controls.handleResize();

		render();
		*/
		camera.aspect = window.innerWidth / window.innerHeight;
		camera.updateProjectionMatrix();

		orb_renderer.setSize( window.innerWidth, window.innerHeight );

		controls.handleResize();

		render();
	}
	
	Orbit.prototype.resizeCanvas = function(){
		onWindowResize();
	}
	Orbit.prototype.stopAnimation = function(){
		cancelAnimationFrame(requestId); 
		global_cameras.orbit.view_locked = locked_view;
		global_cameras.orbit.position = camera.position;
		global_cameras.orbit.up = camera.up;
	}
	
//***********************************************************************************************************************
//		                                                SPRITES
//***********************************************************************************************************************
	//element 0: labels 
	function makeTextSprite(element, message, parameters )
	{
		if ( parameters === undefined ) parameters = {};
		
		var fontface = parameters.hasOwnProperty("fontface") ? 
			parameters["fontface"] : "Arial";
		
		var fontsize = parameters.hasOwnProperty("fontsize") ? 
			parameters["fontsize"] : 12;
		
		var borderThickness = parameters.hasOwnProperty("borderThickness") ? 
			parameters["borderThickness"] : 4;
		
		var borderColor = parameters.hasOwnProperty("borderColor") ?
			parameters["borderColor"] : { r:0, g:0, b:0, a:1.0 };
			
		var fontColor = parameters.hasOwnProperty("fontColor") ?
			parameters["fontColor"] : { r:255, g:255, b:255, a:1.0 };
		
		var backgroundColor = parameters.hasOwnProperty("backgroundColor") ?
			parameters["backgroundColor"] : { r:255, g:255, b:255, a:1.0 };

		//var spriteAlignment = THREE.SpriteAlignment.topLeft;
			
		var canvas = document.createElement('canvas');
		var context = canvas.getContext('2d');
		context.font = "Normal " + fontsize + "px " + fontface;
		
		
		// get size data (height depends only on font size)
		var metrics = context.measureText( message );
		var textWidth = metrics.width;
		
		// background color
		context.fillStyle   = "rgba(" + backgroundColor.r + "," + backgroundColor.g + ","
									  + backgroundColor.b + "," + backgroundColor.a + ")";
		// border color
		context.strokeStyle = "rgba(" + borderColor.r + "," + borderColor.g + ","
									  + borderColor.b + "," + borderColor.a + ")";

		context.lineWidth = borderThickness;
		roundRect(context, borderThickness/2, borderThickness/2, textWidth + borderThickness, fontsize * 1.4 + borderThickness, 6);
		// 1.4 is extra height factor for text below baseline: g,j,p,q.
		
		// text color
		context.fillStyle   = "rgba(" + fontColor.r + "," + fontColor.g + ","
									  + fontColor.b + "," + fontColor.a + ")";

		context.fillText( message, borderThickness, fontsize + borderThickness);
		
		
		
		// canvas contents will be used for a texture
		var texture = new THREE.Texture(canvas) 
		texture.needsUpdate = true;

		var spriteMaterial = new THREE.SpriteMaterial( 
			{ map: texture, useScreenCoordinates: false/*, alignment: spriteAlignment*/ } );
		var sprite = new THREE.Sprite( spriteMaterial );
		
		if(element==1){//Sun
			contextSun = context;
			fontsizeSun = fontsize;
			borderColorSun = borderColor;
			borderThicknessSun = borderThickness; 
			backgroundColorSun = backgroundColor;
			fontColorSun = fontColor;
			sprite.scale.set(40,20,1.0);
		}else{//Axis labels
			sprite.scale.set(0.1,0.05,1.0);
		}
		
		return sprite;	
	}

	// function for drawing rounded rectangles
	function roundRect(ctx, x, y, w, h, r) 
	{
		ctx.beginPath();
		ctx.moveTo(x+r, y);
		ctx.lineTo(x+w-r, y);
		ctx.quadraticCurveTo(x+w, y, x+w, y+r);
		ctx.lineTo(x+w, y+h-r);
		ctx.quadraticCurveTo(x+w, y+h, x+w-r, y+h);
		ctx.lineTo(x+r, y+h);
		ctx.quadraticCurveTo(x, y+h, x, y+h-r);
		ctx.lineTo(x, y+r);
		ctx.quadraticCurveTo(x, y, x+r, y);
		ctx.closePath();
		ctx.fill();
		ctx.stroke();   
	}

}