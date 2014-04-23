package webclient;

import android.content.Context;
import android.webkit.*;

public class UAWebViewClient extends WebViewClient{

	@SuppressWarnings("unused")
	private Context context;
	
	public UAWebViewClient(Context context) {
		super();
		this.context = context;
	}
}
