<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head profile="http://www.w3.org/2005/10/profile">
<link rel="icon" type="image/png" href="http://example.com/myicon.png">

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="./css/bootstrap.min.css">
<link rel="stylesheet" href="css/custom.css">
<!-- 
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
 --><meta name="viewport" content="width=device-width, initial-scale=1">
<title>JSP Ajax 실시간 회원제 채팅 서비스</title>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="js/bootstrap.js"></script>

</head>
<body id="bootstrap-overrides">
	<%
	String userID = null;
	if (session.getAttribute("userID") != null) {
		userID = (String) session.getAttribute("userID");
	}
	if(userID != null){
		session.setAttribute("messageType","오류 메시지");
		session.setAttribute("messageContent","현재 로그인이 되있는 상태입니다.");
		response.sendRedirect("index.jsp");
		return;
	}
	%>
	<jsp:include page="nav_list.jsp" flush="false"/>
	
	<div class="container">
		<form method="POST" action="./userLogin">
			<table class="table table-bordered table-hover" style="text-align:center; border:1px solid #dddddd">
				<thead>
				<tr>
					<th colspan="2"><h4>로그인 양식</h4></th>				
				</tr>
				</thead>
				<tr>
					<td style="width:110px;"><h5>아이디</h5></td>
					<td><input class="form-control" type="text" name="userID" maxlength="20" placeholder="아이디를 입력하세요"></td>
				</tr>
				<tr>
					<td style="width:110px;"><h5>비밀번호</h5></td>
					<td><input class="form-control" type="password" name="userPassword" maxlength="20" placeholder="비밀번호를 입력하세요"></td>
				</tr>
				<tr>
					<td style="text-align:left;" colspan="2"><input class="btn btn-primary pull-right" type="submit" value="로그인"></td>				
				</tr>
			</table>
		
		</form>
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

	<div class="modal fade" id="checkModal" tabindex="-1" role="dialog" aria-hidden="true">
		<div class="vertical-alignment-helper">
			<div class="modal-dialog vertical-align-center">
				<div id="checkType" class="modal-content panel-heading">
					<div class="modal-header panel-heading">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times</span> <span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title">확인 메시지</h4>
					</div>
					<div id="checkMessage" class="modal-body"></div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" data-dismiss="modal">확인</button>
					</div>
				</div>
			</div>
		</div>
	</div>




</body>
</html>