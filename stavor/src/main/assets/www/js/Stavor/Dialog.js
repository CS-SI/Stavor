var Dialog = function (){
	/*this.title = "Stavor says";
	this.text = "";
	
	this.confirmation = false;
	this.callback_confirm = "";
	this.callback_cancel = "";*/
	
	this.dialog = document.getElementById("DivDialogBackground");
	this.dialog_title = document.getElementById("DivDialogTitle");
	this.dialog_text = document.getElementById("DivDialogText");
	this.confirm_button = document.getElementById("DialogConfirmButton");
	this.cancel_button = document.getElementById("DialogCloseButton");
}

Dialog.prototype.showDialog = function(title, message){
	this.title = title;
	this.text = message;
	this.confirmation = false;
	
	//Show dialog
	this.dialog.style.display = "block";
}
Dialog.prototype.showDialog = function(message){
	this.showDialog(this.title, message);
}

Dialog.prototype.showConfirmDialog = function(title, message, callbackOk, callbackCancel){
	this.title = title;
	this.text = message;
	this.confirmation = true;
	this.callback_confirm = callbackOk;
	this.callback_cancel = callbackCancel;
	//Show confirm dialog
	
}
Dialog.prototype.showConfirmDialog = function(message, callbackOk, callbackCancel){
	this.showConfirmDialog(this.title, message, callbackOk, callbackCancel);
}