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
        public static final boolean debug = true;
        public static final boolean output_simulator_toasts = false;
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
		public static final String version_orekit = "7.0";
		public static final String version_xwalk = "10.39.235.15s";
		public static final String version_threejs = "r67";
		public static final String version_gson = "2.2.4";
		public static final String version_androidcolorpicker = "1.0";
		public static final String version_loader = "0.7.3";
		public static final String version_openlayers = "2.13.1";
		public static final String version_http = "1.2.1";
	}
	/**
	 * Simulator configuration
	 * @author Xavier Gibert
	 *
	 */
	public static class Simulator {
		public static final long min_hud_panel_refreshing_interval_ns = 500000000;//[ns] 2Hz max
		public static final long min_hud_model_refreshing_interval_ns = 50000000;//[ns] 20Hz max
		public static final long model_refreshing_interval_safe_guard_ns = 10000000;//[ns] 10ms
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
		public static final String STARTING_PAGE = "file:///android_asset/www/index.html";
	}


    public static class Map {
        //public static final double marker_pos_threshold = 0.2;//In deg
        public static final int station_visibility_points = 30;//Points in the polygon
        public static final int satellite_fov_points = 30;//30
        public static final double solar_terminator_points = 100;
        public static final double solar_terminator_threshold = 60*15;//In seconds
    }
}
