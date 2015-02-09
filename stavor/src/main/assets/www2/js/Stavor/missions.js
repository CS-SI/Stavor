var global_active_mission = -1;
var global_selected_mission = -1;

function onMissionClick(id){
}

function drawList(){
	var div_list = document.getElementById("olListMissions"); 
	var content = "";
	
	
	
	div_list.innerHTML = content;
}

function selectMisison(){
}
function editMission(){
}
function deleteMission(){
	var r = confirm("Delete mission "+getMissionNameById(global_active_mission));
	if(r){
		deleteMissionFromDb(global_active_mission);
		drawList();
	}
}
function createMission(){
}

//Database
function deleteMissionFromDb(id){
}

function getAllMissions(){
}

function getMissionNameById(id){
	return getMissionById(id).name;
}

function getMissionById(id){
	
}