var db_missions = new PouchDB('missions');
	
function addMission(mission) {
  //Check id? or put directly next int
  db.put(mission, function callback(err, result) {
	if (!err) {
	  console.log('Successfully added a Mission!');
	}
  });
}
/*
function showTodos() {
  db.allDocs({include_docs: true, descending: true}, function(err, doc) {
	var out_str = "";
	for(var i = 0; i < doc.rows.length; i++){
		out_str += doc.rows[i].doc._id+"\r\n"; 
	}
	alert(out_str);
  });
}*/