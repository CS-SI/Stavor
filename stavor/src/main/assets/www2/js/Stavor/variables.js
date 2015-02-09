var global_current_visualization = enum_visualizations.ORBITE;
var global_missions_list_is_open = false;

//Flags for asyncronous loading functions
var global_delayed_loading = {
	visualization:{
		attitude: false,
		orbit: false,
		map: false
	},
	database:{
		missions: false
	}
}

var global_menus = {
	attitude:{
		isOpen: false,
		inAnimation: false
	},
	orbit:{
		isOpen: false,
		inAnimation: false
	},
	map:{
		isOpen: false,
		inAnimation: false
	},
	info_panel:{
		isOpen: false
	},
	global:{
		isOpen: false
	},
	mission:{
		isOpen: false
	}
}

//To save camera position and zoom when an option is changed and the visualization needs to recharge
var global_cameras = {
	attitude:{
		position: new THREE.Vector3(300,300,300),
		up: new THREE.Vector3(-0.577,-0.577,0.577),
		view_mode: "FREE"
	},
	orbit:{
		position: new THREE.Vector3(0,0,13),
		view_locked: false
	},
	map:{
	}
}

var global_simulation = {
	config:{
		global:{
			performance_level: enum_performance_level.NORMAL,
			show_fps: false
		},
		attitude:{
			show_sky: true,
			show_sphere: true,
			show_mini_spheres: true,
			show_circles: true,
			show_axis: true,
			show_axis_labels: true,
			//Measures: Inclination, angles and planes
			show_planes: false,
			show_orbital_plane: false,
			plane_xy_color: "#ff0094",
			plane_orb_color: "#65ff00",
			show_inclination: false,
			//Measures: spheric coordinates
			show_spheric_coords: false,
			spheric_coords_selection: enum_basic_indicators.VELOCITY,
			//Mesures: vector's angle
			show_vectors_angle: false,
			vectors_angle_sel1: enum_basic_indicators.VELOCITY,
			vectors_angle_sel2: enum_basic_indicators.ACCELERATION,
			//Spacecraft
			show_sc_axis: true,
			sc_show_eng_texture: true,
			//Sun
			show_sun: true,
			sun_rotates: true,
			sun_rotation_speed: enum_rotation_speed.NORMAL,
			show_sun_texture: true,
			sun_simple_glow: true,//Recomended to not use the shader glow, problems in android
			sun_show_line: true,
			sun_show_dist: true,
			//Earth
			show_earth: true,
			earth_rotates: true,
			earth_rotation_speed: enum_rotation_speed.VERY_SLOW,
			show_earth_texture: true,
			earth_show_line: true,
			earth_show_dist: true,
			//Indicators
			show_velocity: true,
			color_velocity: "#001dff",
			limit_velocity: 10, //Km/s value corresponding to the full length arrow (touching the sphere)
			show_acceleration: true,
			color_acceleration: "#fc00b0",
			limit_acceleration: 5, //Km/s2 value corresponding to the full length arrow (touching the sphere)
			show_momentum: true,
			color_momentum: "#00fc19",
			show_target_a: false,
			color_target_a: "#ff0000",
			value_target_a: new THREE.Vector3( -5, -5, -5 ),
			show_vector_a: false,
			color_vector_a: "#00fffa",
			limit_vector_a: 25,// In the same units of the provided value
			value_vector_a: new THREE.Vector3( -5, -5, -5 ),
			show_direction_a: false,
			color_direction_a: "#ffff00",
			value_direction_a: new THREE.Vector3( -5, -5, -5 )
		},
		orbit:{
			show_sky: true,
			show_axis: true,
			show_axis_labels: true,
			show_earth: true,
			show_earth_axis: true,
			show_earth_atmosphere: true,
			show_earth_clouds: true,
			show_xy_plane: true,
			color_xy_plane: "#ff0094",
			show_spacecraft: true,
			spacecraft_color: "#fff200",
			show_projection: true,
			orbit_color: "#00ff00"
		},
		map:{
			zoom: 0,
			lonLat: new OpenLayers.LonLat( 0, 0 ),
			stations: [],
			show_satellite: true,					
			show_fov: true,
			show_track: true,
			show_sun_icon: true,
			show_sun_terminator: true,
			follow_sc: false
		}
	},
	results:{
		info_panel:{
			attitude:{
				roll: 0,
				pitch: 0,
				yaw: 0
			},
			velocity: 0,
			acceleration: 0,
			orb_radius: 0,
			mass: 0
		},
		spacecraft:{
			attitude: new THREE.Quaternion(0,0,0,1),			
			velocity: new THREE.Vector3( 2.83195518282009,-5.49945264687097E-16,-1.1973314470667253 ), //Km/s
			acceleration: new THREE.Vector3( -4.517719883627554E-4,0.22420886513859567,1.9100613309698625E-4 ), //Km/s2
			momentum: new THREE.Vector3( 5.048428313412141E10,-3.091270787372526E-6,1.1940655832842628E11 ),
			sun_direction: new THREE.Vector3( -1.1414775124432093E7,-1.464188429948789E8,7716114.240559303 ), //Km
			earth_direction: new THREE.Vector3( 7.337791634217176E-12,42164.0,-2.010800849831316E-12 ) //Km
		},
		earth:{
			earth_rotation: new THREE.Quaternion( 0, 0, 0, 1 ),	
			spacecraft_position: new THREE.Vector3( 42164000.0,7.337791634217176E-12,-2.010800849831316E-12 ), //Km
			osculating_orbit:{
				a: 24396159,
				e: 0.73,
				i: Math.PI/9,
				w: Math.PI,
				raan: 0
			}
		},
		map:{
			sun_position:{
				lat: 0,//In degrees
				lon: 0//In degrees
			},
			station_areas: [],
			fov:{
				fov_type: enum_fov_type.NO_POLES,
				closed: [],
				terminator: []
			},
			solarTerminator: []
		}
	}
}

// DERIVATED VARIABLES 
// Segments
var global_3d_segments;

function setPerformanceLevel(){
	if(global_simulation.config.global.performance_level<1)
		global_simulation.config.global.performance_level=1;
	var segments_scale = global_simulation.config.global.performance_level;//Multiply segments of all geometries:

	global_3d_segments = {
		attitude:{
			sc_body_segments: 8 * segments_scale,
			sc_window_segments: 10 * segments_scale,
			sc_engine_segments: 10 * segments_scale,
			sc_eng_disk_segments: this.sc_engine_segments,
			sun_seg: 10 * segments_scale,
			earth_seg: 12 * segments_scale,
			sphere_segments: 20 * segments_scale,
			miniSphere_seg: 7 * segments_scale,
			torus_seg_r: 4 * segments_scale,
			torus_seg_t: 32 * segments_scale,
			arc_seg_r: 4 * segments_scale,
			arc_seg_t: 32 * segments_scale,
			arrow_segments: 4 * segments_scale,
			momentum_segments: 4 * segments_scale,
			target_segments: 8 * segments_scale,
			arc_resolution: 30 * segments_scale,
			plane_resolution: 20 * segments_scale
		},
		orbit:{
			earth_seg: 32 * segments_scale,
			plane_resolution: 20*segments_scale,
			spacecraft_seg: 16*segments_scale
		}
	}
}
setPerformanceLevel();