var Map = function () 
{	
	// Global pointers
	var global = global_simulation.config.global;
	var config = global_simulation.config.map;
	var results = global_simulation.results.map;
	var segments = global_3d_segments.map;
	
	var map;

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
				new OpenLayers.Control.TouchNavigation({
					dragPanOptions: {
						enableKinetic: true
					}
				})
			],
			layers: [
				new OpenLayers.Layer.OSM("OpenStreetMap", null, {
					transitionEffect: 'null',
			wrapDateLine: false
				})
			],
			center: new OpenLayers.LonLat(0, 0),
			zoom: 0
		});

		/*map.events.register("moveend", map, function(){
			 var lyr = map.getLayersByName('vectorLayer')[0];
			 map.removeLayer(lyr);
		});*/

		map.events.register("zoomend", map, function(){
			 storeNewZoom(map.getZoom());
		});

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
    	var lonLat = new OpenLayers.LonLat( 0.0 ,0.0 )
	  .transform(
	    new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
	    map.getProjectionObject() // to Spherical Mercator Projection
	  );
 
   	var zoom=1;

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

	getInitialization();

	//PRE-STORED PATH
	if(show_track){
		lineLayer.addFeatures([myMultyPath.getFeature()]);
	}

	//STATIONS NAME
	addSelectedStationsNames();

	setLoadingProgress(100);

//------------------------------------------------------------------------------

	function reDraw(){

	//Night line and Sun
		drawSolarTerminator();
		
	//Path
		if(show_track){
			lineLayer.removeAllFeatures();
			lineLayer.addFeatures([myMultyPath.getFeature()]);
		}

	//Spacecraft
		drawFov();

	//Follow Spacecraft
		if(follow_sc){
		    	var lonLat = new OpenLayers.LonLat( sc_longitude ,sc_latitude )
			  .transform(
			    new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
			    map.getProjectionObject() // to Spherical Mercator Projection
			  );
			map.setCenter(lonLat, map.getZoom());
		}

	//Stations	
		drawStationsAreas();	
	}
	
}