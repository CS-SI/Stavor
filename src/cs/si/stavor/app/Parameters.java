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
		public static final long splash_min_time_ns = 1000000000;//[ns] 1.0s min
		public static final boolean show_tutorial = true;
		public static final boolean show_tests_section = true;
	}
	/**
	 * About screen information
	 * @author Xavier Gibert
	 *
	 */
	public static class About {
		public static final String project_start_date = "2014/04/01";
		public static final String version_orekit = "6.1";
		public static final String version_xwalk = "7.36.154.1b";
		public static final String version_threejs = "r67";
		public static final String version_gson = "2.2.4";
		public static final String version_androidcolorpicker = "1.0";
		public static final String version_loader = "0.7.3";
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
		
		public static class Remote{
			public static final int remote_connection_timeout_ms = 5000;//[ms]
			public static final String default_host = "192.168.1.2";
			public static final String default_port = "1520";
		}
	}
	/**
	 * URLs for the visualization and tests
	 * @author Xavier Gibert
	 *
	 */
	public static class Web {
		public static final String STARTING_PAGE = "file:///android_asset/www/attitude/index_orbit.html";
		//public static final String TEST_PAGE_1 = "file:///android_asset/www/index.html";
		public static final String TEST_PAGE_1 = "http://localhost:8081/";
		//public static final String TEST_PAGE_1 = "http://webglreport.com";
		//public static final String TEST_PAGE_1 = "http://get.webgl.org/";
		public static final String TEST_PAGE_2 = "http://doesmybrowsersupportwebgl.com/";
		public static final String TEST_PAGE_3 = "http://www.khronos.org/registry/webgl/sdk/tests/webgl-conformance-tests.html";
		public static final String LOCALHOST = "http://localhost:8080/";
	}
}
