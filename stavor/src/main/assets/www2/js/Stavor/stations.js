var global_stations = {
	active: -1
}

function loadStationsStoredVariables(){
	var serialized = localStorage.getItem('stationsConfig'); 
	if(serialized){
		var localData = JSON.parse(serialized);
		global_stations = localData;
	}
}
function saveStationsStoredVariables(){
	var dataToStore = JSON.stringify(global_stations);
	localStorage.setItem('stationsConfig', dataToStore);
}

function onDeleteStationButtonClicked(){
	if(global_stations.active == -1){
		alert("Click on a station first!");
	}else{
		db.transaction(function (tx) {
			tx.executeSql('SELECT name FROM stations WHERE id = '+global_stations.active+';', [], function (tx, results) {	
				var r = confirm("Delete station "+results.rows.item(0).name+"?");
				if(r){
					db.transaction(function (tx) {
						tx.executeSql('DELETE FROM stations WHERE id = '+global_stations.active+';', [], function (tx, results) {	
							global_stations.active = -1;
							drawStationsList();
						}, errorDatabaseHandler);
					});
				}
			}, errorDatabaseHandler);
		});
	}
}
/*function onSelectStationButtonClicked(){
	if(global_station.active == -1){
		alert("Click on a station first!");
	}else{
		if(global_missions.active != global_missions.selected){
			db.transaction(function (tx) {
				tx.executeSql('SELECT name FROM missions WHERE id = '+global_missions.active+';', [], function (tx, results) {	
					var r = confirm("Select mission "+results.rows.item(0).name+" for simulation? (Simulator will be stopped)");
					if(r){
						global_missions.selected = global_missions.active;
						styleMissionRows();
						saveMissionsStoredVariables();
						//TODO reinit simulator
					}
				}, errorDatabaseHandler);
			});
		}
	}
}*/


function onStationClicked(id){
	var num_id = Number(id.substr(4,id.length));
	global_stations.active = num_id;
	styleStationRows();
}

function styleStationRows(){
	var list = document.getElementById("olListStations");
	var listItems = list.getElementsByTagName("li");
	var active_found = false;
	
	for (var i=0; i < listItems.length; i++) {
		var num_id = Number(listItems[i].id.substr(4,listItems[i].id.length));
	
		if (num_id == global_stations.active){
			listItems[i].className = "StationActive";
			active_found = true;
		}else{
			listItems[i].className = "StationNormal";
		}
	}
	
	var recursive = false;
	if(!active_found){
		if(listItems.length > 0){
			global_stations.active = Number(listItems[0].id.substr(4,listItems[0].id.length));
			recursive = true;
		}else{
			global_stations.active = -1;
		}
	}
	
	if(recursive){
		styleStationRows();
	}
}

function drawStationsList(){
	db.transaction(function (tx) {
		tx.executeSql('SELECT * FROM stations ORDER BY name COLLATE NOCASE;', [], function (tx, results) {
			var station_rows = results.rows;
			var div_list = document.getElementById("olListStations"); 
			var content = "";
			var len = station_rows.length, i;
			for (i = 0; i < len; i++) {
				var row = station_rows.item(i); 
				var checked = "";
				if(row.enabled == "true"){
					checked = "checked";
				}
				content += "<li id='sta_"+row.id+"' class='StationNormal' onclick='onStationClicked(this.id)'><input type='checkbox' id='stc_"+row.id+"' "+checked+" onchange='changeStationEnabled(this.id,this.checked)'/>"+row.name+"</li>";
			}
			div_list.innerHTML = content;
			styleStationRows();
		}, errorDatabaseHandler);
	});
}

function resetStationsDb(){
	var r = confirm("Reset stations list to default value?");
	if(r){
		db.transaction(function (tx) {
			tx.executeSql('DROP TABLE stations', [], function (tx, results) {
				window.location.reload();
			}, errorDatabaseHandler);
		});
	}
}

