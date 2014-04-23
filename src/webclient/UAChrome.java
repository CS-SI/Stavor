package webclient;

import android.content.Context;
import android.webkit.WebChromeClient;

public class UAChrome extends WebChromeClient {
	@SuppressWarnings("unused")
	private Context context;

	public UAChrome(Context context) {
		super();
		this.context = context;
	}
}
