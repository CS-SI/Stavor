function initSun(){
	//-----------------------------------------------------------------------------------------------------------------------
	//			SUN
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_sun){
		if(show_sun_texture){
			// base image texture for mesh
			var lavaTexture = new THREE.ImageUtils.loadTexture( 'textures/lava/lava.jpg');
			lavaTexture.wrapS = lavaTexture.wrapT = THREE.RepeatWrapping; 
			// multiplier for distortion speed 		
			var baseSpeed = 0.02;
			// number of times to repeat texture in each direction
			var repeatS = repeatT = 2.0;
			// texture used to generate "randomness", distort all other textures
			var noiseTexture = new THREE.ImageUtils.loadTexture( 'textures/lava/cloud.png' );
			noiseTexture.wrapS = noiseTexture.wrapT = THREE.RepeatWrapping; 
			// magnitude of noise effect
			var noiseScale = 0.5;
			// texture to additively blend with base image texture
			var blendTexture = new THREE.ImageUtils.loadTexture( 'textures/lava/lava.jpg' );
			blendTexture.wrapS = blendTexture.wrapT = THREE.RepeatWrapping; 
			// multiplier for distortion speed 
			var blendSpeed = 0.08;
			// adjust lightness/darkness of blended texture
			var blendOffset = 0.45;
			// texture to determine normal displacement
			var bumpTexture = noiseTexture;
			bumpTexture.wrapS = bumpTexture.wrapT = THREE.RepeatWrapping; 
			// multiplier for distortion speed 		
			var bumpSpeed   = 0.5;
			// magnitude of normal displacement
			var bumpScale   = 2.0;
			
			// use "this." to create global object
			this.customUniforms = {
				baseTexture: 	{ type: "t", value: lavaTexture },
				baseSpeed:		{ type: "f", value: baseSpeed },
				repeatS:		{ type: "f", value: repeatS },
				repeatT:		{ type: "f", value: repeatT },
				noiseTexture:	{ type: "t", value: noiseTexture },
				noiseScale:		{ type: "f", value: noiseScale },
				blendTexture:	{ type: "t", value: blendTexture },
				blendSpeed: 	{ type: "f", value: blendSpeed },
				blendOffset: 	{ type: "f", value: blendOffset },
				bumpTexture:	{ type: "t", value: bumpTexture },
				bumpSpeed: 		{ type: "f", value: bumpSpeed },
				bumpScale: 		{ type: "f", value: bumpScale },
				alpha: 			{ type: "f", value: 1.0 },
				time: 			{ type: "f", value: 1.0 }
			};
			
			// create custom material from the shader code above
			//   that is within specially labeled script tags
			var customMaterialSun = new THREE.ShaderMaterial( 
			{
				uniforms: customUniforms,
				vertexShader:   THREE.ShaderSun.vertexShader,
				fragmentShader: THREE.ShaderSun.fragmentShader
			}   );
		}else{//Not using texture, solid color instead
			var customMaterialSun = new THREE.MeshPhongMaterial( { color: sun_solid_color, metal: true } );
		}
		
		var sunGeometry = new THREE.SphereGeometry( sun_radius, sun_seg, sun_seg );
		sun = new THREE.Mesh(	sunGeometry, customMaterialSun );
		sun.position.set(0, 85, 85);//Don't remove or the dashed material is not created
		scene.add( sun );
		
		if(!sun_simple_glow){
			// SHADER GLOW EFFECT
			var customMaterialGlow = new THREE.ShaderMaterial( 
			{
				uniforms: 
				{ 
					"c":   { type: "f", value: 0.1 },
					"p":   { type: "f", value: 3.4 },
					glowColor: { type: "c", value: new THREE.Color(0xffff00) },
					viewVector: { type: "v3", value: camera.position }
				},
				vertexShader:   THREE.ShaderGlow.vertexShader,
				fragmentShader: THREE.ShaderGlow.fragmentShader,
				side: THREE.FrontSide,
				blending: THREE.AdditiveBlending,
				transparent: true
			}   );
				
			sunGlow = new THREE.Mesh( sunGeometry.clone(), customMaterialGlow.clone() );
			
			sunGlow.position = sun.position;
			sunGlow.scale.multiplyScalar(1.8);
			scene.add( sunGlow );
		}else{
			// SUPER SIMPLE GLOW EFFECT
			// use sprite because it appears the same from all angles
			var spriteMaterial = new THREE.SpriteMaterial( 
			{ 
				map: new THREE.ImageUtils.loadTexture( 'textures/lava/glow.png' ), 
				useScreenCoordinates: false,// alignment: THREE.SpriteAlignment.center,
				color: 0xf79216, transparent: false, blending: THREE.AdditiveBlending
			});
			sunGlow = new THREE.Sprite( spriteMaterial );
			sunGlow.scale.set(20, 20, 1.0);
			sun.add(sunGlow); // this centers the glow at the mesh
		}
		
		if(sun_show_line){
			// SUN LINE
			var lineGeometrySun = new THREE.Geometry();
			lineGeometrySun.dynamic = true;
			var vertArraySun = lineGeometrySun.vertices;
			vertArraySun.push( new THREE.Vector3(sun.position.x,sun.position.y,sun.position.z), new THREE.Vector3(0, 0, 0) );
			lineGeometrySun.computeLineDistances();
			var lineMaterialSun = new THREE.LineDashedMaterial( { color: 0xffd800, dashSize: 2, gapSize: 2 } );
			lineSun = new THREE.Line( lineGeometrySun, lineMaterialSun );
			scene.add(lineSun);
		}
		
		if(sun_show_dist){
			// Sun Sprite
			spriteSun = makeTextSprite( 1, " 1.05 AU ", 
				{ fontsize: 24, borderColor: {r:255, g:255, b:255, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:1.5}, fontColor: {r:255, g:255, b:255, a:1.0} } );
			sun.add( spriteSun );
		}
	}
	
}
function updateSun() {
	//-----------------------------------------------------------------------------------------------------------------------
	//			SUN UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_sun){
		if(show_sun_texture){
			customUniforms.time.value += delta;
		}
		var sun_obj_pos = value_sun.clone().normalize().multiplyScalar(sun_obj_dist);
		sun.position = sun_obj_pos;
		// change the direction this spotlight is facing
		sunLight.position.set(sun.position.x,sun.position.y,sun.position.z);
		if(sun_show_line){
			// SUN LINE
			lineSun.geometry.vertices[0] = new THREE.Vector3(sun.position.x,sun.position.y,sun.position.z);
			lineSun.geometry.computeLineDistances();
			lineSun.geometry.verticesNeedUpdate = true;
			//lineSun.material.attributes.lineDistances.needsUpdate = true;
		}
		if(sun_show_dist){
			var sun_label_distance = value_sun.length()/149597871;//convert Km to AU
			var messageSun = " "+parseFloat(Math.round(sun_label_distance * 1000) / 1000).toFixed(3)+" AU ";
			contextSun.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
			contextSun.fill(); // FILL THE CONTEXT
			// get size data (height depends only on font size)
			var metrics = contextSun.measureText( messageSun );
			var textWidthSun = metrics.width;
			// background color
			contextSun.fillStyle   = "rgba(" + backgroundColorSun.r + "," + backgroundColorSun.g + ","
										  + backgroundColorSun.b + "," + backgroundColorSun.a + ")";
			// border color
			contextSun.strokeStyle = "rgba(" + borderColorSun.r + "," + borderColorSun.g + ","
										  + borderColorSun.b + "," + borderColorSun.a + ")";
			contextSun.lineWidth = borderThicknessSun;
			roundRect(contextSun, borderThicknessSun/2, borderThicknessSun/2, textWidthSun + borderThicknessSun, fontsizeSun * 1.4 + borderThicknessSun, 6);
			// 1.4 is extra height factor for text below baseline: g,j,p,q.
			// text color
			contextSun.fillStyle   = "rgba(" + fontColorSun.r + "," + fontColorSun.g + ","
										  + fontColorSun.b + "," + fontColorSun.a + ")";
			contextSun.fillText( messageSun, borderThicknessSun, fontsizeSun + borderThicknessSun);
			spriteSun.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
		}
		if(!sun_simple_glow){
			moonGlow.material.uniforms.viewVector.value = 
				new THREE.Vector3().subVectors( camera.position, moonGlow.position );
		}
		if(sun_rotates){
			//sun.rotation.x += 0.005;
			sun.rotation.y += 0.001*sun_rotation_speed;
		}
	}
}
