function localizeStrings(){
	$(function(){
		var lang = getURLParameter('lang');
		if(lang){
			var opts = { pathPrefix: "lang", language: lang };
			$("[data-localize]").localize("strings", opts)
		}else{
			//var opts = { pathPrefix: "lang", skipLanguage: /^en/ };
			var opts = { pathPrefix: "lang" };
			$("[data-localize]").localize("strings", opts)
		}
	});
	
	localizeDynamicStrings();
}

function localizeDynamicStrings(){
	document.getElementById("ResetConfigButton").value = $.localize.data.strings.global_config_reset_config_button;
	document.getElementById("ResetMissionsButton").value = $.localize.data.strings.global_config_reset_missions_button;
	document.getElementById("ResetStationsButton").value = $.localize.data.strings.global_config_reset_stations_button;
	
	document.getElementById("SelectMissionButton").title = $.localize.data.strings.mission_list_button_select;
	document.getElementById("CreateMissionButton").title = $.localize.data.strings.mission_list_button_create;
	document.getElementById("CopyMissionButton").title = $.localize.data.strings.mission_list_button_copy;
	document.getElementById("EditMissionButton").title = $.localize.data.strings.mission_list_button_edit;
	document.getElementById("RemoveMissionButton").title = $.localize.data.strings.mission_list_button_remove;
	
	document.getElementById("CreateStationButton").title = $.localize.data.strings.station_list_button_create;
	document.getElementById("EditStationButton").title = $.localize.data.strings.station_list_button_edit;
	document.getElementById("RemoveStationButton").title = $.localize.data.strings.station_list_button_remove;
}

function getURLParameter(name) {
	return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null
}


