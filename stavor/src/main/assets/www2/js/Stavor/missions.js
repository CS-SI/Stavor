var global_active_mission = -1;
var global_selected_mission = -1;


function onDeleteMissionButtonClicked(){
	if(global_active_mission == -1){
		alert("Click on a mission first!");
	}else{
		if(global_active_mission == global_selected_mission){
			alert("The mission selected for simulation cannot be deleted, please select another mission first!");
		}else{
			db.transaction(function (tx) {
				tx.executeSql('SELECT name FROM missions WHERE id = '+global_active_mission+';', [], function (tx, results) {	
					var r = confirm("Delete mission "+results.rows.item(0).name+"?");
					if(r){
						db.transaction(function (tx) {
							tx.executeSql('DELETE FROM missions WHERE id = '+global_active_mission+';', [], function (tx, results) {	
								global_active_mission = -1;
								drawMissionsList();
							}, errorDatabaseHandler);
						});
					}
				}, errorDatabaseHandler);
			});
		}
	}
}
function onSelectMissionButtonClicked(){
	if(global_active_mission == -1){
		alert("Click on a mission first!");
	}else{
		if(global_active_mission != global_selected_mission){
			db.transaction(function (tx) {
				tx.executeSql('SELECT name FROM missions WHERE id = '+global_active_mission+';', [], function (tx, results) {	
					var r = confirm("Select mission "+results.rows.item(0).name+" for simulation? (Simulator will be stopped)");
					if(r){
						global_selected_mission = global_active_mission;
						styleMissionRows();
						//TODO reinit simulator
					}
				}, errorDatabaseHandler);
			});
		}
	}
}


function onMissionClicked(id){
	var num_id = Number(id.substr(4,id.length));
	global_active_mission = num_id;
	styleMissionRows();
}

function styleMissionRows(){
	var list = document.getElementById("olListMissions");
	var listItems = list.getElementsByTagName("li");
	for (var i=0; i < listItems.length; i++) {
		var num_id = Number(listItems[i].id.substr(4,listItems[i].id.length));
	
		if(num_id == global_active_mission && num_id == global_selected_mission){
			listItems[i].className = "MissionActiveSelected";
		}else if (num_id == global_active_mission){
			listItems[i].className = "MissionActive";
		}else if(num_id == global_selected_mission){
			listItems[i].className = "MissionSelected";
		}else{
			listItems[i].className = "MissionNormal";
		}
	}
}

function drawMissionsList(){
	db.transaction(function (tx) {
		tx.executeSql('SELECT * FROM missions;', [], function (tx, results) {
			var mission_rows = results.rows;
			var div_list = document.getElementById("olListMissions"); 
			var content = "";
			var len = mission_rows.length, i;
			for (i = 0; i < len; i++) {
				var row = mission_rows.item(i); 
				content += "<li id='mis_"+row.id+"' class='MissionNormal' onclick='onMissionClicked(this.id)'>"+row.name+"</li>";
			}
			div_list.innerHTML = content;
			styleMissionRows();
		}, errorDatabaseHandler);
	});
}

//Database
var db = openDatabase('stavor', '1.0', 'Stavor database', 2 * 1024 * 1024);

function resetMissionsDb(){
	var r = confirm("Reset missions list to default value?");
	if(r){
		db.transaction(function (tx) {
			tx.executeSql('DROP TABLE missions', [], function (tx, results) {
				window.location.reload();
			}, errorDatabaseHandler);
		});
	}
}

