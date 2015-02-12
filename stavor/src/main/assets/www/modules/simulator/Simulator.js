var Simulator = function(){
	this.enum_simulation_status = {
		STOPPED: 1,
		PAUSED: 2,
		PLAYING: 3
	}
	this.enum_simulation_sense = {
		FORWARD: 1,
		REVERSE: 2
	}
	this.controls = {
		PLAY: document.getElementById("PlayButton"),
		STOP: document.getElementById("StopButton"),
		PROGRESS: document.getElementById("TimeBar"),
		ACCEL: document.getElementById("AccelButton"),
		SLOW: document.getElementById("SlowButton"),
		FORWARD: document.getElementById("ForwardButton"),
		REVERSE: document.getElementById("ReverseButton")
	}
	this.indicators = {
		CLOCK: document.getElementById("SimTime"),
		PROGRESS: document.getElementById("TimeBar")
	}
	
	this.sim_state = {
		state: this.enum_simulation_status.STOPPED,
		sense: this.enum_simulation_sense.FORWARD
	}
	
	this.sim_interface = new AndroidInterface(this);
}
//****************************************************************************
//                         From Stavor to Simulator  --> (Control widgets callbacks)
//****************************************************************************
Simulator.prototype.initializePropagator = function(init){
	this.sim_interface.initializePropagator(init);
}
Simulator.prototype.playButtonClicked = function(){
	this.sim_interface.playButtonClicked();
}
Simulator.prototype.stopButtonClicked = function(){
	this.sim_interface.stopButtonClicked();
}
Simulator.prototype.reverseButtonClicked = function(){
	this.sim_interface.reverseButtonClicked();
}
Simulator.prototype.forwardButtonClicked = function(){
	this.sim_interface.forwardButtonClicked();
}
Simulator.prototype.accelerateButtonClicked = function(){
	this.sim_interface.accelerateButtonClicked();
}
Simulator.prototype.slowButtonClicked = function(){
	this.sim_interface.slowButtonClicked();
}
Simulator.prototype.progressValueChanged = function(value){
	this.sim_interface.progressValueChanged(value);
}
//****************************************************************************
//                        From Simulator to Stavor  <-- (Events thrown by simulator)
//****************************************************************************
Simulator.prototype.updateMissionState = function(state){
	global_simulation.results = state;
	updateIndicators();
}

Simulator.prototype.updateSimulatorState = function(state){
	this.state = state;
	updateControls();
}

Simulator.prototype.alertEndOfSimulation = function(){
	alert("Mission ended");
}

function updateIndicators(){
	updateInfoPanel(false);
	indicators.CLOCK.innerHTML = global_simulation.results.info_panel.time.getFormattedString();
	//TODO update also TimeBar indicator
}

function updateControls(){
	if(this.sim_state.state == this.enum_simulation_status.PLAYING){
		this.controls.PLAY.className = "SimPause";
		this.controls.STOP.className = "SimStopEnabled";
		this.controls.PROGRESS.className = "SimProgressEnabled";
	}else if((this.sim_state.state == this.enum_simulation_status.STOPPED)){
		this.controls.PLAY.className = "SimPlay";
		this.controls.STOP.className = "SimStopDisabled";
		this.controls.PROGRESS.className = "SimProgressDisabled";
		this.controls.PROGRESS.value = 0;
	}else if((this.sim_state.state == this.enum_simulation_status.PAUSED)){
		this.controls.PLAY.className = "SimPlay";
		this.controls.STOP.className = "SimStopEnabled";
		this.controls.PROGRESS.className = "SimProgressEnabled";
	}
	
	if(this.sim_state.sense == this.enum_simulation_sense.FORWARD){
		this.controls.FORWARD.className = "SimSenseActive";
		this.controls.REVERSE.className = "SimSenseNotActive";
	}else if(this.sim_state.sense == this.enum_simulation_sense.REVERSE){
		this.controls.REVERSE.className = "SimSenseActive";
		this.controls.FORWARD.className = "SimSenseNotActive";
	}
}

