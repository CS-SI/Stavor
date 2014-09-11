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
	container = document.getElementById( 'ThreeJS' );
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
	controls.maxDistance = 2000;
	controls.minDistance = 25;
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
}
function updateScene(){
	delta = clock.getDelta();

	//Ligts
	light.position.set(camera.position.x,camera.position.y,camera.position.z);

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
}
