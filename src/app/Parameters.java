package app;

public class Parameters {
	public static class App {
		public static final long splash_min_time_ns = 1000000000;//[ns] 1.0s min
	}
	public static class Simulator {
		public static final long min_hud_panel_refreshing_interval_ns = 500000000;//[ns] 2Hz max
		public static final long min_hud_model_refreshing_interval_ns = 33333333;//[ns] 30Hz max
		public static final int remote_connection_timeout_ms = 5000;//[ms]
	}
	public static class Web {
		public static final String STARTING_PAGE = "file:///android_asset/www/index.html";
		public static final String TEST_PAGE_1 = "http://get.webgl.org/";
		public static final String TEST_PAGE_2 = "http://doesmybrowsersupportwebgl.com/";
		public static final String TEST_PAGE_3 = "http://www.khronos.org/registry/webgl/sdk/tests/webgl-conformance-tests.html";
	}
}
