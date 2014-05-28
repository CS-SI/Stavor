
//element 0: others | 1: sun | 2: earth | 3: GUI view | 4: inclination
function makeTextSprite(element, message, parameters )
{
	if ( parameters === undefined ) parameters = {};
	
	var fontface = parameters.hasOwnProperty("fontface") ? 
		parameters["fontface"] : "Arial";
	
	var fontsize = parameters.hasOwnProperty("fontsize") ? 
		parameters["fontsize"] : 12;
	
	var borderThickness = parameters.hasOwnProperty("borderThickness") ? 
		parameters["borderThickness"] : 4;
	
	var borderColor = parameters.hasOwnProperty("borderColor") ?
		parameters["borderColor"] : { r:0, g:0, b:0, a:1.0 };
		
	var fontColor = parameters.hasOwnProperty("fontColor") ?
		parameters["fontColor"] : { r:255, g:255, b:255, a:1.0 };
	
	var backgroundColor = parameters.hasOwnProperty("backgroundColor") ?
		parameters["backgroundColor"] : { r:255, g:255, b:255, a:1.0 };

	//var spriteAlignment = THREE.SpriteAlignment.topLeft;
		
	var canvas = document.createElement('canvas');
	var context = canvas.getContext('2d');
	context.font = "Normal " + fontsize + "px " + fontface;
	
    
	// get size data (height depends only on font size)
	var metrics = context.measureText( message );
	var textWidth = metrics.width;
	
	// background color
	context.fillStyle   = "rgba(" + backgroundColor.r + "," + backgroundColor.g + ","
								  + backgroundColor.b + "," + backgroundColor.a + ")";
	// border color
	context.strokeStyle = "rgba(" + borderColor.r + "," + borderColor.g + ","
								  + borderColor.b + "," + borderColor.a + ")";

	context.lineWidth = borderThickness;
	roundRect(context, borderThickness/2, borderThickness/2, textWidth + borderThickness, fontsize * 1.4 + borderThickness, 6);
	// 1.4 is extra height factor for text below baseline: g,j,p,q.
	
	// text color
	context.fillStyle   = "rgba(" + fontColor.r + "," + fontColor.g + ","
								  + fontColor.b + "," + fontColor.a + ")";

	context.fillText( message, borderThickness, fontsize + borderThickness);
	
	
	
	// canvas contents will be used for a texture
	var texture = new THREE.Texture(canvas) 
	texture.needsUpdate = true;

	var spriteMaterial = new THREE.SpriteMaterial( 
		{ map: texture, useScreenCoordinates: false/*, alignment: spriteAlignment*/ } );
	var sprite = new THREE.Sprite( spriteMaterial );
	
	if(element==1){//Sun
		contextSun = context;
		fontsizeSun = fontsize;
		borderColorSun = borderColor;
		borderThicknessSun = borderThickness; 
		backgroundColorSun = backgroundColor;
		fontColorSun = fontColor;
		sprite.scale.set(40,20,1.0);
	}else if(element==2){//Earth
		contextEarth = context;
		fontsizeEarth = fontsize;
		borderColorEarth = borderColor;
		borderThicknessEarth = borderThickness; 
		backgroundColorEarth = backgroundColor;
		fontColorEarth = fontColor;
		sprite.scale.set(50,25,1.0);
	}else if(element==3){//GUI view
		spriteMaterial = new THREE.SpriteMaterial( 
			{ map: texture, useScreenCoordinates: true, alignment: THREE.SpriteAlignment.bottomRight } );
		sprite = new THREE.Sprite( spriteMaterial );
		sprite.scale.set(64,64,1.0);
	}else if(element==4){//Inclination
		contextInclination = context;
		fontsizeInclination = fontsize;
		borderColorInclination = borderColor;
		borderThicknessInclination = borderThickness; 
		backgroundColorInclination = backgroundColor;
		fontColorInclination = fontColor;
		sprite.scale.set(20,10,1.0);
	}else if(element==5){//Longitude
		contextLongitude = context;
		fontsizeLongitude = fontsize;
		borderColorLongitude = borderColor;
		borderThicknessLongitude = borderThickness; 
		backgroundColorLongitude = backgroundColor;
		fontColorLongitude = fontColor;
		sprite.scale.set(20,10,1.0);
	}else if(element==6){//Latitude
		contextLatitude = context;
		fontsizeLatitude = fontsize;
		borderColorLatitude = borderColor;
		borderThicknessLatitude = borderThickness; 
		backgroundColorLatitude = backgroundColor;
		fontColorLatitude = fontColor;
		sprite.scale.set(20,10,1.0);
	}else if(element==7){//Angles between vectors
		contextAngles = context;
		fontsizeAngles = fontsize;
		borderColorAngles = borderColor;
		borderThicknessAngles = borderThickness; 
		backgroundColorAngles = backgroundColor;
		fontColorAngles = fontColor;
		sprite.scale.set(20,10,1.0);
	}else{//Axis labels
		sprite.scale.set(20,10,1.0);
	}
	
	return sprite;	
}

