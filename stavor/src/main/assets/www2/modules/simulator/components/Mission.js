var Mission = function () {
	this._id = 0;
	this.name = "CustomMission";
	this.description = "";
	this.duration = 600000.0;
	this.step = 60.0;
	this.initial_date = new SimDate();
	this.initial_mass = 2000.0;
	this.initial_orbit = new KeplerianOrbit();
}



