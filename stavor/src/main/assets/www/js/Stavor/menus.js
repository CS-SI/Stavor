function initializeMapMenu(){
	// get tab container
	var tabcontainer = document.getElementById("MaptabContainer");
		var tabcon = document.getElementById("Maptabscontent");
		//alert(tabcon.childNodes.item(1));
	// set current tab
	var navitem = document.getElementById("MaptabHeader_1");
		
	//store which tab we are on
	var ident = navitem.id.split("_")[1];
		//alert(ident);
	navitem.parentNode.setAttribute("data-current",ident);
	//set current tab with class of activetabheader
	navitem.setAttribute("class","tabActiveHeader");

	//hide two tab contents we don't need
	 var pages = tabcon.getElementsByTagName("div");
		/*for (var i = 1; i < pages.length; i++) {
		 pages.item(i).style.display="none";
		};
		document.getElementById("DivStations").style.display = "block";
		document.getElementById("StationsMenu").style.display = "block";*/
		for (var i = 1; i < pages.length; i++) {
			if(pages.item(i).parentElement === tabcon){
				pages.item(i).style.display="none";
			}
		};
		

	//this adds click event to tabs
	var tabs = tabcontainer.getElementsByTagName("li");
	for (var i = 0; i < tabs.length; i++) {
	  tabs[i].onclick=displayMapMenuPage;
	}
}

function initializeOrbitMenu(){
	// get tab container
	var tabcontainer = document.getElementById("OrbittabContainer");
		var tabcon = document.getElementById("Orbittabscontent");
		//alert(tabcon.childNodes.item(1));
	// set current tab
	var navitem = document.getElementById("OrbittabHeader_1");
		
	//store which tab we are on
	var ident = navitem.id.split("_")[1];
		//alert(ident);
	navitem.parentNode.setAttribute("data-current",ident);
	//set current tab with class of activetabheader
	navitem.setAttribute("class","tabActiveHeader");

	//hide two tab contents we don't need
	 var pages = tabcon.getElementsByTagName("div");
		for (var i = 1; i < pages.length; i++) {
			/*if(pages.item(i).id != "AngleUnitsSelectionRadsRefOrbit" && pages.item(i).id != "AngleUnitsSelectionDegsRefOrbit"){
				pages.item(i).style.display="none";
			}*/
			if(pages.item(i).parentElement === tabcon){
				pages.item(i).style.display="none";
			}
		};

	//this adds click event to tabs
	var tabs = tabcontainer.getElementsByTagName("li");
	for (var i = 0; i < tabs.length; i++) {
	  tabs[i].onclick=displayOrbitMenuPage;
	}
}

function initializeAttitudeMenu(){
	// get tab container
	var tabcontainer = document.getElementById("AttitudetabContainer");
		var tabcon = document.getElementById("Attitudetabscontent");
		//alert(tabcon.childNodes.item(1));
	// set current tab
	var navitem = document.getElementById("AttitudetabHeader_1");
		
	//store which tab we are on
	var ident = navitem.id.split("_")[1];
		//alert(ident);
	navitem.parentNode.setAttribute("data-current",ident);
	//set current tab with class of activetabheader
	navitem.setAttribute("class","tabActiveHeader");

	//hide two tab contents we don't need
	 var pages = tabcon.getElementsByTagName("div");
		for (var i = 1; i < pages.length; i++) {
			if(pages.item(i).parentElement === tabcon){
				pages.item(i).style.display="none";
			}
		};

	//this adds click event to tabs
	var tabs = tabcontainer.getElementsByTagName("li");
	for (var i = 0; i < tabs.length; i++) {
	  tabs[i].onclick=displayAttitudeMenuPage;
	}
}

function displayMapMenuPage() {
  var current = this.parentNode.getAttribute("data-current");
  //remove class of activetabheader and hide old contents
  document.getElementById("MaptabHeader_" + current).removeAttribute("class");
  document.getElementById("Maptabpage_" + current).style.display="none";

  var ident = this.id.split("_")[1];
  //add class of activetabheader to new active tab and show contents
  this.setAttribute("class","tabActiveHeader");
  var mapPage = document.getElementById("Maptabpage_" + ident);
  mapPage.style.display="block";
  
  var pages = mapPage.getElementsByTagName("div");
	for (var i = 1; i < pages.length; i++) {
		if(pages.item(i).id != "MapMenuLoading")
			pages.item(i).style.display="block";
	};
  
  this.parentNode.setAttribute("data-current",ident);
}

