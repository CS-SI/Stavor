package model;

import cs.si.satatt.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ModelConfiguration {
	
	public ModelConfiguration(Context ctx){
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		try{
			performance_level = Integer.parseInt(sharedPref.getString(ctx.getString(R.string.pref_key_detail_level), Integer.toString(performance_level)));
			fps_update_skips = Integer.parseInt(sharedPref.getString(ctx.getString(R.string.pref_key_fps_update_skips), Integer.toString(fps_update_skips)));
			
			sun_rotation_speed = Integer.parseInt(sharedPref.getString(ctx.getString(R.string.pref_key_sun_rotation_speed), Integer.toString(sun_rotation_speed)));
			earth_rotation_speed = Integer.parseInt(sharedPref.getString(ctx.getString(R.string.pref_key_earth_rotation_speed), Integer.toString(earth_rotation_speed)));
			
			limit_velocity = Float.parseFloat(sharedPref.getString(ctx.getString(R.string.pref_key_velocity_limit), Float.toString(limit_velocity)));
			limit_acceleration = Float.parseFloat(sharedPref.getString(ctx.getString(R.string.pref_key_acceleration_limit), Float.toString(limit_acceleration)));
			limit_vector_a = Float.parseFloat(sharedPref.getString(ctx.getString(R.string.pref_key_vector_a_limit), Float.toString(limit_vector_a)));
		}catch(NumberFormatException e){
			System.err.println("Error loading configuration parameter: "+e.getMessage());
		}
		show_fps = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_fps), show_fps);
		show_sky = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_sky), show_sky);
		show_sphere = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_sphere), show_sphere);
		show_mini_spheres = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_mini_spheres), show_mini_spheres);
		show_circles = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_circles), show_circles);
		show_planes = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_planes), show_planes);
		show_axis = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_axis), show_axis);
		show_axis_labels = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_axis_labels), show_axis_labels);
		show_spacecraft = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_spacecraft), show_spacecraft);
		sc_show_eng_texture = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_engine_texture), sc_show_eng_texture);
		sun_rotates = sharedPref.getBoolean(ctx.getString(R.string.pref_key_sun_rotates), sun_rotates);
		show_sun = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_sun), show_sun);
		show_sun_texture = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_sun_texture), show_sun_texture);
		sun_simple_glow = sharedPref.getBoolean(ctx.getString(R.string.pref_key_sun_simple_glow), sun_simple_glow);
		sun_show_line = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_sun_line), sun_show_line);
		sun_show_dist = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_sun_distance), sun_show_dist);
		earth_rotates = sharedPref.getBoolean(ctx.getString(R.string.pref_key_earth_rotates), earth_rotates);
		show_earth = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_earth), show_earth);
		show_earth_texture = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_earth_texture), show_earth_texture);
		earth_show_line = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_earth_line), earth_show_line);
		earth_show_dist = sharedPref.getBoolean(ctx.getString(R.string.pref_key_show_earth_distance), earth_show_dist);
		show_velocity = sharedPref.getBoolean(ctx.getString(R.string.pref_key_velocity_show), show_velocity);
		show_acceleration = sharedPref.getBoolean(ctx.getString(R.string.pref_key_acceleration_show), show_acceleration);
		show_momentum = sharedPref.getBoolean(ctx.getString(R.string.pref_key_momentum_show), show_momentum);
		show_target_a = sharedPref.getBoolean(ctx.getString(R.string.pref_key_target_a_show), show_target_a);
		show_vector_a = sharedPref.getBoolean(ctx.getString(R.string.pref_key_vector_a_show), show_vector_a);
		show_direction_a = sharedPref.getBoolean(ctx.getString(R.string.pref_key_direction_a_show), show_direction_a);
		
		color_velocity = sharedPref.getInt(ctx.getString(R.string.pref_key_velocity_color), color_velocity);
		color_acceleration = sharedPref.getInt(ctx.getString(R.string.pref_key_acceleration_color), color_acceleration);
		color_momentum = sharedPref.getInt(ctx.getString(R.string.pref_key_momentum_color), color_momentum);
		color_target_a = sharedPref.getInt(ctx.getString(R.string.pref_key_target_a_color), color_target_a);
		color_vector_a = sharedPref.getInt(ctx.getString(R.string.pref_key_vector_a_color), color_vector_a);
		color_direction_a = sharedPref.getInt(ctx.getString(R.string.pref_key_direction_a_color), color_direction_a);
	}
	
	
	//Performances
	public int performance_level = 3;//1(simple) to 9(detailed)
	public boolean show_fps = true;
	public int fps_update_skips = 60;
	//General
	public boolean show_sky = true;
	public boolean show_sphere = true;
	public boolean show_mini_spheres = true;
	public boolean show_circles = true;
	public boolean show_planes = true;
	//Axis
	public boolean show_axis = true;
	public boolean show_axis_labels = true;
	//Spacecraft
	public boolean show_spacecraft = true;
	public boolean sc_show_eng_texture = true;
	//Sun
	public boolean show_sun = true;
	public boolean sun_rotates = true;
	public int sun_rotation_speed = 5;//Base rotation speed multiplier
	public boolean show_sun_texture = true;
	public boolean sun_simple_glow = true;
	public boolean sun_show_line = true;
	public boolean sun_show_dist = true;
	//Earth
	public boolean show_earth = true;
	public boolean earth_rotates = true;
	public int earth_rotation_speed = 2;//Base rotation speed multiplier
	public boolean show_earth_texture = true;
	public boolean earth_show_line = true;
	public boolean earth_show_dist = true;
	//Velocity
	public boolean show_velocity = true;
	public int color_velocity = 0x001dff;
	public float limit_velocity = 15;//Km/s
	//Acceleration
	public boolean show_acceleration = true;
	public int color_acceleration = 0xfc00b0;
	public float limit_acceleration = 15;//Km/s2
	//Momentum
	public boolean show_momentum = true;
	public int color_momentum = 0x00fc19;
	//Targets
	public boolean show_target_a = true;
	public int color_target_a = 0xff0000;
	//Vectors
	public boolean show_vector_a = false;
	public int color_vector_a = 0x00fffa;
	public float limit_vector_a = 25;//Same units as value
	//Directions
	public boolean show_direction_a = false;
	public int color_direction_a = 0xffff00;
}
