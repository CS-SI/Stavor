var AndroidInterface = function(sim){
	this.simulator = sim;
}
//****************************************************************************
//                         From Stavor to Simulator  --> (Control widgets callbacks)
//****************************************************************************
AndroidInterface.prototype.changeMission = function(mission){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.changeMission!= "undefined") { // check the method
			 Android.changeMission(mission);
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
AndroidInterface.prototype.accelerateButtonClicked = function(){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.accelerateButtonClicked!= "undefined") { // check the method
			 Android.accelerateButtonClicked();
		  }
   	}
}
AndroidInterface.prototype.progressValueChanged = function(perc){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.progressValueChanged!= "undefined") { // check the method
			var json = JSON.stringify(new MyInteger(perc));
			 Android.missionProgressValueChanged(json);
		  }
   	}
}
AndroidInterface.prototype.progressValueChangedEnd = function(perc){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.progressValueChangedEnd!= "undefined") { // check the method
			var json = JSON.stringify(new MyInteger(perc));
			 Android.missionProgressValueChangedEnd(json);
		  }
   	}
}

AndroidInterface.prototype.sendSimulatorConfiguration = function(sim_conf){
	if (typeof Android != "undefined"){ // check the bridge 
		  if (Android.sendSimulatorConfiguration!= "undefined") { // check the method
			 Android.sendSimulatorConfiguration(sim_conf);
		  }
   	}
}


var MyInteger = function(value){
	this.value = value;
}