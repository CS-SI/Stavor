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
	var menu = document.getElementById("MaptabContainer"); 
	var divi = document.getElementById("DivMap"); 
	var w = Math.min(400,divi.clientWidth);
	if(menu.style.display == "none" || menu.style.display == ""){
		menu.style.right = "-"+w+"px"; 
		menu.style.display = "block";
		$('#MaptabContainer').animate({
			right: '0'
		},"slow");
	}else{
		$('#MaptabContainer').animate({
			right: '-100%'
		},"slow",mapEndClosing);
	}
}
function mapEndClosing(){
	var menu = document.getElementById("MaptabContainer"); 
	menu.style.display = "none";
}
function switchOrbitMenu(){
	var menu = document.getElementById("OrbittabContainer"); 
	var divi = document.getElementById("DivOrbit"); 
	var w = Math.min(400,divi.clientWidth);
	if(menu.style.display == "none" || menu.style.display == ""){
		updateOrbitOptions();
		menu.style.right = "-"+w+"px"; 
		menu.style.display = "block";
		$('#OrbittabContainer').animate({
			right: '0'
		},"slow");
	}else{
		$('#OrbittabContainer').animate({
			right: '-100%'
		},"slow",orbitEndClosing);
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
			break;
		case "opt-orb-ShowEarthAtmosphere":
			global_simulation.config.orbit.show_earth_atmosphere = val;
			break;
		case "opt-orb-ShowEarthClouds":
			global_simulation.config.orbit.show_earth_clouds = val;
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
	document.getElementById("opt-orb-ShowSpacecraft").checked = global_simulation.config.orbit.show_spacecraft;
	document.getElementById("opt-orb-SpacecraftColor").value = global_simulation.config.orbit.spacecraft_color;
	document.getElementById("opt-orb-ShowProjection").checked = global_simulation.config.orbit.show_projection;
	document.getElementById("opt-orb-OrbitColor").value = global_simulation.config.orbit.orbit_color;
}
function orbitEndClosing(){
	var menu = document.getElementById("OrbittabContainer"); 
	menu.style.display = "none";
}
function switchAttitudeMenu(){
	var menu = document.getElementById("AttitudetabContainer"); 
	var divi = document.getElementById("DivAttitude"); 
	var w = Math.min(400,divi.clientWidth);
	if(menu.style.display == "none" || menu.style.display == ""){
		menu.style.right = "-"+w+"px"; 
		menu.style.display = "block";
		$('#AttitudetabContainer').animate({
			right: '0'
		},"slow");
	}else{
		$('#AttitudetabContainer').animate({
			right: '-100%'
		},"slow",attitudeEndClosing);
	}
}
function attitudeEndClosing(){
	var menu = document.getElementById("AttitudetabContainer"); 
	menu.style.display = "none";
}