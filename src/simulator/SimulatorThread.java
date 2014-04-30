package simulator;

import model.ModelSimulation;
import android.os.AsyncTask;
import android.widget.Toast;

public class SimulatorThread extends AsyncTask<ModelSimulation, SimResults, Boolean> {
 
	ModelSimulation sim;
	
    @Override
    protected Boolean doInBackground(ModelSimulation... params) {
    	sim = params[0];
        while(true) {
        	//SimResults results = simulate();
            //publishProgress(results);
            if(isCancelled())
                break;
        }
 
        return true;
    }
 
    @Override
    protected void onProgressUpdate(SimResults... values) {
        sim.updateSimulation(values[0].spacecraftState, values[0].sim_progress);
    }
 
    @Override
    protected void onPreExecute() {
    }
 
    @Override
    protected void onPostExecute(Boolean result) {
    	/*if(result)
            Toast.makeText(MainHilos.this, "Tarea finalizada!",
                    Toast.LENGTH_SHORT).show();*/
    }
 
    @Override
    protected void onCancelled() {
        /*Toast.makeText(MainHilos.this, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();*/
    }
}