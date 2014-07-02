function initEarth(){
	//-----------------------------------------------------------------------------------------------------------------------
	//			EARTH
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_earth){
		var earth_geometry = new THREE.SphereGeometry( earth_radius, earth_seg, earth_seg ) ;
		if(show_earth_texture){	 
			if(typeof textureEarth === 'undefined'){
			   // your code here.
				textureEarth= new THREE.ImageUtils.loadTexture( 'textures/earth/Land_ocean_ice_cloud_2048.jpg' );
			};
			var earth_material = new THREE.MeshBasicMaterial( { map: textureEarth, overdraw: true } )
		}else{
			if(!canvas_mode)
				var earth_material = new THREE.MeshPhongMaterial( { color: earth_solid_color, metal: true } );
			else
				var earth_material = new THREE.MeshBasicMaterial( { color: earth_solid_color } );
		}
		earth = new THREE.Mesh( earth_geometry, earth_material ) ;
		earth.position.set(75, 0, 75);//Don't remove or the dashed material is not created
		scene.add( earth );
		
		if(earth_show_line){
			// EARTH LINE
			var lineGeometryEarth = new THREE.Geometry();
			var vertArrayEarth = lineGeometryEarth.vertices;
			vertArrayEarth.push( new THREE.Vector3(earth.position.x,earth.position.y,earth.position.z), new THREE.Vector3(0, 0, 0) );
			lineGeometryEarth.computeLineDistances();
			var lineMaterialEarth = new THREE.LineDashedMaterial( { color: 0x0099ff, dashSize: 2, gapSize: 2 } );
			lineEarth = new THREE.Line( lineGeometryEarth, lineMaterialEarth );
			scene.add(lineEarth);
		}
		if(earth_show_dist){
			// Earth Sprite
			spriteEarth = makeTextSprite( 2, " 36150 Km ", 
				{ fontsize: 20, borderColor: {r:0, g:0, b:0, a:1.0}, borderThickness: 1, backgroundColor: {r:0, g:0, b:0, a:0.5}, fontColor: {r:95, g:247, b:252, a:1.0} } );
			earth.add( spriteEarth );
		}
	}
}
function updateEarth(){
	//-----------------------------------------------------------------------------------------------------------------------
	//			EARTH UPDATE
	//-----------------------------------------------------------------------------------------------------------------------
	if(show_earth){
		var earth_obj_pos = value_earth.clone().normalize().multiplyScalar(earth_obj_dist);
		earth.position = earth_obj_pos;
		//XGGDEBUG: maybe it does not need to update the line after updating the object position since it is link to its coordinates.
		if(earth_show_line){
			// EARTH LINE
			lineEarth.geometry.vertices[0].set(earth.position.x,earth.position.y,earth.position.z);
			lineEarth.geometry.computeLineDistances();
			lineEarth.geometry.verticesNeedUpdate = true;
			//lineEarth.material.attributes.lineDistances.needsUpdate = true;
		}
		if(earth_show_dist){
			var earth_label_distance = value_earth.length();//Km
			var messageEarth = " "+parseFloat(Math.round(earth_label_distance * 1) / 1).toFixed(0)+" Km ";
			contextEarth.fillStyle = "rgba(0, 0, 0, 1.0)"; // CLEAR WITH COLOR BLACK (new BG color)
			contextEarth.fill(); // FILL THE CONTEXT
			// get size data (height depends only on font size)
			var metricsEarth = contextEarth.measureText( messageEarth );
			var textWidthEarth = metricsEarth.width;
			// background color
			contextEarth.fillStyle   = "rgba(" + backgroundColorEarth.r + "," + backgroundColorEarth.g + ","
										  + backgroundColorEarth.b + "," + backgroundColorEarth.a + ")";
			// border color
			contextEarth.strokeStyle = "rgba(" + borderColorEarth.r + "," + borderColorEarth.g + ","
										  + borderColorEarth.b + "," + borderColorEarth.a + ")";
			contextEarth.lineWidth = borderThicknessEarth;
			roundRect(contextEarth, borderThicknessEarth/2, borderThicknessEarth/2, textWidthEarth + borderThicknessEarth, fontsizeEarth * 1.4 + borderThicknessEarth, 6);
			// 1.4 is extra height factor for text below baseline: g,j,p,q.
			// text color
			contextEarth.fillStyle   = "rgba(" + fontColorEarth.r + "," + fontColorEarth.g + ","
										  + fontColorEarth.b + "," + fontColorEarth.a + ")";
			contextEarth.fillText( messageEarth, borderThicknessEarth, fontsizeEarth + borderThicknessEarth);
			spriteEarth.material.map._needsUpdate = true; // AND UPDATE THE IMAGE..
		}
		if(earth_rotates){
			earth.rotation.y += 0.001*earth_rotation_speed;
		}
	}

}
