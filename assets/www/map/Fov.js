function drawFov(){

	/*var radius = sc_altitude*Math.tan((payload_beamwidth*Math.PI/180)/2);
	var sc_position = new OpenLayers.Geometry.Point(sc_longitude, sc_latitude).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
	var circle = OpenLayers.Geometry.Polygon.createRegularPolygon(
	    sc_position,
	    radius,
	    30 // According to the API you only need 20 sides to approximate a circle.
	);
	sc_layer.removeAllFeatures();
	var sc_feature = new OpenLayers.Feature.Vector(circle, null, sc_style);
	sc_layer.addFeatures([sc_feature]);*/


	sc_layer.removeAllFeatures();
	var fovPointsA = [];
	var fovPointsB = [];
	var fov_tmp_long = 0, fov_tmp_lat = 0;
	var first = true;
	for (var i in fov) {
		var point = new OpenLayers.Geometry.Point(fov[i].longitude, fov[i].latitude);
		// transform from WGS 1984 to Spherical Mercator
		point.transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());


		if((i!=0) && ((fov[i].longitude*fov_tmp_long)<0) && (Math.abs(fov[i].longitude)+Math.abs(fov_tmp_long)>180.0)){
			var avg_lat = (fov[i].latitude+fov_tmp_lat)/2;
			if(fov[i].longitude > 0)
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
		fov_tmp_lat = fov[i].latitude;
		fov_tmp_long = fov[i].longitude;

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

