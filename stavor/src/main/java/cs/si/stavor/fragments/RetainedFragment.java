package cs.si.stavor.fragments;

import android.app.Fragment;
import android.os.Bundle;

import org.xwalk.core.XWalkView;

import cs.si.stavor.simulator.Simulator;

/**
 * Fragment to store information when restarting app (e.g. rotating device)
 * @author Xavier Gibert
 *
 */
public class RetainedFragment extends Fragment {
    private Simulator simulator;
    private XWalkView browser;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(XWalkView browser, Simulator simulator) {
        this.browser = browser;
    	this.simulator = simulator;
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public XWalkView getBrowser() {
        return browser;
    }


}
