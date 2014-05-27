function getCamDistance(){
	return Math.sqrt(camera.position.x*camera.position.x+camera.position.y*camera.position.y+camera.position.z*camera.position.z);
}
function getCamEquilater(){
	return getCamDistance()/Math.sqrt(3);
}
function restoreMiniSpheres(){
	if(show_mini_spheres){
		miniSphereX.visible=true;
		miniSphereXX.visible=true;
		miniSphereY.visible=true;
		miniSphereYY.visible=true;
		miniSphereZ.visible=true;
		miniSphereZZ.visible=true;
	}
}
function restorePlanets(){
	if(show_earth){
		earth.visible=true;
	}
	if(show_sun){
		sun.visible=true;
		sunGlow.visible=true;
	}
}
var selected_view = "XYZ";
function changeView(view_mode){
	restoreMiniSpheres();
	restorePlanets();
	switch(view_mode){
		case "XYZ"://xyz
			camera.position = new THREE.Vector3(getCamEquilater(),getCamEquilater(),getCamEquilater());
			camera.up = new THREE.Vector3(-0.577,0.577,-0.577);
			break;
		case "X"://+X
			miniSphereX.visible=false;
			camera.position = new THREE.Vector3(getCamDistance(),0,0);
			camera.up = new THREE.Vector3(0,1,0);
			break;
		case "-X"://-X
			miniSphereXX.visible=false;
			camera.position = new THREE.Vector3(-getCamDistance(),0,0);
			camera.up = new THREE.Vector3(0,1,0);
			break;
		case "Y"://+Y
			miniSphereY.visible=false;
			camera.position = new THREE.Vector3(0,getCamDistance(),0);
			camera.up = new THREE.Vector3(0,0,1);
			break;
		case "-Y"://-Y
			miniSphereYY.visible=false;
			camera.position = new THREE.Vector3(0,-getCamDistance(),0);
			camera.up = new THREE.Vector3(0,0,1);
			break;
		case "Z"://+Z
			miniSphereZ.visible=false;
			camera.position = new THREE.Vector3(0,0,getCamDistance());
			camera.up = new THREE.Vector3(0,1,0);
			break;
		case "-Z"://-Z
			miniSphereZZ.visible=false;
			camera.position = new THREE.Vector3(0,0,-getCamDistance());
			camera.up = new THREE.Vector3(0,1,0);
			break;
		case "Earth"://Earth
			earth.visible = false;
			camera.position = earth.position.clone().normalize().multiplyScalar(getCamDistance());
			break;
		case "Sun"://Sun
			sun.visible = false;
			sunGlow.visible=false;
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
		default://xyz
			camera.position = new THREE.Vector3(getCamEquilater(),getCamEquilater(),getCamEquilater());
			break;
	}
	selected_view = view_mode;
	camera.lookAt(scene.position);
}
function onWindowResize() {

	camera.aspect = window.innerWidth / window.innerHeight;
	camera.updateProjectionMatrix();

	renderer.setSize( window.innerWidth, window.innerHeight );

	controls.handleResize();

	render();

}
function updateView() {
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

}