// function for drawing rounded rectangles
function roundRect(ctx, x, y, w, h, r) 
{
    ctx.beginPath();
    ctx.moveTo(x+r, y);
    ctx.lineTo(x+w-r, y);
    ctx.quadraticCurveTo(x+w, y, x+w, y+r);
    ctx.lineTo(x+w, y+h-r);
    ctx.quadraticCurveTo(x+w, y+h, x+w-r, y+h);
    ctx.lineTo(x+r, y+h);
    ctx.quadraticCurveTo(x, y+h, x, y+h-r);
    ctx.lineTo(x, y+r);
    ctx.quadraticCurveTo(x, y, x+r, y);
    ctx.closePath();
    ctx.fill();
	ctx.stroke();   
}
function updateInclinationSprite(inclination){
	//Update Sprite
	var messageInclination = " i="+parseFloat((inclination * 180) / Math.PI).toFixed(1)+"º ";
	contextInclination.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
	contextInclination.fill(); // FILL THE CONTEXT
	// get size data (height depends only on font size)
	var metricsInclination = contextInclination.measureText( messageInclination );
	var textWidthInclination = metricsInclination.width;
	// background color
	contextInclination.fillStyle   = "rgba(" + backgroundColorInclination.r + "," + backgroundColorInclination.g + ","
								  + backgroundColorInclination.b + "," + backgroundColorInclination.a + ")";
	// border color
	contextInclination.strokeStyle = "rgba(" + borderColorInclination.r + "," + borderColorInclination.g + ","
								  + borderColorInclination.b + "," + borderColorInclination.a + ")";
	contextInclination.lineWidth = borderThicknessInclination;
	roundRect(contextInclination, borderThicknessInclination/2, borderThicknessInclination/2, textWidthInclination + borderThicknessInclination, fontsizeInclination * 1.4 + borderThicknessInclination, 6);
	// 1.4 is extra height factor for text below baseline: g,j,p,q.
	// text color
	contextInclination.fillStyle   = "rgba(" + fontColorInclination.r + "," + fontColorInclination.g + ","
								  + fontColorInclination.b + "," + fontColorInclination.a + ")";
	contextInclination.fillText( messageInclination, borderThicknessInclination, fontsizeInclination + borderThicknessInclination);
	spriteInclination.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
	spriteInclination.position = value_earth.clone().setZ(0).normalize().multiplyScalar(arc_sprite_radius);
}
function updateInclinationArc(inclination){
	//ReDraw Arc
	scene.remove(incl_arc);

	incl_arc = new THREE.Mesh( new THREE.TorusGeometry( arc_radius, arc_tube, arc_seg_r, arc_seg_t, inclination ), mat_arc );

	var incl_inst_rot = new THREE.Quaternion().setFromUnitVectors( axis_x, value_earth.clone().normalize() );

	incl_arc.quaternion.copy(incl_inst_rot.multiply(incl_offset));

	scene.add(incl_arc);
}
function updateLatitudeSprite(lat){
	//Update Sprite
	var messageLatitude = " "+parseFloat((lat * 180) / Math.PI).toFixed(1)+"º ";
	contextLatitude.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
	contextLatitude.fill(); // FILL THE CONTEXT
	// get size data (height depends only on font size)
	var metricsLatitude = contextLatitude.measureText( messageLatitude );
	var textWidthLatitude = metricsLatitude.width;
	// background color
	contextLatitude.fillStyle   = "rgba(" + backgroundColorLatitude.r + "," + backgroundColorLatitude.g + ","
								  + backgroundColorLatitude.b + "," + backgroundColorLatitude.a + ")";
	// border color
	contextLatitude.strokeStyle = "rgba(" + borderColorLatitude.r + "," + borderColorLatitude.g + ","
								  + borderColorLatitude.b + "," + borderColorLatitude.a + ")";
	contextLatitude.lineWidth = borderThicknessLatitude;
	roundRect(contextLatitude, borderThicknessLatitude/2, borderThicknessLatitude/2, textWidthLatitude + borderThicknessLatitude, fontsizeLatitude * 1.4 + borderThicknessLatitude, 6);
	// 1.4 is extra height factor for text below baseline: g,j,p,q.
	// text color
	contextLatitude.fillStyle   = "rgba(" + fontColorLatitude.r + "," + fontColorLatitude.g + ","
								  + fontColorLatitude.b + "," + fontColorLatitude.a + ")";
	contextLatitude.fillText( messageLatitude, borderThicknessLatitude, fontsizeLatitude + borderThicknessLatitude);
	lat_sprite.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
	lat_sprite.position = value_earth.clone().setZ(0).normalize().multiplyScalar(arc_sprite_radius);
}
function updateLongitudeSprite(lng){
	//Update Sprite
	var messageLongitude = " "+parseFloat((lng * 180) / Math.PI).toFixed(1)+"º ";
	contextLongitude.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
	contextLongitude.fill(); // FILL THE CONTEXT
	// get size data (height depends only on font size)
	var metricsLongitude = contextLongitude.measureText( messageLongitude );
	var textWidthLongitude = metricsLongitude.width;
	// background color
	contextLongitude.fillStyle   = "rgba(" + backgroundColorLongitude.r + "," + backgroundColorLongitude.g + ","
								  + backgroundColorLongitude.b + "," + backgroundColorLongitude.a + ")";
	// border color
	contextLongitude.strokeStyle = "rgba(" + borderColorLongitude.r + "," + borderColorLongitude.g + ","
								  + borderColorLongitude.b + "," + borderColorLongitude.a + ")";
	contextLongitude.lineWidth = borderThicknessLongitude;
	roundRect(contextLongitude, borderThicknessLongitude/2, borderThicknessLongitude/2, textWidthLongitude + borderThicknessLongitude, fontsizeLongitude * 1.4 + borderThicknessLongitude, 6);
	// 1.4 is extra height factor for text below baseline: g,j,p,q.
	// text color
	contextLongitude.fillStyle   = "rgba(" + fontColorLongitude.r + "," + fontColorLongitude.g + ","
								  + fontColorLongitude.b + "," + fontColorLongitude.a + ")";
	contextLongitude.fillText( messageLongitude, borderThicknessLongitude, fontsizeLongitude + borderThicknessLongitude);
	long_sprite.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
}
function updateAnglesSprite(angle){
	//Update Sprite
	var messageAngles = " "+parseFloat((angle * 180) / Math.PI).toFixed(1)+"º ";
	contextAngles.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
	contextAngles.fill(); // FILL THE CONTEXT
	// get size data (height depends only on font size)
	var metricsAngles = contextAngles.measureText( messageAngles );
	var textWidthAngles = metricsAngles.width;
	// background color
	contextAngles.fillStyle   = "rgba(" + backgroundColorAngles.r + "," + backgroundColorAngles.g + ","
								  + backgroundColorAngles.b + "," + backgroundColorAngles.a + ")";

	// border color
	contextAngles.strokeStyle = "rgba(" + borderColorAngles.r + "," + borderColorAngles.g + ","
								  + borderColorAngles.b + "," + borderColorAngles.a + ")";
	contextAngles.lineWidth = borderThicknessAngles;
	roundRect(contextAngles, borderThicknessAngles/2, borderThicknessAngles/2, textWidthAngles + borderThicknessAngles, fontsizeAngles * 1.4 + borderThicknessAngles, 6);
	// 1.4 is extra height factor for text below baseline: g,j,p,q.
	// text color
	contextAngles.fillStyle   = "rgba(" + fontColorAngles.r + "," + fontColorAngles.g + ","
								  + fontColorAngles.b + "," + fontColorAngles.a + ")";
	contextAngles.fillText( messageAngles, borderThicknessAngles, fontsizeAngles + borderThicknessAngles);
	vectors_sprite.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
}
