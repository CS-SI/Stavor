package fragments;

import android.app.Fragment;
import android.os.Bundle;
import simulator.Simulator;

/**
 * Fragment to store information when restarting app (e.g. rotating device)
 * @author Xavier Gibert
 *
 */
public class RetainedFragment extends Fragment {
	// data object we want to retain
    private Simulator data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(Simulator data) {
        this.data = data;
    }

    public Simulator getData() {
        return data;
    }

}
