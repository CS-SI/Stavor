//SubPath
var SubPath = function() {
	this.points = new Array();
}

SubPath.prototype = {

	addPoint: function(point) {
		this.points.push(new OpenLayers.Geometry.Point(point.longitude, point.latitude).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject()));
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
		//Check if discontinuity
		if(this.tmp_lon>=0)
			var tmp_positive = true;
		else
			var tmp_positive = false;
		if(point.longitude>=0)
			var act_positive = true;
		else
			var act_positive = false;
		if(!this.firstPoint && (tmp_positive!=act_positive)){
			//TODO Add missing points to close path in both subpathes 179.99, it would be easier if we consider just jumps in the dateline. 	
			this.paths.push(new SubPath());
		}

		//Add point
		this.paths[this.paths.length-1].addPoint(point);

		//Store temporals
		this.tmp_lat = point.latitude;
		this.tmp_lon = point.longitude;
		this.tmp_alt = point.altitude;
		this.firstPoint=false;
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

var pathStyle = { 
	strokeColor: '#ff0000', 
  	strokeOpacity: 0.5,
  	strokeWidth: 3
};

var myMultyPath = new Paths();
