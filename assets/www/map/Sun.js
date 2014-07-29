function drawSun(){
	sun_layer.removeAllFeatures();    
	var marker_sun = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(sun_lon, sun_lat).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()), null, {
		externalGraphic: "sun.png",
		graphicWidth: 32,
		graphicHeight: 32,
		fillOpacity: 1
	});
	sun_layer.addFeatures([marker_sun]);
	sun_lon_tmp = sun_lon;
}

var nightStyle = { 
	strokeColor: '#FFcc00', 
  	strokeOpacity: 0.5,
  	strokeWidth: 2
};
var solarTerminator;
function drawSolarTerminator(){
	night_layer.removeAllFeatures();

	var solarPoints = [];
	for (var i in solarTerminator) {
		var point = new OpenLayers.Geometry.Point(
			solarTerminator[i].longitude, 
			solarTerminator[i].latitude
		).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
		
		solarPoints.push(point);
		
	}				

	if(solarPoints.length>0){
		solarPoints.push(solarPoints[0]);
		var linearRing = new OpenLayers.Geometry.LinearRing(solarPoints);
		var geometry = new OpenLayers.Geometry.Polygon([linearRing]);
		var polygonFeature = new OpenLayers.Feature.Vector(geometry, null, nightStyle);
		night_layer.addFeatures([polygonFeature]);
	}


	//if(sun_lon!=0.0){
	/*night_layer.removeAllFeatures();

	var start = { x: sun_lon, y: addLats(sun_lat,90.0) };
	var end = { x: sun_lon+90.0, y: sun_lat };
	var generator = new arc.GreatCircle(start, end, {'name': 'NightLine'});
	var line = generator.Arc(25,{offset:10});
	night_layer.addFeatures(new OpenLayers.Feature.Vector(line.openlayers(),null,nightStyle));

	var start = { x: sun_lon-90.0, y: sun_lat };
	var end = { x: sun_lon, y: addLats(sun_lat,90.0) };
	var generator = new arc.GreatCircle(start, end, {'name': 'NightLine2'});
	var line = generator.Arc(25,{offset:10});
	night_layer.addFeatures(new OpenLayers.Feature.Vector(line.openlayers(),null,nightStyle));

	var start = { x: sun_lon-180.0, y: addLats(sun_lat,-90.0) };
	var end = { x: sun_lon-90.0, y: sun_lat };
	var generator = new arc.GreatCircle(start, end, {'name': 'NightLine3'});
	var line = generator.Arc(25,{offset:10});
	night_layer.addFeatures(new OpenLayers.Feature.Vector(line.openlayers(),null,nightStyle));

	var start = { x: sun_lon+90.0, y: sun_lat };
	var end = { x: sun_lon+180.0, y: addLats(sun_lat,-90.0) };
	var generator = new arc.GreatCircle(start, end, {'name': 'NightLine4'});
	var line = generator.Arc(25,{offset:10});
	night_layer.addFeatures(new OpenLayers.Feature.Vector(line.openlayers(),null,nightStyle));*/
	//}
}
