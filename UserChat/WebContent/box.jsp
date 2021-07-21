<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%
String userID = null;
if (session.getAttribute("userID") != null) {
	userID = (String) session.getAttribute("userID");
}
if (userID == null) {
	session.setAttribute("messageType", "오류 메시지");
	session.setAttribute("messageContent", "현재 로그인이 되어 있지않는 상태입니다.");
	response.sendRedirect("index.jsp");
	return;
}
%>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="./css/bootstrap.min.css">
<link rel="stylesheet" href="css/custom.css">
<title>JSP Ajax 실시간 회원제 채팅 서비스</title>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="js/bootstrap.js"></script>
<script type="text/javascript">
	function getUnread(){
		$.ajax({
			 type: "POST",
			 url: "./chatUnread",
			 data: {
				 userID: encodeURIComponent('<%=userID%>'),
			 },
			 success: function(result){
				 if(result >= 1){
					 showUnread(result);
				 } else {
					 showUnread("");
				 }
			 }
		});
	}
	function getInfiniteUnread(){
		setInterval(function(){
			getUnread();
		}, 4000);
	}
		function showUnread(result) {
			$('#unread').html(result);
	}
	
		function chatBoxFunction(){
			var userID = '<%=userID%>';
		$.ajax({
			type : "POST",
			url : "./chatBox",
			data : {
				userID : encodeURIComponent(userID),
			},
			success : function(data) {
				if (data == "")
					return;
				$('#boxTable').html('');
				var parsed = JSON.parse(data);
				var result = parsed.result;
				for (var i = 0; i < result.length; i++) {
					if (result[i][0].value.replace(" ", "").replace("&nbsp;","") == userID.replace(" ", "").replace("&nbsp;", "")) {
						result[i][0].value = result[i][1].value;
					} else {
						result[i][1].value = result[i][0].value
								.replace(" ", "").replace("&nbsp;", "");
					}
					addBox(result[i][0].value, result[i][1].value.replace(" ",
							"").replace("&nbsp;", ""), result[i][2].value,
							result[i][3].value);
				}
			}
		});
	}

	//화면에 실제 메시지함을 출력해주는 부분
	function addBox(lastID, toID, chatContent, chatTime) {
			$('#boxTable').append(
					'<tr onclick="location.href=\'chat.jsp?toID='
							+ encodeURIComponent(toID) + '\'">'
							+ '<td style="width: 150px">' + '<h5>' + lastID
							+ '</h5></td>' + '<td>' + '<h5>' + chatContent
							+ '<span id="unread" class="label label-info">'+'</span>'
							+ '</h5>' + '<div class="pull-right">' + chatTime
							+ '</div>' + '</td>' + '</tr>');
	}

	function getInfiniteBox() {
		setInterval(function() {
			chatBoxFunction();
		}, 300);
	}
</script>
</head>
<body id="bootstrap-overrides">


	<jsp:include page="nav_list.jsp" flush="false"/>
	
	<div class="container">
		<table class="table" style="margin: 0 auto;">
			<thead>
				<tr>
					<th><h4>주고받은 메시지 목록</h4></th>
				</tr>
			</thead>
			<div style="overflow-y: auto; width: 100%; max-height: 450px;">
				<table class="table table-bordered table-hover" style="text-align: center; border: 1px solid #ddd; margin: 0 auto;">
					<tbody id="boxTable">
					</tbody>
				</table>
			</div>
		</table>
	</div>




	<%
	String messageContent = null;
	if (session.getAttribute("messageContent") != null) {
		messageContent = (String) session.getAttribute("messageContent");
	}
	String messageType = null;
	if (session.getAttribute("messageType") != null) {
		messageType = (String) session.getAttribute("messageType");
	}
	if (messageContent != null) {
	%>
	<div class="modal fade" id="messageModal" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="vertical-alignment-helper">
			<div class="modal-dialog vertical-align-center">
				<div class="modal-content <%if (messageType.equals("오류 메시지"))
	out.println("panel-warning");
else
	out.println("panel-success");%>">
					<div class="modal-header panel-heading">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times</span> <span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title">
							<%=messageType%>
						</h4>
					</div>
					<div class="modal-body">
						<%=messageContent%>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" data-dismiss="modal">확인</button>
					</div>
				</div>
			</div>
		</div>
	</div>



	<script type="text/javascript">
		$("#messageModal").modal("show");
	</script>
	<!-- 오류가 생성되었다면 원래 있던것을 지워서 null로 변경 -->
	<%
	session.removeAttribute("messageContent");
	session.removeAttribute("messageType");
	}
	%>

	<%
	if (userID != null) {
	%>
	<script type="text/javascript">
		$(document).ready(function() {
			getUnread();
			getInfiniteUnread();
			chatBoxFunction();
			getInfiniteBox();
		});
	</script>
	<%
	}
	%>

</body>
</html>