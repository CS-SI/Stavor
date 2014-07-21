/* Great Circle functions */

/*
   modified by Arnd Wippermann
   Line  76 and 92  : return of distance in rad
   Line 188         : add transformation
*/

var geo = {};

geo.EARTH_RADIUS = 3958.75;     // in miles
geo.DEG2RAD = Math.PI / 180.0;  // factor to convert degrees to radians
geo.RAD2DEG = 180.0 / Math.PI;  // factor to convert radians to degrees

// These Points must be geographic coordinates
// Domain is -180.0 <= x <= 180 AND -90.0 <= y <= 90.0
geo.Point = function (x, y) {
	this.x = parseFloat(x);
	this.y = parseFloat(y);
}

// Inherit OpneLayers Point methods and properties
geo.Point.prototype = new OpenLayers.Geometry.Point( 0,0 );


/*
 *  Calculates a bearing from the object's point to the passed point
 *  Return value is in decimal degrees in the range 0 <= bearing < 360
 */
geo.Point.prototype.geoBearingTo = function (pnt) {
var x = [];
var y = [];
var bearing;
var adjust;

	if( this.isValidGeoPoint() && pnt.isValidGeoPoint()) {
		x[0] = this.x * geo.DEG2RAD;
		y[0] = this.y * geo.DEG2RAD;
		x[1] = pnt.x * geo.DEG2RAD;
		y[1] = pnt.y * geo.DEG2RAD;

	 	var a = Math.cos(y[1]) * Math.sin(x[1] - x[0]);
		var b = Math.cos(y[0]) * Math.sin(y[1]) - Math.sin(y[0])
			* Math.cos(y[1]) * Math.cos(x[1] - x[0]);

		if((a == 0) && (b == 0)) {
			bearing = 0;
			return bearing;
		}

		if( b == 0) {
			if( a < 0)
				bearing = 270;
			else
				bearing = 90;
			return bearing;
		}

		if( b < 0)
			adjust = Math.PI;
		else {
			if( a < 0)
				adjust = 2 * Math.PI;
			else
				adjust = 0;
		}
		bearing = (Math.atan(a/b) + adjust) * geo.RAD2DEG;
		return bearing;
	}
	else
		return null;
}

/*
 *  Calculates the distance from the object point to the passed point
 *  Return value is rad //in miles
 */
geo.Point.prototype.geoDistanceTo = function( pnt ) {
var x = [];
var y = [];

	if( this.isValidGeoPoint() && pnt.isValidGeoPoint()) {
		x[0] = this.x * geo.DEG2RAD;
		y[0] = this.y * geo.DEG2RAD;
		x[1] = pnt.x * geo.DEG2RAD;
		y[1] = pnt.y * geo.DEG2RAD;

		var a = Math.pow( Math.sin(( y[1]-y[0] ) / 2.0 ), 2);
		var b = Math.pow( Math.sin(( x[1]-x[0] ) / 2.0 ), 2);
		var c = Math.pow(( a + Math.cos( y[1] ) * Math.cos( y[0] ) * b ), 0.5);

		return ( 2 * Math.asin( c ));// * geo.EARTH_RADIUS );
	} else
		return null;
}


/*
 *  Determines the location of a point that is 'distance' miles from
 *  object point in a direction of 'bearing'.
 */
geo.Point.prototype.geoWaypoint = function( distance, bearing ) {
var wp = new geo.Point( 0, 0 );

	// Math.* trig functions require angles to be in radians
	var x = this.x * geo.DEG2RAD;
	var y = this.y * geo.DEG2RAD;
	var radBearing = bearing * geo.DEG2RAD;

	// Convert arc distance to radians
	var c = distance / geo.EARTH_RADIUS;

	wp.y = Math.asin( Math.sin(y) * Math.cos(c) + Math.cos(y)
	    * Math.sin(c) * Math.cos(radBearing)) * geo.RAD2DEG;

	var a = Math.sin(c) * Math.sin(radBearing);
	var b = Math.cos(y) * Math.cos(c) - Math.sin(y) * Math.sin(c) * Math.cos(radBearing)

	if( b == 0 )
		wp.x = this.x;
	else
		wp.x = this.x + Math.atan(a/b) * geo.RAD2DEG;

	return wp;
}

