function addSelectedStationsNames(){
	for (var arrayIndex in stations){
		var point = new OpenLayers.Geometry.Point(stations[arrayIndex].longitude, stations[arrayIndex].latitude).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
		var station_name_feature = new OpenLayers.Feature.Vector(point, null, null);
		station_name_feature.attributes = {
			label:stations[arrayIndex].name
		}
		stations_name_layer.addFeatures([station_name_feature]);
	}
}

function drawStationsAreas(){
	if(Math.abs(sc_altitude-sc_altitude_tmp)>sc_altitude_step){
		stations_area_layer.removeAllFeatures();
		for (var arrayIndex in station_areas){
			if(station_areas[arrayIndex].type==0)
				paintClosedArea(arrayIndex);
			else
				paintOpenArea(arrayIndex,station_areas[arrayIndex].type);

		}
	
		sc_altitude_tmp = sc_altitude;
	}
}

function paintOpenArea(arrayIndex,type){
	var areaFirst = [];
	var area_tmp_long = 0, area_tmp_lat = 0;
	for (var i in station_areas[arrayIndex].points) {
		var coord = station_areas[arrayIndex].points[i];
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
	for (var i in station_areas[arrayIndex].points) {
		var coord = station_areas[arrayIndex].points[i];
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

