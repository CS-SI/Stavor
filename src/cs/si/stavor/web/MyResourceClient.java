package cs.si.stavor.web;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

/**
     * XWalk client class
     * @author Xavier Gibert
     *
     */
    public class MyResourceClient extends XWalkResourceClient {
        public MyResourceClient(XWalkView view) {
            super(view);
        }
        /*@Override
  		public void onProgressChanged(XWalkView view, int progress) {
  			// Activities and WebViews measure progress with different scales.
  			// The progress meter will automatically disappear when we reach 100%
  			try{
  				if(progress==100)
  					simulator.setBrowserLoaded(true);
  				setProgress(progress * 100);
  			}catch(NullPointerException nulle){
  				
  			}
  		}*/
    }