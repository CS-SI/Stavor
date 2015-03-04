function initializePanGestures(){
	//initializeInfoPanelPanGestures();
	initializeMissionPanGestures();
	initializeAttitudeMenuPanGestures();
	initializeOrbitMenuPanGestures();
	initializeMapMenuPanGestures();
	setLoadingText("Gestures linked!");
}

function initializeMapMenuPanGestures(){
	var div_menu = document.getElementById("MaptabContainer");
	var menu_options;
	var hammertime = new Hammer(div_menu, menu_options);
	hammertime.on('pan', function(ev) {
		//console.log(ev);
		var dist = ev.deltaX;
		if(dist>0 && global_menus.map.isOpen){
			switchMapMenu();
		}
	});
	hammertime.get('pan').set({ direction: Hammer.DIRECTION_HORIZONTAL, threshold: 20 });
}

function initializeOrbitMenuPanGestures(){
	var div_menu = document.getElementById("OrbittabContainer");
	var menu_options;
	var hammertime = new Hammer(div_menu, menu_options);
	hammertime.on('pan', function(ev) {
		//console.log(ev);
		var dist = ev.deltaX;
		if(dist>0 && global_menus.orbit.isOpen){
			switchOrbitMenu();
		}
	});
	hammertime.get('pan').set({ direction: Hammer.DIRECTION_HORIZONTAL, threshold: 20 });
}

function initializeAttitudeMenuPanGestures(){
	var div_menu = document.getElementById("AttitudetabContainer");
	var menu_options;
	var hammertime = new Hammer(div_menu, menu_options);
	hammertime.on('pan', function(ev) {
		//console.log(ev);
		var dist = ev.deltaX;
		if(dist>0 && global_menus.attitude.isOpen){
			switchAttitudeMenu();
		}
	});
	hammertime.get('pan').set({ direction: Hammer.DIRECTION_HORIZONTAL, threshold: 20 });
}

function initializeInfoPanelPanGestures(){
	var div_menu = document.getElementById("DivInfoPanel");
	var menu_options;
	var hammertime = new Hammer(div_menu, menu_options);
	hammertime.on('pan', function(ev) {
		//console.log(ev);
		var dist = ev.deltaY;
		if(dist<0 && global_menus.info_panel.isOpen){
			switchInfoPanel();
		}else if(dist>0 && !global_menus.info_panel.isOpen){
			switchInfoPanel();
		}
	});
	hammertime.get('pan').set({ direction: Hammer.DIRECTION_ALL, threshold: 0 });
}

function initializeMissionPanGestures(){
	var div_menu = document.getElementById("DivMissions");
	var menu_options;
	var hammertime = new Hammer(div_menu, menu_options);
	hammertime.on('pan', function(ev) {
		//console.log(ev);
		var dist = ev.deltaX;
		if(dist<0 && global_missions_list_is_open){
			switchMissionsListStatus();
		}
	});
	hammertime.get('pan').set({ direction: Hammer.DIRECTION_HORIZONTAL, threshold: 20 });
}