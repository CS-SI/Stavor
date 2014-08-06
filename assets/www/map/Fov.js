function drawFov(){
	if(show_fov){
		sc_layer.removeAllFeatures();

		//NORMAL CASE
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
		}

	}
}

