var Map = function () 
{	
	// Global pointers
	var global = global_simulation.config.global;
	var config = global_simulation.config.map;
	var results = global_simulation.results.map;
	var segments = global_3d_segments.map;
	
	var map;
	
	//******************************************************
//---------------------- FOV --------------------------
//******************************************************
function drawFov(){
	if(config.show_fov){
		sc_layer.removeAllFeatures();
		var fov_terminator = results.fov.terminator;
		//WHOLE EARTH CASE
		if(typeof fov_terminator != "undefined" && fov_terminator.length > 0){
			var fovPoints = [];
			var sign = 1;
			var sign_lat = -1;
			//draw polygon 
			for (var i in fov_terminator) {
				if(i==0){
					//Sign
					if(fov_terminator[i].longitude>0)
						sign = -1;
					if(sc_latitude>=0)
						sign_lat = 1;
					var point = new OpenLayers.Geometry.Point(
						-179.999*sign, 
						89.99*sign_lat
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					fovPoints.push(point);
					//open polygon 2
					var point = new OpenLayers.Geometry.Point(
						-179.999*sign, 
						fov_terminator[i].latitude
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					fovPoints.push(point);
				}

				var point = new OpenLayers.Geometry.Point(
					fov_terminator[i].longitude, 
					fov_terminator[i].latitude
				).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
				fovPoints.push(point);

				if(i==fov_terminator.length-1){
					//close polygon 1
					var point = new OpenLayers.Geometry.Point(
						179.999*sign, 
						fov_terminator[i].latitude
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					fovPoints.push(point);
					//close polygon				
					var point = new OpenLayers.Geometry.Point(
						179.999*sign, 
						89.99*sign_lat
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					fovPoints.push(point);
				}
		
			}		

			if(fovPoints.length>0){
				//fovPoints.push(fovPoints[0]);
				/*console.log("sc_latitude: ".concat(String(sc_latitude)));
				for(var i in fovPoints){
				var pt = fovPoints[i].clone();
				pt=pt.transform(map.getProjectionObject(),new OpenLayers.Projection("EPSG:4326"));
				//console.log("( ".concat(String(pt.y).concat(" , ".concat(String(pt.x).concat(" )")))));
				}*/
				var linearRing = new OpenLayers.Geometry.LinearRing(fovPoints);
				var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
				var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, sc_style);
				sc_layer.addFeatures([polygonFeature]);
			}
		}else{
			//Normal FOVs
			if(results.fov.fov_type==0)
				paintFovClosedArea();
			else
				paintFovOpenArea();
		}

	}
}
function paintFovClosedArea(){
	//NORMAL CASE
	var fovPointsA = [];
	var fovPointsB = [];
	var fov_tmp_long = 0, fov_tmp_lat = 0;
	var first = true;
	for (var i in results.fov.closed) {
		var point = new OpenLayers.Geometry.Point(results.fov.closed[i].longitude, results.fov.closed[i].latitude);
		// transform from WGS 1984 to Spherical Mercator
		point.transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());


		if((i!=0) && ((results.fov.closed[i].longitude*fov_tmp_long)<0) && (Math.abs(results.fov.closed[i].longitude)+Math.abs(fov_tmp_long)>180.0)){
			var avg_lat = (results.fov.closed[i].latitude+fov_tmp_lat)/2;
			if(results.fov.closed[i].longitude > 0)
				var new_lon = -179.999999;
			else
				var new_lon = 179.999999;
			if(first){
				fovPointsA.push(new OpenLayers.Geometry.Point(new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				fovPointsB.push(new OpenLayers.Geometry.Point(-new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				first=false;
			}else{
				fovPointsA.push(new OpenLayers.Geometry.Point(-new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				fovPointsB.push(new OpenLayers.Geometry.Point(+new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				first=true;
			}
		}
		fov_tmp_lat = results.fov.closed[i].latitude;
		fov_tmp_long = results.fov.closed[i].longitude;

		if(first){
			fovPointsA.push(point);
		}else{
			fovPointsB.push(point);
		}
	}				

	if(fovPointsA.length>0){
		fovPointsA.push(fovPointsA[0]);
		var linearRing = new OpenLayers.Geometry.LinearRing(fovPointsA);
		var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
		var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, sc_style);
		sc_layer.addFeatures([polygonFeature]);
	}
	if(fovPointsB.length>0){
		fovPointsB.push(fovPointsB[0]);
		var linearRing = new OpenLayers.Geometry.LinearRing(fovPointsB);
		var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
		var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, sc_style);
		sc_layer.addFeatures([polygonFeature]);
	}
}
function paintFovOpenArea(){
	//Poles CASE
	var fovPointsA = [];
	var fov_tmp_long = 0, fov_tmp_lat = 0;
	var first = true;
	for (var i in results.fov.terminator) {
		var point = new OpenLayers.Geometry.Point(results.fov.terminator[i].longitude, results.fov.terminator[i].latitude);
		// transform from WGS 1984 to Spherical Mercator
		point.transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());


		if((i!=0) && ((results.fov.terminator[i].longitude*fov_tmp_long)<0) && (Math.abs(results.fov.terminator[i].longitude)+Math.abs(fov_tmp_long)>180.0)){
			var avg_lat = (results.fov.terminator[i].latitude+fov_tmp_lat)/2;
			if(results.fov.terminator[i].longitude > 0)
				var new_lon = -179.999999;
			else
				var new_lon = 179.999999;
			if(fov_type == 1)
				var new_lat = 90.0;
			else
				var new_lat = -90.0;

			fovPointsA.push(new OpenLayers.Geometry.Point(new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
			fovPointsA.push(new OpenLayers.Geometry.Point(new_lon, new_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
			fovPointsA.push(new OpenLayers.Geometry.Point(-new_lon, new_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
			fovPointsA.push(new OpenLayers.Geometry.Point(-new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));

		}
		fov_tmp_lat = results.fov.terminator[i].latitude;
		fov_tmp_long = results.fov.terminator[i].longitude;

		fovPointsA.push(point);
	}				

	if(fovPointsA.length>0){
		fovPointsA.push(fovPointsA[0]);
		var linearRing = new OpenLayers.Geometry.LinearRing(fovPointsA);
		var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
		var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, sc_style);
		sc_layer.addFeatures([polygonFeature]);
	}
}

//******************************************************
//---------------------- Stations --------------------------
//******************************************************
function addSelectedStationsNames(){
	stations_name_layer.removeAllFeatures();
	for (var arrayIndex in config.stations){
		var point = new OpenLayers.Geometry.Point(config.stations[arrayIndex].longitude, config.stations[arrayIndex].latitude).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
		var station_name_feature = new OpenLayers.Feature.Vector(point, null, null);
		station_name_feature.attributes = {
			label:config.stations[arrayIndex].name
		}
		stations_name_layer.addFeatures([station_name_feature]);
	}
}

function drawStationsAreas(){
	if(Math.abs(sc_altitude-sc_altitude_tmp)>sc_altitude_step){
		stations_area_layer.removeAllFeatures();
		for (var arrayIndex in results.station_areas){
			if(results.station_areas[arrayIndex].type==0)
				paintClosedArea(arrayIndex);
			else
				paintOpenArea(arrayIndex,results.station_areas[arrayIndex].type);

		}
	
		sc_altitude_tmp = sc_altitude;
	}
}

function paintOpenArea(arrayIndex,type){
	var areaFirst = [];
	var area_tmp_long = 0, area_tmp_lat = 0;
	for (var i in results.station_areas[arrayIndex].points) {
		var coord = results.station_areas[arrayIndex].points[i];
		var point = new OpenLayers.Geometry.Point(
							coord.longitude, 
							coord.latitude
			).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());

		if((i!=0) && ((coord.longitude*area_tmp_long)<0) && (Math.abs(coord.longitude)+Math.abs(area_tmp_long)>180.0)){
			var avg_lat = (coord.latitude+area_tmp_lat)/2;
			if(coord.longitude > 0)
				var new_lon = -179.999999;
			else
				var new_lon = 179.999999;
			if(type == 1)
				var new_lat = 90.0;
			else
				var new_lat = -90.0;

			areaFirst.push(new OpenLayers.Geometry.Point(new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
			areaFirst.push(new OpenLayers.Geometry.Point(new_lon, new_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
			areaFirst.push(new OpenLayers.Geometry.Point(-new_lon, new_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
			areaFirst.push(new OpenLayers.Geometry.Point(-new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));

		}
		area_tmp_lat = coord.latitude;
		area_tmp_long = coord.longitude;

		areaFirst.push(point);					
	}				
	if(areaFirst.length>0){
		//areaFirst.push(areaFirst[0]);
		var linearRing = new OpenLayers.Geometry.LinearRing(areaFirst);
		var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
		var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, station_area_style);
		stations_area_layer.addFeatures([polygonFeature]);
	}
}

function paintClosedArea(arrayIndex){
	var areaFirst = [];
	var areaSecond = [];
	var area_tmp_long = 0, area_tmp_lat = 0;
	var first = true;
	for (var i in results.station_areas[arrayIndex].points) {
		var coord = results.station_areas[arrayIndex].points[i];
		var point = new OpenLayers.Geometry.Point(
							coord.longitude, 
							coord.latitude
			).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());

		if((i!=0) && ((coord.longitude*area_tmp_long)<0) && (Math.abs(coord.longitude)+Math.abs(area_tmp_long)>180.0)){
			var avg_lat = (coord.latitude+area_tmp_lat)/2;
			if(coord.longitude > 0)
				var new_lon = -179.999999;
			else
				var new_lon = 179.999999;
			if(first){
				areaFirst.push(new OpenLayers.Geometry.Point(new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				areaSecond.push(new OpenLayers.Geometry.Point(-new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				first=false;
			}else{
				areaFirst.push(new OpenLayers.Geometry.Point(-new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				areaSecond.push(new OpenLayers.Geometry.Point(+new_lon, avg_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
				first=true;
			}
		}
		area_tmp_lat = coord.latitude;
		area_tmp_long = coord.longitude;

		if(first){
			areaFirst.push(point);
		}else{
			areaSecond.push(point);
		}					
	}				
	if(areaFirst.length>0){
		//areaFirst.push(areaFirst[0]);
		var linearRing = new OpenLayers.Geometry.LinearRing(areaFirst);
		var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
		var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, station_area_style);
		stations_area_layer.addFeatures([polygonFeature]);
	}
	if(areaSecond.length>0){
		//areaSecond.push(areaSecond[0]);
		var linearRing = new OpenLayers.Geometry.LinearRing(areaSecond);
		var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
		var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, station_area_style);
		stations_area_layer.addFeatures([polygonFeature]);
	}
}
//******************************************************
//---------------------- Sun --------------------------
//******************************************************
function drawSun(){
	if(config.show_sun_icon){
		sun_layer.removeAllFeatures();    
		var marker_sun = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(results.sun_position.lon, results.sun_position.lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()), null, {
			externalGraphic: "modules/map/textures/sun.png",
			graphicWidth: 32,
			graphicHeight: 32,
			fillOpacity: 1
		});
		sun_layer.addFeatures([marker_sun]);
		sun_lon_tmp = results.sun_position.lon;
	}
}
var nightStyle = {
      strokeColor: "#FFcc00",
      strokeOpacity: 0.5,
      strokeWidth: 1,
      fillOpacity: 0.3,
	fillColor: "#000000"
};

function drawSolarTerminator(){
	if(typeof results.solarTerminator != "undefined" && results.solarTerminator.length > 0){
		drawSun();
		if(config.show_sun_terminator){
			night_layer.removeAllFeatures();

			var solarPoints = [];
			var sign = 1;
			var sign_lat = 1;
			//draw polygon 
			for (var i in results.solarTerminator) {
				if(i==0){
					//Sign
					if(results.solarTerminator[i].longitude>0)
						sign = -1;
					if(results.sun_position.lat>=0)
						sign_lat = -1;
					//open polygon 1
					var point = new OpenLayers.Geometry.Point(
						-179.999*sign, 
						89.99*sign_lat
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					solarPoints.push(point);
					//open polygon 2
					var point = new OpenLayers.Geometry.Point(
						-179.999*sign, 
						results.solarTerminator[i].latitude
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					solarPoints.push(point);
				}

				var point = new OpenLayers.Geometry.Point(
					results.solarTerminator[i].longitude, 
					results.solarTerminator[i].latitude
				).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
				solarPoints.push(point);

				if(i==results.solarTerminator.length-1){
					//close polygon 1
					var point = new OpenLayers.Geometry.Point(
						179.999*sign, 
						results.solarTerminator[i].latitude
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					solarPoints.push(point);
					//close polygon
					var point = new OpenLayers.Geometry.Point(
						179.999*sign, 
						89.99*sign_lat
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					solarPoints.push(point);
				}
		
			}		

			if(solarPoints.length>0){
				solarPoints.push(solarPoints[0]);

				var linearRing = new OpenLayers.Geometry.LinearRing(solarPoints);
				var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
				var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, nightStyle);
				night_layer.addFeatures([polygonFeature]);
			}
		}
	}

}

//******************************************************
//---------------------- Interface ---------------------
//******************************************************

function clearPath(){
	myMultyPath = new Paths();
	lineLayer.removeAllFeatures();
	sc_layer.removeAllFeatures();
	//stations_layer.removeAllFeatures();
	stations_area_layer.removeAllFeatures();
	//reDraw();
}
function addPathPoints(points){
	try{
	for(var i in points){
		myMultyPath.addPointToPath(points[i]);
	}
	if(points.length>1){
		sc_latitude = points[points.length-1].latitude;
		sc_longitude = points[points.length-1].longitude;
		sc_altitude = points[points.length-1].altitude;
		
		reDraw();
	}
	}catch( e ){
		console.log("StavorException in reDraw map: "+e.message+e.stack);
	}
}
function addPathPoint(point){
	
	myMultyPath.addPointToPath(point);
	sc_latitude = point.latitude;
	sc_longitude = point.longitude;
	sc_altitude = point.altitude;

	if(config.show_satellite){
		sc_marker_layer.removeAllFeatures();    
		var marker_sat = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(sc_longitude, sc_latitude).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()), null, {
			externalGraphic: "modules/map/textures/sat.png",
			graphicWidth: 30,
			graphicHeight: 30,
			fillOpacity: 1
		});
		sc_marker_layer.addFeatures([marker_sat]);
	}

	reDraw();
}
function changeView(view_mode){
	switch(view_mode){
		case "Free"://free
			global_cameras.map.view_locked = false;
			break;
		case "Locked"://locked
			global_cameras.map.view_locked = true;
			break;
		default://xyz
			global_cameras.map.view_locked = false;
			break;
	}
}

//******************************************************
//---------------------- END --------------------------
//******************************************************	

	/*OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {                
		defaultHandlerOptions: {
			'single': true,
			'double': false,
			'pixelTolerance': 0,
			'stopSingle': false,
			'stopDouble': false
		},

		initialize: function(options) {
			this.handlerOptions = OpenLayers.Util.extend(
				{}, this.defaultHandlerOptions
			);
			OpenLayers.Control.prototype.initialize.apply(
				this, arguments
			); 
			this.handler = new OpenLayers.Handler.Click(
				this, {
					'click': this.trigger
				}, this.handlerOptions
			);
		}, 

		trigger: function(e) {		
			changeVisualization(enum_visualizations.MAP);
		}

	});*/

	// Get rid of address bar on iphone/ipod
	var fixSize = function() {
		window.scrollTo(0,0);
		document.body.style.height = '100%';
		if (!(/(iphone|ipod)/.test(navigator.userAgent.toLowerCase()))) {
			if (document.body.parentNode) {
				document.body.parentNode.style.height = '100%';
			}
		}
	};
	setTimeout(fixSize, 700);
	setTimeout(fixSize, 1500);

	var init = function () {
		// create map
		map = new OpenLayers.Map({
			div: "map",
			theme: null,
			numZoomLevels: 18,
			controls: [
				/*new OpenLayers.Control.TouchNavigation({
					dragPanOptions: {
						enableKinetic: true
					}
				})*/
				new OpenLayers.Control.Navigation({
					dragPanOptions: {
						enableKinetic: true
					}
				})/*,
				new OpenLayers.Control.Attribution(),
				new OpenLayers.Control.Zoom()*/
				/*new OpenLayers.Control.Navigation(
                    {mouseWheelOptions: {interval: 100}}
                ),
                new OpenLayers.Control.PanZoom()*/
			],
			layers: [
				new OpenLayers.Layer.OSM("OpenStreetMap", ["http://a.basemaps.cartocdn.com/dark_all/${z}/${x}/${y}.png"]/*null*/, {
					transitionEffect: 'null',
			wrapDateLine: false
				})
			],
			center: global_cameras.map.position,
			zoom: global_cameras.map.zoom
		});

		/*map.events.register("moveend", map, function(){
			 var lyr = map.getLayersByName('vectorLayer')[0];
			 map.removeLayer(lyr);
		});*/

		map.events.register("zoomend", map, function(){
			 //storeNewZoom(map.getZoom());
		});
		
		/*var click = new OpenLayers.Control.Click();
		map.addControl(click);
		click.activate();*/

	};

	//HARD-CODED PARAMS
	var sc_altitude_step = 10;

	//VOLATILE PARAMS
	var sc_latitude = 0;
	var sc_altitude_tmp = 0;
	var sc_longitude = 0;
	var sc_altitude = 0;
	var sun_lon_tmp = 0;

	init();
	/*var lonLat = new OpenLayers.LonLat( 0.0 ,0.0 )
	  .transform(
	    new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
	    map.getProjectionObject() // to Spherical Mercator Projection
	  );
 
   	var zoom=1;*/

	//setLoadingProgress(60);

 /*
    var markers = new OpenLayers.Layer.Markers( "Markers" );
    map.addLayer(markers);
 
    markers.addMarker(new OpenLayers.Marker(lonLat));
 
    
*/

//******************************************************
//         SUN MARKER
//******************************************************

	var sun_layer = new OpenLayers.Layer.Vector( "Sun Marker" );
	map.addLayer(sun_layer);

//******************************************************
//         NIGHT LINE
//******************************************************

	var night_layer = new OpenLayers.Layer.Vector("Night Layer");
	map.addLayer(night_layer);

//******************************************************
//         STATION AREA
//******************************************************

	var station_area_style = {
	      strokeColor: "#009c00",
	      strokeOpacity: 0.5,
	      strokeWidth: 1,
	      fillOpacity: 0.1,
		fillColor: "#00FF00"
	};

	var station_area_style2 = {
	      strokeColor: "#00009c",
	      strokeOpacity: 0.5,
	      strokeWidth: 1,
	      fillOpacity: 0.1,
		fillColor: "#0000FF"
	};

	var Rt = 6371000;

	var stations_area_layer = new OpenLayers.Layer.Vector("Stations Area Layer");

	/*var stations_area_layer = new OpenLayers.Layer.Vector("Stations Area Layer", {
		   projection: map.displayProjection,
		   preFeatureInsert: function(feature) {
		   	feature.geometry.transform(new OpenLayers.Projection("EPSG:4258"), new OpenLayers.Projection("EPSG:4326"));
		   }
		}); */

	map.addLayer(stations_area_layer);


//******************************************************
//         STATION NAME
//******************************************************
	var stationNameStyle = 
		OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
	stationNameStyle.strokeColor = "#098700";
	stationNameStyle.strokeOpacity = "0.8";
	stationNameStyle.strokeWidth = "0.1";
	stationNameStyle.fillColor = "#00FF00";
	stationNameStyle.fillOpacity = 0.4;
	stationNameStyle.strokeWidth = 1;
	stationNameStyle.label = "${label}";
	stationNameStyle.labelOutlineColor = "white";
	stationNameStyle.labelOutlineWidth = 0.3;
	stationNameStyle.fontSize="10px";
	stationNameStyle.fontWeight="bold";
	stationNameStyle.fontColor="#000000";
	stationNameStyle.pointRadius="1";
	stationNameStyle.labelYOffset="10";	
	
	var smap = new OpenLayers.StyleMap({"default": stationNameStyle});

	var stations_name_layer = new OpenLayers.Layer.Vector("StationsName Layer", {"styleMap": smap});
	map.addLayer(stations_name_layer);

//******************************************************
//         PATH
//******************************************************
	var style_path = { 
		strokeColor: '#ff0000', 
	  	strokeOpacity: 0.5,
	  	strokeWidth: 3
	};

	var lineLayer = new OpenLayers.Layer.Vector("Line Layer"); 
	map.addLayer(lineLayer);                    
	map.addControl(new OpenLayers.Control.DrawFeature(lineLayer, OpenLayers.Handler.Path)); 

//******************************************************
//         SPACECRAFT
//******************************************************
	var sc_style = {
	      strokeColor: "#FF0000",
	      strokeOpacity: 0.5,
	      strokeWidth: 1,
	      fillOpacity: 0.3,
		fillColor: "#FF2200"
	};

	var sc_layer = new OpenLayers.Layer.Vector("SC Layer");
	map.addLayer(sc_layer);


	var sc_marker_layer = new OpenLayers.Layer.Vector( "SC Marker" );
	map.addLayer(sc_marker_layer);

//******************************************************
//---------------------- INIT --------------------------
//******************************************************

	//PRE-STORED PATH
	if(config.show_track){
		lineLayer.addFeatures([myMultyPath.getFeature()]);
	}

	//STATIONS NAME
	addSelectedStationsNames();

	//setLoadingProgress(100);
	global_delayed_loading.visualization.map = true;
	setLoadingText("Map module loaded!");
	hideSplash();
	setMapReloaded();
//------------------------------------------------------------------------------

	function reDraw(){
	//Night line and Sun
		drawSolarTerminator();
	//Path
		if(config.show_track){
			lineLayer.removeAllFeatures();
			lineLayer.addFeatures([myMultyPath.getFeature()]);
		}

	//Spacecraft
		drawFov();

	//Follow Spacecraft
		if(global_cameras.map.view_locked){
		    	var lonLat = new OpenLayers.LonLat( sc_longitude ,sc_latitude )
			  .transform(
			    new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
			    map.getProjectionObject() // to Spherical Mercator Projection
			  );
			map.setCenter(lonLat, map.getZoom());
		}

	//Stations	
		addSelectedStationsNames();
		drawStationsAreas();	
	}
	
	
	/*var container = document.getElementById( 'map' );
	container.addEventListener("transitionend", onWindowResize, false);*/
	//window.addEventListener( 'resize', onWindowResize, false );
	
	function onWindowResize(){//XGGDEBUG: not used yet in window
		map.updateSize();
	}
	
	Map.prototype.resizeCanvas = function(){
		onWindowResize();
	}
	
	Map.prototype.stopAnimation = function(){
		//global_cameras.map.view_locked = global_cameras.map.view_locked;
		global_cameras.map.position = map.getCenter();
		global_cameras.map.zoom = map.getZoom();
		map.destroy();
	}
	
	Map.prototype.updateModelState = function(){
		if (typeof results.point != "undefined"){
			addPathPoint(results.point);
		}
	}
	
	Map.prototype.clearDataLogs = function(){
		clearPath();
	}
	
	Map.prototype.addPathPoints = function(points){
		addPathPoints(points);
	}
	
	Map.prototype.getMapObject = function(){
		return map;
	}
	
}

//******************************************************
//---------------------- MultiPath --------------------------
//******************************************************
//SubPath
var SubPath = function() {
	this.points = new Array();
}

SubPath.prototype = {

	addPoint: function(point) {
		this.points.push(new OpenLayers.Geometry.Point(point.longitude, point.latitude).transform(new OpenLayers.Projection("EPSG:4326"), global_map.getMapObject().getProjectionObject()));
	}
}

//Paths
var Paths = function() {
	this.paths = new Array();
	this.paths.push(new SubPath());

	this.firstPoint = true;
	this.tmp_lat = 0.0;
	this.tmp_lon = 0.0;
	this.tmp_alt = 0.0;
}

Paths.prototype = {

	addPointToPath: function(point) {
		if(global_simulation.config.map.show_track){
			//Check if discontinuity
			if(this.tmp_lon>=0)
				var tmp_positive = true;
			else
				var tmp_positive = false;
			if(point.longitude>=0)
				var act_positive = true;
			else
				var act_positive = false;
			if(!this.firstPoint && (tmp_positive!=act_positive) && (Math.abs(point.longitude)>90.0) && (this.tmp_lon+point.longitude<90.0)){
				//end of previous path
				var tip = new Object();
				tip.altitude = this.tmp_alt;
				tip.latitude = (this.tmp_lat + point.latitude) / 2;
				if(this.tmp_lon>=0){
					tip.longitude = 179.99999;
				}else{
					tip.longitude = -179.99999;
				}
				this.paths[this.paths.length-1].addPoint(tip);
				//create new subPath
				this.paths.push(new SubPath());
				//start of new subPath
				tip.longitude = -tip.longitude;			
				this.paths[this.paths.length-1].addPoint(tip);
			}

			//Add point
			this.paths[this.paths.length-1].addPoint(point);

			//Store temporals
			this.tmp_lat = point.latitude;
			this.tmp_lon = point.longitude;
			this.tmp_alt = point.altitude;
			this.firstPoint=false;
		}
	},

	getFeature: function() {
		var lineStrings = new Array();
		for ( var i in this.paths ){
			var ol_points = this.paths[i].points;
			lineStrings.push(new OpenLayers.Geometry.LineString(ol_points));
		}
		return new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Collection(lineStrings), null, pathStyle);
	}
}

var myMultyPath = new Paths();

var pathStyle = { 
	strokeColor: '#ff0000', 
  	strokeOpacity: 0.5,
  	strokeWidth: 3
};