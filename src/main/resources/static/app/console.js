function queryUrl(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null; // 返回参数值
}

function parsWebSocket() {
	var host = window.location.host;
	var theme = queryUrl("theme")
	var webSocket = null;
	var type = "1";
	if (type == "1") {
		webSocket = new WebSocket("ws://" + host + "/autoSshSocket/" + theme);
	} else {
		webSocket = new WebSocket("ws://" + host + "/autoTelnetSocket/+"
				+ theme);
	}
	return webSocket;
}

function console_main() {
	var term = new Terminal();
	term.open(document.getElementById('terminal'));
	term.focus();
	var webSocket = parsWebSocket()
	var attachAddon = new AttachAddon.AttachAddon(webSocket);
	term.loadAddon(attachAddon);
	var fitAddon = new FitAddon.FitAddon();
	term.loadAddon(fitAddon);
	fitAddon.fit();
}

console_main()