
//Database
var db = openDatabase('stavor', '1.0', 'Stavor database', 2 * 1024 * 1024);

function successDatabaseHandler(){
}
function errorDatabaseHandler(transaction, error) {
	if(parameters.debug) alert("Error : " + error.message);
}