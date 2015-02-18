package cs.si.stavor.simulator;

/**
 * Created by xgibert on 17/02/2015.
 */
public class SimulatorControlsStatus {
    public boolean isConnected;
    public boolean isStopped;
    public boolean isPlaying;
    public boolean isForward;
    public int progress;
    public SimulatorControlsStatus(Simulator simulator) {
        if(simulator.getSimulatorStatus() == SimulatorStatus.Connected)
            isConnected = true;
        else
            isConnected = false;

        if(simulator.getSimulationStatus() == SimulationStatus.Play){
            isStopped = false;
            isPlaying = true;
        }else if(simulator.getSimulationStatus() == SimulationStatus.Pause){
            isStopped = false;
            isPlaying = false;
        }else{
            isStopped = true;
            isPlaying = false;
        }

        if(simulator.getSimulationSense() == SimulationSense.Forward)
            isForward = true;
        else
            isForward = false;

        progress = simulator.getSimulationProgress();
    }
}