function initializeStationsDb(){
	db.transaction(function (tx) {
		tx.executeSql('CREATE TABLE stations (id INTEGER PRIMARY KEY, isDefault BOOLEAN, name VARCHAR(255), enabled BOOLEAN, json BLOB)', [], function (tx, results) {
			var default_stations = new Array();
			var station;
			
			station = new Station();
			station.name = "Villafranca";
			station.enabled = false;
			station.latitude = 40.442592;
			station.longitude = -3.951583;
			station.altitude = 664.8;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Kourou";
			station.enabled = true;
			station.latitude = 5.251439;
			station.longitude = -52.804664;
			station.altitude = 14.6709;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Cebreros";
			station.enabled = false;
			station.latitude = 40.452689;
			station.longitude = -4.36755;
			station.altitude = 794.095;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Kiruna";
			station.enabled = false;
			station.latitude = 67.857128;
			station.longitude = 20.964325;
			station.altitude = 402.1724;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Maspalomas";
			station.enabled = false;
			station.latitude = 27.762889;
			station.longitude = -15.6338;
			station.altitude = 205.1177;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Poker Flat";
			station.enabled = false;
			station.latitude = 65.116667;
			station.longitude = -147.461667;
			station.altitude = 430.34;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Santiago";
			station.enabled = false;
			station.latitude = -33.151794;
			station.longitude = -70.667312;
			station.altitude = 730.0;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Canberra";
			station.enabled = false;
			station.latitude = -39.018556;
			station.longitude = 148.983058;
			station.altitude = 680.0;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Goldstone";
			station.enabled = false;
			station.latitude = 35.339907;
			station.longitude = -116.883019;
			station.altitude = 956.059;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			station = new Station();
			station.name = "Tokyo";
			station.enabled = true;
			station.latitude = 35.708762;
			station.longitude = 139.491778;
			station.altitude = -641.245;
			station.elevation = 1;
			default_stations[default_stations.length] = station;
			
			
			for(var i = 0; i < default_stations.length; i++)
				tx.executeSql('INSERT INTO stations (isDefault, name, enabled, json) VALUES (?, ?, ?, ?)', [true, default_stations[i].name, default_stations[i].enabled, JSON.stringify(default_stations[i])], successDatabaseHandler, errorDatabaseHandler);
			});
			
			loadStationsStoredVariables();
			global_delayed_loading.database.stations = true;
			setLoadingText("Stations loaded!");
			hideSplash();
		}, function(){
			loadStationsStoredVariables();
			global_delayed_loading.database.stations = true;
			setLoadingText("Stations loaded!");
			hideSplash();
		});
}

function addStationToDb(station){
	var json = JSON.stringify(station);
	db.transaction(function (tx) {
		tx.executeSql('INSERT INTO stations (isDefault, name, enabled, json) VALUES (?, ?, ?, ?)', [false, station.name, station.enabled, json], onStationEditorConfirm);
	});
}
function onStationEditorConfirm(){
	closeStationEditor();
	drawStationsList();
}

function editStationToDb(id,station){
	var json = JSON.stringify(station);
	db.transaction(function (tx) {
		tx.executeSql('UPDATE stations SET name=?, enabled=?, json=? WHERE id=?', [station.name, station.enabled, json, id], onStationEditorConfirm);
	});
}

function changeStationEnabled(id,enabled){
	var num_id = Number(id.substr(4,id.length));
	db.transaction(function (tx) {
		tx.executeSql('UPDATE stations SET enabled=? WHERE id=?', [enabled, num_id], onStationEditorConfirm);
	});
}