function initializeMissionsDb(){
	db.transaction(function (tx) {
		tx.executeSql('CREATE TABLE missions (id INTEGER PRIMARY KEY, isDefault BOOLEAN, name VARCHAR(255), json BLOB)', [], function (tx, results) {
			var default_missions = new Array();
			var mission;
			
			//Example 1- LEO
			mission = new Mission();
			mission.name = "LEO";
			mission.description = "Example of Low Earth Orbit";
			mission.duration = 100000.0;
			mission.step = 10.0;
			mission.initial_orbit.a = 7.0E6;
			mission.initial_orbit.e = 0.0;
			mission.initial_orbit.i = 0;
			//mission.initial_orbit.omega = ;
			mission.initial_orbit.raan = 0.0;
			//mission.initial_orbit.lM = ;
			default_missions[default_missions.length] = mission;
			
			//Example 2- LEO Polar
			mission = new Mission();
			mission.name = "LEO - Polar";
			mission.description = "Example of Polar Low Earth Orbit";
			mission.duration = 100000.0;
			mission.step = 10.0;
			mission.initial_orbit.a = 7.0E6;
			mission.initial_orbit.e = 0.0;
			mission.initial_orbit.i = 1.57;
			//mission.initial_orbit.omega = ;
			mission.initial_orbit.raan = 0.0;
			//mission.initial_orbit.lM = ;
			default_missions[default_missions.length] = mission;
			
			//Example 3- MEO
			mission = new Mission();
			mission.name = "MEO";
			mission.description = "Example of Medium Earth Orbit";
			mission.duration = 100000.0;
			mission.step = 10.0;
			mission.initial_orbit.a = 2.9E7;
			mission.initial_orbit.e = 0.0;
			mission.initial_orbit.i = 0.90;
			//mission.initial_orbit.omega = ;
			mission.initial_orbit.raan = 0.0;
			//mission.initial_orbit.lM = ;
			default_missions[default_missions.length] = mission;
			
			//Example 4- GEO
			mission = new Mission();
			mission.name = "GEO";
			mission.description = "Example of Geostationary Orbit";
			mission.duration = 100000.0;
			mission.step = 10.0;
			mission.initial_orbit.a = 4.2164E7;
			mission.initial_orbit.e = 0.0;
			mission.initial_orbit.i = 0.90;
			//mission.initial_orbit.omega = ;
			mission.initial_orbit.raan = 0.0;
			//mission.initial_orbit.lM = ;
			default_missions[default_missions.length] = mission;
			
			//Example 5- GEO
			mission = new Mission();
			mission.name = "GEO - Equatorial";
			mission.description = "Example of Equatorial Geostationary Orbit";
			mission.duration = 100000.0;
			mission.step = 10.0;
			mission.initial_orbit.a = 4.2164E7;
			mission.initial_orbit.e = 0.0;
			mission.initial_orbit.i = 0.0;
			//mission.initial_orbit.omega = ;
			mission.initial_orbit.raan = 0.0;
			//mission.initial_orbit.lM = ;
			default_missions[default_missions.length] = mission;
			
			//Example 6- GTO
			mission = new Mission();
			mission.name = "GTO";
			mission.description = "Geostationary Transfer Orbit";
			mission.duration = 100000.0;
			mission.step = 10.0;
			mission.initial_orbit.a = 2.4396159E7;
			mission.initial_orbit.e = 0.72831215;
			//mission.initial_orbit.i = 0.0;
			//mission.initial_orbit.omega = ;
			//mission.initial_orbit.raan = 0.0;
			//mission.initial_orbit.lM = ;
			default_missions[default_missions.length] = mission;
			
			for(var i = 0; i < default_missions.length; i++)
				tx.executeSql('INSERT INTO missions (isDefault, name, json) VALUES (?, ?, ?)', [true, default_missions[i].name, JSON.stringify(default_missions[i])], successDatabaseHandler, errorDatabaseHandler);
			});
		});//This will stop here if Table already exists
}

function addMissionToDb(mission){
	var json = JSON.stringify(mission);
	db.transaction(function (tx) {
		tx.executeSql('INSERT INTO missions (isDefault, name, json) VALUES (?, ?, ?)', [false, mission.name, json]);
	});
}

function successDatabaseHandler(){
}
function errorDatabaseHandler(transaction, error) {
	if(parameters.debug) alert("Error : " + error.message);
}