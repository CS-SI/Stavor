function switchAttitudeViewTypeMenu(){
	var div = document.getElementById("AttitudeViewTypeMenu");
	if(global_menus.attitude.isViewsMenuOpen){
		div.className = "ViewTypeMenu ViewTypeMenuClosed";
	}else{
		div.className = "ViewTypeMenu ViewTypeMenuOpen";
	}
	global_menus.attitude.isViewsMenuOpen = !global_menus.attitude.isViewsMenuOpen;
}
function switchOrbitViewTypeMenu(){
	var button = document.getElementById("ViewButtonOrbit");
	global_cameras.orbit.view_locked = !global_cameras.orbit.view_locked;
	if(global_cameras.orbit.view_locked){
		button.className = "ViewButtonLocked";
	}else{
		button.className = "ViewButtonUnlocked";
	}
}
function switchMapViewTypeMenu(){
	var button = document.getElementById("ViewButtonMap");
	global_cameras.map.view_locked = !global_cameras.map.view_locked;
	if(global_cameras.map.view_locked){
		button.className = "ViewButtonLocked";
	}else{
		button.className = "ViewButtonUnlocked";
	}
}

function updateAttitudeViewTypeMenu(){
		
}

function changeAttitudeSelectedViewType(view_str){


	updateAttitudeViewTypeMenu();
}