function updateStationEditor(station,station_id){
	/*var field;
	field = document.getElementById("mis-Id");
	field.value = mission_id;
	
	field = document.getElementById("mis-Title");
	if(mission_id == -1){
		field.innerHTML = "Create Mission";
	}else{
		field.innerHTML = "Edit Mission";
	}
	field = document.getElementById("mis-Name");
	field.value = mission.name;
	field = document.getElementById("mis-Description");
	field.value = mission.description;
	field = document.getElementById("mis-Mass");
	field.value = mission.initial_mass;
	
	$('.datepicker').pickadate({
		clear: '',
		format: 'yyyy/mm/dd',
		formatSubmit: 'yyyy/m/d'
	});
	$('.timepicker').pickatime({
		clear: '',
		format: 'HH:i',
		formatLabel: 'HH:i',
		formatSubmit: 'H:i'
	});
	
	field = document.getElementById("mis-Date");
	field.className="MissionField";
	field.value = mission.initial_date.year+"/"+mission.initial_date.month+"/"+mission.initial_date.day;
	field = document.getElementById("mis-Time");
	field.className="MissionField";
	field.value = mission.initial_date.hour+":"+mission.initial_date.minute;
	
	
	
	field = document.getElementById("mis-Duration");
	field.value = mission.duration;
	field = document.getElementById("mis-Speed");
	field.value = mission.step*parameters.simulator_target_fps;
	field = document.getElementById("mis-a");
	field.value = mission.initial_orbit.a;
	field = document.getElementById("mis-e");
	field.value = mission.initial_orbit.e;
	field = document.getElementById("mis-i");
	field.value = mission.initial_orbit.i;
	field = document.getElementById("mis-omega");
	field.value = mission.initial_orbit.omega;
	field = document.getElementById("mis-raan");
	field.value = mission.initial_orbit.raan;
	field = document.getElementById("mis-lm");
	field.value = mission.initial_orbit.lM;*/
}

function saveStationEditor(){
	try{
		/*var field;
		
		var mission = new Mission();
		
		field = document.getElementById("mis-Name");
		mission.name = field.value;
		field = document.getElementById("mis-Description");
		mission.description = field.value;
		field = document.getElementById("mis-Mass");
		mission.initial_mass = field.value;
		
		field = document.getElementById("mis-Date");
		var dates = field.value.split('/');
		mission.initial_date.year = Number(dates[0]);
		mission.initial_date.month = Number(dates[1]);
		mission.initial_date.day = Number(dates[2]);
		field = document.getElementById("mis-Time");
		var times = field.value.split(':');
		mission.initial_date.hour = Number(times[0]);
		mission.initial_date.minute = Number(times[1]);
		
		field = document.getElementById("mis-Duration");
		mission.duration = Number(field.value);
		field = document.getElementById("mis-Speed");
		mission.step = Number(field.value)/parameters.simulator_target_fps;
		
		
		field = document.getElementById("mis-a");
		mission.initial_orbit.a = Number(field.value);
		field = document.getElementById("mis-e");
		mission.initial_orbit.e = Number(field.value);
		field = document.getElementById("mis-i");
		mission.initial_orbit.i = Number(field.value);
		field = document.getElementById("mis-omega");
		mission.initial_orbit.omega = Number(field.value);
		field = document.getElementById("mis-raan");
		mission.initial_orbit.raan = Number(field.value);
		field = document.getElementById("mis-lm");
		mission.initial_orbit.lM = Number(field.value);
		
		field = document.getElementById("mis-Id");
		var id = Number(field.value);
		if(id == -1){//Create mode
			addMissionToDb(mission);
		}else{//Edit mode
			editMissionToDb(id,mission);
		}*/
		
	}catch(err){
		alert("Format error");
	}
}

function closeStationEditor(){
	if(global_menus.station.isOpen){
		$( "#StationEditorBackground" ).fadeOut( "slow", function() {
			// Animation complete.
		  });
		global_menus.station.isOpen = !global_menus.station.isOpen;
	}
}

function openStationEditor(id){
	db.transaction(function (tx) {
		tx.executeSql('SELECT * FROM stations WHERE id = '+global_stations.active+';', [], function (tx, results) {	
			if(!global_menus.station.isOpen){
				if(id == -1){//Create mode
					var station = new Station();
					updateStationEditor(station,id);
					$( "#StationEditorBackground" ).fadeIn( "slow", function() {
						// Animation complete.
					  });
					global_menus.station.isOpen = !global_menus.station.isOpen;
				}else{//Edit Mode
					var station = JSON.parse(results.rows.item(0).json);
					updateStationEditor(station,id);
					$( "#StationEditorBackground" ).fadeIn( "slow", function() {
						// Animation complete.
					  });
					global_menus.station.isOpen = !global_menus.station.isOpen;
				}
			}
		}, errorDatabaseHandler);
	});
}