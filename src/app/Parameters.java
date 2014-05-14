package app;

public class Parameters {
	public static class App {
		public static final long splash_min_time_ns = 1000000000;//[ns] 1.0s min
	}
	public static class About {
		public static final String project_start_date = "2014/04/01";
		public static final String project_license = "Undef";
		public static final String version_orekit = "6.1";
		public static final String version_xwalk = "6.35.131.4 beta";
		public static final String version_threejs = "r66 (mod)";
		public static final String version_gson = "2.2.4";
		public static final String version_androidcolorpicker = "1.0";
	}
	public static class Hud {
		public static final boolean start_panel_open = true;
	}
	public static class Simulator {
		public static final long min_hud_panel_refreshing_interval_ns = 1000000000;//[ns] 1Hz max
		public static final long min_hud_model_refreshing_interval_ns = 40000000;//[ns] 25Hz max
		public static final long model_refreshing_interval_safe_guard_ns = 5000000;//[ns] 5ms
		public static final int remote_connection_timeout_ms = 5000;//[ms]
	}
	public static class Web {
		public static final String STARTING_PAGE = "file:///android_asset/www/index.html";
		public static final String TEST_PAGE_1 = "http://get.webgl.org/";
		public static final String TEST_PAGE_2 = "http://doesmybrowsersupportwebgl.com/";
		public static final String TEST_PAGE_3 = "http://www.khronos.org/registry/webgl/sdk/tests/webgl-conformance-tests.html";
	}
}
