function init() 
{
	//***********************************************************************************************************************
	//		SCENE ELEMENTS
	//***********************************************************************************************************************
	setLoadingProgress(1);	
	//setLoadingProgress(15);
	initScene();
	//***********************************************************************************************************************
	//		STATIC ELEMENTS
	//***********************************************************************************************************************
	//setLoadingProgress(35);
	setLoadingProgress(5);

	//-----------------------------------------------------------------------------------------------------------------------
	//			SKY
	//-----------------------------------------------------------------------------------------------------------------------
	
	if(show_sky){
		// create the geometry sphere
		var sky_geometry  = new THREE.SphereGeometry(1000, 32, 32);
		// create the material, using a texture of startfield
		var sky_material  = new THREE.MeshBasicMaterial();
		sky_material.map   = THREE.ImageUtils.loadTexture('modules/attitude/textures/sky/stars.jpg');
		sky_material.map.wrapS = sky_material.map.wrapT = THREE.RepeatWrapping;
		sky_material.map.repeat.set( 8, 8 ); 
		sky_material.side  = THREE.BackSide;
		// create the mesh based on geometry and material
		var sky_mesh  = new THREE.Mesh(sky_geometry, sky_material);
		sky_mesh.name = "SKY";
		scene.add(sky_mesh);
	}

	initReference();
	//***********************************************************************************************************************
	//		DYNAMIC ELEMENTS
	//***********************************************************************************************************************
	initAngles();
	//setLoadingProgress(65);
	setLoadingProgress(10);
	initIndicators();
	//setLoadingProgress(75);
	setLoadingProgress(13);
	initSun();
	//setLoadingProgress(85);
	setLoadingProgress(15);
	initEarth();
	changeView(selected_view);
	//setLoadingProgress(100);
	setLoadingProgress(18);
}
