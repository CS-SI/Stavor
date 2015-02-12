var OrekitData = function(){
	this.utcTaiHistory = "";
}

var orekitData = new OrekitData();

var loadOrekitFilesText = function(){
	var url = 'modules/simulator/orekit/orekit-data/UTC-TAI.history';
	var xhr = createCORSRequest('GET', url);
	if (!xhr) {
	  throw new Error('CORS not supported');
	}else{
		xhr.open('GET', url, true);
		//xhr.responseType = 'text';

		xhr.onload = function(e) {
		  //if (this.status == 200) {
			// Note: .response instead of .responseText
			//var blob = new Blob([this.response], {type: 'text/plain'});
			//alert(this.responseText);
			orekitData.utcTaiHistory = this.responseText;
		  //}
		};

		xhr.send();
	}
}
function createCORSRequest(method, url) {
  var xhr = new XMLHttpRequest();
  if ("withCredentials" in xhr) {

	// Check if the XMLHttpRequest object has a "withCredentials" property.
	// "withCredentials" only exists on XMLHTTPRequest2 objects.
	xhr.open(method, url, true);

  } else if (typeof XDomainRequest != "undefined") {

	// Otherwise, check if XDomainRequest.
	// XDomainRequest only exists in IE, and is IE's way of making CORS requests.
	xhr = new XDomainRequest();
	xhr.open(method, url);

  } else {

	// Otherwise, CORS is not supported by the browser.
	xhr = null;

  }
  return xhr;
}


var loadOrekitFilesBloB = function(){
	var xhr = new XMLHttpRequest();
	xhr.open('GET', 'modules/simulator/orekit/orekit-data/UTC-TAI.history', true);
	xhr.responseType = 'blob';

	xhr.onload = function(e) {
	  if (this.status == 200) {
		// Note: .response instead of .responseText
		var blob = new Blob([this.response], {type: 'text/plain'});
		alert(blob);
	  }
	};

	xhr.send();
}
var loadOrekitFilesArray = function(){
	var xhr = new XMLHttpRequest();
	xhr.open('GET', '/path/to/image.png', true);
	xhr.responseType = 'arraybuffer';

	xhr.onload = function(e) {
	  var uInt8Array = new Uint8Array(this.response); // this.response == uInt8Array.buffer
	  // var byte3 = uInt8Array[4]; // byte at offset 4
	};

	xhr.send();
}