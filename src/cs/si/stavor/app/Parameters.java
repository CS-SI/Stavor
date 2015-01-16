package cs.si.stavor.app;

/**
 * Global app configuration parameters
 * @author Xavier Gibert
 *
 */
public class Parameters {
	/**
	 * General application parameters
	 * @author Xavier Gibert
	 *
	 */
	public static class App {
		public static final int splash_min_time_ms = 3000;//[ms] 3.0s min
		public static final boolean show_tests_section = false;
		public static final boolean pro_version = false;
		
		//FIRST RUN PARAMETERS
		public static final boolean show_guide = true;
		//Deprecated: it was the old crappy tutorial based on dialog boxes
		public static final boolean show_tutorial = false;
		public static final boolean show_orekit_data_installation_message = false;
		//If we want to show the sections menu open during the first run of the application and always until user makes use of it then set to false.
		public static final boolean first_start_app_closed_section_menu = true; 
	}
	/**
	 * About screen information
	 * @author Xavier Gibert
	 *
	 */
	public static class About {
		public static final String jocs_site = "http://jocsmobile.wordpress.com";
		public static final String orekit_site = "http://orekit.org";
		public static final String project_start_date = "2014/04/01";
		public static final String version_orekit = "6.1";
		public static final String version_xwalk = "10.39.235.15s";
		public static final String version_threejs = "r67";
		public static final String version_gson = "2.2.4";
		public static final String version_androidcolorpicker = "1.0";
		public static final String version_loader = "0.7.3";
		public static final String version_openlayers = "2.13.1";
		public static final String version_http = "1.2.1";
	}
	/**
	 * Visualization configuration
	 * @author Xavier Gibert
	 *
	 */
	public static class Hud {
		public static final boolean start_panel_open = true;
	}
	/**
	 * Simulator configuration
	 * @author Xavier Gibert
	 *
	 */
	public static class Simulator {
		public static final long min_hud_panel_refreshing_interval_ns = 500000000;//[ns] 2Hz max
		public static final long min_hud_model_refreshing_interval_ns = 40000000;//[ns] 25Hz max
		public static final long model_refreshing_interval_safe_guard_ns = 5000000;//[ns] 5ms
		public static final int amount_mission_examples = 6;
		public static class Remote{
			public static final int remote_connection_timeout_ms = 5000;//[ms]
			public static final String default_host = "192.168.1.2";
			public static final String default_port = "1520";
			public static boolean default_ssl = false;
			public static int objects_per_ssl_request = 50;
		}
	}
	/**
	 * URLs for the visualization and tests
	 * @author Xavier Gibert
	 *
	 */
	public static class Web {
		public static final String STARTING_PAGE = "file:///android_asset/www/huds/index.html";
		public static final String STARTING_PAGE_ORBIT = "file:///android_asset/www/huds/index_orbit.html";
		public static final String STARTING_PAGE_MAP = "file:///android_asset/www/map/index.html";
		//public static final String TEST_PAGE_1 = "file:///android_asset/www/index.html";
		//public static final String TEST_PAGE_1 = "http://127.0.0.1:8081/";
		//public static final String TEST_PAGE_1 = "http://webglreport.com";
		public static final String TEST_PAGE_1 = "http://get.webgl.org/";
		public static final String TEST_PAGE_2 = "http://doesmybrowsersupportwebgl.com/";
		public static final String TEST_PAGE_3 = "http://www.khronos.org/registry/webgl/sdk/tests/webgl-conformance-tests.html";
		public static final String LOCALHOST = "http://localhost:8080/";
	}
	public static class Map {
		//public static final double marker_pos_threshold = 0.2;//In deg
		public static final int station_visibility_points = 30;//Points in the polygon
		public static final int satellite_fov_points = 30;//30
		public static final double solar_terminator_points = 100;
		public static final double solar_terminator_threshold = 60*15;//In seconds
	}
}