function displayOrbitMenuPage() {
  var current = this.parentNode.getAttribute("data-current");
  //remove class of activetabheader and hide old contents
  document.getElementById("OrbittabHeader_" + current).removeAttribute("class");
  document.getElementById("Orbittabpage_" + current).style.display="none";

  var ident = this.id.split("_")[1];
  //add class of activetabheader to new active tab and show contents
  this.setAttribute("class","tabActiveHeader");
  document.getElementById("Orbittabpage_" + ident).style.display="block";
  this.parentNode.setAttribute("data-current",ident);
}

function displayAttitudeMenuPage() {
  var current = this.parentNode.getAttribute("data-current");
  //remove class of activetabheader and hide old contents
  document.getElementById("AttitudetabHeader_" + current).removeAttribute("class");
  document.getElementById("Attitudetabpage_" + current).style.display="none";

  var ident = this.id.split("_")[1];
  //add class of activetabheader to new active tab and show contents
  this.setAttribute("class","tabActiveHeader");
  document.getElementById("Attitudetabpage_" + ident).style.display="block";
  this.parentNode.setAttribute("data-current",ident);
}

function switchMapMenu(){
	var menu = document.getElementById("MaptabContainer"); 
	var divi = document.getElementById("DivMap"); 
	if(!global_menus.map.isOpen){
		updateMapOptions();
		menu.className = "tabContainer openVisMenu";
	}else{
		menu.className = "tabContainer closeVisMenu";
	}
	global_menus.map.isOpen = !global_menus.map.isOpen;
}

function updateMapOption(id,val){
	if(val != "" || typeof val == "boolean"){
		setMapReloading();
		switch(id) {
			case "opt-map-ShowFov":
				global_simulation.config.map.show_fov = val;
				break;
			case "opt-map-ShowTrack":
				global_simulation.config.map.show_track = val;
				break;
			case "opt-map-ShowSunIcon":
				global_simulation.config.map.show_sun_icon = val;
				break;
			case "opt-map-ShowSunTerminator":
				global_simulation.config.map.show_sun_terminator = val;
				break;
			case "opt-map-ApertureAngle":
				global_simulation.config.map.fov.aperture_angle = val;
				global_simulator.sendSimulatorConfiguration();
				break;
			case "opt-map-DirectionX":
				if(val == 0 && global_simulation.config.map.fov.direction.y == 0 && global_simulation.config.map.fov.direction.z == 0){
					Dialog.showDialog("str_dialog_sensor_direction_title", "str_dialog_sensor_direction_message", function(){updateMapOptions();});
				}else{
					global_simulation.config.map.fov.direction.x = val;
					global_simulator.sendSimulatorConfiguration();
				}
				break;
			case "opt-map-DirectionY":
				if(val == 0 && global_simulation.config.map.fov.direction.x == 0 && global_simulation.config.map.fov.direction.z == 0){
					Dialog.showDialog("str_dialog_sensor_direction_title", "str_dialog_sensor_direction_message", function(){updateMapOptions();});
				}else{
					global_simulation.config.map.fov.direction.y = val;
					global_simulator.sendSimulatorConfiguration();
				}
				break;
			case "opt-map-DirectionZ":
				if(val == 0 && global_simulation.config.map.fov.direction.x == 0 && global_simulation.config.map.fov.direction.y == 0){
					Dialog.showDialog("str_dialog_sensor_direction_title", "str_dialog_sensor_direction_message", function(){updateMapOptions();});
				}else{
					global_simulation.config.map.fov.direction.z = val;
					global_simulator.sendSimulatorConfiguration();
				}
				break;
			default:
		}
		global_map.stopAnimation();
		global_map = new Map();
		saveStoredVariables();
	}else{
		updateMapOptions();
	}
}