/*
 *  GreatCircle Constructor
 */
geo.GreatCircle = function (p0, p1) {
var p = p0.clone();
    this.p0 = p;
    p = p1.clone();
    this.p1 = p;
}


/*
 *		Create a great circle with point and bearing
 */
geo.pointAndBearingGreatCircle = function (pnt, bearing) {

	var p1 = pnt.geoWaypoint (100, bearing);
	var gc = new geo.GreatCircle (pnt, p1);
	return (gc);
}

/*
 *	Determine the intersection point between two great circles
 *	Returns a geo.Point
 */
/*
geo.GreatCircle.prototype.intersectGreatCircle = function (gc2) {
	var brg13 = this.p0.geoBearingTo (this.p1) * geo.DEG2RAD;
	var brg23 = gc2.p0.geoBearingTo (gc2.p1) * geo.DEG2RAD;
	var dst12 = this.p0.geoDistanceTo (gc2.p0) / geo.EARTH_RADIUS;
	var brg12 = this.p0.geoBearingTo (gc2.p0);
	var brg21 = gc2.p0.geoBearingTo (this.p0);

}
*/

geo.GreatCircle.prototype.toLineString = function (startLon, endLon) {

	// Convert incoming coordinates to radians
	var lat1 = this.p0.y * geo.DEG2RAD;
	var lon1 = this.p0.x * geo.DEG2RAD;
	var lat2 = this.p1.y * geo.DEG2RAD;
	var lon2 = this.p1.x * geo.DEG2RAD;

   var waypoints = [];
   var k = 0;                            // waypoints index
	var j = Math.abs(endLon-startLon);    // number of increments on g.c. route
	var arc = Math.PI / 180.0;            // Size of increments in radians

	lon = Math.min (lon1, lon2);
	if (Math.abs(lon1-lon2) <= geo.TOL) {
		lon2 = lon + Math.PI; // Avoid 'divide by zero' error in following eq.
	}

	// If longitudes and latitudes are each 180 degrees apart then
	// tweak one lat by a millionth of a degree to avoid ambiguity in cross-polar route
	if (Math.abs(lon2-lon1) == Math.PI) {
		if (lat1+lat2 == 0.0)  {
			lat2 += Math.PI/180000000 ;
		}
	}

	var lon = startLon * geo.DEG2RAD;
	endLon *= geo.DEG2RAD;
	while( lon <= endLon) {
		lat = Math.atan(( Math.sin(lat1) * Math.cos(lat2) * Math.sin(lon-lon2)
			- Math.sin(lat2) * Math.cos(lat1) * Math.sin(lon-lon1))
			/ (Math.cos(lat1) * Math.cos(lat2) * Math.sin(lon1-lon2)))

		waypoints[k++] = new OpenLayers.Geometry.Point ((lon * geo.RAD2DEG)* OpenLayers.INCHES_PER_UNIT['degrees']/OpenLayers.INCHES_PER_UNIT['m'], (lat * geo.RAD2DEG) * OpenLayers.INCHES_PER_UNIT['degrees']/OpenLayers.INCHES_PER_UNIT['m']).transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection(map.projection));
		if (lon < endLon && (lon+arc) >= endLon)
			lon = endLon;
		else
    		lon = lon + arc;
	}
   var ls = new OpenLayers.Geometry.LineString( waypoints );
   return ls;
}


geo.Point.prototype.isValidGeoPoint = function () {
	if((this.x != null) && (this.y != null) && (this.x != NaN) && (this.y != NaN)) {
    	if ((this.x >= -180) && (this.x <= 180) && (this.y >= -90) && (this.y <= 90))
	    	return( true );
    	else
	    	return( false );
	}
    else
        return( false );
}

