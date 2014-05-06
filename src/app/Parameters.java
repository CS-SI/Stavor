package app;

public class Parameters {
	public static class Simulator {
		public static final long min_hud_panel_refreshing_interval_ns = 500000000;//2Hz max
		public static final long min_hud_model_refreshing_interval_ns = 40000000;//25Hz max
	}
	public static class Web {
		public static final String STARTING_PAGE = "file:///android_asset/www/index.html";
		public static final String TEST_PAGE_1 = "http://get.webgl.org/";
		public static final String TEST_PAGE_2 = "http://doesmybrowsersupportwebgl.com/";
		public static final String TEST_PAGE_3 = "http://www.khronos.org/registry/webgl/sdk/tests/webgl-conformance-tests.html";
	}
}
