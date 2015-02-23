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
		Dialog.showDialog("Stavor says", "Click on a station first!", function(){});
	}else{
		db.transaction(function (tx) {
			tx.executeSql('SELECT name FROM stations WHERE id = '+global_stations.active+';', [], function (tx, results) {	
				Dialog.showConfirmDialog("Stavor says","Delete station "+results.rows.item(0).name+"?",function(){
					db.transaction(function (tx) {
						tx.executeSql('DELETE FROM stations WHERE id = '+global_stations.active+';', [], function (tx, results) {	
							global_stations.active = -1;
							drawStationsList();
						}, errorDatabaseHandler);
					});
				},function(){});
			}, errorDatabaseHandler);
		});
	}
}


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
	Dialog.showConfirmDialog("Stavor says","Reset missions list to default value?",function(){
		db.transaction(function (tx) {
			tx.executeSql('DROP TABLE stations', [], function (tx, results) {
				window.location.reload();
			}, errorDatabaseHandler);
		});
	},function(){});
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
	var field;
	field = document.getElementById("sta-Id");
	field.value = station_id;
	
	field = document.getElementById("sta-Title");
	if(station_id == -1){
		field.innerHTML = "Create Station";
	}else{
		field.innerHTML = "Edit Station";
	}
	field = document.getElementById("sta-Name");
	field.value = station.name;
	field = document.getElementById("sta-longitude");
	field.value = station.longitude;
	field = document.getElementById("sta-latitude");
	field.value = station.latitude;
	field = document.getElementById("sta-altitude");
	field.value = station.altitude;
	field = document.getElementById("sta-elevation");
	field.value = station.elevation;
}

function saveStationEditor(){
	try{
		var field;
		
		var station = new Station();
		
		field = document.getElementById("sta-Name");
		station.name = field.value;
		field = document.getElementById("sta-longitude");
		station.longitude = Number(field.value);
		field = document.getElementById("sta-latitude");
		station.latitude = Number(field.value);
		field = document.getElementById("sta-altitude");
		station.altitude = Number(field.value);
		field = document.getElementById("sta-elevation");
		station.elevation = Number(field.value);
		
		field = document.getElementById("sta-Id");
		var id = Number(field.value);
		if(id == -1){//Create mode
			addStationToDb(station);
		}else{//Edit mode
			editStationToDb(id,station);
		}
		
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