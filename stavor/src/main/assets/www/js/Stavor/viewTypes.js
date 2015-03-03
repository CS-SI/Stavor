function switchAttitudeViewTypeMenu(){
	var div = document.getElementById("AttitudeViewTypeMenu");
	if(global_menus.attitude.isViewsMenuOpen){
		div.className = "ViewTypeMenu ViewTypeMenuClosed";
	}else{
		updateAttitudeViewTypeMenu();
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
	var view = global_cameras.attitude.view_mode;
	var divs = document.getElementsByName("ViewTypeButtons");
	for(var i = 0; i < divs.length; i++){
		divs[i].className = "ViewTypeButton";
	}
	document.getElementById("att-view-"+view).className = "ViewTypeButton selected";
		
}

function changeAttitudeSelectedViewType(view_str){
	global_attitude.selectView(view_str);
	updateAttitudeViewTypeMenu();
}