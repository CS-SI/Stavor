var KeplerianOrbit = function(){
	
	this.mu =  3.986004415e+14;
	/*
	 * Semi major axis in meters
	 */
	this.a = 24396159;
	/*
	 * Eccentricity
	 */
	this.e = 0.72831215;
	/*
	 * Inclination
	 */
	this.i = Math.PI*7/180;//rad
	/*
	 * Perigee argument
	 */
	this.omega = Math.PI;//rad
	/*
	 * Right ascension of ascending node
	 */
	this.raan = 0;//rad
	/*
	 * Mean anomaly
	 */
	this.lM = 0;
}