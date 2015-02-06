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
		for (var i = 1; i < pages.length; i++) {
		 pages.item(i).style.display="none";
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
		 pages.item(i).style.display="none";
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
		 pages.item(i).style.display="none";
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
  document.getElementById("Maptabpage_" + ident).style.display="block";
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
	if(global_menus.map.inAnimation == false){
		global_menus.map.inAnimation = true;
		var menu = document.getElementById("MaptabContainer"); 
		var divi = document.getElementById("DivMap"); 
		var w = Math.min(400,divi.clientWidth);
		if(!global_menus.map.isOpen){
			menu.style.right = "-"+w+"px"; 
			menu.style.display = "block";
			$('#MaptabContainer').animate({
				right: '0'
			},"slow",endMapAnimation);
		}else{
			$('#MaptabContainer').animate({
				right: '-100%'
			},"slow",mapEndClosing);
		}
		global_menus.map.isOpen = !global_menus.map.isOpen;
	}
}
function mapEndClosing(){
	var menu = document.getElementById("MaptabContainer"); 
	menu.style.display = "none";
	endMapAnimation();
}
function switchOrbitMenu(){
	if(global_menus.orbit.inAnimation == false){
		global_menus.orbit.inAnimation = true;
		var menu = document.getElementById("OrbittabContainer"); 
		var divi = document.getElementById("DivOrbit"); 
		var w = Math.min(400,divi.clientWidth);
		if(!global_menus.orbit.isOpen){
			updateOrbitOptions();
			menu.style.right = "-"+w+"px"; 
			menu.style.display = "block";
			$('#OrbittabContainer').animate({
				right: '0'
			},"slow",endOrbitAnimation);
		}else{
			$('#OrbittabContainer').animate({
				right: '-100%'
			},"slow",orbitEndClosing);
		}
		global_menus.orbit.isOpen = !global_menus.orbit.isOpen;
	}
}
function updateOrbitOption(id,val){
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
			global_simulation.config.orbit.color_xy_plane = "#"+val;
			break;
		case "opt-orb-ShowSpacecraft":
			global_simulation.config.orbit.show_spacecraft = val;
			break;
		case "opt-orb-SpacecraftColor":
			global_simulation.config.orbit.spacecraft_color = "#"+val;
			break;
		case "opt-orb-ShowProjection":
			global_simulation.config.orbit.show_projection = val;
			break;
		case "opt-orb-OrbitColor":
			global_simulation.config.orbit.orbit_color = "#"+val;
			break;
		default:
	}
	global_orbit.stopAnimation();
	global_orbit = new Orbit();
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
	document.getElementById("opt-orb-PlaneXyColor").style.backgroundColor = global_simulation.config.orbit.color_xy_plane;
	
	document.getElementById("opt-orb-ShowSpacecraft").checked = global_simulation.config.orbit.show_spacecraft;
	
	document.getElementById("opt-orb-SpacecraftColor").value = global_simulation.config.orbit.spacecraft_color;
	document.getElementById("opt-orb-SpacecraftColor").style.backgroundColor = global_simulation.config.orbit.spacecraft_color;
	
	document.getElementById("opt-orb-ShowProjection").checked = global_simulation.config.orbit.show_projection;
	document.getElementById("opt-orb-OrbitColor").value = global_simulation.config.orbit.orbit_color;
	document.getElementById("opt-orb-OrbitColor").style.backgroundColor = global_simulation.config.orbit.orbit_color;
}
function orbitEndClosing(){
	var menu = document.getElementById("OrbittabContainer"); 
	menu.style.display = "none";
	endOrbitAnimation();
}
function switchAttitudeMenu(){
	if(global_menus.attitude.inAnimation == false){
		global_menus.attitude.inAnimation = true;
		var menu = document.getElementById("AttitudetabContainer"); 
		var divi = document.getElementById("DivAttitude"); 
		var w = Math.min(400,divi.clientWidth);
		if(!global_menus.attitude.isOpen){
			updateAttitudeOptions();
			menu.style.right = "-"+w+"px"; 
			menu.style.display = "block";
			$('#AttitudetabContainer').animate({
				right: '0'
			},"slow",endAttitudeAnimation);
		}else{
			$('#AttitudetabContainer').animate({
				right: '-100%'
			},"slow",attitudeEndClosing);
		}
		global_menus.attitude.isOpen = !global_menus.attitude.isOpen;
	}
}
function updateAttitudeOption(id,val){
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
			global_simulation.config.attitude.plane_xy_color = "#"+val;
			break;
		case "opt-att-OrbitalPlaneColor":
			global_simulation.config.attitude.plane_orb_color = "#"+val;
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
			global_simulation.config.attitude.color_velocity = "#"+val;
			break;
		case "opt-att-ShowAcceleration":
			global_simulation.config.attitude.show_acceleration = val;
			break;
		case "opt-att-LimitAcceleration":
			global_simulation.config.attitude.limit_acceleration = val;
			break;
		case "opt-att-AccelerationColor":
			global_simulation.config.attitude.color_acceleration = "#"+val;
			break;
		case "opt-att-ShowMomentum":
			global_simulation.config.attitude.show_momentum = val;
			break;
		case "opt-att-MomentumColor":
			global_simulation.config.attitude.color_momentum = "#"+val;
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
			global_simulation.config.attitude.color_target_a = "#"+val;
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
			global_simulation.config.attitude.color_vector_a = "#"+val;
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
			global_simulation.config.attitude.spheric_coords_selection = "#"+val;
			break;
		default:
	}
	global_attitude.stopAnimation();
	global_attitude = new Attitude();
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
	document.getElementById("opt-att-RotationPlaneColor").style.backgroundColor = global_simulation.config.attitude.plane_xy_color;
	document.getElementById("opt-att-ShowOrbitalPlane").checked = global_simulation.config.attitude.show_orbital_plane;
	document.getElementById("opt-att-OrbitalPlaneColor").value = global_simulation.config.attitude.plane_orb_color;
	document.getElementById("opt-att-OrbitalPlaneColor").style.backgroundColor = global_simulation.config.attitude.plane_orb_color;
	document.getElementById("opt-att-ShowSphericCoordinates").checked = global_simulation.config.attitude.show_spheric_coords;
	document.getElementById("opt-att-SphericCoordsSelection").value = global_simulation.config.attitude.spheric_coords_selection;
	document.getElementById("opt-att-ShowVectorsAngle").checked = global_simulation.config.attitude.show_vectors_angle;
	document.getElementById("opt-att-AnglesSelection1").value = global_simulation.config.attitude.vectors_angle_sel1;
	document.getElementById("opt-att-AnglesSelection2").value = global_simulation.config.attitude.vectors_angle_sel2;
	
	//Indicators
	document.getElementById("opt-att-ShowVelocity").checked = global_simulation.config.attitude.show_velocity;
	document.getElementById("opt-att-LimitVelocity").value = global_simulation.config.attitude.limit_velocity;
	document.getElementById("opt-att-VelocityColor").value = global_simulation.config.attitude.color_velocity;
	document.getElementById("opt-att-VelocityColor").style.backgroundColor = global_simulation.config.attitude.color_velocity;
	document.getElementById("opt-att-ShowAcceleration").checked = global_simulation.config.attitude.show_acceleration;
	document.getElementById("opt-att-LimitAcceleration").value = global_simulation.config.attitude.limit_acceleration;
	document.getElementById("opt-att-AccelerationColor").value = global_simulation.config.attitude.color_acceleration;
	document.getElementById("opt-att-AccelerationColor").style.backgroundColor = global_simulation.config.attitude.color_acceleration;
	document.getElementById("opt-att-ShowMomentum").checked = global_simulation.config.attitude.show_momentum;
	document.getElementById("opt-att-MomentumColor").value = global_simulation.config.attitude.color_momentum;
	document.getElementById("opt-att-MomentumColor").style.backgroundColor = global_simulation.config.attitude.color_momentum;
	document.getElementById("opt-att-ShowTarget").checked = global_simulation.config.attitude.show_target_a;
	document.getElementById("opt-att-TargetValueX").value = global_simulation.config.attitude.value_target_a.x;
	document.getElementById("opt-att-TargetValueY").value = global_simulation.config.attitude.value_target_a.y;
	document.getElementById("opt-att-TargetValueZ").value = global_simulation.config.attitude.value_target_a.z;
	document.getElementById("opt-att-TargetColor").value = global_simulation.config.attitude.color_target_a;
	document.getElementById("opt-att-TargetColor").style.backgroundColor = global_simulation.config.attitude.color_target_a;
	document.getElementById("opt-att-ShowVector").checked = global_simulation.config.attitude.show_vector_a;
	document.getElementById("opt-att-LimitVector").value = global_simulation.config.attitude.limit_vector_a;
	document.getElementById("opt-att-VectorValueX").value = global_simulation.config.attitude.value_vector_a.x;
	document.getElementById("opt-att-VectorValueY").value = global_simulation.config.attitude.value_vector_a.y;
	document.getElementById("opt-att-VectorValueZ").value = global_simulation.config.attitude.value_vector_a.z;
	document.getElementById("opt-att-VectorColor").value = global_simulation.config.attitude.color_vector_a;
	document.getElementById("opt-att-VectorColor").style.backgroundColor = global_simulation.config.attitude.color_vector_a;
	document.getElementById("opt-att-ShowDirection").checked = global_simulation.config.attitude.show_direction_a;
	document.getElementById("opt-att-DirectionValueX").value = global_simulation.config.attitude.value_direction_a.x;
	document.getElementById("opt-att-DirectionValueY").value = global_simulation.config.attitude.value_direction_a.y;
	document.getElementById("opt-att-DirectionValueZ").value = global_simulation.config.attitude.value_direction_a.z;
	document.getElementById("opt-att-DirectionColor").value = global_simulation.config.attitude.color_direction_a;
	document.getElementById("opt-att-DirectionColor").style.backgroundColor = global_simulation.config.attitude.color_direction_a;
	
}
function attitudeEndClosing(){
	var menu = document.getElementById("AttitudetabContainer"); 
	menu.style.display = "none";
	endAttitudeAnimation();
}
function endAttitudeAnimation(){
	global_menus.attitude.inAnimation = false;
}
function endOrbitAnimation(){
	global_menus.orbit.inAnimation = false;
}
function endMapAnimation(){
	global_menus.map.inAnimation = false;
}