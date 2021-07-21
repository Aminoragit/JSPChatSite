<%@page import="java.net.URLDecoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://www.w3.org/2005/10/profile">

<%
String userID = null;
if (session.getAttribute("userID") != null) {
	userID = (String) session.getAttribute("userID");
}
String toID = null;
if (request.getParameter("toID") != null) {
	toID = (String) request.getParameter("toID");
}
if (userID == null) {
	session.setAttribute("messageType", "오류 메시지");
	session.setAttribute("messageContent", "현재 로그인이 되있지 않은 상태입니다.");
	response.sendRedirect("index.jsp");
	return;
}
if (toID == null) {
	session.setAttribute("messageType", "오류 메시지");
	session.setAttribute("messageContent", "대화 상대가 지정되지 않았습니다.");
	response.sendRedirect("index.jsp");
	return;
}
if(userID.equals(URLDecoder.decode(toID,"UTF-8"))){
	session.setAttribute("messageType", "오류 메시지");
	session.setAttribute("messageContent", "커스텀 예외처리(자기 자신에겐 메시지를 보낼수 없습니다.)");
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
	function autoClosingAlert(selector, delay){
		var alert = $(selector).alert();
		alert.show();
		window.setTimeout(function() {alert.hide() }, delay);
	}
	<!-- 일단 insert는 되는거 보니까 insert 문제는 아닌것 같고 -->
	function submitFunction(){
		var fromID= '<%=userID%> ';
		var toID= '<%=toID%>';
		var chatContent = $("#chatContent").val();
		$.ajax({
			type : "POST",
			url : "./chatSubmitServlet",
			data : {
				fromID : encodeURIComponent(fromID),
				toID : encodeURIComponent(toID),
				chatContent : encodeURIComponent(chatContent)
			},
			success : function(result) {
				if (result == 1) {
					autoClosingAlert("#successMessage", 2000);
				} else if (result == 0) {
					autoClosingAlert("#dangerMessage", 2000);
				} else {
					autoClosingAlert("#warningMessage", 2000);
				}
			}
		});
		$("#chatContent").val(" "); //전송 완료후 값 비우기
	}
	var lastID = 0;
	function chatListFunction(type){
		var fromID = '<%=userID%>';
		var toID = '<%=toID%>';
		$.ajax({
			type: "POST",
			url: "./chatListServlet",
			data: {
				fromID: encodeURIComponent(fromID),
				toID: encodeURIComponent(toID),
				listType: type
			},
			success: function(data){
				if(data == "") return;
				var parsed = JSON.parse(data);
				var result = parsed.result;
				for (var i = 0; i < result.length; i++) {
					if(result[i][0].value.replace(" ", "").replace("&nbsp;","") == fromID.replace(" ", "").replace("&nbsp;",""))
						result[i][0].value = "나";
						addChat(result[i][0].value, result[i][2].value, result[i][3].value);
				}
				lastID = Number(parsed.last);
			}
		});
	}
	function addChat(chatName, chatContent, chatTime) {
	$('#chatList')
				.append(
						'<div class="row">'
								+ '<div class="col-lg-12">'
								+ '<div class="media">'
								+ '<a class="pull-left" href="#">'
								+ '<img class="media-object img-circle" style="width:30px; height:30px;" src="images/anonymous.png" alt="">'
								+ '</a>' + '<div class="media-body">'
								+ '<h4 class="media-heading">' + chatName
								+ '<span class="small pull-right">' + chatTime
								+ '</span>' + '</h4>' + '<p>' + chatContent
								+ '</p>' + '</div>' + '</div>' + '</div>'
								+ '</div>' + '<hr>');
		$('#chatList').scrollTop($('#chatList')[0].scrollHeight);
<%System.out.println("addChat종료");%>
	}
	function getInfiniteChat() {
		setInterval(function() {
			chatListFunction(lastID);
		}, 3000);
	}
	function getUnread(){
		$.ajax({
			type: "POST",
			 url: "./chatUnread",
			 data: {
				 userID: encodeURIComponent('<%=userID%>'),
			},
			success : function(result) {
				if (result >= 1) {
					showUnread(result);
				} else {
					showUnread("");
				}
			}
		});
	}
	function getInfiniteUnread() {
		setInterval(function() {
			getUnread();
		}, 400);
	}
	function showUnread(result) {
		$('#unread').html(result);
	}
	
	
</script>
</head>
<body id="bootstrap-overrides">

	<jsp:include page="nav_list.jsp" flush="false"/>

	<div class="container bootstrap snippet">
		<div class="row">
			<div class="col-xs-12">
				<div class="portlet portlet-default">
					<div class="portlet-heading">
						<div class="portlet-title">
							<h4>
								<i class="fa fa-circle text-green"></i>실시간 채팅창
							</h4>
						</div>
						<div class="clearfix"></div>
					</div>
					<div id="chat" class="panel-collapse collapse in">
						<div id="chatList" class="portlet-body chat-widget" style="overflow-y: auto; width: auto; height: 600px;"></div>
						<div class="portlet-footer">
							<div class="row" style="height: 90px;">
								<div class="form-group col-xs-10">
									<textarea style="height: 80px;" id="chatContent" class="form-control" placeholder="메시지를 입력하세요." maxlength="100"></textarea>
								</div>
								<div class="form-group col-xs-2">
									<button type="button" class="btn btn-default pull-right" onclick="submitFunction();">전송</button>
									<div class="clearfix"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="alert alert-success" id="successMessage" style="display: none;">
		<strong>메시지 전송에 성공했습니다.</strong>
	</div>
	<div class="alert alert-danger" id="dangerMessage" style="display: none;">
		<strong>이름과 내용을 모두 입력하시오.</strong>
	</div>
	<div class="alert alert-warning" id="warningMessage" style="display: none;">
		<strong>DB 에러 발생</strong>
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




	<script>
		$("#messageModal").modal("show");
	</script>

	<!-- 오류가 생성되었다면 원래 있던것을 지워서 null로 변경 -->
	<%
	session.removeAttribute("messageContent");
	session.removeAttribute("messageType");
	}
	%>
	<script type="text/javascript">
		$(document).ready(function() {
			chatListFunction('0');
			getInfiniteChat();
			getInfiniteUnread();
		})
	</script>
</body>
</html>