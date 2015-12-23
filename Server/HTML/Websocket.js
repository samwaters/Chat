var wsClient = {};
$(document).ready(function() {
	log("Client started");
	$("#connectBtn").on("click", function() {
		var $this = $(this);
		if($this.is(".btn-success"))
		{
			createWebsocket();
		}
		else
		{
			wsClient.socket.close();
		}
	});
});

function createWebsocket()
{
	var host = $("#server").val();
	var port = $("#port").val();
	var url = "ws://" + host + ":" + port;
	wsClient.socket = new WebSocket(url);
	wsClient.socket.onclose = function(e) { log("Disconnecting from server"); serverOptions(false); };
	wsClient.socket.onerror = function(e) { log("Socket error!"); serverOptions(false); };
	wsClient.socket.onmessage = function(e) { log("Message received: " + e.data); };
	wsClient.socket.onopen = function(e) { log("Connecting to " + url); serverOptions(true); };
}

function log(message)
{
	var d = new Date();
	var timeString = d.getFullYear() + "-" + zeroPad(d.getMonth() + 1) + "-" + zeroPad(d.getDay()) + " " + zeroPad(d.getHours()) + ":" + zeroPad(d.getMinutes());
	$("#log").prepend("<p>[" + timeString + "] " + message + "</p>"); 
}

function serverOptions(disable)
{
	var $cb = $("#connectBtn");
	if(disable == true)
	{
		$cb.removeClass("btn-success").addClass("btn-danger").html("Disconnect From Server");
		$("#server, #port, #serverProtocolJSON, #serverProtocolXML").addClass("disabled").attr("disabled", "disabled");
	}
	else
	{
		$cb.removeClass("btn-danger").addClass("btn-success").html("Connect To Server");
		$("#server, #port, #serverProtocolJSON, #serverProtocolXML").removeClass("disabled").removeAttr("disabled");
	}
}

function zeroPad(string)
{
	string = "" + string; 
	if(string.length < 2)
	{
		return "0" + string;
	}
	return string;
}