function updateMapOptions(){
	document.getElementById("opt-map-ShowFov").checked = global_simulation.config.map.show_fov;
	document.getElementById("opt-map-ShowTrack").checked = global_simulation.config.map.show_track;
	document.getElementById("opt-map-ShowSunIcon").checked = global_simulation.config.map.show_sun_icon;
	document.getElementById("opt-map-ShowSunTerminator").checked = global_simulation.config.map.show_sun_terminator;
	
	document.getElementById("opt-map-ApertureAngle").value = global_simulation.config.map.fov.aperture_angle;
	document.getElementById("opt-map-DirectionX").value = global_simulation.config.map.fov.direction.x;
	document.getElementById("opt-map-DirectionY").value = global_simulation.config.map.fov.direction.y;
	document.getElementById("opt-map-DirectionZ").value = global_simulation.config.map.fov.direction.z;
}

function switchOrbitMenu(){
		var menu = document.getElementById("OrbittabContainer"); 
		var divi = document.getElementById("DivOrbit"); 
		if(!global_menus.orbit.isOpen){
			updateOrbitOptions();
			menu.className = "tabContainer openVisMenu";
		}else{
			menu.className = "tabContainer closeVisMenu";
		}
		global_menus.orbit.isOpen = !global_menus.orbit.isOpen;
	//}
}
function updateOrbitOption(id,val){
	if(val != "" || typeof val == "boolean"){
		setOrbitReloading();
		switch(id) {
			case "opt-orb-ShowSky":
				global_simulation.config.orbit.show_sky = val;
				break;
			case "opt-orb-ShowAxis":
				global_simulation.config.orbit.show_axis = val;
				break;
			case "opt-orb-ShowAxisLabels":
				global_simulation.config.orbit.show_axis_labels = val;
				break;
			case "opt-orb-ShowEarthModel":
				global_simulation.config.orbit.show_earth = val;
				if(!val){
					global_simulation.config.orbit.show_earth_axis = val;
					global_simulation.config.orbit.show_earth_atmosphere = val;
					global_simulation.config.orbit.show_earth_clouds = val;
					updateOrbitOptions();
				}
				break;
			case "opt-orb-ShowEarthAxis":
				global_simulation.config.orbit.show_earth_axis = val;
				if(val){
					global_simulation.config.orbit.show_earth = val;
					updateOrbitOptions();
				}
				break;
			case "opt-orb-ShowEarthAtmosphere":
				global_simulation.config.orbit.show_earth_atmosphere = val;
				if(val){
					global_simulation.config.orbit.show_earth = val;
					updateOrbitOptions();
				}
				break;
			case "opt-orb-ShowEarthClouds":
				global_simulation.config.orbit.show_earth_clouds = val;
				if(val){
					global_simulation.config.orbit.show_earth = val;
					updateOrbitOptions();
				}
				break;
			case "opt-orb-ShowXyPlane":
				global_simulation.config.orbit.show_xy_plane = val;
				break;
			case "opt-orb-PlaneXyColor":
				global_simulation.config.orbit.color_xy_plane = val;
				break;
			case "opt-orb-ShowSpacecraft":
				global_simulation.config.orbit.show_spacecraft = val;
				break;
			case "opt-orb-SpacecraftColor":
				global_simulation.config.orbit.spacecraft_color = val;
				break;
			case "opt-orb-ShowProjection":
				global_simulation.config.orbit.show_projection = val;
				break;
			case "opt-orb-OrbitColor":
				global_simulation.config.orbit.orbit_color = val;
				break;
			case "opt-orb-ShowRefOrbit":
				global_simulation.config.orbit.ref_orbit.show = val;
				break;
			case "opt-orb-RefOrbitColor":
				global_simulation.config.orbit.ref_orbit.color = val;
				break;
			case "opt-orb-RefOrbit-a":
				global_simulation.config.orbit.ref_orbit.a = val;
				break;
			case "opt-orb-RefOrbit-e":
				global_simulation.config.orbit.ref_orbit.e = val;
				break;
			case "opt-orb-RefOrbit-i":
				global_simulation.config.orbit.ref_orbit.i = val;
				if(!global_angle_in_rads){
					global_simulation.config.orbit.ref_orbit.i = global_simulation.config.orbit.ref_orbit.i * Math.PI / 180.0;
				}
				break;
			case "opt-orb-RefOrbit-omega":
				global_simulation.config.orbit.ref_orbit.w = val;
				if(!global_angle_in_rads){
					global_simulation.config.orbit.ref_orbit.w = global_simulation.config.orbit.ref_orbit.w * Math.PI / 180.0;
				}
				break;
			case "opt-orb-RefOrbit-raan":
				global_simulation.config.orbit.ref_orbit.raan = val;
				if(!global_angle_in_rads){
					global_simulation.config.orbit.ref_orbit.raan = global_simulation.config.orbit.ref_orbit.raan * Math.PI / 180.0;
				}
				break;
			default:
		}
		global_orbit.stopAnimation();
		global_orbit = new Orbit();
		saveStoredVariables();
	}else{
		updateOrbitOptions();
	}
}
function updateOrbitOptions(){
	document.getElementById("opt-orb-ShowSky").checked = global_simulation.config.orbit.show_sky;
	document.getElementById("opt-orb-ShowAxis").checked = global_simulation.config.orbit.show_axis;
	document.getElementById("opt-orb-ShowAxisLabels").checked = global_simulation.config.orbit.show_axis_labels;
	document.getElementById("opt-orb-ShowEarthAxis").checked = global_simulation.config.orbit.show_earth_axis;
	document.getElementById("opt-orb-ShowEarthModel").checked = global_simulation.config.orbit.show_earth;
	document.getElementById("opt-orb-ShowEarthAtmosphere").checked = global_simulation.config.orbit.show_earth_atmosphere;
	document.getElementById("opt-orb-ShowEarthClouds").checked = global_simulation.config.orbit.show_earth_clouds;
	document.getElementById("opt-orb-ShowXyPlane").checked = global_simulation.config.orbit.show_xy_plane;
	
	document.getElementById("opt-orb-PlaneXyColor").value = global_simulation.config.orbit.color_xy_plane;
	//document.getElementById("opt-orb-PlaneXyColor").style.backgroundColor = global_simulation.config.orbit.color_xy_plane;
	
	document.getElementById("opt-orb-ShowSpacecraft").checked = global_simulation.config.orbit.show_spacecraft;
	
	document.getElementById("opt-orb-SpacecraftColor").value = global_simulation.config.orbit.spacecraft_color;
	//document.getElementById("opt-orb-SpacecraftColor").style.backgroundColor = global_simulation.config.orbit.spacecraft_color;
	
	document.getElementById("opt-orb-ShowProjection").checked = global_simulation.config.orbit.show_projection;
	document.getElementById("opt-orb-OrbitColor").value = global_simulation.config.orbit.orbit_color;
	//document.getElementById("opt-orb-OrbitColor").style.backgroundColor = global_simulation.config.orbit.orbit_color;
	
	
	document.getElementById("opt-orb-ShowRefOrbit").checked = global_simulation.config.orbit.ref_orbit.show;
	document.getElementById("opt-orb-RefOrbitColor").value = global_simulation.config.orbit.ref_orbit.color;
	//document.getElementById("opt-orb-RefOrbitColor").style.backgroundColor = global_simulation.config.orbit.ref_orbit.color;
	
	if(global_angle_in_rads){
		document.getElementById("AngleUnitsSelectionRadsRefOrbit").className = "AngleUnitsSelected";
		document.getElementById("AngleUnitsSelectionDegsRefOrbit").className = "AngleUnitsUnselected";
		var div;
		div	= document.getElementById("RefOrbit-Inclination");
		div.innerHTML = div.innerHTML.replace("(deg)","(rad)");
		div = document.getElementById("RefOrbit-Omega");
		div.innerHTML = div.innerHTML.replace("(deg)","(rad)");
		div = document.getElementById("RefOrbit-Raan");
		div.innerHTML = div.innerHTML.replace("(deg)","(rad)");
	}else{
		document.getElementById("AngleUnitsSelectionRadsRefOrbit").className = "AngleUnitsUnselected";
		document.getElementById("AngleUnitsSelectionDegsRefOrbit").className = "AngleUnitsSelected";
		var div;
		div = document.getElementById("RefOrbit-Inclination");
		div.innerHTML = div.innerHTML.replace("(rad)","(deg)");
		div = document.getElementById("RefOrbit-Omega");
		div.innerHTML = div.innerHTML.replace("(rad)","(deg)");
		div = document.getElementById("RefOrbit-Raan");
		div.innerHTML = div.innerHTML.replace("(rad)","(deg)");
	}
	
	var field;
	field = document.getElementById("opt-orb-RefOrbit-a");
	field.value = global_simulation.config.orbit.ref_orbit.a;
	field = document.getElementById("opt-orb-RefOrbit-e");
	field.value = global_simulation.config.orbit.ref_orbit.e;
	field = document.getElementById("opt-orb-RefOrbit-i");
	field.value = global_simulation.config.orbit.ref_orbit.i;
	if(!global_angle_in_rads){
		field.value = field.value * 180.0 / Math.PI;
	}
	field = document.getElementById("opt-orb-RefOrbit-omega");
	field.value = global_simulation.config.orbit.ref_orbit.w;
	if(!global_angle_in_rads){
		field.value = field.value * 180.0 / Math.PI;
	}
	field = document.getElementById("opt-orb-RefOrbit-raan");
	field.value = global_simulation.config.orbit.ref_orbit.raan;
	if(!global_angle_in_rads){
		field.value = field.value * 180.0 / Math.PI;
	}
}

