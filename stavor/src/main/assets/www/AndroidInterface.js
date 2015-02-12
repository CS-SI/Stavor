var AndroidInterface = function(sim){
	this.simulator = sim;
}
//****************************************************************************
//                         From Stavor to Simulator  --> (Control widgets callbacks)
//****************************************************************************
AndroidInterface.prototype.initializePropagator = function(init){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.initializePropagator!= "undefined") { // check the method
			 Android.initializePropagator(init);
		  }
   	}
}
AndroidInterface.prototype.playButtonClicked = function(){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.playButtonClicked!= "undefined") { // check the method
			 Android.playButtonClicked();
		  }
   	}
}
AndroidInterface.prototype.stopButtonClicked = function(){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.stopButtonClicked!= "undefined") { // check the method
			 Android.stopButtonClicked();
		  }
   	}
}
AndroidInterface.prototype.reverseButtonClicked = function(){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.reverseButtonClicked!= "undefined") { // check the method
			 Android.reverseButtonClicked();
		  }
   	}
}
AndroidInterface.prototype.forwardButtonClicked = function(){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.forwardButtonClicked!= "undefined") { // check the method
			 Android.forwardButtonClicked();
		  }
   	}
}
AndroidInterface.prototype.slowButtonClicked = function(){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.slowButtonClicked!= "undefined") { // check the method
			 Android.slowButtonClicked();
		  }
   	}
}
AndroidInterface.prototype.progressValueChanged = function(value){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.progressValueChanged!= "undefined") { // check the method
			 Android.progressValueChanged();
		  }
   	}
}
//****************************************************************************
//                        From Simulator to Stavor  <-- (Events thrown by simulator)
//****************************************************************************
AndroidInterface.prototype.updateMissionState = function(state){
	this.simulator.updateMissionState(state);
}

AndroidInterface.prototype.updateSimulatorState = function(state){
	this.simulator.updateSimulatorState(state);
}