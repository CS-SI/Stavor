var Simulator = function(controls,indicators){
	this.enum_simulation_status = {
		STOPPED: 1,
		PAUSED: 2,
		PLAYING: 3
	}
	this.controls = controls;
	this.indicators = indicators;
	
	this.state = enum_simulation_status.STOPPED;
}
//****************************************************************************
//                         From Stavor to Simulator  --> (Control widgets callbacks)
//****************************************************************************
Simulator.prototype.initializePropagator = function(init){

}
Simulator.prototype.playButtonClicked = function(play){
	
}
Simulator.prototype.stopButtonClicked = function(){
	
}
Simulator.prototype.reverseButtonClicked = function(){
	
}
Simulator.prototype.forwardButtonClicked = function(){
	
}
Simulator.prototype.slowButtonClicked = function(){
	
}
Simulator.prototype.progressValueChanged = function(){
	
}
//****************************************************************************
//                        From Simulator to Stavor  <-- (Events thrown by simulator)
//****************************************************************************
Simulator.prototype.updateMissionState = function(state){
	global_simulation.results = state;
	updateInfoPanel(false);
}

Simulator.prototype.updateSimulatorState = function(state){
	this.state = state;
	updateControls();
}

function updateControls(){
	//If play/pause/stop/initialized status has been changed -> change widgets
	if(this.state.playing){
	}else{
	}
	//Use state.current_time to put the slidder and indicate the time;
}

Simulator.prototype.alertEndOfSimulation = function(){
	alert("Mission ended");
}