function switchAttitudeMenu(){
	var menu = document.getElementById("AttitudetabContainer"); 
	var divi = document.getElementById("DivAttitude"); 
	if(!global_menus.attitude.isOpen){
		updateAttitudeOptions();
		menu.className = "tabContainer openVisMenu";
		document.getElementById("AttitudeMenuLoading").style.display="none";
	}else{
		menu.className = "tabContainer closeVisMenu";
	}
	global_menus.attitude.isOpen = !global_menus.attitude.isOpen;
}
function updateAttitudeOption(id,val){
	if(val != "" || typeof val == "boolean"){
		setAttitudeReloading();
		switch(id) {
			//Models
			case "opt-att-ShowSky":
				global_simulation.config.attitude.show_sky = val;
				break;
			case "opt-att-ShowAxis":
				global_simulation.config.attitude.show_axis = val;
				break;
			case "opt-att-ShowAxisLabels":
				global_simulation.config.attitude.show_axis_labels = val;
				break;
			case "opt-att-ShowSphere":
				global_simulation.config.attitude.show_sphere = val;
				break;
			case "opt-att-ShowMiniSpheres":
				global_simulation.config.attitude.show_mini_spheres = val;
				break;
			case "opt-att-ShowCircles":
				global_simulation.config.attitude.show_circles = val;
				break;
			case "opt-att-ShowScAxis":
				global_simulation.config.attitude.show_sc_axis = val;
				break;
			case "opt-att-ShowEngineTexture":
				global_simulation.config.attitude.sc_show_eng_texture = val;
				break;
			case "opt-att-ShowSunModel":
				global_simulation.config.attitude.show_sun = val;
				if(!val){
					global_simulation.config.attitude.show_sun_texture = val;
					global_simulation.config.attitude.sun_rotates = val;
					global_simulation.config.attitude.sun_show_line = val;
					global_simulation.config.attitude.sun_show_dist = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-ShowSunSurface":
				global_simulation.config.attitude.show_sun_texture = val;
				if(val){
					global_simulation.config.attitude.show_sun = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-SunRotates":
				global_simulation.config.attitude.sun_rotates = val;
				if(val){
					global_simulation.config.attitude.show_sun = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-SunRotationSpeed":
				global_simulation.config.attitude.sun_rotation_speed = val;
				break;
			case "opt-att-ShowSunLine":
				global_simulation.config.attitude.sun_show_line = val;
				if(val){
					global_simulation.config.attitude.show_sun = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-ShowSunDistance":
				global_simulation.config.attitude.sun_show_dist = val;
				if(val){
					global_simulation.config.attitude.show_sun = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-ShowEarthModel":
				global_simulation.config.attitude.show_earth = val;
				if(!val){
					global_simulation.config.attitude.show_earth_texture = val;
					global_simulation.config.attitude.earth_rotates = val;
					global_simulation.config.attitude.earth_show_line = val;
					global_simulation.config.attitude.earth_show_dist = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-ShowEarthSurface":
				global_simulation.config.attitude.show_earth_texture = val;
				if(val){
					global_simulation.config.attitude.show_earth = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-EarthRotates":
				global_simulation.config.attitude.earth_rotates = val;
				if(val){
					global_simulation.config.attitude.show_earth = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-EarthRotationSpeed":
				global_simulation.config.attitude.earth_rotation_speed = val;
				break;
			case "opt-att-ShowEarthLine":
				global_simulation.config.attitude.earth_show_line = val;
				if(val){
					global_simulation.config.attitude.show_earth = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-ShowEarthDistance":
				global_simulation.config.attitude.earth_show_dist = val;
				if(val){
					global_simulation.config.attitude.show_earth = val;
					updateAttitudeOptions();
				}
				break;
			//Measures	
			case "opt-att-ShowOrbitalPlane":
				global_simulation.config.attitude.show_orbital_plane = val;
				if(!val){
					global_simulation.config.attitude.show_inclination = val;
					updateAttitudeOptions();
				}
				break;
			case "opt-att-ShowInclination":
				global_simulation.config.attitude.show_inclination = val;
				if(val){
					global_simulation.config.attitude.show_orbital_plane = val;
					updateAttitudeOptions();
				}
				break;
			/*case "opt-att-ShowEarthPlane":
				global_simulation.config.attitude.show_planes = val;
				break;*/
			case "opt-att-RotationPlaneColor":
				global_simulation.config.attitude.plane_xy_color = val;
				break;
			case "opt-att-OrbitalPlaneColor":
				global_simulation.config.attitude.plane_orb_color = val;
				break;
			case "opt-att-ShowSphericCoordinates":
				global_simulation.config.attitude.show_spheric_coords = val;
				break;
			case "opt-att-SphericCoordsSelection":
				global_simulation.config.attitude.spheric_coords_selection = Number(val);
				break;
			case "opt-att-ShowVectorsAngle":
				global_simulation.config.attitude.show_vectors_angle = val;
				break;
			case "opt-att-AnglesSelection1":
				global_simulation.config.attitude.vectors_angle_sel1 = Number(val);
				break;
			case "opt-att-AnglesSelection2":
				global_simulation.config.attitude.vectors_angle_sel2 = Number(val);
				break;
			
			//Indicators
			case "opt-att-ShowVelocity":
				global_simulation.config.attitude.show_velocity = val;
				break;
			case "opt-att-LimitVelocity":
				global_simulation.config.attitude.limit_velocity = val;
				break;
			case "opt-att-VelocityColor":
				global_simulation.config.attitude.color_velocity = val;
				break;
			case "opt-att-ShowAcceleration":
				global_simulation.config.attitude.show_acceleration = val;
				break;
			case "opt-att-LimitAcceleration":
				global_simulation.config.attitude.limit_acceleration = val;
				break;
			case "opt-att-AccelerationColor":
				global_simulation.config.attitude.color_acceleration = val;
				break;
			case "opt-att-ShowMomentum":
				global_simulation.config.attitude.show_momentum = val;
				break;
			case "opt-att-MomentumColor":
				global_simulation.config.attitude.color_momentum = val;
				break;
			case "opt-att-ShowTarget":
				global_simulation.config.attitude.show_target_a = val;
				break;
			case "opt-att-TargetValueX":
				global_simulation.config.attitude.value_target_a.setX(val);
				break;
			case "opt-att-TargetValueY":
				global_simulation.config.attitude.value_target_a.setY(val);
				break;
			case "opt-att-TargetValueZ":
				global_simulation.config.attitude.value_target_a.setZ(val);
				break;
			case "opt-att-TargetColor":
				global_simulation.config.attitude.color_target_a = val;
				break;
			case "opt-att-ShowVector":
				global_simulation.config.attitude.show_vector_a = val;
				break;
			case "opt-att-LimitVector":
				global_simulation.config.attitude.limit_vector_a = val;
				break;
			case "opt-att-VectorValueX":
				global_simulation.config.attitude.value_vector_a.setX(val);
				break;
			case "opt-att-VectorValueY":
				global_simulation.config.attitude.value_vector_a.setY(val);
				break;
			case "opt-att-VectorValueZ":
				global_simulation.config.attitude.value_vector_a.setZ(val);
				break;
			case "opt-att-VectorColor":
				global_simulation.config.attitude.color_vector_a = val;
				break;
			case "opt-att-ShowDirection":
				global_simulation.config.attitude.show_direction_a = val;
				break;
			case "opt-att-DirectionValueX":
				global_simulation.config.attitude.value_direction_a.setX(val);
				break;
			case "opt-att-DirectionValueY":
				global_simulation.config.attitude.value_direction_a.setY(val);
				break;
			case "opt-att-DirectionValueZ":
				global_simulation.config.attitude.value_direction_a.setZ(val);
				break;
			case "opt-att-color_direction_a":
				global_simulation.config.attitude.spheric_coords_selection = val;
				break;
			default:
		}
		global_attitude.stopAnimation();
		global_attitude = new Attitude();
		saveStoredVariables();
	}else{
		updateAttitudeOptions();
	}
}
function updateAttitudeOptions(){
	//Models
	document.getElementById("opt-att-ShowSky").checked = global_simulation.config.attitude.show_sky;
	document.getElementById("opt-att-ShowAxis").checked = global_simulation.config.attitude.show_axis;
	document.getElementById("opt-att-ShowAxisLabels").checked = global_simulation.config.attitude.show_axis_labels;
	document.getElementById("opt-att-ShowSphere").checked = global_simulation.config.attitude.show_sphere;
	document.getElementById("opt-att-ShowMiniSpheres").checked = global_simulation.config.attitude.show_mini_spheres;
	document.getElementById("opt-att-ShowCircles").checked = global_simulation.config.attitude.show_circles;
	document.getElementById("opt-att-ShowScAxis").checked = global_simulation.config.attitude.show_sc_axis;
	document.getElementById("opt-att-ShowEngineTexture").checked = global_simulation.config.attitude.sc_show_eng_texture;
	document.getElementById("opt-att-ShowSunModel").checked = global_simulation.config.attitude.show_sun;
	document.getElementById("opt-att-ShowSunSurface").checked = global_simulation.config.attitude.show_sun_texture;
	document.getElementById("opt-att-SunRotates").checked = global_simulation.config.attitude.sun_rotates;
	document.getElementById("opt-att-SunRotationSpeed").value = global_simulation.config.attitude.sun_rotation_speed;
	document.getElementById("opt-att-ShowSunLine").checked = global_simulation.config.attitude.sun_show_line;
	document.getElementById("opt-att-ShowSunDistance").checked = global_simulation.config.attitude.sun_show_dist;
	document.getElementById("opt-att-ShowEarthModel").checked = global_simulation.config.attitude.show_earth;
	document.getElementById("opt-att-ShowEarthSurface").checked = global_simulation.config.attitude.show_earth_texture;
	document.getElementById("opt-att-EarthRotates").checked = global_simulation.config.attitude.earth_rotates;
	document.getElementById("opt-att-EarthRotationSpeed").value = global_simulation.config.attitude.earth_rotation_speed;
	document.getElementById("opt-att-ShowEarthLine").checked = global_simulation.config.attitude.earth_show_line;
	document.getElementById("opt-att-ShowEarthDistance").checked = global_simulation.config.attitude.earth_show_dist;
	
	//Measures
	document.getElementById("opt-att-ShowInclination").checked = global_simulation.config.attitude.show_inclination;
	//document.getElementById("opt-att-ShowEarthPlane").checked = global_simulation.config.attitude.show_planes;
	document.getElementById("opt-att-RotationPlaneColor").value = global_simulation.config.attitude.plane_xy_color;
	//document.getElementById("opt-att-RotationPlaneColor").style.backgroundColor = global_simulation.config.attitude.plane_xy_color;
	document.getElementById("opt-att-ShowOrbitalPlane").checked = global_simulation.config.attitude.show_orbital_plane;
	document.getElementById("opt-att-OrbitalPlaneColor").value = global_simulation.config.attitude.plane_orb_color;
	//document.getElementById("opt-att-OrbitalPlaneColor").style.backgroundColor = global_simulation.config.attitude.plane_orb_color;
	document.getElementById("opt-att-ShowSphericCoordinates").checked = global_simulation.config.attitude.show_spheric_coords;
	document.getElementById("opt-att-SphericCoordsSelection").value = global_simulation.config.attitude.spheric_coords_selection;
	document.getElementById("opt-att-ShowVectorsAngle").checked = global_simulation.config.attitude.show_vectors_angle;
	document.getElementById("opt-att-AnglesSelection1").value = global_simulation.config.attitude.vectors_angle_sel1;
	document.getElementById("opt-att-AnglesSelection2").value = global_simulation.config.attitude.vectors_angle_sel2;
	
	//Indicators
	document.getElementById("opt-att-ShowVelocity").checked = global_simulation.config.attitude.show_velocity;
	document.getElementById("opt-att-LimitVelocity").value = global_simulation.config.attitude.limit_velocity;
	document.getElementById("opt-att-VelocityColor").value = global_simulation.config.attitude.color_velocity;
	//document.getElementById("opt-att-VelocityColor").style.backgroundColor = global_simulation.config.attitude.color_velocity;
	document.getElementById("opt-att-ShowAcceleration").checked = global_simulation.config.attitude.show_acceleration;
	document.getElementById("opt-att-LimitAcceleration").value = global_simulation.config.attitude.limit_acceleration;
	document.getElementById("opt-att-AccelerationColor").value = global_simulation.config.attitude.color_acceleration;
	//document.getElementById("opt-att-AccelerationColor").style.backgroundColor = global_simulation.config.attitude.color_acceleration;
	document.getElementById("opt-att-ShowMomentum").checked = global_simulation.config.attitude.show_momentum;
	document.getElementById("opt-att-MomentumColor").value = global_simulation.config.attitude.color_momentum;
	//document.getElementById("opt-att-MomentumColor").style.backgroundColor = global_simulation.config.attitude.color_momentum;
	document.getElementById("opt-att-ShowTarget").checked = global_simulation.config.attitude.show_target_a;
	document.getElementById("opt-att-TargetValueX").value = global_simulation.config.attitude.value_target_a.x;
	document.getElementById("opt-att-TargetValueY").value = global_simulation.config.attitude.value_target_a.y;
	document.getElementById("opt-att-TargetValueZ").value = global_simulation.config.attitude.value_target_a.z;
	document.getElementById("opt-att-TargetColor").value = global_simulation.config.attitude.color_target_a;
	//document.getElementById("opt-att-TargetColor").style.backgroundColor = global_simulation.config.attitude.color_target_a;
	document.getElementById("opt-att-ShowVector").checked = global_simulation.config.attitude.show_vector_a;
	document.getElementById("opt-att-LimitVector").value = global_simulation.config.attitude.limit_vector_a;
	document.getElementById("opt-att-VectorValueX").value = global_simulation.config.attitude.value_vector_a.x;
	document.getElementById("opt-att-VectorValueY").value = global_simulation.config.attitude.value_vector_a.y;
	document.getElementById("opt-att-VectorValueZ").value = global_simulation.config.attitude.value_vector_a.z;
	document.getElementById("opt-att-VectorColor").value = global_simulation.config.attitude.color_vector_a;
	//document.getElementById("opt-att-VectorColor").style.backgroundColor = global_simulation.config.attitude.color_vector_a;
	document.getElementById("opt-att-ShowDirection").checked = global_simulation.config.attitude.show_direction_a;
	document.getElementById("opt-att-DirectionValueX").value = global_simulation.config.attitude.value_direction_a.x;
	document.getElementById("opt-att-DirectionValueY").value = global_simulation.config.attitude.value_direction_a.y;
	document.getElementById("opt-att-DirectionValueZ").value = global_simulation.config.attitude.value_direction_a.z;
	document.getElementById("opt-att-DirectionColor").value = global_simulation.config.attitude.color_direction_a;
	//document.getElementById("opt-att-DirectionColor").style.backgroundColor = global_simulation.config.attitude.color_direction_a;
	
}
function updateGlobalOption(id,val){
	if(val != "" || typeof val == "boolean"){
		switch(id) {
			case "opt-glo-ShowFps":
				global_simulation.config.global.show_fps = val;		
				break;
			case "opt-glo-PerformanceLevel":
				global_simulation.config.global.performance_level = Number(val);
				setPerformanceLevel();
				break;
			default:
		}	
		/*global_orbit.stopAnimation();
		global_orbit = new Orbit();
		global_attitude.stopAnimation();
		global_attitude = new Attitude();*/
		saveStoredVariables();
		window.location.reload();
	}else{
		initializeGlobalMenu();
	}
}
function initializeGlobalMenu(){

	document.getElementById("opt-glo-ShowFps").checked = global_simulation.config.global.show_fps;
	document.getElementById("opt-glo-PerformanceLevel").value = global_simulation.config.global.performance_level;
}