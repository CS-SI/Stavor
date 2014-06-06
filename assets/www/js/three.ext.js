
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

	this.line = new THREE.Line( lineGeometry, new THREE.LineBasicMaterial( { color: hex, linewidth: 6 } ) );
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

	//var coneGeometry = new THREE.CylinderGeometry( headLength, headLength, headWidth, segments, 1 );
	//Cross shape for target
	// points that define shape
	var pts = [];
	var cross_scale = 0.3;
	pts.push( new THREE.Vector2 (0, 2*cross_scale));
	pts.push( new THREE.Vector2 (-5*cross_scale, 7*cross_scale));
	pts.push( new THREE.Vector2 (-7*cross_scale, 5*cross_scale));
	pts.push( new THREE.Vector2 (-2*cross_scale, 0));
	pts.push( new THREE.Vector2 (-7*cross_scale, -5*cross_scale));
	pts.push( new THREE.Vector2 (-5*cross_scale, -7*cross_scale));
	pts.push( new THREE.Vector2 (0, -2*cross_scale));
	pts.push( new THREE.Vector2 (5*cross_scale, -7*cross_scale));
	pts.push( new THREE.Vector2 (7*cross_scale, -5*cross_scale));
	pts.push( new THREE.Vector2 (2*cross_scale, 0));
	pts.push( new THREE.Vector2 (7*cross_scale, 5*cross_scale));
	pts.push( new THREE.Vector2 (5*cross_scale, 7*cross_scale));

	// shape to extrude
	var shape = new THREE.Shape( pts );

	// extrude options
	var options = { 
		amount: headWidth,              // default 100, only used when path is null
		bevelEnabled: false, 
		bevelSegments: 2, 
		steps: 1,                // default 1, try 3 if path defined
		extrudePath: null        // or path
	};

	// geometry
	var coneGeometry = new THREE.ExtrudeGeometry( shape, options );

	coneGeometry.applyMatrix( new THREE.Matrix4().makeTranslation( 0, - 0.5, 0 ).makeRotationX( Math.PI/2 ) );
	//End of cross shape definition
	

	//coneGeometry.applyMatrix( new THREE.Matrix4().makeTranslation( 0, - 0.5, 0 ) );

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

/**
 * @author WestLangley / http://github.com/WestLangley
 * @author zz85 / http://github.com/zz85
 * @author bhouston / http://exocortex.com
 *
 * Creates an arrow for visualizing directions
 *
 * Parameters:
 *  dir - Vector3
 *  origin - Vector3
 *  length - Number
 *  color - color in hex value
 *  headLength - Number
 *  headWidth - Number
 */

THREE.VectorHelper = function ( dir, origin, length, color, headLength, headWidth, segments ) {

	// dir is assumed to be normalized

	THREE.Object3D.call( this );

	if ( color === undefined ) color = 0xffff00;
	if ( length === undefined ) length = 1;
	if ( headLength === undefined ) headLength = 0.2 * length;
	if ( headWidth === undefined ) headWidth = 0.2 * headLength;

	this.position = origin;

	var lineGeometry = new THREE.Geometry();
	lineGeometry.vertices.push( new THREE.Vector3( 0, 0, 0 ) );
	lineGeometry.vertices.push( new THREE.Vector3( 0, 1, 0 ) );

	this.line = new THREE.Line( lineGeometry, new THREE.LineBasicMaterial( { color: color, linewidth: 2 } ) );
	this.line.matrixAutoUpdate = false;
	this.add( this.line );

	var coneGeometry = new THREE.CylinderGeometry( 0, 0.5, 1, segments, 1 );
	coneGeometry.applyMatrix( new THREE.Matrix4().makeTranslation( 0, - 0.5, 0 ) );

	//this.cone = new THREE.Mesh( coneGeometry, new THREE.MeshBasicMaterial( { color: color } ) );
	if(!canvas_mode)
		this.cone = new THREE.Mesh( coneGeometry, new THREE.MeshLambertMaterial( { color: color, shading: THREE.SmoothShading, blending: THREE.AdditiveBlending, vertexColors: THREE.VertexColors } ) );
	else
		this.cone = new THREE.Mesh( coneGeometry, new THREE.MeshLambertMaterial( { color: color } ) );


	this.cone.matrixAutoUpdate = false;
	this.add( this.cone );

	this.setDirection( dir );
	this.setLength( length, headLength, headWidth );

};

THREE.VectorHelper.prototype = Object.create( THREE.Object3D.prototype );

THREE.VectorHelper.prototype.setDirection = function () {

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

THREE.VectorHelper.prototype.setLength = function ( length, headLength, headWidth ) {

	if ( headLength === undefined ) headLength = 0.2 * length;
	if ( headWidth === undefined ) headWidth = 0.2 * headLength;

	this.line.scale.set( 1, length, 1 );
	this.line.updateMatrix();

	this.cone.scale.set( headWidth, headLength, headWidth );
	this.cone.position.y = length;
	this.cone.updateMatrix();

};

THREE.VectorHelper.prototype.setColor = function ( color ) {

	this.line.material.color.set( color );
	this.cone.material.color.set( color );

};


