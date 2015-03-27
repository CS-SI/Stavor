function localizeStrings(){
	$(function(){
		var lang = getURLParameter('lang');
		if(lang){
			$("[data-translate]").jqTranslate('strings',{defaultLang: 'en', asyncLangLoad: false, path: "lang", onComplete: dialogCallback, forceLang: lang});
		}else{
			$("[data-translate]").jqTranslate('strings',{defaultLang: 'en', asyncLangLoad: true, path: "lang", onComplete: dialogCallback});
		}
	});
	
}

function dialogCallback(){
	//data.title = data.title + currentBugName();
	//defaultCallback(data);
	localizeDynamicStrings();
}

function localizeDynamicStrings(){
	document.getElementById("ResetConfigButton").value = document.getElementById("str_global_config_reset_config_button").innerHTML;
	document.getElementById("ResetMissionsButton").value = document.getElementById("str_global_config_reset_missions_button").innerHTML;
	document.getElementById("ResetStationsButton").value = document.getElementById("str_global_config_reset_stations_button").innerHTML;
	
	document.getElementById("SelectMissionButton").title = document.getElementById("str_mission_list_button_select").innerHTML;
	document.getElementById("CreateMissionButton").title = document.getElementById("str_mission_list_button_create").innerHTML;
	document.getElementById("CopyMissionButton").title = document.getElementById("str_mission_list_button_copy").innerHTML;
	document.getElementById("EditMissionButton").title = document.getElementById("str_mission_list_button_edit").innerHTML;
	document.getElementById("RemoveMissionButton").title = document.getElementById("str_mission_list_button_remove").innerHTML;
	
	document.getElementById("CreateStationButton").title = document.getElementById("str_station_list_button_create").innerHTML;
	document.getElementById("EditStationButton").title = document.getElementById("str_station_list_button_edit").innerHTML;
	document.getElementById("RemoveStationButton").title = document.getElementById("str_station_list_button_remove").innerHTML;
}

function getURLParameter(name) {
	return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null
}


