
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
