// ########################################
// some constants
// ########################################

//var earth_radius = 3958.75;           // in miles
var earth_radius = 6371.11;           // in km

// ########################################
// calculate route
// ########################################

var routeNo = 0;

function GreatCircleLine(pOrigin, pDest)
{
    var gc = new geo.GreatCircle (pOrigin, pDest);
    var x0 = pOrigin.x;
    var x1 = pDest.x;

    var ls = [];
    if (orthodromeFlag==true)
    	ls[ls.length] = gc.toLineString(-180, 180);

  	if (x0 < x1 && (x1-x0)< 180) //modifiziert
  		ls[ls.length] = gc.toLineString(x0, x1);
  	else
  	{   //### part modifiziert #################
  		if (Math.abs(x0-x1) < 180)
  			ls[ls.length] = gc.toLineString(x1, x0);
  		else if(x0>x1){
  			ls[ls.length] = gc.toLineString(x0, 180);
  			ls[ls.length] = gc.toLineString(-180, x1);
        	}
  		else{
  			ls[ls.length] = gc.toLineString(x1, 180);
  			ls[ls.length] = gc.toLineString(-180, x0);
        	}
        //######################################
	}
    return(ls);
}

function show_nightline(pt1, pt2)
{

    var pOrigin = new geo.Point(pt1.x, pt1.y);
    var pDest = new geo.Point(pt2.x, pt2.y);

    var ls = GreatCircleLine(pOrigin, pDest);   

    var theStyle = [];
    if (orthodromeFlag==true)
        theStyle[theStyle.length] = null;

    theStyle[theStyle.length] = {strokeColor : night_layer.styleMap.styles["default"].defaultStyle.strokeColor, strokeWidth:6};
    theStyle[theStyle.length] = {strokeColor : night_layer.styleMap.styles["default"].defaultStyle.strokeColor, strokeWidth:6};

    // Create a feature from the waypoints LineString and display on map
    var route = [];
    for (var i=0; i<ls.length; i++)
     	route[i] = new OpenLayers.Feature.Vector (ls[i], null, theStyle[i]);
    night_layer.addFeatures(route);
}
