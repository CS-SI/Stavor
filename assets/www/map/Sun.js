function drawSun(){
	if(show_sun_icon){
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
}
var nightStyle = {
      strokeColor: "#FFcc00",
      strokeOpacity: 0.5,
      strokeWidth: 1,
      fillOpacity: 0.1,
	fillColor: "#000000"
};
var solarTerminator;
function drawSolarTerminator(){
	if(typeof solarTerminator != "undefined" && solarTerminator.length > 0){
		drawSun();
		if(show_sun_terminator){
			night_layer.removeAllFeatures();

			var solarPoints = [];
			var sign = 1;
			//draw polygon 
			for (var i in solarTerminator) {
				if(i==0){
					//Sign
					if(solarTerminator[i].longitude>0)
						sign = -1;
					//open polygon 1
					var point = new OpenLayers.Geometry.Point(
						-179.999*sign, 
						89.99
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					solarPoints.push(point);
					//open polygon 2
					var point = new OpenLayers.Geometry.Point(
						-179.999*sign, 
						solarTerminator[i].latitude
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					solarPoints.push(point);
				}

				var point = new OpenLayers.Geometry.Point(
					solarTerminator[i].longitude, 
					solarTerminator[i].latitude
				).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
				solarPoints.push(point);

				if(i==solarTerminator.length-1){
					//close polygon 1
					var point = new OpenLayers.Geometry.Point(
						179.999*sign, 
						solarTerminator[i].latitude
					).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
					solarPoints.push(point);
					//close polygon
					var point = new OpenLayers.Geometry.Point(
						179.999*sign, 
						89.99
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
