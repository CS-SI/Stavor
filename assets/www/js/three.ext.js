//XGGDEBUG: MyFunctions ******************************************************************************************************

/**
 * @author Xavier Gibert-González
 *
 * Creates a crossing arrow for visualizing momentum
 *
 * Parameters:
 *  dir - Vector3
 *  origin - Vector3
 *  length - Number
 *  hex - color in hex value
 *  headLength - Number
 *  headWidth - Number
 */

THREE.MomentumHelper = function ( dir, origin, length, hex, headLength, headWidth, segments ) {

	// dir is assumed to be normalized

	THREE.Object3D.call( this );

	if ( hex === undefined ) hex = 0xffff00;
	if ( length === undefined ) length = 1;
	if ( headLength === undefined ) headLength = 0.2 * length;
	if ( headWidth === undefined ) headWidth = 0.2 * headLength;

	this.position = origin;

	var lineGeometry = new THREE.Geometry();
	lineGeometry.vertices.push( new THREE.Vector3( 0, -0.8, 0 ) );
	lineGeometry.vertices.push( new THREE.Vector3( 0, 0.90, 0 ) );
	//lineGeometry.dynamic = true;

	this.line = new THREE.Line( lineGeometry, new THREE.LineBasicMaterial( { color: hex, linewidth: 7 } ) );
	this.line.matrixAutoUpdate = false;
	this.add( this.line );
	
	/*var materials = [

					new THREE.MeshLambertMaterial( { color: hex, shading: THREE.SmoothShading, blending: THREE.AdditiveBlending, vertexColors: THREE.VertexColors } ),
					new THREE.MeshBasicMaterial( { color: 0x000000, shading: THREE.SmoothShading, wireframe: true, transparent: true } )
	
	];*/

	var coneGeometry = new THREE.CylinderGeometry( 0, 0.5, 1, segments, 1 );
	coneGeometry.applyMatrix( new THREE.Matrix4().makeTranslation( 0, - 0.5, 0 ) );
	//coneGeometry.dynamic = true;
	
	if(!canvas_mode)
		this.cone = new THREE.Mesh( coneGeometry, new THREE.MeshLambertMaterial( { color: hex, shading: THREE.SmoothShading, blending: THREE.AdditiveBlending, vertexColors: THREE.VertexColors } ) );
	else
		this.cone = new THREE.Mesh( coneGeometry, new THREE.MeshLambertMaterial( { color: hex } ) );
	//this.cone = THREE.SceneUtils.createMultiMaterialObject( coneGeometry, materials );
	this.cone.matrixAutoUpdate = false;
	this.add( this.cone );

	this.setDirection( dir );
	this.setLength( length, headLength, headWidth );

};

THREE.MomentumHelper.prototype = Object.create( THREE.Object3D.prototype );

THREE.MomentumHelper.prototype.setDirection = function () {

	var axis = new THREE.Vector3();
	var radians;

	return function ( dir ) {

		// dir is assumed to be normalized

		if ( dir.y > 0.99999 ) {

			this.quaternion.set( 0, 0, 0, 1 );

		} else if ( dir.y < - 0.99999 ) {

			this.quaternion.set( 1, 0, 0, 0 );

		} else {

			axis.set( dir.z, 0, - dir.x ).normalize();

			radians = Math.acos( dir.y );

			this.quaternion.setFromAxisAngle( axis, radians );

		}

	};

}();

THREE.MomentumHelper.prototype.setLength = function ( length, headLength, headWidth ) {

	if ( headLength === undefined ) headLength = 0.2 * length;
	if ( headWidth === undefined ) headWidth = 0.2 * headLength;

	this.line.scale.set( 1, length, 1 );
	this.line.updateMatrix();

	this.cone.scale.set( headWidth, headLength, headWidth );
	this.cone.position.y = length;
	this.cone.updateMatrix();

};

THREE.MomentumHelper.prototype.setColor = function ( hex ) {

	this.line.material.color.setHex( hex );
	this.cone.material.color.setHex( hex );

};

//************************************************************************************************

/**
 * @author Xavier Gibert-González
 *
 * Creates a target in the sphere
 *
 * Parameters:
 *  dir - Vector3
 *  origin - Vector3
 *  length - Number
 *  hex - color in hex value
 *  headLength - Number
 *  headWidth - Number
 */

THREE.TargetHelper = function ( dir, origin, length, hex, headLength, headWidth, segments ) {

	// dir is assumed to be normalized

	THREE.Object3D.call( this );

	if ( hex === undefined ) hex = 0xffff00;
	if ( length === undefined ) length = 1;
	if ( headLength === undefined ) headLength = 0.2 * length;
	if ( headWidth === undefined ) headWidth = 0.2 * headLength;

	this.position = origin;

	var lineGeometry = new THREE.Geometry();
	lineGeometry.vertices.push( new THREE.Vector3( 0, 0, 0 ) );
	lineGeometry.vertices.push( new THREE.Vector3( 0, 1, 0 ) );
	lineGeometry.dynamic = true;//xgg
	lineGeometry.computeLineDistances();//xgg
	
	this.line = new THREE.Line( lineGeometry, new THREE.LineDashedMaterial( { color: hex, linewidth: 2, dashSize: 2, gapSize: 2} ) );
	this.line.matrixAutoUpdate = false;
	this.add( this.line );

	var coneGeometry = new THREE.CylinderGeometry( headLength, headLength, headWidth, segments, 1 );
	coneGeometry.applyMatrix( new THREE.Matrix4().makeTranslation( 0, - 0.5, 0 ) );

	if(!canvas_mode)
		this.cone = new THREE.Mesh( coneGeometry, new THREE.MeshLambertMaterial( { color: hex, shading: THREE.SmoothShading, blending: THREE.AdditiveBlending, vertexColors: THREE.VertexColors } ) );
	else
		this.cone = new THREE.Mesh( coneGeometry, new THREE.MeshLambertMaterial( { color: hex } ) );
	this.cone.matrixAutoUpdate = false;
	this.add( this.cone );

	this.setDirection( dir );
	this.setLength( length, headLength, headWidth );

};

THREE.TargetHelper.prototype = Object.create( THREE.Object3D.prototype );

THREE.TargetHelper.prototype.setDirection = function () {

	var axis = new THREE.Vector3();
	var radians;

	return function ( dir ) {

		// dir is assumed to be normalized

		if ( dir.y > 0.99999 ) {

			this.quaternion.set( 0, 0, 0, 1 );

		} else if ( dir.y < - 0.99999 ) {

			this.quaternion.set( 1, 0, 0, 0 );

		} else {

			axis.set( dir.z, 0, - dir.x ).normalize();

			radians = Math.acos( dir.y );

			this.quaternion.setFromAxisAngle( axis, radians );

		}

	};

}();

THREE.TargetHelper.prototype.setLength = function ( length, headLength, headWidth ) {

	if ( headLength === undefined ) headLength = 0.2 * length;
	if ( headWidth === undefined ) headWidth = 0.2 * headLength;

	this.line.scale.set( 1, length, 1 );
	this.line.updateMatrix();

	this.cone.scale.set( headLength, headWidth, headLength );
	this.cone.position.y = length;
	this.cone.updateMatrix();

};

THREE.TargetHelper.prototype.setColor = function ( hex ) {

	//this.line.material.color.setHex( hex );
	this.cone.material.color.setHex( hex );

};

//XGGDEBUG: END -------------------------------------------------------------------------------------------------------------


