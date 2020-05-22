function stringIsNull(str) {
	if (!str || str == "")
		return true;
	var regu = "^[ ]+$";
	var re = new RegExp(regu);
	return re.test(str);
}

function changeType() {
	var type = $("#type").val();
	if (type == "1") {
		$("#controlShow").show()
	} else {
		$("#controlShow").hide()
	}
}

function login() {
	var ip = $("#ip").val()
	if (stringIsNull(ip)) {
		alert("请输入IP地址");
		return;
	}
	var port = $("#port").val()
	if (stringIsNull(port)) {
		alert("请输入端口号")
		return;
	}
	var type = $("#type").val();
	var url = null, data = null;
	if (type == "1") {
		var name = $("#name").val()
		var password = $("#password").val()
		url = "/webTool/loginSsh";
		data = {
			ip : ip,
			port : port,
			user : name,
			passWord : password
		}
	} else {
		url = "/webTool/loginTelnet";
		data = {
			ip : ip,
			port : port
		}
	}
	sendRequest(url, data)
}

function sendRequest(url, data) {
	$("#wait_icon").show()
	$.ajax({
		method : 'GET',
		url : url,
		data : data,
		dataType : "json",
		success : function(data) {
			$("#wait_icon").hide()
			if (!data.ok) {
				alert(data.msg + "：" + data.obj)
				return;
			}
			jumpToController(data.obj)
		},
		error : function() {
			$("#wait_icon").hide()
			alert("网络错误")
		}
	});
}

function jumpToController(obj) {
	var type = $("#type").val();
	window.open("/jumpConsole?theme=" + obj + "&type=" + type, '_blank');
}
